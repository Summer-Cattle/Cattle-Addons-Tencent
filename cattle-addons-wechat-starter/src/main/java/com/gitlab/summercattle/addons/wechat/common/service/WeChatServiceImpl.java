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
package com.gitlab.summercattle.addons.wechat.common.service;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.addons.wechat.common.AppType;
import com.gitlab.summercattle.addons.wechat.common.WeChatService;
import com.gitlab.summercattle.addons.wechat.constants.WeChatConstants;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import com.gitlab.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsServer;
import com.gitlab.summercattle.addons.wechat.utils.ServerUtils;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.redis.RedisTemplateUtils;
import com.gitlab.summercattle.commons.utils.reflect.ClassType;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;
import com.gitlab.summercattle.commons.utils.spring.RestTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

public class WeChatServiceImpl implements WeChatService {

	private static final Logger logger = LoggerFactory.getLogger(WeChatServiceImpl.class);

	private static final String WECHAT_ACCESS_TOKEN = "Wechat_AT_";

	@Override
	public String getAccessToken(AppType type, String source) throws CommonException {
		if (null == type) {
			throw new CommonException("应用类型为空");
		}
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		String accessToken = null;
		String strEnabled = SpringContext.getProperty(WeChatConstants.PROPERTY_PREFIX + ".prod.enabled");
		boolean enabled = BooleanUtils.toBoolean(strEnabled);
		if (enabled) {
			String appId;
			String secret;
			if (type == AppType.OfficialAccounts) {
				OfficialAccountsServer officialAccountsServer = ServerUtils.getOfficialAccountsServer(source);
				appId = officialAccountsServer.getAppId();
				secret = officialAccountsServer.getSecret();
			}
			else if (type == AppType.MiniProgram) {
				MiniProgramServer miniProgramServer = ServerUtils.getMiniProgramServer(source);
				appId = miniProgramServer.getAppId();
				secret = miniProgramServer.getSecret();
			}
			else {
				throw new CommonException("应用类型'" + type.toString() + "'无效");
			}
			RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
			String key = WECHAT_ACCESS_TOKEN + appId;
			if (redisUtils.exists(key)) {
				accessToken = (String) redisUtils.get(key);
			}
			if (StringUtils.isBlank(accessToken)) {
				String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
				logger.debug("微信的访问令牌请求地址:" + url);
				RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
				JSONObject respJson = restTemplateUtils.get(url, null, null, JSONObject.class);
				logger.debug("微信的访问令牌返回:" + respJson.toString());
				if (respJson.containsKey("errmsg")) {
					throw new CommonException(
							"微信获取访问令牌接口异常,错误码:" + ((String) ReflectUtils.convertValue(ClassType.String, respJson.getIntValue("errcode"))) + ",错误信息:"
									+ respJson.getString("errmsg"));
				}
				accessToken = respJson.getString("access_token");
				redisUtils.set(key, accessToken, respJson.getLongValue("expires_in"));
			}
		}
		else {
			String url = SpringContext.getProperty("wechat.prod.url");
			if (StringUtils.isBlank(url)) {
				throw new CommonException("微信后台地址为空");
			}
			if (!url.endsWith("/")) {
				url += "/";
			}
			url += "WeChat/accessToken?type=" + type.toString() + "&source=" + source;
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONObject respJson = restTemplateUtils.get(url, MediaType.APPLICATION_JSON, null, JSONObject.class);
			if (!respJson.getBooleanValue("success")) {
				throw new CommonException(respJson.getString("message"));
			}
			accessToken = respJson.getString("data");
		}
		return accessToken;
	}
}