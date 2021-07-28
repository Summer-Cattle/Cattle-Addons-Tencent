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
package io.github.summercattle.addons.wechat.auth.info;

import java.util.Date;
import java.util.List;

public class UserInfo {

	private String language;

	private Date subscribeTime;

	private String remark;

	private long groupId;

	private List<Long> tagIdList;

	private String subscribeScene;

	private long qrScene;

	private String qrSceneStr;

	private String nickName;

	/**
	 * 性别(1-男性,2-女性,0-未知)
	 */
	private int sex;

	private String country;

	private String province;

	private String city;

	private String headImgUrl;

	private String unionId;

	public UserInfo(String nickName, int sex, String country, String province, String city, String headImgUrl, String unionId, long subscribeTime,
			String language, String remark, long groupId, List<Long> tagIdList, String subscribeScene, long qrScene, String qrSceneStr) {
		this.nickName = nickName;
		this.sex = sex;
		this.country = country;
		this.province = province;
		this.city = city;
		this.headImgUrl = headImgUrl;
		this.unionId = unionId;
		this.subscribeTime = new Date(subscribeTime * 1000);
		this.language = language;
		this.remark = remark;
		this.groupId = groupId;
		this.tagIdList = tagIdList;
		this.subscribeScene = subscribeScene;
		this.qrScene = qrScene;
		this.qrSceneStr = qrSceneStr;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getSubscribeTime() {
		return subscribeTime;
	}

	public void setSubscribeTime(Date subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public List<Long> getTagIdList() {
		return tagIdList;
	}

	public void setTagIdList(List<Long> tagIdList) {
		this.tagIdList = tagIdList;
	}

	public String getSubscribeScene() {
		return subscribeScene;
	}

	public void setSubscribeScene(String subscribeScene) {
		this.subscribeScene = subscribeScene;
	}

	public long getQrScene() {
		return qrScene;
	}

	public void setQrScene(long qrScene) {
		this.qrScene = qrScene;
	}

	public String getQrSceneStr() {
		return qrSceneStr;
	}

	public void setQrSceneStr(String qrSceneStr) {
		this.qrSceneStr = qrSceneStr;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
}