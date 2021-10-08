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
package com.gitlab.summercattle.addons.wechat.officialaccounts.event;

import java.util.Date;

import org.dom4j.Element;

import com.gitlab.summercattle.addons.wechat.officialaccounts.message.send.SendMessage;
import com.gitlab.summercattle.commons.exception.CommonException;

public abstract class ReceiveEvent {

	private String fromUserName;

	private String toUserName;

	private Date createTime;

	public abstract void from(Element element) throws CommonException;

	public abstract SendMessage receive() throws CommonException;

	public void parse(String fromUserName, String toUserName, Date createTime, Element element) throws CommonException {
		this.fromUserName = fromUserName;
		this.toUserName = toUserName;
		this.createTime = createTime;
		from(element);
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public Date getCreateTime() {
		return createTime;
	}
}