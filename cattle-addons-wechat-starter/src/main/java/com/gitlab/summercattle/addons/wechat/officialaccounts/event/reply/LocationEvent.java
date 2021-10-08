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
package com.gitlab.summercattle.addons.wechat.officialaccounts.event.reply;

import java.math.BigDecimal;

import org.dom4j.Element;

import com.gitlab.summercattle.addons.wechat.officialaccounts.event.RcvEvent;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.ReceiveEventType;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.reflect.ClassType;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;

@RcvEvent(ReceiveEventType.Location)
public abstract class LocationEvent extends ReplyEvent {

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal precision;

	@Override
	public void from(Element element) throws CommonException {
		latitude = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, element.elementText("Latitude"));
		longitude = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, element.elementText("Longitude"));
		precision = (BigDecimal) ReflectUtils.convertValue(ClassType.BigDecimal, element.elementText("Precision"));
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getPrecision() {
		return precision;
	}
}