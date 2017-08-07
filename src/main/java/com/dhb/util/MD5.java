package com.dhb.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.dhb.ysb.service.YSBUtil;



public class MD5 {  
	public static String encrypt(String srcMessage,String key) {
		try {
			if (key == null) {
				return null;
			}
			char[] keyChar = key.toCharArray();
			if (keyChar.length != 16) {
				return null;
			}
			byte[] bytes = srcMessage.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			bytes = md.digest();
			int j = bytes.length;
			char[] chars = new char[j * 2];
			int k = 0;
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				chars[k++] = keyChar[b >>> 4 & 0xf];
				chars[k++] = keyChar[b & 0xf];
			}

			return new String(chars);
		} catch (Exception e) {
			return null;
		}
	}
	public static String encrypt(String srcMessage) {
		try {
			byte[] bytes = srcMessage.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			bytes = md.digest();
			StringBuffer hexValue = new StringBuffer(); 
	        for (int i = 0; i < bytes.length; i++){  
	            int val = bytes[i] & 0xff;  
	            if (val < 16)  
	                hexValue.append("0");  
	            hexValue.append(Integer.toHexString(val)); 
	        }  
	        return hexValue.toString();  
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
//		String merchId = "111301000000004";
//		Double banlance = 3.00;
//		String accNo = "6222020200057200491";
//		String tranNo = "12345678900321";
//		String key = "6b37adb8641debea";
//		System.out.println(String.format("%.2f", banlance));
//		String sign = merchId+banlance+accNo+tranNo;
//		System.out.println(sign);
//		String ret = MD5.encrypt(sign, key);
//		System.out.println(ret);
//		System.out.println(merchId+String.format("%.2f", banlance)+accNo+tranNo);
//		String sign = MD5.encrypt(merchId+String.format("%.2f", banlance)+accNo+tranNo,key);
//		String waitSign = "currency=CNY&merch_no=000000000000000&nfc_merch=wecat&nfc_type=nfc_reversal&order_no=PanTest1470897840037&remark=微信冲正测试1&reversal_order_no=PanTest1470797311614";
//		String sign = "";
//		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
//		try {
//			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "6b37adb8641debea");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	System.out.println(sign);
		System.out.println(encrypt("111111000000000"));
	}
}