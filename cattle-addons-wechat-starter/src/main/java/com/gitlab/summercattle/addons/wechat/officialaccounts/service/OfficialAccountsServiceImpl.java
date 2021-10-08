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
package com.gitlab.summercattle.addons.wechat.officialaccounts.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONObject;
import com.gitlab.summercattle.addons.wechat.WeChatUtils;
import com.gitlab.summercattle.addons.wechat.common.AppType;
import com.gitlab.summercattle.addons.wechat.miniprogram.configure.MiniProgramServer;
import com.gitlab.summercattle.addons.wechat.officialaccounts.OfficialAccountsService;
import com.gitlab.summercattle.addons.wechat.officialaccounts.menu.MenuBar;
import com.gitlab.summercattle.addons.wechat.officialaccounts.menu.match.MatchRule;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.TemplateMessage;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.TemplateMessageData;
import com.gitlab.summercattle.addons.wechat.utils.ServerUtils;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.spring.RestTemplateUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

public class OfficialAccountsServiceImpl implements OfficialAccountsService {

	private static final Logger logger = LoggerFactory.getLogger(OfficialAccountsServiceImpl.class);

	@Override
	public void createMenu(String source, MenuBar menuBar) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		if (null == menuBar) {
			throw new CommonException("一级菜单为空");
		}
		JSONObject menuJson = menuBar.toJSON();
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
		logger.debug("发送微信公众服务号自定义菜单创建接口,地址:" + url + ",请求Json:" + menuJson.toString());
		RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
		JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, menuJson, JSONObject.class);
		logger.debug("发送微信公众服务号自定义菜单创建接口,返回Json:" + resultJson.toString());
		int errcode = resultJson.getInteger("errcode");
		if (errcode != 0) {
			throw new CommonException("微信公众服务号自定义菜单创建接口异常,错误码:" + errcode + ",错误信息:" + resultJson.getString("errmsg"));
		}
	}

	@Override
	public void deleteMenu(String source) throws CommonException {
		if (StringUtils.isBlank(source)) {
			throw new CommonException("来源为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + accessToken;
		logger.debug("发送微信公众服务号自定义菜单删除接口,地址:" + url);
		RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
		JSONObject resultJson = restTemplateUtils.get(url, MediaType.APPLICATION_JSON, null, JSONObject.class);
		logger.debug("发送微信公众服务号自定义菜单删除接口,返回Json:" + resultJson.toString());
		int errcode = resultJson.getInteger("errcode");
		if (errcode != 0) {
			throw new CommonException("微信公众服务号自定义菜单删除接口异常,错误码:" + errcode + ",错误信息:" + resultJson.getString("errmsg"));
		}
	}

	@Override
	public void createConditionalMenu(String source, String code, MenuBar menuBar, MatchRule[] matchRules) throws CommonException {
	}

	@Override
	public void deleteConditionalMenu(String source, String code) throws CommonException {
	}

	@Override
	public String sendTemplateMessage(String source, int userType, String bindInfo, TemplateMessage message) throws CommonException {
		String openId = WeChatUtils.getUserService().getOpenId(AppType.OfficialAccounts, source, userType, bindInfo);
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("微信公众号没有关注");
		}
		if (null == message) {
			throw new CommonException("微信公众号模板消息为空");
		}
		if (StringUtils.isBlank(message.getTemplateId())) {
			throw new CommonException("微信服务号模板消息的模板Id为空");
		}
		if (message.getKeywords().size() == 0) {
			throw new CommonException("微信公众号模板消息的模板数据为空");
		}
		if (StringUtils.isNotBlank(message.getMiniProgramPagePath()) && StringUtils.isBlank(message.getMiniProgramSource())) {
			throw new CommonException("微信小程序来源为空");
		}
		String miniProgramAppId = null;
		if (StringUtils.isNotBlank(message.getMiniProgramSource())) {
			MiniProgramServer miniProgramServer = ServerUtils.getMiniProgramServer(source);
			miniProgramAppId = miniProgramServer.getAppId();
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}

		JSONObject messageObj = new JSONObject();
		messageObj.put("touser", openId);
		messageObj.put("template_id", message.getTemplateId());
		if (StringUtils.isNotBlank(message.getUrl())) {
			messageObj.put("url", message.getUrl());
		}
		if (StringUtils.isNotBlank(miniProgramAppId)) {
			JSONObject minAppObj = new JSONObject();
			minAppObj.put("appid", miniProgramAppId);
			if (StringUtils.isNotBlank(message.getMiniProgramPagePath())) {
				minAppObj.put("pagepath", message.getMiniProgramPagePath());
			}
			messageObj.put("miniprogram", minAppObj);
		}
		JSONObject dataObj = new JSONObject();
		if (message.getFirst() != null) {
			dataObj.put("first", geMessagetData(message.getFirst()));
		}
		for (int i = 0; i < message.getKeywords().size(); i++) {
			dataObj.put("keyword" + String.valueOf(i + 1), geMessagetData(message.getKeywords().get(i)));
		}
		if (message.getRemark() != null) {
			dataObj.put("remark", geMessagetData(message.getRemark()));
		}
		messageObj.put("data", dataObj);
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
		logger.debug("发送微信公众号模板消息接口,地址:" + url + ",请求Json:" + messageObj.toString());
		RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
		JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, messageObj, JSONObject.class);
		logger.debug("发送微信公众号模板消息接口,返回Json:" + resultJson.toString());
		int errcode = resultJson.getIntValue("errcode");
		if (errcode != 0) {
			throw new CommonException("微信公众号模板消息接口异常,错误码:" + errcode + ",错误信息:" + resultJson.getString("errmsg"));
		}
		return resultJson.getString("msgid");
	}

	private JSONObject geMessagetData(TemplateMessageData messageData) {
		JSONObject dataObj = new JSONObject();
		dataObj.put("value", messageData.getValue());
		if (StringUtils.isNotBlank(messageData.getColor())) {
			dataObj.put("color", messageData.getColor());
		}
		return dataObj;
	}
}