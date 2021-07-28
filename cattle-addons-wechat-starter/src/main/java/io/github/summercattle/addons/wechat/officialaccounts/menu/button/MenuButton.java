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
package io.github.summercattle.addons.wechat.officialaccounts.menu.button;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.officialaccounts.menu.Button;
import io.github.summercattle.addons.wechat.officialaccounts.menu.ButtonType;
import io.github.summercattle.commons.exception.CommonException;

public class MenuButton extends Button {

	private static final long serialVersionUID = 1L;

	private final int MAX_BUTTONS = 5;

	private Button[] buttons;

	public MenuButton(String name, Button[] buttons) {
		super(name);
		this.buttons = buttons;
	}

	@Override
	public ButtonType getType() {
		return ButtonType.Menu;
	}

	@Override
	public void setJSON(JSONObject jsonObj) throws CommonException {
		if (null == buttons || buttons.length == 0) {
			throw new CommonException("二级菜单数为空");
		}
		if (buttons.length > MAX_BUTTONS) {
			throw new CommonException("二级菜单数不能超过" + MAX_BUTTONS + "个");
		}
		JSONArray buttonsArray = new JSONArray();
		for (int i = 0; i < buttons.length; i++) {
			buttonsArray.add(buttons[i].toJSON());
		}
		jsonObj.put("sub_button", buttonsArray);
	}

	@Override
	public JSONObject toJSON() throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("菜单标题为空");
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", name);
		setJSON(jsonObj);
		return jsonObj;
	}

	public Button[] getButtons() {
		return buttons;
	}
}