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
package com.gitlab.summercattle.addons.wechat.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.gitlab.summercattle.addons.wechat.auth.controller.UserBindInfoController;
import com.gitlab.summercattle.addons.wechat.auth.service.UserBindInfoService;
import com.gitlab.summercattle.addons.wechat.common.controller.AccessTokenController;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramConfigProperties;
import com.gitlab.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsConfigProperties;
import com.gitlab.summercattle.addons.wechat.officialaccounts.controller.OfficialAccountsMenuController;
import com.gitlab.summercattle.addons.wechat.officialaccounts.controller.OfficialAccountsReceiveController;
import com.gitlab.summercattle.addons.wechat.officialaccounts.service.OfficialAccountsReceiveService;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ MiniProgramConfigProperties.class, OfficialAccountsConfigProperties.class, WeChatConfigProperties.class })
@ComponentScan(basePackageClasses = { OfficialAccountsReceiveController.class, OfficialAccountsReceiveService.class, AccessTokenController.class,
		UserBindInfoController.class, UserBindInfoService.class, OfficialAccountsMenuController.class })
@PropertySource("classpath:/com/gitlab/summercattle/addons/wechat/configure/wechat.properties")
public class WeChatAutoConfiguration {
}