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
package io.github.summercattle.addons.wechat.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.summercattle.addons.wechat.miniprogram.configure.MiniProgramConfigProperties;
import io.github.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import io.github.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsConfigProperties;
import io.github.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsServer;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.spring.SpringContext;

public class ServerUtils {

	public static MiniProgramServer getMiniProgramServer(String source) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		MiniProgramConfigProperties properties = SpringContext.getBean(MiniProgramConfigProperties.class);
		List<MiniProgramServer> serverInfos = properties.getServerInfo();
		MiniProgramServer serverInfo = null;
		if (null != serverInfos) {
			for (MiniProgramServer lServerInfo : serverInfos) {
				if (source.equalsIgnoreCase(lServerInfo.getSource())) {
					serverInfo = lServerInfo;
					break;
				}
			}
		}
		if (null == serverInfo) {
			throw new CommonException("微信小程序'" + source + "'的服务信息为空");
		}
		if (StringUtils.isBlank(serverInfo.getAppId())) {
			throw new CommonException("微信小程序'" + source + "'的应用Id为空");
		}
		if (StringUtils.isBlank(serverInfo.getSecret())) {
			throw new CommonException("微信小程序'" + source + "'的密钥为空");
		}
		return serverInfo;
	}

	public static OfficialAccountsServer getOfficialAccountsServer(String source) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		OfficialAccountsConfigProperties properties = SpringContext.getBean(OfficialAccountsConfigProperties.class);
		List<OfficialAccountsServer> serverInfos = properties.getServerInfo();
		OfficialAccountsServer serverInfo = null;
		if (null != serverInfos) {
			for (OfficialAccountsServer lServerInfo : serverInfos) {
				if (source.equalsIgnoreCase(lServerInfo.getSource())) {
					serverInfo = lServerInfo;
					break;
				}
			}
		}
		if (null == serverInfo) {
			throw new CommonException("微信服务公众号'" + source + "'的服务信息为空");
		}
		if (StringUtils.isBlank(serverInfo.getAppId())) {
			throw new CommonException("微信服务公众号'" + source + "'的应用为空");
		}
		if (StringUtils.isBlank(serverInfo.getSecret())) {
			throw new CommonException("微信服务公众号'" + source + "'的密钥为空");
		}
		if (StringUtils.isBlank(serverInfo.getToken())) {
			throw new CommonException("微信服务公众号'" + source + "'的令牌为空");
		}
		if (StringUtils.isBlank(serverInfo.getMessageAesKey())) {
			throw new CommonException("微信服务公众号'" + source + "'的消息密钥为空");
		}
		return serverInfo;
	}
}