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
package com.gitlab.summercattle.addons.wechat.miniprogram.service;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.addons.wechat.WeChatUtils;
import com.gitlab.summercattle.addons.wechat.auth.UserSession;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindInfo;
import com.gitlab.summercattle.addons.wechat.common.AppType;
import com.gitlab.summercattle.addons.wechat.constants.WeChatConstants;
import com.gitlab.summercattle.addons.wechat.miniprogram.MiniProgramService;
import com.gitlab.summercattle.addons.wechat.miniprogram.MiniProgramUserSession;
import com.gitlab.summercattle.addons.wechat.miniprogram.bind.UserPhoneNumberBindHandle;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramConfigProperties;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import com.gitlab.summercattle.addons.wechat.utils.ServerUtils;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.db.object.DataTable;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.auxiliary.CommonUtils;
import com.gitlab.summercattle.commons.utils.guice.GuiceUtils;
import com.gitlab.summercattle.commons.utils.redis.RedisTemplateUtils;
import com.gitlab.summercattle.commons.utils.reflect.ClassType;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;
import com.gitlab.summercattle.commons.utils.security.CommonEncryptUtils;
import com.gitlab.summercattle.commons.utils.security.constants.CommonEncryptType;
import com.gitlab.summercattle.commons.utils.security.constants.PaddingType;
import com.gitlab.summercattle.commons.utils.spring.RestTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

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
			String key = WeChatConstants.WECHAT_SESSION + source + "_" + session.getSessionId();
			redisUtils.set(key, session, sessionTimeout);
			return session;
		});
	}

	@Override
	public void bind(String source, String sessionId, String encryptData, String iv, Map<String, Object> parameters) throws CommonException {
		UserSession session = WeChatUtils.getUserService().getSession(source, sessionId);
		if (AppType.MiniProgram != session.getType()) {
			throw new CommonException("应用类型'" + session.getType().toString() + "'不支持");
		}
		if (StringUtils.isBlank(encryptData)) {
			throw new CommonException("加密数据为空");
		}
		if (StringUtils.isBlank(iv)) {
			throw new CommonException("加密初始化向量为空");
		}
		byte[] keyBytes = Base64.decodeBase64(((MiniProgramUserSession) session).getSessionKey());
		byte[] ivBytes = Base64.decodeBase64(iv);
		byte[] encryptDatas = Base64.decodeBase64(encryptData);
		byte[] decyrptDatas = CommonEncryptUtils.decyrptCBC(CommonEncryptType.AES, encryptDatas, keyBytes, ivBytes, PaddingType.PKCS7Padding);
		String strPhoneNumberJson = org.apache.commons.codec.binary.StringUtils.newStringUtf8(decyrptDatas);
		JSONObject phoneNumberJson = JSON.parseObject(strPhoneNumberJson);
		String phoneNumber = phoneNumberJson.getString("phoneNumber");
		UserPhoneNumberBindHandle userPhoneNumberBind = GuiceUtils.getInstance(UserPhoneNumberBindHandle.class);
		if (null == userPhoneNumberBind) {
			throw new CommonException("用户手机号绑定处理为空");
		}
		boolean exist = userPhoneNumberBind.exist(phoneNumber, StringUtils.isNotBlank(session.getUnionId()), parameters);
		if (!exist) {
			if (userPhoneNumberBind.isAutoCreated(parameters)) {
				userPhoneNumberBind.create(phoneNumber, parameters);
			}
			else {
				throw new CommonException("用户绑定异常");
			}
		}
		UserBindInfo bindResult = userPhoneNumberBind.process(phoneNumber, parameters);
		if (null == bindResult) {
			throw new CommonException("用户绑定反馈信息为空");
		}
		if (bindResult.getUserType() == 0) {
			throw new CommonException("用户类型没有设置");
		}
		if (StringUtils.isBlank(bindResult.getInfo())) {
			throw new CommonException("绑定信息为空");
		}
		String field;
		String otherField;
		if (session.getType() == AppType.OfficialAccounts) {
			field = "OA_OPENID";
			otherField = "MP_OPENID";
		}
		else if (session.getType() == AppType.MiniProgram) {
			field = "MP_OPENID";
			otherField = "OA_OPENID";
		}
		else {
			throw new CommonException("应用类型'" + session.getType().toString() + "'暂不支持");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and USER_TYPE=? and BIND_INFO=?",
					new Object[] { source, bindResult.getUserType(), bindResult.getInfo() });
			if (dt.first()) {
				throw new CommonException("用户已经被绑定");
			}
			dt = ctx.select("W_USER_BIND", "SOURCE=? and " + field + "=?", new Object[] { source, session.getOpenId() });
			if (!dt.first()) {
				if (StringUtils.isNotBlank(session.getUnionId())) {
					dt = ctx.select("W_USER_BIND", "SOURCE=? and UNIONID=?", new Object[] { source, session.getUnionId() });
					if (!dt.first()) {
						dt.insert();
					}
				}
				else {
					dt.insert();
				}
			}
			dt.setString("SOURCE", source);
			dt.setString(field, session.getOpenId());
			dt.setString("UNIONID", session.getUnionId());
			dt.setInt("USER_TYPE", bindResult.getUserType());
			dt.setString("BIND_INFO", bindResult.getInfo());
			if (StringUtils.isNotBlank(session.getUnionId())) {
				String strEnabled = SpringContext.getProperty("wechat.prod.enabled");
				boolean enabled = BooleanUtils.toBoolean(strEnabled);
				if (!enabled) {
					String url = SpringContext.getProperty("wechat.prod.url");
					if (StringUtils.isBlank(url)) {
						throw new CommonException("微信后台地址为空");
					}
					if (!url.endsWith("/")) {
						url += "/";
					}
					url += "WeChat/userBindInfo?source=" + source + "&unionId=" + session.getUnionId();
					RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
					JSONObject respJson = restTemplateUtils.get(url, MediaType.APPLICATION_JSON, null, JSONObject.class);
					if (!respJson.getBooleanValue("success")) {
						throw new CommonException(respJson.getString("message"));
					}
					JSONObject resultJson = respJson.getJSONObject("data");
					if (resultJson.containsKey(otherField)) {
						dt.setString(otherField, resultJson.getString(otherField));
					}
				}
			}
			ctx.save(dt);
			return null;
		});
		RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
		String key = WeChatConstants.WECHAT_SESSION + source + "_" + sessionId;
		if (redisUtils.exists(key)) {
			redisUtils.del(key);
		}
		session.setBindUser(true);
		session.setUserType(bindResult.getUserType());
		session.setBindInfo(bindResult.getInfo());
		MiniProgramConfigProperties configProperties = SpringContext.getBean(MiniProgramConfigProperties.class);
		int sessionTimeout = configProperties.getSessionTimeout();
		redisUtils.set(key, session, sessionTimeout);
	}
}