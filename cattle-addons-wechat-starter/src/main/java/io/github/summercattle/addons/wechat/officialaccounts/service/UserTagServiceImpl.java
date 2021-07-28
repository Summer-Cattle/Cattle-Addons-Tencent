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
package io.github.summercattle.addons.wechat.officialaccounts.service;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.github.summercattle.addons.wechat.WeChatUtils;
import io.github.summercattle.addons.wechat.common.AppType;
import io.github.summercattle.addons.wechat.officialaccounts.UserTagService;
import io.github.summercattle.commons.db.DbUtils;
import io.github.summercattle.commons.db.object.DataTable;
import io.github.summercattle.commons.exception.CommonException;
import io.github.summercattle.commons.utils.spring.RestTemplateUtils;
import io.github.summercattle.commons.utils.spring.SpringContext;

public class UserTagServiceImpl implements UserTagService {

	private static final Logger logger = LoggerFactory.getLogger(UserTagServiceImpl.class);

	@Override
	public String addTag(String source, String name) throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户标签为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and NAME=?", new Object[] { source, name });
			if (dt.first()) {
				return null;
			}
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			int tagId = -1;
			JSONArray tagArray = getTags(restTemplateUtils, accessToken);
			for (int i = 0; i < tagArray.size(); i++) {
				JSONObject tagObj = tagArray.getJSONObject(i);
				if (tagObj.getString("name").equals(name)) {
					tagId = tagObj.getIntValue("id");
					break;
				}
			}
			if (tagId == -1) {
				JSONObject requestJson = new JSONObject();
				JSONObject tagJson = new JSONObject();
				tagJson.put("name", name);
				requestJson.put("tag", tagJson);
				String url = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=" + accessToken;
				logger.debug("发送微信公众号创建标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
				JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
				logger.debug("发送微信公众号创建标签接口,返回Json:" + resultJson.toString());
				if (resultJson.containsKey("errmsg")) {
					throw new CommonException(
							"微信公众号创建标签接口异常,错误码:" + resultJson.getIntValue("errcode") + ",错误信息:" + resultJson.getString("errmsg"));
				}
				JSONObject resultTagJson = resultJson.getJSONObject("tag");
				tagId = resultTagJson.getIntValue("id");
			}
			dt.insert();
			dt.setString("SOURCE", source);
			dt.setString("NAME", name);
			dt.setInt("TAG_ID", tagId);
			String id = (String) dt.getPrimaryValue();
			ctx.save(dt);
			return id;
		});
	}

	@Override
	public void modifyTag(String source, String id, String name) throws CommonException {
		if (StringUtils.isBlank(id)) {
			throw new CommonException("用户标签Id为空");
		}
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户新标签为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and ID=?", new Object[] { source, id });
			if (!dt.first()) {
				throw new CommonException("没有找到标签Id'" + id + "'的数据");
			}
			if (name.equals(dt.getString("NAME"))) {
				throw new CommonException("用户旧标签与新标签不能相同");
			}
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONArray tagArray = getTags(restTemplateUtils, accessToken);
			for (int i = 0; i < tagArray.size(); i++) {
				JSONObject tagObj = tagArray.getJSONObject(i);
				if (tagObj.getString("name").equals(name)) {
					throw new CommonException("标签'" + name + "'已经存在");
				}
			}
			JSONObject requestJson = new JSONObject();
			JSONObject tagJson = new JSONObject();
			tagJson.put("id", dt.getInt("TAG_ID"));
			tagJson.put("name", name);
			requestJson.put("tag", tagJson);
			String url = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=" + accessToken;
			logger.debug("发送微信公众号编辑标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号编辑标签接口,返回Json:" + resultJson.toString());
			int returnCode = resultJson.getIntValue("errcode");
			if (returnCode != 0) {
				throw new CommonException("微信公众号编辑标签接口异常,错误码:" + returnCode + ",错误信息:" + resultJson.getString("errmsg"));
			}
			dt.setString("NAME", name);
			ctx.save(dt);
			return null;
		});
	}

	@Override
	public void deleteTag(String source, String id) throws CommonException {
		if (StringUtils.isBlank(id)) {
			throw new CommonException("用户标签Id为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and ID=?", new Object[] { source, id });
			if (!dt.first()) {
				throw new CommonException("没有找到标签Id'" + id + "'的数据");
			}
			int tagId = dt.getInt("TAG_ID");
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONArray tagArray = getTags(restTemplateUtils, accessToken);
			long count = -1;
			for (int i = 0; i < tagArray.size(); i++) {
				JSONObject tagObj = tagArray.getJSONObject(i);
				if (tagObj.getIntValue("id") == tagId) {
					count = tagObj.getLong("count");
					break;
				}
			}
			if (count == -1) {
				throw new CommonException("没有找到标签Id'" + id + "'的数据");
			}
			if (count > 100000) {
				throw new CommonException("标签Id'" + id + "'的数据,粉丝超过10w,不允许直接删除");
			}
			JSONObject requestJson = new JSONObject();
			JSONObject tagJson = new JSONObject();
			tagJson.put("id", tagId);
			requestJson.put("tag", tagJson);
			String url = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=" + accessToken;
			logger.debug("发送微信公众号删除标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号删除标签接口,返回Json:" + resultJson.toString());
			int returnCode = resultJson.getIntValue("errcode");
			if (returnCode != 0) {
				throw new CommonException("微信公众号删除标签接口异常,错误码:" + returnCode + ",错误信息:" + resultJson.getString("errmsg"));
			}
			dt.delete();
			ctx.save(dt);
			return null;
		});
	}

	private JSONArray getTags(RestTemplateUtils restTemplateUtils, String accessToken) throws CommonException {
		String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + accessToken;
		logger.debug("发送微信公众号已创建标签接口,地址:" + url);
		JSONObject respJson = restTemplateUtils.get(url, null, null, JSONObject.class);
		logger.debug("发送微信公众号已创建标签接口,返回Json:" + respJson.toString());
		if (respJson.containsKey("errmsg")) {
			throw new CommonException("微信公众号已创建标签接口异常,错误码:" + respJson.getIntValue("errcode") + ",错误信息:" + respJson.getString("errmsg"));
		}
		return respJson.getJSONArray("tags");
	}

	@Override
	public void batchTagging(String source, String name, String[] openIds) throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户标签为空");
		}
		if (openIds == null || openIds.length == 0) {
			throw new CommonException("用户微信标识信息为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and NAME=?", new Object[] { source, name });
			if (!dt.first()) {
				throw new CommonException("没有找到标签'" + name + "'的数据");
			}
			JSONObject requestJson = new JSONObject();
			JSONArray openIdArray = new JSONArray();
			for (String openId : openIds) {
				if (StringUtils.isNotBlank(openId)) {
					openIdArray.add(openId);
				}
			}
			if (openIdArray.size() == 0) {
				throw new CommonException("用户微信标识信息为空");
			}
			requestJson.put("openid_list", openIdArray);
			requestJson.put("tagid", dt.getInt("TAG_ID"));
			String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=" + accessToken;
			logger.debug("发送微信公众号批量为用户打标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号批量为用户打标签接口,返回Json:" + resultJson.toString());
			int returnCode = resultJson.getIntValue("errcode");
			if (returnCode != 0) {
				throw new CommonException("微信公众号批量为用户打标签接口异常,错误码:" + returnCode + ",错误信息:" + resultJson.getString("errmsg"));
			}
			return null;
		});
	}

	@Override
	public void unbatchTagging(String source, String name, String[] openIds) throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户标签为空");
		}
		if (openIds == null || openIds.length == 0) {
			throw new CommonException("用户微信标识信息为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and NAME=?", new Object[] { source, name });
			if (!dt.first()) {
				throw new CommonException("没有找到标签'" + name + "'的数据");
			}
			JSONObject requestJson = new JSONObject();
			JSONArray openIdArray = new JSONArray();
			for (String openId : openIds) {
				if (StringUtils.isNotBlank(openId)) {
					openIdArray.add(openId);
				}
			}
			if (openIdArray.size() == 0) {
				throw new CommonException("用户微信标识信息为空");
			}
			requestJson.put("openid_list", openIdArray);
			requestJson.put("tagid", dt.getInt("TAG_ID"));
			String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=" + accessToken;
			logger.debug("发送微信公众号批量为用户取消标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号批量为用户取消标签接口,返回Json:" + resultJson.toString());
			int returnCode = resultJson.getIntValue("errcode");
			if (returnCode != 0) {
				throw new CommonException("微信公众号批量为用户取消标签接口异常,错误码:" + returnCode + ",错误信息:" + resultJson.getString("errmsg"));
			}
			return null;
		});
	}

	@Override
	public String[] getUserTags(String source, String openId) throws CommonException {
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("用户微信标识信息为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			JSONObject requestJson = new JSONObject();
			requestJson.put("openid", openId);
			String url = "https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=" + accessToken;
			logger.debug("发送微信公众号获得用户所有标签接口,地址:" + url + ",请求Json:" + requestJson.toString());
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号获得用户所有标签接口,返回Json:" + resultJson.toString());
			if (resultJson.containsKey("errmsg")) {
				throw new CommonException(
						"微信公众号获得用户所有标签接口异常,错误码:" + resultJson.getIntValue("errcode") + ",错误信息:" + resultJson.getString("errmsg"));
			}
			JSONArray tagIdArray = resultJson.getJSONArray("tagid_list");
			if (tagIdArray.size() > 0) {
				Object[] values = new Object[tagIdArray.size() + 1];
				StringBuffer filterBuffer = new StringBuffer();
				for (int i = 0; i < tagIdArray.size(); i++) {
					if (i > 0) {
						filterBuffer.append(" or ");
					}
					filterBuffer.append(" TAG_ID=?");
					values[i + 1] = tagIdArray.getIntValue(i);
				}
				values[0] = source;
				DataTable dt = ctx.select("W_USER_TAG", "SOURCE=? and (" + filterBuffer.toString() + ")", values);
				List<String> tags = new Vector<String>();
				dt.beforeFirst();
				while (dt.next()) {
					tags.add(dt.getString("NAME"));
				}
				return tags.toArray(new String[0]);
			}
			return new String[0];
		});
	}

	@Override
	public String[] getUsers(String source, String name, String openId) throws CommonException {
		if (StringUtils.isBlank(name)) {
			throw new CommonException("用户标签为空");
		}
		if (StringUtils.isBlank(openId)) {
			throw new CommonException("用户微信标识信息为空");
		}
		String accessToken = WeChatUtils.getWeChatService().getAccessToken(AppType.OfficialAccounts, source);
		if (StringUtils.isBlank(accessToken)) {
			throw new CommonException("微信公众号访问令牌为空");
		}
		return DbUtils.getDbTransaction().doDal(ctx -> {
			DataTable dt = ctx.select("W_USER_TAG", null, "SOURCE=? and NAME=?", new Object[] { source, name });
			if (!dt.first()) {
				throw new CommonException("没有找到标签'" + name + "'的数据");
			}
			JSONObject requestJson = new JSONObject();
			requestJson.put("tagid", dt.getInt("TAG_ID"));
			if (StringUtils.isNotBlank(openId)) {
				requestJson.put("next_openid", openId);
			}
			String url = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + accessToken;
			logger.debug("发送微信公众号获取标签下的用户接口,地址:" + url + ",请求Json:" + requestJson.toString());
			RestTemplateUtils restTemplateUtils = SpringContext.getBean(RestTemplateUtils.class);
			JSONObject resultJson = restTemplateUtils.post(url, MediaType.APPLICATION_JSON, null, requestJson, JSONObject.class);
			logger.debug("发送微信公众号获取标签下的用户接口,返回Json:" + resultJson.toString());
			if (resultJson.containsKey("errmsg")) {
				throw new CommonException(
						"微信公众号获取标签下的用户接口异常,错误码:" + resultJson.getIntValue("errcode") + ",错误信息:" + resultJson.getString("errmsg"));
			}
			String[] openIds = new String[resultJson.getIntValue("count")];
			JSONArray openIdArray = resultJson.getJSONObject("data").getJSONArray("openid");
			for (int i = 0; i < openIdArray.size(); i++) {
				openIds[i] = openIdArray.getString(i);
			}
			return openIds;
		});
	}
}