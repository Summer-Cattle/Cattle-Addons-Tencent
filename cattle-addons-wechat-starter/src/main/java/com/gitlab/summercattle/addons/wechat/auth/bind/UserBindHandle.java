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
package com.gitlab.summercattle.addons.wechat.auth.bind;

import java.util.Map;

import com.gitlab.summercattle.commons.exception.CommonException;

/**
 * 用户绑定处理
 */
public interface UserBindHandle {

	/**
	 * 用户对象不存在时,是否自动创建
	 * @param objectType 对象类型
	 * @param parameters 参数
	 * @return 用户对象不存在时,是否创建
	 * @throws CommonException 异常
	 */
	boolean isAutoCreated(UserBindObjectType objectType, Map<String, Object> parameters) throws CommonException;

	/**
	 * 用户对象是否存在
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @return 用户对象是否存在
	 * @param hasUnionId 是否存在Union标识
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	boolean exist(UserBindObjectType objectType, String userObject, boolean hasUnionId, Map<String, Object> parameters) throws CommonException;

	/**
	 * 验证密码
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @param password 密码
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void verifyPassword(UserBindObjectType objectType, String userObject, String password, Map<String, Object> parameters) throws CommonException;

	/**
	 * 创建
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void create(UserBindObjectType objectType, String userObject, Map<String, Object> parameters) throws CommonException;

	/**
	 * 绑定处理
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @param parameters 参数
	 * @return 绑定反馈信息
	 * @throws CommonException 异常
	 */
	UserBindInfo process(UserBindObjectType objectType, String userObject, Map<String, Object> parameters) throws CommonException;

	/**
	 * 发送验证码
	 * @param objectType 对象类型
	 * @param userObject 用户对象
	 * @param captcha 验证码
	 * @param seconds 验证码秒数
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void sendCaptcha(UserBindObjectType objectType, String userObject, String captcha, long seconds, Map<String, Object> parameters)
			throws CommonException;
}