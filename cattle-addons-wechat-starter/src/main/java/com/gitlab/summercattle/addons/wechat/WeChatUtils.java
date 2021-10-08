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
package com.gitlab.summercattle.addons.wechat;

import com.gitlab.summercattle.addons.wechat.auth.UserService;
import com.gitlab.summercattle.addons.wechat.common.WeChatService;
import com.gitlab.summercattle.addons.wechat.miniprogram.MiniProgramService;
import com.gitlab.summercattle.addons.wechat.officialaccounts.OfficialAccountsService;
import com.gitlab.summercattle.addons.wechat.officialaccounts.UserTagService;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.guice.GuiceUtils;

public class WeChatUtils {

	public static WeChatService getWeChatService() throws CommonException {
		return GuiceUtils.getInstance(WeChatService.class);
	}

	public static MiniProgramService getMiniProgramService() throws CommonException {
		return GuiceUtils.getInstance(MiniProgramService.class);
	}

	public static UserService getUserService() throws CommonException {
		return GuiceUtils.getInstance(UserService.class);
	}

	public static OfficialAccountsService getOfficialAccountsService() throws CommonException {
		return GuiceUtils.getInstance(OfficialAccountsService.class);
	}

	public static UserTagService getUserTagService() throws CommonException {
		return GuiceUtils.getInstance(UserTagService.class);
	}
}