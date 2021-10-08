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
package com.gitlab.summercattle.addons.wechat.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.db.object.DataQuery;
import com.gitlab.summercattle.commons.db.object.DataTable;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.quartz.TriggerType;
import com.gitlab.summercattle.commons.quartz.annotation.QuartzJob;
import com.gitlab.summercattle.commons.quartz.job.StatefulJob;
import com.gitlab.summercattle.commons.utils.spring.RestTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

@QuartzJob(type = TriggerType.Minute, description = "同步绑定信息", interval = 8, enabledProperty = "wechat.prod.enabled", matchConditionalValue = false)
public class SynchronousBindJob implements StatefulJob {

	private static final Logger logger = LoggerFactory.getLogger(SynchronousBindJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("同步绑定信息");
		try {
			String url = SpringContext.getProperty("wechat.prod.url");
			if (StringUtils.isBlank(url)) {
				throw new CommonException("微信后台地址为空");
			}
			DbUtils.getDbTransaction().doDal(ctx -> {
				String sql = "select ID,SOURCE,UNIONID from WX_USER_BIND";
				DataQuery dq = ctx.query(sql, new Object[0]);
				dq.beforeFirst();
				while (dq.next()) {
					String id = dq.getString("ID");
					String source = dq.getString("SOURCE");
					String unionId = dq.getString("UNIONID");
					if (StringUtils.isNotBlank(unionId)) {
						String reqUrl = url;
						if (!reqUrl.endsWith("/")) {
							reqUrl += "/";
						}
						reqUrl += "WeChat/userBindInfo?source=" + source + "&unionId=" + unionId;
						RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
						JSONObject respJson = restTemplateUtils.get(reqUrl, MediaType.APPLICATION_JSON, null, JSONObject.class);
						if (!respJson.getBooleanValue("success")) {
							throw new CommonException(respJson.getString("message"));
						}
						JSONObject resultJson = respJson.getJSONObject("data");
						String oaOpenId = resultJson.getString("OA_OPENID");
						DataTable dt = ctx.select("WX_USER_BIND", id);
						if (dt.first()) {
							String mpOpenId = dt.getString("MP_OPENID");
							if (StringUtils.isBlank(oaOpenId) && StringUtils.isBlank(mpOpenId)) {
								dt.delete();
							}
							else {
								dt.setString("OA_OPENID", oaOpenId);
							}
						}
						ctx.save(dt);
					}
				}
				return null;
			});
		}
		catch (CommonException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
}