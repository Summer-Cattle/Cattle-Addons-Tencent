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
package io.github.summercattle.addons.wechat.officialaccounts.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.summercattle.addons.wechat.WeChatUtils;
import io.github.summercattle.addons.wechat.officialaccounts.menu.MenuBar;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.exception.ExceptionWrapUtils;

@RestController
@RequestMapping("/WeChat/menu")
public class OfficialAccountsMenuController {

	@PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void createMenu(@RequestParam("source") String source, @RequestBody MenuBar menuBar) {
		try {
			WeChatUtils.getOfficialAccountsService().createMenu(source, menuBar);
		}
		catch (CommonException e) {
			throw ExceptionWrapUtils.wrapRuntime(e);
		}
	}

	@GetMapping(path = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteMenu(@RequestParam("source") String source) {
		try {
			WeChatUtils.getOfficialAccountsService().deleteMenu(source);
		}
		catch (CommonException e) {
			throw ExceptionWrapUtils.wrapRuntime(e);
		}
	}
}