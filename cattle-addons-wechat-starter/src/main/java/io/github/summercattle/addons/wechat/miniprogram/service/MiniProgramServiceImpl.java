/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.summercattle.addons.wechat.miniprogram.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.auth.service.UserServiceImpl;
import io.github.summercattle.addons.wechat.miniprogram.MiniProgramService;
import io.github.summercattle.addons.wechat.miniprogram.MiniProgramUserSession;
import io.github.summercattle.addons.wechat.miniprogram.configure.MiniProgramConfigProperties;
import io.github.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import io.github.summercattle.addons.wechat.utils.ServerUtils;
import io.github.summercattle.commons.db.DbUtils;
import io.github.summercattle.commons.db.object.DataTable;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.auxiliary.CommonUtils;
import io.github.summercattle.commons.utils.redis.RedisTemplateUtils;
import io.github.summercattle.commons.utils.reflect.ClassType;
import io.github.summercattle.commons.utils.reflect.ReflectUtils;
import io.github.summercattle.commons.utils.spring.RestTemplateUtils;
import io.github.summercattle.commons.utils.spring.SpringContext;

public class MiniProgramServiceImpl implements MiniProgramService {

	private static final Logger logger = LoggerFactory.getLogger(MiniProgramServiceImpl.class);

	@Override
	public MiniProgramUserSession getSession(String source, String jsCode) throws CommonException {
		MiniProgramServer miniProgramServer = ServerUtils.getMiniProgramServer(source);
		if (StringUtils.isBlank(jsCode)) {
			throw new CommonException("临时登录凭证为空");
		}
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + miniProgramServer.getAppId() + "&secret=" + miniProgramServer.getSecret()
				+ "&js_code=" + jsCode + "&grant_type=authorization_code";
		logger.debug("微信小程序的会话请求地址:" + url);
		RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
		String strJson = restTemplateUtils.get(url, null, null, String.class);
		logger.debug("微信小程序的临时登录凭证:" + jsCode + ",返回:" + strJson);
		JSONObject respJson = JSON.parseObject(strJson);
		if (respJson.containsKey("errmsg")) {
			throw new CommonException("微信小程序登录接口异常,错误码:" + ((String) ReflectUtils.convertValue(ClassType.String, respJson.getIntValue("errcode")))
					+ ",错误信息:" + respJson.getString("errmsg"));
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			String openId = respJson.getString("openid");
			String unionId = respJson.getString("unionid");
			String sessionKey = respJson.getString("session_key");
			MiniProgramConfigProperties configProperties = SpringContext.getBean(MiniProgramConfigProperties.class);
			MiniProgramUserSession session = null;
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and MP_OPENID=?", new Object[] { source, openId });
			long sessionTimeout = 0;
			if (!dt.first()) {
				if (StringUtils.isNotBlank(unionId)) {
					dt = ctx.select("W_USER_BIND", "SOURCE=? and UNIONID=?", new Object[] { source, unionId });
					if (dt.first()) {
						int userType = dt.getInt("USER_TYPE");
						String bindInfo = dt.getString("BIND_INFO");
						if (userType != 0 && StringUtils.isNotBlank(bindInfo)) {
							dt.setString("MP_OPENID", openId);
							String sessionId = CommonUtils.getUUID();
							session = new MiniProgramUserSession(sessionId, openId, unionId, sessionKey, userType, bindInfo);
							ctx.save(dt);
							sessionTimeout = configProperties.getSessionTimeout();
						}
					}
				}
			}
			else {
				int userType = dt.getInt("USER_TYPE");
				String bindInfo = dt.getString("BIND_INFO");
				if (userType != 0 && StringUtils.isNotBlank(bindInfo)) {
					String sessionId = CommonUtils.getUUID();
					session = new MiniProgramUserSession(sessionId, openId, unionId, sessionKey, userType, bindInfo);
					sessionTimeout = configProperties.getSessionTimeout();
				}
			}
			if (null == session) {
				String sessionId = CommonUtils.getUUID();
				session = new MiniProgramUserSession(sessionId, openId, unionId, sessionKey);
				sessionTimeout = configProperties.getTemporarySessionTimeout();
			}
			RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
			String key = UserServiceImpl.WECHAT_SESSION + source + "_" + session.getSessionId();
			redisUtils.set(key, session, sessionTimeout);
			return session;
		});
	}
}