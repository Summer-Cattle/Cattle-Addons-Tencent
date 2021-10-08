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
package com.gitlab.summercattle.addons.wechat.miniprogram.bind;

import java.util.Map;

import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindInfo;
import com.gitlab.summercattle.commons.exception.CommonException;

/**
 * 用户手机号绑定处理
 */
public interface UserPhoneNumberBindHandle {

	/**
	 * 用户对象不存在时,是否自动创建
	 * @param parameters 参数
	 * @return 用户对象不存在时,是否创建
	 * @throws CommonException 异常
	 */
	boolean isAutoCreated(Map<String, Object> parameters) throws CommonException;

	/**
	 * 用户对象是否存在
	 * @param phoneNumber 手机号
	 * @return 用户对象是否存在
	 * @param hasUnionId 是否存在Union标识
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	boolean exist(String phoneNumber, boolean hasUnionId, Map<String, Object> parameters) throws CommonException;

	/**
	 * 创建
	 * @param phoneNumber 手机号
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void create(String phoneNumber, Map<String, Object> parameters) throws CommonException;

	/**
	 * 绑定处理
	 * @param phoneNumber 手机号
	 * @param parameters 参数
	 * @return 绑定反馈信息
	 * @throws CommonException 异常
	 */
	UserBindInfo process(String phoneNumber, Map<String, Object> parameters) throws CommonException;
}