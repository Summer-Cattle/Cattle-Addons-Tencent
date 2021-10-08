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
package com.gitlab.summercattle.addons.wechat.auth.service;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.db.object.DataTable;
import com.gitlab.summercattle.commons.exception.CommonException;

@Service
public class UserBindInfoService {

	public JSONObject get(String source, String unionId) throws CommonException {
		return DbUtils.getDbTransaction().doDal(ctx -> {
			JSONObject jsonObj = new JSONObject();
			DataTable dt = ctx.select("W_USER_BIND", "SOURCE=? and UNIONID=?", new Object[] { source, unionId });
			if (dt.first()) {
				jsonObj.put("OA_OPENID", dt.getString("OA_OPENID"));
				jsonObj.put("MP_OPENID", dt.getString("MP_OPENID"));
			}
			return jsonObj;
		});
	}
}