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
package com.gitlab.summercattle.addons.wechat.auth.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.addons.wechat.WeChatUtils;
import com.gitlab.summercattle.addons.wechat.auth.UserService;
import com.gitlab.summercattle.addons.wechat.auth.UserSession;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindAuthType;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindHandle;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindInfo;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindObjectType;
import com.gitlab.summercattle.addons.wechat.auth.info.UserInfo;
import com.gitlab.summercattle.addons.wechat.common.AppType;
import com.gitlab.summercattle.addons.wechat.configure.WeChatConfigProperties;
import com.gitlab.summercattle.addons.wechat.constants.WeChatConstants;
import com.gitlab.summercattle.addons.wechat.constants.WeChatExceptionConstants;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramConfigProperties;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.db.object.DataTable;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.guice.GuiceUtils;
import com.gitlab.summercattle.commons.utils.redis.RedisTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.RestTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private static final String WECHAT_SESSION_CAPTCHA = "Wechat_SC_";

	@Override
	public void generateBindCaptcha(String source, String sessionId, UserBindObjectType objectType, String userObject, int captchaLength,
			Map<String, Object> parameters) throws CommonException {
		UserSession session = getSession(source, sessionId);
		if (null == session) {
			throw new CommonException("会话标识'" + sessionId + "'不存在");
		}
		if (session.isBindUser()) {
			throw new CommonException("用户已经绑定");
		}
		if (captchaLength <= 0) {
			throw new CommonException("验证码长度必须大于零");
		}
		UserBindHandle userBind = GuiceUtils.getInstance(UserBindHandle.class);
		if (null == userBind) {
			throw new CommonException("用户绑定处理为空");
		}
		String key = WECHAT_SESSION_CAPTCHA + source + "_" + sessionId;
		String captcha = getBindCaptcha(key);
		WeChatConfigProperties configProperties = SpringContext.getBean(WeChatConfigProperties.class);
		int captchaTimeout = configProperties.getCaptchaTimeout();
		if (StringUtils.isBlank(captcha)) {
			captcha = com.gitlab.summercattle.commons.utils.auxiliary.StringUtils.getRandomString(true, captchaLength);
			RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
			redisUtils.set(key, captcha, captchaTimeout);
		}
		userBind.sendCaptcha(objectType, userObject, captcha, captchaTimeout, parameters);
	}

	private String getBindCaptcha(String key) throws CommonException {
		RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
		if (redisUtils.exists(key)) {
			return (String) redisUtils.get(key);
		}
		return null;
	}

	@Override
	public void bind(String source, String sessionId, UserBindObjectType objectType, UserBindAuthType authType, String userObject, String authInfo,
			Map<String, Object> parameters) throws CommonException {
		UserSession session = getSession(source, sessionId);
		if (null == session) {
			throw new CommonException("会话标识'" + sessionId + "'不存在");
		}
		if (session.isBindUser()) {
			throw new CommonException("用户已经绑定");
		}
		if (null == objectType) {
			throw new CommonException("用户绑定对象类型为空");
		}
		if (null == authType) {
			throw new CommonException("用户绑定验证类型为空");
		}
		if (StringUtils.isBlank(userObject)) {
			throw new CommonException("用户对象为空");
		}
		if (StringUtils.isBlank(authInfo)) {
			throw new CommonException("验证信息为空");
		}
		UserBindHandle userBind = GuiceUtils.getInstance(UserBindHandle.class);
		if (null == userBind) {
			throw new CommonException("用户绑定处理为空");
		}
		boolean exist = userBind.exist(objectType, userObject, StringUtils.isNotBlank(session.getUnionId()), parameters);
		if (authType == UserBindAuthType.Captcha) {
			String key = WECHAT_SESSION_CAPTCHA + source + "_" + sessionId;
			String captcha = getBindCaptcha(key);
			if (StringUtils.isBlank(captcha) || !captcha.equals(authInfo)) {
				throw new CommonException("验证码失效");
			}
			RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
			redisUtils.del(key);
			if (!exist) {
				if (userBind.isAutoCreated(objectType, parameters)) {
					userBind.create(objectType, userObject, parameters);
				}
				else {
					throw new CommonException("用户绑定异常");
				}
			}
		}
		else if (authType == UserBindAuthType.Password) {
			if (!exist) {
				if (userBind.isAutoCreated(objectType, parameters)) {
					userBind.create(objectType, userObject, parameters);
				}
				else {
					throw new CommonException("用户绑定异常");
				}
			}
			userBind.verifyPassword(objectType, userObject, authInfo, parameters);
		}
		else {
			throw new CommonException("用户绑定验证类型'" + authType.toString() + "'不支持");
		}
		UserBindInfo bindResult = userBind.process(objectType, userObject, parameters);
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
		int sessionTimeout = 0;
		if (session.getType() == AppType.MiniProgram) {
			MiniProgramConfigProperties configProperties = SpringContext.getBean(MiniProgramConfigProperties.class);
			sessionTimeout = configProperties.getSessionTimeout();
		}
		else {
			throw new CommonException("应用类型'" + session.getType().toString() + "'暂不支持");
		}
		redisUtils.set(key, session, sessionTimeout);
	}

	@Override
	public void unbind(String source, String sessionId) throws CommonException {
		UserSession session = getSession(source, sessionId);
		if (null == session) {
			throw new CommonException("会话标识'" + sessionId + "'不存在");
		}
		if (!session.isBindUser()) {
			throw new CommonException("用户未绑定");
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
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and " + field + "=?", new Object[] { source, session.getOpenId() });
			if (dt.first()) {
				String otherValue = dt.getString(otherField);
				if (StringUtils.isNotBlank(session.getUnionId())) {
					String strEnabled = SpringContext.getProperty(WeChatConstants.PROPERTY_PREFIX + ".prod.enabled");
					boolean enabled = BooleanUtils.toBoolean(strEnabled);
					if (!enabled) {
						String url = SpringContext.getProperty(WeChatConstants.PROPERTY_PREFIX + ".prod.url");
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
							otherValue = resultJson.getString(otherField);
							if (StringUtils.isNotBlank(otherValue)) {
								dt.setString(otherField, otherValue);
							}
						}
					}
				}
				if (StringUtils.isNotBlank(otherValue)) {
					dt.setString(field, "");
					dt.setObject("USER_TYPE", null);
					dt.setObject("BIND_INFO", null);
				}
				else {
					dt.delete();
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
	}

	@Override
	public boolean isOtherBind(String source, String sessionId) throws CommonException {
		UserSession session = getSession(source, sessionId);
		if (session.isBindUser()) {
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
			return DbUtils.getDbTransaction().doDal(ctx -> {
				DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and " + field + "=?", new Object[] { source, session.getOpenId() });
				if (dt.first()) {
					return StringUtils.isNotBlank(dt.getString(otherField));
				}
				return false;
			});
		}
		return false;
	}

	@Override
	public UserSession getSession(String source, String sessionId) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (StringUtils.isBlank(sessionId)) {
			throw new CommonException("会话标识为空");
		}
		RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
		String key = WeChatConstants.WECHAT_SESSION + source + "_" + sessionId;
		if (!redisUtils.exists(key)) {
			throw new CommonException(WeChatExceptionConstants.WECHAT_SESSION_INVALID, "会话'" + sessionId + "'已经失效");
		}
		return (UserSession) redisUtils.get(key);
	}

	@Override
	public UserSession[] getSessions(String source) throws CommonException {
		RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
		Set<String> keys = redisUtils.keys(WeChatConstants.WECHAT_SESSION + source + "_" + "*");
		UserSession[] userSessions = new UserSession[keys.size()];
		int i = 0;
		for (String key : keys) {
			userSessions[i] = (UserSession) redisUtils.get(key);
			i++;
		}
		return userSessions;
	}

	@Override
	public String getOpenId(AppType type, String source, int userType, String bindInfo) throws CommonException {
		String field;
		if (type == AppType.OfficialAccounts) {
			field = "OA_OPENID";
		}
		else if (type == AppType.MiniProgram) {
			field = "MP_OPENID";
		}
		else {
			throw new CommonException("应用类型'" + type.toString() + "'暂不支持");
		}
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (userType == 0) {
			throw new CommonException("用户类型没有设置");
		}
		if (StringUtils.isBlank(bindInfo)) {
			throw new CommonException("绑定信息为空");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and USER_TYPE=? and BIND_INFO=?", new Object[] { source, userType, bindInfo });
			if (dt.first()) {
				return dt.getString(field);
			}
			return null;
		});
	}

	@Override
	public UserBindInfo getUserBindInfo(AppType type, String source, String openId) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		String field;
		if (type == AppType.OfficialAccounts) {
			field = "OA_OPENID";
			if (StringUtils.isBlank(openId)) {
				throw new CommonException("微信公众号标识Id为空");
			}
		}
		else if (type == AppType.MiniProgram) {
			field = "MP_OPENID";
			if (StringUtils.isBlank(openId)) {
				throw new CommonException("微信小程序标识Id为空");
			}
		}
		else {
			throw new CommonException("应用类型'" + type.toString() + "'暂不支持");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and " + field + "=?", new Object[] { source, openId });
			if (dt.first()) {
				return new UserBindInfo(dt.getInt("USER_TYPE"), dt.getString("BIND_INFO"));
			}
			return null;
		});
	}

	@Override
	public UserInfo getUserInfo(String source, String openId, String lang) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("微信公众号标识Id为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + openId + "&lang="
				+ (StringUtils.isNotBlank(lang) ? lang : "zh_CN");
		logger.debug("微信的获得用户信息请求地址:" + url);
		RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
		JSONObject resultJson = restTemplateUtils.get(url, MediaType.APPLICATION_JSON, null, JSONObject.class);
		logger.debug("微信的获得用户信息返回:" + resultJson.toString());
		if (resultJson.containsKey("errmsg")) {
			throw new CommonException("微信的获得用户信息接口异常,错误码:" + resultJson.getString("errcode") + ",错误信息:" + resultJson.getString("errmsg"));
		}
		if (!resultJson.getString("openid").equals(openId)) {
			throw new CommonException("微信的获得用户信息接口得到数据异常");
		}
		List<Long> tagIdList = null;
		JSONArray tagIdListJson = resultJson.getJSONArray("tagid_list");
		if (tagIdListJson != null && tagIdListJson.size() > 0) {
			tagIdList = new Vector<Long>();
			for (int i = 0; i < tagIdListJson.size(); i++) {
				tagIdList.add(tagIdListJson.getLong(i));
			}
		}
		if (resultJson.getInteger("subscribe") == 1) {
			return new UserInfo(resultJson.getString("nickname"), resultJson.getInteger("sex"), resultJson.getString("country"),
					resultJson.getString("province"), resultJson.getString("city"), resultJson.getString("headimgurl"),
					resultJson.getString("unionid"), resultJson.getLong("subscribe_time"), resultJson.getString("language"),
					resultJson.getString("remark"), resultJson.getLong("groupid"), tagIdList, resultJson.getString("subscribe_scene"),
					resultJson.getLong("qr_scene"), resultJson.getString("qr_scene_str"));
		}
		return null;
	}

	@Override
	public void bind(AppType type, String source, String openId, String unionId) throws CommonException {
		String field;
		if (type == AppType.OfficialAccounts) {
			field = "OA_OPENID";
		}
		else if (type == AppType.MiniProgram) {
			field = "MP_OPENID";
		}
		else {
			throw new CommonException("应用类型'" + type.toString() + "'暂不支持");
		}
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("微信标识Id为空");
		}
		if (StringUtils.isBlank(unionId)) {
			throw new CommonException("微信唯一标识Id为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and UNIONID=?", new Object[] { source, unionId });
			if (!dt.first()) {
				dt.insert();
			}
			dt.setString("SOURCE", source);
			dt.setString(field, openId);
			dt.setString("UNIONID", unionId);
			ctx.save(dt);
			return null;
		});
	}

	@Override
	public void unbind(AppType type, String source, String openId) throws CommonException {
		String field;
		String otherField;
		if (type == AppType.OfficialAccounts) {
			field = "OA_OPENID";
			otherField = "MP_OPENID";
		}
		else if (type == AppType.MiniProgram) {
			field = "MP_OPENID";
			otherField = "OA_OPENID";
		}
		else {
			throw new CommonException("应用类型'" + type.toString() + "'无效");
		}
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("微信标识Id为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and " + field + "=?", new Object[] { source, openId });
			if (dt.first()) {
				String otherValue = dt.getString(otherField);
				if (StringUtils.isNotBlank(otherValue)) {
					dt.setString(field, "");
				}
				else {
					dt.delete();
				}
			}
			ctx.save(dt);
			return null;
		});
	}
}