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

import java.math.BigDecimal;

import org.dom4j.Element;

import com.gitlab.summercattle.addons.wechat.officialaccounts.event.RcvEvent;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.ReceiveEventType;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.reflect.ClassType;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;

@RcvEvent(ReceiveEventType.Location_Select)
public abstract class LocationSelectEvent extends NoReplyEvent {

	private String key;

	private BigDecimal locationX;

	private BigDecimal locationY;

	private int scale;

	private String label;

	private String poiName;

	@Override
	public void from(Element element) throws CommonException {
		key = element.elementText("EventKey");
		Element sendLocationElement = element.element("SendLocationInfo");
		locationX = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, sendLocationElement.elementText("Location_X"));
		locationY = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, sendLocationElement.elementText("Location_Y"));
		scale = (int) ReflectUtils.convertValue(ClassType.Int, sendLocationElement.elementText("Scale"));
		label = sendLocationElement.elementText("Label");
		poiName = sendLocationElement.elementText("Poiname");
	}

	public String getKey() {
		return key;
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

	public String getPoiName() {
		return poiName;
	}
}