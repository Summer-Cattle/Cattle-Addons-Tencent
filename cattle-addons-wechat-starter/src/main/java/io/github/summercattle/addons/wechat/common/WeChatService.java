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
package io.github.summercattle.addons.wechat.common;

import io.github.summercattle.commons.exception.CommonException;

/**
 * 微信服务
 */
public interface WeChatService {

	/**
	 * 获得访问令牌
	 * @param type 应用类型
	 * @param source 来源
	 * @return 访问令牌
	 * @throws CommonException 异常
	 */
	String getAccessToken(AppType type, String source) throws CommonException;
}