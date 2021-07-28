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
package io.github.summercattle.addons.wechat.officialaccounts;

import io.github.summercattle.addons.wechat.officialaccounts.menu.MenuBar;
import io.github.summercattle.addons.wechat.officialaccounts.menu.match.MatchRule;
import io.github.summercattle.addons.wechat.officialaccounts.message.TemplateMessage;
import io.github.summercattle.commons.exception.CommonException;

/**
 * 公众号服务
 */
public interface OfficialAccountsService {

	/**
	 * 创建菜单
	 * @param source 来源
	 * @param menuBar 菜单栏
	 * @throws CommonException 异常
	 */
	void createMenu(String source, MenuBar menuBar) throws CommonException;

	/**
	 * 删除菜单
	 * @param source 来源
	 * @throws CommonException 异常
	 */
	void deleteMenu(String source) throws CommonException;

	/**
	 * 创建个性化菜单
	 * @param source 来源
	 * @param code 编码
	 * @param menuBar 菜单栏
	 * @param matchRules 匹配规则
	 * @throws CommonException 异常
	 */
	void createConditionalMenu(String source, String code, MenuBar menuBar, MatchRule[] matchRules) throws CommonException;

	/**
	 * 删除个性化菜单
	 * @param source 来源
	 * @param code 编码
	 * @throws CommonException 异常
	 */
	void deleteConditionalMenu(String source, String code) throws CommonException;

	/**
	 * 发送模板消息
	 * @param source 来源
	 * @param userType 用户类型
	 * @param bindInfo 绑定信息
	 * @param message 消息
	 * @return 模板消息Id
	 * @throws CommonException 异常
	 */
	String sendTemplateMessage(String source, int userType, String bindInfo, TemplateMessage message) throws CommonException;
}