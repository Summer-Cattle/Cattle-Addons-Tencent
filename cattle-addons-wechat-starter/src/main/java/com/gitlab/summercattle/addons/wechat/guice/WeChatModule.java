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
package com.gitlab.summercattle.addons.wechat.guice;

import com.gitlab.summercattle.addons.wechat.auth.UserService;
import com.gitlab.summercattle.addons.wechat.auth.bind.UserBindHandle;
import com.gitlab.summercattle.addons.wechat.common.WeChatService;
import com.gitlab.summercattle.addons.wechat.miniprogram.MiniProgramService;
import com.gitlab.summercattle.addons.wechat.miniprogram.bind.UserPhoneNumberBindHandle;
import com.gitlab.summercattle.addons.wechat.officialaccounts.OfficialAccountsService;
import com.gitlab.summercattle.addons.wechat.officialaccounts.UserTagService;
import com.gitlab.summercattle.commons.utils.guice.annotation.GuiceModule;
import com.gitlab.summercattle.commons.utils.guice.module.GuiceAbstractModule;

@GuiceModule
public class WeChatModule extends GuiceAbstractModule {

	@Override
	public void configure() {
		bindClass(WeChatService.class);
		bindClass(MiniProgramService.class);
		bindClass(OfficialAccountsService.class);
		bindClass(UserTagService.class);
		bindClass(UserService.class);
		bindClass(UserBindHandle.class);
		bindClass(UserPhoneNumberBindHandle.class);
	}
}