/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by ap
import io.github.summercattle.commons.exception.CommonException;plicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.summercattle.addons.wechat.officialaccounts.menu.button;

import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.officialaccounts.menu.Button;
import io.github.summercattle.addons.wechat.officialaccounts.menu.ButtonType;
import io.github.summercattle.commons.exception.CommonException;

public class ViewButton extends Button {

	private static final long serialVersionUID = 1L;

	private String url;

	public ViewButton(String name, String url) {
		super(name);
		this.url = url;
	}

	@Override
	public ButtonType getType() {
		return ButtonType.View;
	}

	@Override
	public void setJSON(JSONObject jsonObj) throws CommonException {
		jsonObj.put("url", url);
	}

	public String getUrl() {
		return url;
	}
}