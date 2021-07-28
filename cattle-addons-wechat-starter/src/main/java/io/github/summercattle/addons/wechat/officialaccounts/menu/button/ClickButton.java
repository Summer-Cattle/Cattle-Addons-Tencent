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

import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.officialaccounts.menu.Button;
import io.github.summercattle.addons.wechat.officialaccounts.menu.ButtonType;
import io.github.summercattle.commons.exception.CommonException;

public class ClickButton extends Button {

	private static final long serialVersionUID = 1L;

	private String key;

	public ClickButton(String name, String key) {
		super(name);
		this.key = key;
	}

	@Override
	public ButtonType getType() {
		return ButtonType.Click;
	}

	@Override
	public void setJSON(JSONObject jsonObj) throws CommonException {
		if (StringUtils.isBlank(key)) {
			throw new CommonException("菜单键值为空");
		}
		jsonObj.put("key", key);
	}

	public String getKey() {
		return key;
	}
}