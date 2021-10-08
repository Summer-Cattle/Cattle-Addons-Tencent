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
package com.gitlab.summercattle.addons.wechat.officialaccounts.message;

import java.util.List;
import java.util.Vector;

public class TemplateMessage {

	private String templateId;

	private String url;

	private String miniProgramSource;

	private String miniProgramPagePath;

	private TemplateMessageData first;

	private List<TemplateMessageData> keywords = new Vector<TemplateMessageData>();

	private TemplateMessageData remark;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMiniProgramSource() {
		return miniProgramSource;
	}

	public void setMiniProgramSource(String miniProgramSource) {
		this.miniProgramSource = miniProgramSource;
	}

	public String getMiniProgramPagePath() {
		return miniProgramPagePath;
	}

	public void setMiniProgramPagePath(String miniProgramPagePath) {
		this.miniProgramPagePath = miniProgramPagePath;
	}

	public TemplateMessageData getFirst() {
		return first;
	}

	public void setFirst(TemplateMessageData first) {
		this.first = first;
	}

	public List<TemplateMessageData> getKeywords() {
		return keywords;
	}

	public void addKeyword(TemplateMessageData keyword) {
		keywords.add(keyword);
	}

	public TemplateMessageData getRemark() {
		return remark;
	}

	public void setRemark(TemplateMessageData remark) {
		this.remark = remark;
	}
}