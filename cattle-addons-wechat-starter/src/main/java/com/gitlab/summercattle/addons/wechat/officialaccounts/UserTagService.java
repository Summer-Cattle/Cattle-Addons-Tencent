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
package com.gitlab.summercattle.addons.wechat.officialaccounts;

import com.gitlab.summercattle.commons.exception.CommonException;

/**
 * 用户标签服务
 */
public interface UserTagService {

	/**
	 * 新增标签
	 * @param source 来源
	 * @param name 名称
	 * @return 标签Id
	 * @throws CommonException 异常
	 */
	String addTag(String source, String name) throws CommonException;

	/**
	 * 修改标签名称
	 * @param source 来源
	 * @param id 标签Id
	 * @param name 新的名称
	 * @throws CommonException 异常
	 */
	void modifyTag(String source, String id, String name) throws CommonException;

	/**
	 * 删除标签
	 * @param source 来源
	 * @param id 标签Id
	 * @throws CommonException 异常
	 */
	void deleteTag(String source, String id) throws CommonException;

	/**
	 * 批量为用户打标签
	 * @param source 来源
	 * @param name 名称
	 * @param openIds 用户微信标识信息
	 * @throws CommonException 异常
	 */
	void batchTagging(String source, String name, String[] openIds) throws CommonException;

	/**
	 * 批量为用户取消标签
	 * @param source 来源
	 * @param name 名称
	 * @param openIds 用户微信标识信息
	 * @throws CommonException 异常
	 */
	void unbatchTagging(String source, String name, String[] openIds) throws CommonException;

	/**
	 * 获得用户所有标签
	 * @param source 来源
	 * @param openId 用户微信标识
	 * @return 标签信息
	 * @throws CommonException 异常
	 */
	String[] getUserTags(String source, String openId) throws CommonException;

	/**
	 * 获取标签下的用户
	 * @param source 来源
	 * @param name 名称
	 * @param openId 用户微信标识
	 * @return 用户微信标识信息
	 * @throws CommonException 异常
	 */
	String[] getUsers(String source, String name, String openId) throws CommonException;
}