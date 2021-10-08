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
package com.gitlab.summercattle.addons.wechat.officialaccounts.message.send;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.gitlab.summercattle.commons.exception.CommonException;

public class NewsSendMessage extends SendMessage {

	private List<NewsItem> items;

	public NewsSendMessage(String fromUserName, String toUserName, List<NewsItem> items) {
		super(fromUserName, toUserName);
		this.items = items;
	}

	@Override
	public SendMessageType getType() {
		return SendMessageType.News;
	}

	@Override
	public void setXml(Element element) throws CommonException {
		element.addElement("ArticleCount").setText(String.valueOf(items.size()));
		Element articles = element.addElement("Articles");
		for (int i = 0; i < items.size(); i++) {
			NewsItem item = items.get(i);
			Element itemElement = articles.addElement("item");
			itemElement.addElement("Title").add(DocumentHelper.createCDATA(item.getTitle()));
			itemElement.addElement("Description").add(DocumentHelper.createCDATA(item.getDescription()));
			itemElement.addElement("PicUrl").add(DocumentHelper.createCDATA(item.getPicUrl().toString()));
			itemElement.addElement("Url").add(DocumentHelper.createCDATA(item.getUrl().toString()));
		}
	}

	@Override
	void messageCheck() throws CommonException {
		if (items == null || items.size() == 0) {
			throw new CommonException("图文消息为空");
		}
		if (items.size() > 8) {
			throw new CommonException("图文消息限制为8条以内");
		}
		for (int i = 0; i < items.size(); i++) {
			NewsItem item = items.get(i);
			if (StringUtils.isBlank(item.getTitle())) {
				throw new CommonException("第" + String.valueOf(i + 1) + "条图文消息的标题为空");
			}
			if (StringUtils.isBlank(item.getDescription())) {
				throw new CommonException("第" + String.valueOf(i + 1) + "条图文消息的描述为空");
			}
			if (item.getPicUrl() == null) {
				throw new CommonException("第" + String.valueOf(i + 1) + "条图文消息的图片链接为空");
			}
			if (item.getUrl() == null) {
				throw new CommonException("第" + String.valueOf(i + 1) + "条图文消息的跳转链接为空");
			}
		}
	}
}