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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsServer;
import io.github.summercattle.addons.wechat.officialaccounts.service.OfficialAccountsReceiveService;
import io.github.summercattle.addons.wechat.utils.ServerUtils;
import io.github.summercattle.commons.exception.CommonException;

@Controller
@RequestMapping("/WeChat/officialAccounts")
@ConditionalOnProperty(prefix = "wechat.prod", name = "enabled", matchIfMissing = true)
public class OfficialAccountsReceiveController {

	private static final Logger logger = LoggerFactory.getLogger(OfficialAccountsReceiveController.class);

	@Autowired
	private OfficialAccountsReceiveService officialAccountsReceiveService;

	@GetMapping(produces = MediaType.TEXT_XML_VALUE)
	@ResponseBody
	public String get(@RequestParam("source") String source, @RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
			@RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
		boolean valid = false;
		try {
			OfficialAccountsServer officialAccountsServer = ServerUtils.getOfficialAccountsServer(source);
			valid = officialAccountsReceiveService.validateSignature(officialAccountsServer, signature, timestamp, nonce);
		}
		catch (CommonException e) {
			logger.error(e.getMessage(), e);
		}
		if (valid) {
			return echostr;
		}
		return "";
	}

	@PostMapping(produces = MediaType.TEXT_XML_VALUE)
	@ResponseBody
	public String receive(@RequestParam("source") String source, @RequestParam("signature") String signature,
			@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestParam("encrypt_type") String encryptType,
			@RequestParam("msg_signature") String msgSignature, @RequestParam("openid") String openid, @RequestBody String xml) {
		try {
			OfficialAccountsServer officialAccountsServer = ServerUtils.getOfficialAccountsServer(source);
			if (officialAccountsReceiveService.validateSignature(officialAccountsServer, signature, timestamp, nonce)) {
				String str = officialAccountsReceiveService.receive(officialAccountsServer, encryptType, openid, msgSignature, timestamp, nonce, xml);
				if (StringUtils.isNotBlank(str)) {
					return str;
				}
			}
		}
		catch (CommonException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}
}