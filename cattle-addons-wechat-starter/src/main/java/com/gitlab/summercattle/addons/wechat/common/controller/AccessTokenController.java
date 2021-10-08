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
package com.gitlab.summercattle.addons.wechat.common.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitlab.summercattle.addons.wechat.WeChatUtils;
import com.gitlab.summercattle.addons.wechat.common.AppType;
import com.gitlab.summercattle.addons.wechat.constants.WeChatConstants;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.exception.CommonRuntimeException;
import com.gitlab.summercattle.commons.resp.Response;
import com.gitlab.summercattle.commons.utils.exception.ExceptionWrapUtils;

@RestController
@RequestMapping("/WeChat/accessToken")
@ConditionalOnProperty(prefix = WeChatConstants.PROPERTY_PREFIX + ".prod", name = "enabled", matchIfMissing = true)
public class AccessTokenController {

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<String> get(@RequestParam("type") String strType, @RequestParam("source") String source) {
		try {
			if (StringUtils.isBlank(strType)) {
				throw new CommonException("应用类型为空");
			}
			AppType type = EnumUtils.getEnum(AppType.class, strType);
			if (null == type) {
				throw new CommonRuntimeException("应用类型'" + strType + "'无效");
			}
			return new Response<String>(WeChatUtils.getWeChatService().getAccessToken(type, source));
		}
		catch (Throwable e) {
			throw ExceptionWrapUtils.wrapRuntime(e);
		}
	}
}