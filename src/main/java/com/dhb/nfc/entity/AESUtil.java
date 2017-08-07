package com.dhb.nfc.entity;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	public static String AES_KEY = "全通支付下发的AES_Key";

	
    public static String decrypt(String sSrc,String key) {
    	if(sSrc==null||sSrc.length()<=0){
    		return "";
    	}
        try {
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String sSrc,String key) {
    	if(sSrc==null||sSrc.length()<=0){
    		return "";
    	}
        try {
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            return byte2hex(encrypted).toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static void main(String[] args) throws Exception {
//    	String key = StringUtil.getRandomString(16);
//    	String encrypt = encrypt("heiheiheihei", key);
//    	String decrypt = decrypt(encrypt, key);
//    	System.out.println(encrypt);
//    	System.out.println(decrypt);
//    	String data = encrypt("orderId=m_0000001&fee=1&time=20091225091010","jw80ixynsa9ihf3k");
//    	System.out.println(data);
//    	System.out.println(decrypt("4fd268846f58516325ecba3be9b92a8528accab9cac69e662c68b7ac0e03cc7e135a49b17974fd2617988092c06d32f6", "jw80ixynsa9ihf3k"));
    	
    	
    	
    }
}
