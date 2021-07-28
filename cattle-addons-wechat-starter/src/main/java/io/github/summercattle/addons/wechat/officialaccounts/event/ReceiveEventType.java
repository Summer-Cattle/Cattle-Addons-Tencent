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
package io.github.summercattle.addons.wechat.officialaccounts.event;

/**
 * 事件类型
 */
public enum ReceiveEventType {

	/**
	 * 点击
	 */
	Click,
	/**
	 * 网页
	 */
	View,
	/**
	 * 订阅
	 */
	Subscribe,
	/**
	 * 取消订阅
	 */
	Unsubscribe,
	/**
	* 扫码带提示
	*/
	ScanCode_WaitMsg,
	/**
	 * 扫码推事件
	 */
	ScanCode_Push,
	/**
	 * 弹出地理位置选择器的事件
	 */
	Location_Select,
	/**
	 * 上报地理位置事件
	 */
	Location,
	/**
	 * 扫码
	 */
	Scan,
	/**
	 * 点击菜单转小程序的事件
	 */
	View_MiniProgram;

	public static ReceiveEventType parse(String lEventType) {
		ReceiveEventType result = null;
		for (ReceiveEventType lType : values()) {
			if (lType.toString().equalsIgnoreCase(lEventType)) {
				result = lType;
				break;
			}
		}
		return result;
	}
}