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
package io.github.summercattle.addons.wechat.miniprogram.configure;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.summercattle.addons.wechat.constants.WeChatConstants;

@ConfigurationProperties(prefix = MiniProgramConfigProperties.PREFIX)
public class MiniProgramConfigProperties {

	public static final String PREFIX = WeChatConstants.PROPERTY_PREFIX + ".mini-program";

	private List<MiniProgramServer> serverInfo;

	private int temporarySessionTimeout;

	private int sessionTimeout;

	public List<MiniProgramServer> getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(List<MiniProgramServer> serverInfo) {
		this.serverInfo = serverInfo;
	}

	public int getTemporarySessionTimeout() {
		return temporarySessionTimeout;
	}

	public void setTemporarySessionTimeout(int temporarySessionTimeout) {
		this.temporarySessionTimeout = temporarySessionTimeout;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}