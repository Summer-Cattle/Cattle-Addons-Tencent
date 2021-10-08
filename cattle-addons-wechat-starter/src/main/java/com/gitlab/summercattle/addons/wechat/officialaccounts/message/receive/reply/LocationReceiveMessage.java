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
package com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.reply;

import java.math.BigDecimal;

import org.dom4j.Element;

import com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.RcvMessage;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.ReceiveMessageType;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.reflect.ClassType;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;

@RcvMessage(ReceiveMessageType.Location)
public abstract class LocationReceiveMessage extends ReplyMessage {

	private BigDecimal locationX;

	private BigDecimal locationY;

	private int scale;

	private String label;

	@Override
	public void from(Element element) throws CommonException {
		locationX = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, element.elementText("Location_X"));
		locationY = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, element.elementText("Location_Y"));
		scale = (int) ReflectUtils.convertValue(ClassType.Int, element.elementText("Scale"));
		label = element.elementText("Label");
	}

	public BigDecimal getLocationX() {
		return locationX;
	}

	public BigDecimal getLocationY() {
		return locationY;
	}

	public int getScale() {
		return scale;
	}

	public String getLabel() {
		return label;
	}
}