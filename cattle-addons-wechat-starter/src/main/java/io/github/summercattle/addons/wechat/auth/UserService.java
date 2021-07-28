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
package io.github.summercattle.addons.wechat.auth;

import java.util.Map;

import io.github.summercattle.addons.wechat.auth.bind.UserBindAuthType;
import io.github.summercattle.addons.wechat.auth.bind.UserBindInfo;
import io.github.summercattle.addons.wechat.auth.bind.UserBindObjectType;
import io.github.summercattle.addons.wechat.auth.info.UserInfo;
import io.github.summercattle.addons.wechat.common.AppType;
import io.github.summercattle.commons.exception.CommonException;

/**
 * 用户服务
 */
public interface UserService {

	/**
	 * 绑定
	 * @param source 来源
	 * @param sessionId 临时会话Id
	 * @param objectType 对象类型
	 * @param authType 验证类型
	 * @param userObject 用户对象
	 * @param authInfo 验证信息
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void bind(String source, String sessionId, UserBindObjectType objectType, UserBindAuthType authType, String userObject, String authInfo,
			Map<String, Object> parameters) throws CommonException;

	/**
	 * 解除绑定
	 * @param source 来源
	 * @param sessionId 会话Id
	 * @throws CommonException 异常
	 */
	void unbind(String source, String sessionId) throws CommonException;

	/**
	 * 绑定
	 * @param type 应用类型
	 * @param source 来源
	 * @param openId 微信标识Id
	 * @param unionId 微信唯一标识Id
	 * @throws CommonException 异常
	 */
	void bind(AppType type, String source, String openId, String unionId) throws CommonException;

	/**
	 * 解除绑定
	 * @param type 应用类型
	 * @param source 来源
	 * @param openId 微信标识Id
	 * @throws CommonException 异常
	 */
	void unbind(AppType type, String source, String openId) throws CommonException;

	/**
	 * 其他的是否绑定
	 * @param source 来源
	 * @param sessionId 会话Id
	 * @return 其他的是否绑定 
	 * @throws CommonException 异常
	 */
	boolean isOtherBind(String source, String sessionId) throws CommonException;

	/**
	 * 生成绑定验证码
	 * @param source 来源
	 * @param sessionId 临时会话Id
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @param captchaLength 验证码长度
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void generateBindCaptcha(String source, String sessionId, UserBindObjectType objectType, String userObject, int captchaLength,
			Map<String, Object> parameters) throws CommonException;

	/**
	 * 得到会话标识
	 * @param source 来源 
	 * @param sessionId 会话Id
	 * @return 用户会话
	 * @throws CommonException 异常
	 */
	UserSession getSession(String source, String sessionId) throws CommonException;

	/**
	 * 得到所有会话
	 * @param source 来源
	 * @return 所有会话
	 * @throws CommonException 异常
	 */
	UserSession[] getSessions(String source) throws CommonException;

	/**
	 * 得到微信标识Id
	 * @param type 类型
	 * @param source 来源
	 * @param userType 用户类型
	 * @param bindInfo 绑定信息
	 * @return 微信标识Id
	 * @throws CommonException 异常
	 */
	String getOpenId(AppType type, String source, int userType, String bindInfo) throws CommonException;

	/**
	 * 得到用户绑定信息
	 * @param type 类型
	 * @param source 来源
	 * @param openId 微信标识Id
	 * @return 用户绑定信息
	 * @throws CommonException 异常
	 */
	UserBindInfo getUserBindInfo(AppType type, String source, String openId) throws CommonException;

	/**
	 * 得到微信公众号用户信息
	 * @param source 来源
	 * @param openId 微信标识Id
	 * @param lang 语言
	 * @return 用户信息
	 * @throws CommonException 异常
	 */
	UserInfo getUserInfo(String source, String openId, String lang) throws CommonException;
}