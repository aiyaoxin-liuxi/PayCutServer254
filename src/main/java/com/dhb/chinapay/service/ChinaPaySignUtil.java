package com.dhb.chinapay.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chinapay.Base64;
import chinapay.PrivateKey;
import chinapay.SecureLink;

import com.dhb.util.PropFileUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ChinaPaySignUtil {
	private static final Log logger = LogFactory.getLog(ChinaPaySignUtil.class);
	
	public static Map<String,String> parseResponse(String result){
		if(Strings.isNullOrEmpty(result)){
			return null;
		}
		Map<String,String> map =Maps.newHashMap();
		String [] paramPair=result.split("&");
		
		for(String s:paramPair){
			String [] keyVal=s.split("=");
			if(keyVal.length==2){
				map.put(keyVal[0], keyVal[1]);
			}
		}
		return map;

	}
	public static List<String> parseQueryResponse(String result){
		String temp = result;
		if(Strings.isNullOrEmpty(temp)){
			return null;
		}
		temp = temp.trim();
		String [] paramPair=result.split("\\|");
		
		return Lists.newArrayList(paramPair);

	}
	public static String sign(String plainData,String merchId,String merchKeyPath){
		if(Strings.isNullOrEmpty(plainData)||Strings.isNullOrEmpty(merchId)){
			return null;
		}
		PrivateKey key = new PrivateKey();
		boolean flag =false;
		//String merchKeyPath = PropFileUtil.getByFileAndKey("chinapay.properties","merchkeyForCutPath");
		String Base64Data = new String(Base64.encode(plainData.getBytes()));
		flag=key.buildKey(merchId, 0, merchKeyPath);
		if(!flag){
			logger.error("build merch key error");
		}
		SecureLink sl = new SecureLink(key);
		String signData = sl.Sign(Base64Data);
		logger.debug("plainData("+plainData+"),base64("+Base64Data+"),signData("+signData+")");
		return signData;
	}
	
	public static boolean verifySign(String plateData,String chkValue,String pulicKeyPath){
		PrivateKey key = new PrivateKey();
		
		//String keyPath = PropFileUtil.getByFileAndKey("chinapay.properties","publickeyForCutPath");
		boolean flag =key.buildKey("999999999999999", 0, pulicKeyPath);
		if(!flag){
			logger.error("build pubic key error");
			return false;
		}
		SecureLink sl = new SecureLink(key);
		String Base64Data = new String(Base64.encode(plateData.getBytes()));
		logger.debug("return verifysign platedata:(" + plateData
				+ "),Base64Data:(" + Base64Data + "),chkValue:(" + chkValue
				+ ")");
		boolean verifyFlag=sl.verifyAuthToken(Base64Data, chkValue); 
		if(verifyFlag){
			logger.debug("verify sign sucessful");
		}else{
			logger.debug("verify sign fail");
		}
		return verifyFlag;

	}
	public static boolean verifySignForWhiteList(String plateData,String chkValue){
		PrivateKey key = new PrivateKey();
		String keyPath =  PropFileUtil.getByFileAndKey("chinapay.properties","publickeyForCutPath");
		boolean flag =key.buildKey("999999999999999", 0, keyPath);
		if(!flag){
			logger.error("build pubic key error");
			return false;
		}
		SecureLink sl = new SecureLink(key);
	
		boolean verifyFlag=sl.verifyAuthToken(plateData, chkValue); 
		if(verifyFlag){
			logger.debug("verify sign sucessful");
		}else{
			logger.debug("verify sign fail");
		}
		return verifyFlag;

	}
}
