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
package io.github.summercattle.addons.wechat.officialaccounts.message.receive.reply;

import org.dom4j.Element;

import io.github.summercattle.addons.wechat.officialaccounts.message.receive.RcvMessage;
import io.github.summercattle.addons.wechat.officialaccounts.message.receive.ReceiveMessageType;
import io.github.summercattle.commons.exception.CommonException;

@RcvMessage(ReceiveMessageType.Text)
public abstract class TextReceiveMessage extends ReplyMessage {

	private String text;

	@Override
	public void from(Element element) throws CommonException {
		text = element.elementText("Content");
	}

	public String getText() {
		return text;
	}
}