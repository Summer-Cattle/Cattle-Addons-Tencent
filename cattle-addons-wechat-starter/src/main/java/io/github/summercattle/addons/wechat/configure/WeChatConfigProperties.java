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
package io.github.summercattle.addons.wechat.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.summercattle.addons.wechat.constants.WeChatConstants;

@ConfigurationProperties(prefix = WeChatConstants.PROPERTY_PREFIX)
public class WeChatConfigProperties {

	private int captchaTimeout;

	public int getCaptchaTimeout() {
		return captchaTimeout;
	}

	public void setCaptchaTimeout(int captchaTimeout) {
		this.captchaTimeout = captchaTimeout;
	}
}