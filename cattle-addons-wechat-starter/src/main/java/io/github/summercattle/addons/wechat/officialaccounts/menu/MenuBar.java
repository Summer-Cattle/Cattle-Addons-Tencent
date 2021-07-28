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
package io.github.summercattle.addons.wechat.officialaccounts.menu;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.commons.exception.CommonException;

public class MenuBar implements Serializable {

	private static final long serialVersionUID = 1L;

	private Button leftButton;

	private Button middenButton;

	private Button rightButton;

	public MenuBar(Button leftButton, Button middenButton, Button rightButton) {
		this.leftButton = leftButton;
		this.middenButton = middenButton;
		this.rightButton = rightButton;
	}

	public JSONObject toJSON() throws CommonException {
		if (null == leftButton && null == middenButton && null == rightButton) {
			throw new CommonException("一级菜单为空");
		}
		JSONArray buttons = new JSONArray();
		if (null != leftButton) {
			buttons.add(leftButton.toJSON());
		}
		if (null != middenButton) {
			buttons.add(middenButton.toJSON());
		}
		if (null != rightButton) {
			buttons.add(rightButton.toJSON());
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("button", buttons);
		return jsonObj;
	}

	public Button getLeftButton() {
		return leftButton;
	}

	public Button getMiddenButton() {
		return middenButton;
	}

	public Button getRightButton() {
		return rightButton;
	}
}