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
package com.gitlab.summercattle.addons.wechat.officialaccounts.menu;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.commons.exception.CommonException;

public abstract class Button implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract ButtonType getType();

	public abstract void setJSON(JSONObject jsonObj) throws CommonException;

	protected String name;

	public Button(String name) {
		this.name = name;
	}

	public JSONObject toJSON() throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("菜单标题为空");
		}
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", name);
		jsonObj.put("type", getType().toString().toLowerCase());
		setJSON(jsonObj);
		return jsonObj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}