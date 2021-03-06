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
package com.gitlab.summercattle.addons.wechat.miniprogram;

import java.util.Map;

import com.gitlab.summercattle.commons.exception.CommonException;

/**
 * 小程序服务
 */
public interface MiniProgramService {

	/**
	 * 得到会话
	 * @param source 来源
	 * @param jsCode 登录时获取的code
	 * @return 小程序会话
	 * @throws CommonException 异常
	 */
	MiniProgramUserSession getSession(String source, String jsCode) throws CommonException;

	/**
	 * 绑定
	 * @param source 来源
	 * @param sessionId 临时会话Id
	 * @param encryptData 加密后的数据
	 * @param iv 加密初始化向量
	 * @param parameters 参数
	 * @throws CommonException 异常
	 */
	void bind(String source, String sessionId, String encryptData, String iv, Map<String, Object> parameters) throws CommonException;
}