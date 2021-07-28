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
package io.github.summercattle.addons.wechat.auth.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.auth.service.UserBindInfoService;
import io.github.summercattle.addons.wechat.constants.WeChatConstants;
import io.github.summercattle.commons.exception.CommonRuntimeException;
import io.github.summercattle.commons.resp.Response;
import io.github.summercattle.commons.utils.exception.ExceptionWrapUtils;

@RestController
@RequestMapping("/WeChat/userBindInfo")
@ConditionalOnProperty(prefix = WeChatConstants.PROPERTY_PREFIX + ".prod", name = "enabled", matchIfMissing = true)
public class UserBindInfoController {

	@Autowired
	private UserBindInfoService userBindInfoService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Response<JSONObject> get(@RequestParam("source") String source, @RequestParam("unionId") String unionId) {
		try {
			if (StringUtils.isBlank(source)) {
				throw new CommonRuntimeException("来源为空");
			}
			if (StringUtils.isBlank(unionId)) {
				throw new CommonRuntimeException("微信唯一标识Id为空");
			}
			return new Response<JSONObject>(userBindInfoService.get(source, unionId));
		}
		catch (Throwable e) {
			throw ExceptionWrapUtils.wrapRuntime(e);
		}
	}
}