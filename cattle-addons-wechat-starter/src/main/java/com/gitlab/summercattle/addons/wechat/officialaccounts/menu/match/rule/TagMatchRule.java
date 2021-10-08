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
package com.gitlab.summercattle.addons.wechat.officialaccounts.menu.match.rule;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.summercattle.addons.wechat.officialaccounts.menu.match.MatchInfo;
import com.gitlab.summercattle.addons.wechat.officialaccounts.menu.match.MatchRule;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.db.object.DataTable;
import com.gitlab.summercattle.commons.exception.CommonException;

public class TagMatchRule implements MatchRule {

	private String name;

	public TagMatchRule(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return "用户标签";
	}

	@Override
	public MatchInfo toMatchInfo(String source) throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户标签名称为空");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("WX_TAG", "SOURCE=? and NAME=?", new Object[] { source, name });
			if (!dt.first()) {
				throw new CommonException("用户标签名称'" + name + "'不存在");
			}
			return new MatchInfo("TAG_ID", dt.getString("TAG_ID"));
		});
	}
}