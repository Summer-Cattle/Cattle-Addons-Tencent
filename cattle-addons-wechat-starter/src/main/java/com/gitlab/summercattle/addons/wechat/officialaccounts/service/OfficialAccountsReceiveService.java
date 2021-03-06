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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gitlab.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsConfigProperties;
import com.gitlab.summercattle.addons.wechat.officialaccounts.configure.OfficialAccountsServer;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.RcvEvent;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.ReceiveEvent;
import com.gitlab.summercattle.addons.wechat.officialaccounts.event.ReceiveEventType;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.RcvMessage;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.ReceiveMessage;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.receive.ReceiveMessageType;
import com.gitlab.summercattle.addons.wechat.officialaccounts.message.send.SendMessage;
import com.gitlab.summercattle.addons.wechat.officialaccounts.security.ByteGroup;
import com.gitlab.summercattle.addons.wechat.officialaccounts.security.PKCS7Encoder;
import com.gitlab.summercattle.commons.db.DbUtils;
import com.gitlab.summercattle.commons.exception.CommonException;
import com.gitlab.summercattle.commons.utils.auxiliary.Dom4jUtils;
import com.gitlab.summercattle.commons.utils.exception.ExceptionWrapUtils;
import com.gitlab.summercattle.commons.utils.redis.RedisTemplateUtils;
import com.gitlab.summercattle.commons.utils.reflect.ClassUtils;
import com.gitlab.summercattle.commons.utils.reflect.ReflectUtils;
import com.gitlab.summercattle.commons.utils.spring.SpringContext;

@Service
public class OfficialAccountsReceiveService {

	private static final Logger logger = LoggerFactory.getLogger(OfficialAccountsReceiveService.class);

	public static final String WECHAT_RCV = "Wechat_RCV_";

	public boolean validateSignature(OfficialAccountsServer officialAccountsServer, String signature, String timestamp, String nonce)
			throws CommonException {
		return validateSignatureData(signature, officialAccountsServer.getToken(), timestamp, nonce);
	}

	private boolean validateSignatureData(String signature, String token, String... datas) throws CommonException {
		if (StringUtils.isBlank(signature)) {
			throw new CommonException("????????????????????????");
		}
		if (StringUtils.isBlank(token)) {
			throw new CommonException("????????????");
		}
		if (datas == null || datas.length == 0) {
			throw new CommonException("????????????");
		}
		String lSignature = signature(token, datas);
		return lSignature.equals(signature.toLowerCase());
	}

	private String signature(String token, String... datas) {
		String[] arrays = new String[datas.length + 1];
		arrays[0] = token;
		for (int i = 0; i < datas.length; i++) {
			arrays[i + 1] = datas[i];
		}
		Arrays.sort(arrays);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrays.length; i++) {
			sb.append(arrays[i]);
		}
		return Hex.encodeHexString(DigestUtils.sha1(sb.toString()), true);
	}

	public String receive(OfficialAccountsServer officialAccountsServer, String encryptType, String openid, String msgSignature, String timestamp,
			String nonce, String xml) throws CommonException {
		Element receiveElement;
		if (StringUtils.isNotBlank(encryptType)) {
			if (encryptType.equals("aes")) {
				try {
					Element encryptElement = Dom4jUtils.getDocument(xml).getRootElement();
					String toUserName = encryptElement.elementText("ToUserName");
					String encrypt = encryptElement.elementText("Encrypt");
					if (validateSignatureData(msgSignature, officialAccountsServer.getToken(), timestamp, nonce, encrypt)) {
						byte[] key = Base64.getDecoder().decode(officialAccountsServer.getMessageAesKey() + "=");
						Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
						SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
						IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(key, 0, 16));
						cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
						byte[] datas = cipher.doFinal(Base64.getDecoder().decode(encrypt));
						byte[] bytes = PKCS7Encoder.decode(datas);
						byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
						int xmlLength = recoverNetworkBytesOrder(networkOrder);
						String fromApiId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), "UTF-8");
						String xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), "UTF-8");
						if (!officialAccountsServer.getAppId().equals(fromApiId)) {
							throw new CommonException("?????????????????????Post???????????????");
						}
						receiveElement = Dom4jUtils.getDocument(xmlContent).getRootElement();
						if (!toUserName.equals(receiveElement.elementText("ToUserName"))) {
							throw new CommonException("?????????????????????Post???????????????");
						}
					}
					else {
						throw new CommonException("??????????????????????????????");
					}
				}
				catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
					throw ExceptionWrapUtils.wrap(e);
				}
			}
			else {
				throw new CommonException("????????????'" + encryptType + "'??????");
			}
		}
		else {
			receiveElement = Dom4jUtils.getDocument(xml).getRootElement();
		}
		String fromUserName = receiveElement.elementText("FromUserName");
		if (!openid.equals(fromUserName)) {
			throw new CommonException("?????????????????????Post???????????????");
		}
		String toUserName = receiveElement.elementText("ToUserName");
		String msgType = receiveElement.elementText("MsgType");
		String strCreateTime = receiveElement.elementText("CreateTime");
		Date createTime = new Date(NumberUtils.toLong(strCreateTime) * 1000);
		OfficialAccountsConfigProperties officialAccountsConfigProperties = SpringContext.getBean(OfficialAccountsConfigProperties.class);
		RedisTemplateUtils redisUtils = SpringContext.getBean(RedisTemplateUtils.class);
		SendMessage sendMessage = null;
		ReceiveMessageType rcvMessageType = ReceiveMessageType.parse(msgType);
		if (rcvMessageType != null) {
			int receiveMessageTimeout = officialAccountsConfigProperties.getReceiveMessageTimeout();
			if (rcvMessageType == ReceiveMessageType.Event) {
				String key = WECHAT_RCV + fromUserName + "_" + strCreateTime;
				if (!redisUtils.exists(key)) {
					redisUtils.set(key, createTime, receiveMessageTimeout);
					String lEventType = receiveElement.elementText("Event");
					ReceiveEventType rcvEventType = ReceiveEventType.parse(lEventType);
					if (rcvEventType != null) {
						Class<ReceiveEvent>[] rcvEventClasses = ClassUtils.getSubTypesOf(ReceiveEvent.class);
						ReceiveEvent event = null;
						for (Class<ReceiveEvent> lEvent : rcvEventClasses) {
							RcvEvent rcvEvent = ReflectUtils.getAnnotation(lEvent, RcvEvent.class);
							if (null != rcvEvent && rcvEvent.value() == rcvEventType) {
								event = ClassUtils.instance(lEvent);
								break;
							}
						}
						if (event != null) {
							event.parse(fromUserName, toUserName, createTime, receiveElement);
							sendMessage = event.receive();
						}
					}
					else {
						logger.debug("?????????????????????Post??????????????????:" + Dom4jUtils.asXmlWithoutPretty(receiveElement, "UTF-8"));
					}
				}
			}
			else {
				String msgId = receiveElement.elementText("MsgId");
				String key = WECHAT_RCV + msgId;
				if (!redisUtils.exists(key)) {
					redisUtils.set(key, createTime, receiveMessageTimeout);
					Class<ReceiveMessage>[] rcvMessageClasses = ClassUtils.getSubTypesOf(ReceiveMessage.class);
					ReceiveMessage message = null;
					for (Class<ReceiveMessage> receiveMessage : rcvMessageClasses) {
						RcvMessage rcvMessage = ReflectUtils.getAnnotation(receiveMessage, RcvMessage.class);
						if (null != rcvMessage && rcvMessage.value() == rcvMessageType) {
							message = ClassUtils.instance(receiveMessage);
							break;
						}
					}
					if (message != null) {
						message.parse(fromUserName, toUserName, createTime, receiveElement);
						sendMessage = message.receive();
					}
				}
			}
		}
		else {
			logger.debug("?????????????????????Post??????????????????:" + Dom4jUtils.asXmlWithoutPretty(receiveElement, "UTF-8"));
		}
		if (null != sendMessage) {
			String result = sendMessage.toStringData();
			logger.debug("?????????????????????????????????:" + result);
			if (StringUtils.isNotBlank(encryptType)) {
				if (encryptType.equals("aes")) {
					byte[] key = Base64.getDecoder().decode(officialAccountsServer.getMessageAesKey() + "=");
					String randomString = com.gitlab.summercattle.commons.utils.auxiliary.StringUtils.getRandomString(16);
					ByteGroup byteCollector = new ByteGroup();
					byteCollector.addBytes(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(randomString));
					byte[] textBytes = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(result);
					byteCollector.addBytes(networkBytesOrder(textBytes.length));
					byteCollector.addBytes(textBytes);
					byteCollector.addBytes(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(officialAccountsServer.getAppId()));
					byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
					byteCollector.addBytes(padBytes);
					byte[] unencrypted = byteCollector.toBytes();
					try {
						// ?????????????????????AES???CBC??????
						Cipher lCipher = Cipher.getInstance("AES/CBC/NoPadding");
						SecretKeySpec lkeySpec = new SecretKeySpec(key, "AES");
						IvParameterSpec liv = new IvParameterSpec(key, 0, 16);
						lCipher.init(Cipher.ENCRYPT_MODE, lkeySpec, liv);
						// ??????
						byte[] encrypted = lCipher.doFinal(unencrypted);
						// ??????BASE64????????????????????????????????????
						String base64Encrypted = Base64.getEncoder().encodeToString(encrypted);
						long returnTime = DbUtils.getDbTool().getCurrentDate().getTime() / 1000;
						String signature = signature(officialAccountsServer.getToken(), String.valueOf(returnTime), randomString, base64Encrypted);
						return generate(base64Encrypted, signature, String.valueOf(returnTime), randomString);
					}
					catch (IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException
							| NoSuchAlgorithmException | NoSuchPaddingException e) {
						throw ExceptionWrapUtils.wrap(e);
					}
				}
				else {
					throw new CommonException("????????????\"" + encryptType + "\"??????");
				}
			}
			else {
				return result;
			}
		}
		return null;
	}

	private int recoverNetworkBytesOrder(byte[] orderBytes) {
		int sourceNumber = 0;
		for (int i = 0; i < 4; i++) {
			sourceNumber <<= 8;
			sourceNumber |= orderBytes[i] & 0xff;
		}
		return sourceNumber;
	}

	private byte[] networkBytesOrder(int sourceNumber) {
		byte[] orderBytes = new byte[4];
		orderBytes[3] = (byte) (sourceNumber & 0xFF);
		orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
		orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
		orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
		return orderBytes;
	}

	private String generate(String encrypt, String signature, String timestamp, String nonce) throws CommonException {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("xml");
		rootElement.addElement("Encrypt").add(DocumentHelper.createCDATA(encrypt));
		rootElement.addElement("MsgSignature").add(DocumentHelper.createCDATA(signature));
		rootElement.addElement("TimeStamp").setText(timestamp);
		rootElement.addElement("Nonce").add(DocumentHelper.createCDATA(nonce));
		return Dom4jUtils.asXmlWithoutPretty(rootElement, "UTF-8");
	}
}