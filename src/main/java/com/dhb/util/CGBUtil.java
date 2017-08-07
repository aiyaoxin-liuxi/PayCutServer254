//package com.dhb.util;
//
//import java.security.Key;
//import java.security.KeyFactory;
//import java.security.MessageDigest;
//import java.security.Security;
//import java.security.spec.X509EncodedKeySpec;
//
//import javax.crypto.Cipher;
//
//import sun.misc.BASE64Decoder;
//
//public class CGBUtil {
//	public static String sha1(String data) throws Exception {
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.update(data.getBytes());
//        StringBuffer buf = new StringBuffer();
//        byte[] bits = md.digest();
//        for(int i=0;i<bits.length;i++){
//            int a = bits[i];
//            if(a<0) a+=256;
//            if(a<16) buf.append("0");
//            buf.append(Integer.toHexString(a));
//        }
//        return buf.toString();
//
//    }
//	public static String addZeroForNum(String str, int strLength) {
//		int strLen = str.length();
//		if (strLen < strLength) {
//			while (strLen < strLength) {
//				StringBuffer sb = new StringBuffer();
//				sb.append(" ").append(str);//左补0
////	    		sb.append(str).append(" ");//右补0
//				str = sb.toString();
//				strLen = str.length();
//			}
//		}
//		return str;
//	 }
//	public static byte[] encryptByPublicKey(byte[] data, String key)throws Exception {
//		// 对公钥解密
//		byte[] keyBytes = decryptBASE64(key);
//	
//		// 取得公钥
//		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//	//	KeyFactory keyFactory = KeyFactory.getInstance("RSA/None/NoPadding");
//		
//		Key publicKey = keyFactory.generatePublic(x509KeySpec);
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());  
//	//	final Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//	
//		// 对数据加密
//	//	Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//		Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//	
//		return cipher.doFinal(data);
//	}
//	public static byte[] decryptBASE64(String key) throws Exception {
//		return (new BASE64Decoder()).decodeBuffer(key);
//	}
//	private static  char[] HEXDIGITS = "0123456789abcdef".toCharArray();
//	public static String toHexString(byte[] bytes) {
//			StringBuilder sb = new StringBuilder(bytes.length * 3);
//			for (int b : bytes) {
//			    b &= 0xff;
//			    sb.append(HEXDIGITS[b >> 4]);
//			    sb.append(HEXDIGITS[b & 15]);
//			    sb.append(' ');
//			}
//			return sb.toString();
//	}
//	public static String Bytes2HexString(byte[] b) {
//      String ret = "";
//      for (int i = 0; i < b.length; i++) {
//          String hex = Integer.toHexString(b[i] & 0xFF);
//          if (hex.length() == 1) {
//              hex = '0' + hex;
//          }
//          ret += "0x" + hex.toUpperCase();
//          if(i!=b.length-1){
//          	ret +=" ";
//          }
//      }
//      return ret;
//  }
//}
