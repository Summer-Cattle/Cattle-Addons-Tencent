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
package com.gitlab.summercattle.addons.wechat.officialaccounts.event.noreply;

import org.dom4j.Element;

import com.gitlab.summercattle.addons.wechat.officialaccounts.event.RcvEvent;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.ReceiveEventType;
import com.gitlab.summercattle.commons.exception.CommonException;

@RcvEvent(ReceiveEventType.ScanCode_Push)
public abstract class ScanCodePushEvent extends NoReplyEvent {

	private String key;

	private String scanType;

	private String scanResult;

	@Override
	public void from(Element element) throws CommonException {
		key = element.elementText("EventKey");
		Element scanCodeElement = element.element("ScanCodeInfo");
		scanType = scanCodeElement.elementText("ScanType");
		scanResult = scanCodeElement.elementText("ScanResult");
	}

	public String getKey() {
		return key;
	}

	public String getScanType() {
		return scanType;
	}

	public String getScanResult() {
		return scanResult;
	}
}