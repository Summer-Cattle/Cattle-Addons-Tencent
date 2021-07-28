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

import java.io.Serializable;

import io.github.summercattle.addons.wechat.common.AppType;

public abstract class UserSession implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会话类型
	 * @return 会话类型
	 */
	public abstract AppType getType();

	/**
	 * 会话标识
	 */
	private String sessionId;

	/**
	 * 是否绑定用户
	 */
	private boolean bindUser;

	/**
	 * 用户唯一标识
	 */
	private String openId;

	/**
	 * 用户在开放平台的唯一标识符
	 */
	private String unionId;

	/**
	 * 用户类型
	 */
	private int userType;

	/**
	 * 绑定信息
	 */
	private String bindInfo;

	public UserSession() {
	}

	public UserSession(String sessionId, String openId, String unionId) {
		bindUser = false;
		this.sessionId = sessionId;
		this.openId = openId;
		this.unionId = unionId;
	}

	public UserSession(String sessionId, String openId, String unionId, int userType, String bindInfo) {
		bindUser = true;
		this.sessionId = sessionId;
		this.openId = openId;
		this.unionId = unionId;
		this.userType = userType;
		this.bindInfo = bindInfo;
	}

	public boolean isBindUser() {
		return bindUser;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getOpenId() {
		return openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public int getUserType() {
		return userType;
	}

	public String getBindInfo() {
		return bindInfo;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setBindUser(boolean bindUser) {
		this.bindUser = bindUser;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public void setBindInfo(String bindInfo) {
		this.bindInfo = bindInfo;
	}
}