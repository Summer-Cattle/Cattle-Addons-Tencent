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

import java.io.Serializable;

import com.gitlab.summercattle.addons.wechat.auth.UserSession;
import com.gitlab.summercattle.addons.wechat.common.AppType;

/**
 * 微信小程序用户会话
 */
public class MiniProgramUserSession extends UserSession implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会话密钥
	 */
	private String sessionKey;

	public MiniProgramUserSession() {
		super();
	}

	public MiniProgramUserSession(String sessionId, String openId, String unionId, String sessionKey) {
		super(sessionId, openId, unionId);
		this.sessionKey = sessionKey;
	}

	public MiniProgramUserSession(String sessionId, String openId, String unionId, String sessionKey, int userType, String bindInfo) {
		super(sessionId, openId, unionId, userType, bindInfo);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	@Override
	public AppType getType() {
		return AppType.MiniProgram;
	}
}