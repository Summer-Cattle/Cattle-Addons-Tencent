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

import io.github.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import io.github.summercattle.addons.wechat.officialaccounts.menu.Button;
import io.github.summercattle.addons.wechat.officialaccounts.menu.ButtonType;
import io.github.summercattle.addons.wechat.utils.ServerUtils;
import io.github.summercattle.commons.exception.CommonException;

public class MiniProgramButton extends Button {

	private static final long serialVersionUID = 1L;

	private String url;

	private String source;

	private String pagePath;

	public MiniProgramButton(String name, String url, String source, String pagePath) {
		super(name);
		this.url = url;
		this.source = source;
		this.pagePath = pagePath;
	}

	@Override
	public ButtonType getType() {
		return ButtonType.MiniProgram;
	}

	@Override
	public void setJSON(JSONObject jsonObj) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("小程序来源为空");
		}
		MiniProgramServer miniProgramServer = ServerUtils.getMiniProgramServer(source);
		jsonObj.put("url", url);
		jsonObj.put("appid", miniProgramServer.getAppId());
		jsonObj.put("pagepath", pagePath);
	}

	public String getUrl() {
		return url;
	}

	public String getSource() {
		return source;
	}

	public String getPagePath() {
		return pagePath;
	}
}