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
package io.github.summercattle.addons.wechat.officialaccounts.event.reply;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import io.github.summercattle.addons.wechat.officialaccounts.event.RcvEvent;
import io.github.summercattle.addons.wechat.officialaccounts.event.ReceiveEventType;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.reflect.ClassType;
import io.github.summercattle.commons.utils.reflect.ReflectUtils;

@RcvEvent(ReceiveEventType.Subscribe)
public abstract class SubscribeEvent extends ReplyEvent {

	private int sceneId;

	private String ticket;

	@Override
	public void from(Element element) throws CommonException {
		String key = element.elementText("EventKey");
		if (StringUtils.isNotBlank(key) && key.startsWith("qrscene_")) {
			sceneId = (int) ReflectUtils.convertValue(ClassType.Int, key.substring(8));
		}
		ticket = element.elementText("Ticket");
	}

	public int getSceneId() {
		return sceneId;
	}

	public String getTicket() {
		return ticket;
	}
}