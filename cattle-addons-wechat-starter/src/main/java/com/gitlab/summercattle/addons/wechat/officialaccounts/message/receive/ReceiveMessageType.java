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
package com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive;

public enum ReceiveMessageType {

	/**
	 * 文本消息
	 */
	Text,
	/**
	 * 事件
	 */
	Event,
	/**
	 * 地理位置
	 */
	Location;

	public static ReceiveMessageType parse(String msgType) {
		ReceiveMessageType result = null;
		for (ReceiveMessageType type : values()) {
			if (type.toString().equalsIgnoreCase(msgType)) {
				result = type;
				break;
			}
		}
		return result;
	}
}