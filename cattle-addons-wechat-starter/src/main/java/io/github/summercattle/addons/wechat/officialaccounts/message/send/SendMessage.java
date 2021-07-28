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
package io.github.summercattle.addons.wechat.officialaccounts.message.send;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.github.summercattle.commons.db.DbUtils;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.auxiliary.Dom4jUtils;

public abstract class SendMessage {

	private String fromUserName;

	private String toUserName;

	public abstract SendMessageType getType();

	public abstract void setXml(Element element) throws CommonException;

	abstract void messageCheck() throws CommonException;

	public SendMessage(String fromUserName, String toUserName) {
		this.fromUserName = fromUserName;
		this.toUserName = toUserName;
	}

	private void check() throws CommonException {
		if (StringUtils.isBlank(fromUserName)) {
			throw new CommonException("开发者微信号为空");
		}
		if (StringUtils.isBlank(toUserName)) {
			throw new CommonException("接收方帐号为空");
		}
		messageCheck();
	}

	public String toStringData() throws CommonException {
		check();
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("xml");
		rootElement.addElement("ToUserName").add(DocumentHelper.createCDATA(toUserName));
		rootElement.addElement("FromUserName").add(DocumentHelper.createCDATA(fromUserName));
		long createTime = DbUtils.getDbTool().getCurrentDate().getTime() / 1000;
		rootElement.addElement("CreateTime").setText(String.valueOf(createTime));
		rootElement.addElement("MsgType").add(DocumentHelper.createCDATA(getType().toString().toLowerCase()));
		setXml(rootElement);
		return Dom4jUtils.asXmlWithoutPretty(rootElement, "UTF-8");
	}
}