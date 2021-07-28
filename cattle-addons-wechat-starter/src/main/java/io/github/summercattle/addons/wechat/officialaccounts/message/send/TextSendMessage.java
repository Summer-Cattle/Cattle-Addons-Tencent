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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.github.summercattle.commons.exception.CommonException;

public class TextSendMessage extends SendMessage {

	private String text;

	public TextSendMessage(String fromUserName, String toUserName, String text) {
		super(fromUserName, toUserName);
		this.text = text;
	}

	@Override
	public SendMessageType getType() {
		return SendMessageType.Text;
	}

	@Override
	public void setXml(Element element) throws CommonException {
		element.addElement("Content").add(DocumentHelper.createCDATA(text));
	}

	@Override
	void messageCheck() throws CommonException {
		if (StringUtils.isBlank(text)) {
			throw new CommonException("消息内容为空");
		}
	}
}