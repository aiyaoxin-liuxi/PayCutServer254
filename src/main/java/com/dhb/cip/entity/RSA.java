package com.dhb.cip.entity;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;

public class RSA
{
/**
 * RSA软加密
 */
private static final String KEY_ALGORITHM = "RSA";
    
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    
    private static KeyFactory keyFactory = null;
    
    static
    {
        try
        {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("keyFactory初始化失败");
        }
    }
    
    /**
     * RSA签名
     * 
     * @param content 待签名数据
     * @param privateKey 私钥
     * @param input_charset 编码格式
     * @return 签名值
     * @throws Exception
     */
    public static String sign(String content, String privateKey, String input_charset)
        throws Exception
    {
        PrivateKey priKey = toPrivateKey(privateKey);
        
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(priKey);
        
        signature.update(content.getBytes(Charset.forName(input_charset)));
        
        byte[] signed = signature.sign();
        
        String signStr = Base64.encodeBase64String(signed);
        
        return signStr;
        
    }
    
    /**
     * RSA验签名检查
     * 
     * @param content 待验签数据原文
     * @param sign 签名值
     * @param public_key 公钥
     * @param input_charset 编码格式
     * @return 布尔值
     * @throws Exception
     */
    public static boolean verifySign(String content, String sign, String public_key, String input_charset)
        throws Exception
    {
        PublicKey pubKey = toPublicKey(public_key);
        
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        
        signature.initVerify(pubKey);
        
        signature.update(content.getBytes(Charset.forName(input_charset)));
        
        boolean bverify = signature.verify(Base64.decodeBase64(sign));
        return bverify;
        
    }
    
    public static String encrypt(String plaintext, String secretKey, String input_charset)
        throws Exception
    {
        // 取得公钥
        PublicKey pubKey = toPublicKey(secretKey);
        
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        
        byte[] encDataByte = cipher.doFinal(plaintext.getBytes(Charset.forName(input_charset)));
        
        return Base64.encodeBase64String(encDataByte);
    }
    
    public static String decrypt(final String decryptText, String secretKey, String input_charset)
        throws Exception
    {
        byte[] dataByte = Base64.decodeBase64(decryptText);
        
        // 取得私钥
        PrivateKey privateKey = toPrivateKey(secretKey);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decDataByte = cipher.doFinal(dataByte);
        
        return new String(decDataByte, Charset.forName(input_charset));
    }
    
    public static Map<String, String> genKeyPairs(int keySize)
    {
        Map<String, String> keyPairs = new HashMap<String, String>();
        
        try
        {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(keySize);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            
            String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
            String privateKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
            
            keyPairs.put("PublicKey", publicKey);
            keyPairs.put("PrivateKey", privateKey);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println(e.getMessage());
        }
        
        return keyPairs;
    }
    
    /**
     * 得到私钥
     * 
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static PrivateKey toPrivateKey(String key)
        throws Exception
    {
        // 取得私钥数组
        byte[] keyBytes = Base64.decodeBase64(key);
        
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        
        return privateKey;
    }
    
    /**
     * 
     * 得到公钥
     * 
     * @param key 密钥字符串（经过base64编码）
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static PublicKey toPublicKey(String key)
        throws Exception
    {
        // 取得公钥数组
        byte[] keyByte = Base64.decodeBase64(key);
        
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        
        return publicKey;
    }
    public static void main(String[] args) throws Exception
    {
        /*int keySize = 2048;
        Map<String, String> keyPairs = genKeyPairs(keySize);
        System.err.println("RSA keySize:" + keySize);
        System.err.println("RSA KeyPairs:" + keyPairs);*/
    	
        String content = "charset=UTF-8&currency=CNY&frontUrl=http://yangquan.gaohuitong.com/appgateway-web/simulate/index&merOrderDesc=sss&merOrderId=wx1479865809000&merchantId=000110048160017&notifyUrl=http://yangquan.gaohuitong.com/appgateway-web/simulate/index&service=wpnpay&signType=RSA&tranAmt=0.01&tranTime=1479865809000&version=V1";
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCftDNUhCN/444xUiIkV+dxH6lb7of4nkmcvpygKImyepNwusRcvTdtZp/z+K/IHOcEYGS8iSSioM1ZWX1uStXPx4Zo9nYBBcLd3f4TuAF2dzwh1CiVVUV5Ve0xYJD6CHp3Z0Dw0xoKYooPpYUpwvtsDmkus8TfA1bABWizCsCc6Tsqswd15sxqc6+XuCxc97HAwPME8zZLtK7RUGn84P5Kf6zvLqjqUhG0gpjZfFAsNHNvUSvza8Y9obNrKaVGvRWYxaicoaPJH0ZxXmegNnV9VZNZXB982EtUEwoPk0ufz58RQiDnWA5QawIknRXDLUyMoy0F8QoIsvKjzI6GpmTXAgMBAAECggEAWDYpkAo4rYAcX0O1lgtzy/koC55SPlH36Psj+hbKD+pCnCadJXhiMCxaN2Dqfwbv12wC2FyL/sQBCNQ0QwJU3SKhLELN5TywaOogV/Xv4OZ1MV5FWE60RBPhIr/q9CBQvLkslpiTrp7FEWVkiy+mvgWrtV8YY/ItLX0PWq2avE/3MXlhBPorM63rMOwO9DwfhCHnYHabe1eWCw8APfJqeETM4IAah5G7ySAaX3uqziSYDO1uSIEBVxtlEcIXlDrA9Sj4TR0fXzkc3ozoutgpk+DTUz23FcEyK+YrkVRi4BXkQDsAH7YNCUw2FSZulkUyKHryakPNX8+V9MmrbL7AsQKBgQD3WGfcnrtC1xSPLDCOkd2C1Juys13zIv/4DxYH1dLq+0bdIKIbH/7i41IAAMcetGICRSFlAOT0oisqOMZbpK6QGqH8V0esEoDp4loBjKpeNzFWHm+sDULuC/SO3uw897VMKGN6yxr8IemRgEZFbn1KDx74Qcgnt6RpIG3wRG4wWQKBgQClSr9M8HOCEPZ0YVK0VG7qN0oxHkoxeOUiHxyUbYtRY86BDsqYspxP5qsYVGPZTXNki2xpQCsU52A+YYAf+8/7djDfhW9g1iKZv4aV5iq2mzVAxdT6AYimQAiIj0iYSCiM5jrmncEX30arOVfI1ing+KHoykEWSNixRPgAb8MYrwKBgQDJh/ioI5UEcuZHeYPexi6r6LsrsUW9UykoXnJe0/PUjgRBK9OpMjqldv5bDkcvV13754O8HixuvqtY7YWBKf8pXunZBuxY4YK0Dj+zv38Y4POL7aSjlPKRrqAGwM/PJS1M7iOP62kDQkZizRd0fwAKlaNwN3j0E4ccONYazEbTkQKBgASmmcNccKOUPpr/sggI6CYG8Dt5krTZpfjTz0YN3wGnQUQ4WlL5k5Rb9Sx2E2kl2L1XfvFnMM8hw3991tEPkMaOiMmBQ6UB4W9aCDtngoQo0dLEvj1albG304WkInLGdP2h7L5Yafp1+dMPhfzMqdj+pe+a4UHzQsWaHzBD9MVZAoGAEzN1Qsx0U/jdSGQUrb2XBHQq/c0PAcZ8VdXYRO2LoG9bk2DSXx+OcITXs8bUS+DV3HJGZ37ZT/vn21K+egxZzh9NHYvrUn9B19OjSjcEdSoGm3bLT8xvOMePyA/EYKjCx7umM0ozDmrmEo76xu4gLcWfdQVyT4YJnwLsCYCiL7A=";
        System.err.println(RSA.sign(content, privateKey, "UTF-8"));
        
        String sign = RSA.sign(content, privateKey, "UTF-8");
        //String sign = "QCAuH3Oeek+c0Z8iMeb5FVLsss1j6lx5v7yx9EwZFhAEo5T5gE4tnntfEbnbXp6wT47aJ9ga2PwuLGWvDTwC+TWHsCuwQCtMjyPmxf6htzYXKlvYr/+lw/GhzYQz7zBTiRjx1WtGkaNvS2KUAzEm0NWSjmpq49mVUK0V1XEOfswOdMnHqn+0nNdgNS38k49j+AXAzjdIa6QjP0qvkKG4Ad1qU9rZ9ne00NsKdN51RrDxWjrb5SeplPMW2s83Sz72dznH1Np98M51M61FWHxvjuDr6hzbxkan5Cmkh2PNjNLLHlDhYA+hlpdRQK04AZdiUVGEIIApCeZ57v4pqzbKfg==";
        //String publickKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5ZH2IhAu4FSOevgOabhZKwesmDZZ8VIhgovMwavS7zKLhRdeslANhL/RdWPxYq1rPPyya//BG917Va4oCdmKwWpW+rmVNcY+kn5BoCpQ/8WB4Gz7HWfr8k3SDWwmacK1XmHhuwQKWrOe6uhbu4ifMB1E0fcM9FpZUy+PzLrydy0kNEPJZRAwV9HV9aDhjOnMI0sHMsnRXeWfo2Vp5XTw+p2kFEoVG2wwu85tJ6mZiRfxhiM4G0yH4y1wN1gYsHsWZVEsrh7Vdh8VAEE3d0flB/xWpa4lAjvXTKGQ8qyEsFr5xMrRZwg1xNs9AoXBmlgC/MOadXbCVklabQsFwqPv8QIDAQAB";
//        String publickKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn7QzVIQjf+OOMVIiJFfncR+pW+6H+J5JnL6coCiJsnqTcLrEXL03bWaf8/ivyBznBGBkvIkkoqDNWVl9bkrVz8eGaPZ2AQXC3d3+E7gBdnc8IdQolVVFeVXtMWCQ+gh6d2dA8NMaCmKKD6WFKcL7bA5pLrPE3wNWwAVoswrAnOk7KrMHdebManOvl7gsXPexwMDzBPM2S7Su0VBp/OD+Sn+s7y6o6lIRtIKY2XxQLDRzb1Er82vGPaGzaymlRr0VmMWonKGjyR9GcV5noDZ1fVWTWVwffNhLVBMKD5NLn8+fEUIg51gOUGsCJJ0Vwy1MjKMtBfEKCLLyo8yOhqZk1wIDAQAB";
//        System.err.print(RSA.verifySign(content, sign, publickKey, "UTF-8"));
    
        //AES 软解密
        /*String abc = "YD+ZHxUSwfujPTo3KLFxAP9QAOSnEHLKvGklp71883BVRsEH68bFm5ce9PiavAEkcBC/slmWuNQRfAGXfAPh+CjFkRST+ApciXvtgPsA8mds4SruR+SUC6GB1uht458gNVQlBZzNXlciUynzTT9XjdvoterKHXcOmk8IbKZ/9zoh3yh3F9rDzWcgLYpcoY2TLNerk5wVIrRRH+D/kNaWzvYwvMtmMyNx93vmRlKsduqolEmXIIo4zXuMQFBUcnpaTLKj8NmvmURWUumWTveUEjQ01UxeK0fM+YjbzKWrGLEjw+NcM7WiERuY4+MP9jk8cwAHPI2tyHfVvgC0Z98RGc6rgbdOi/8VrPmNX68+qrqHilSvQjj9FffGC4zqacI4NXW3eY77vtOB/CBx4ouv39KH230qQskaXcE2Z6KvDSRS0i25GNeAmiaPd+/if0dQsV/W+495bePcLPcoTeGTzkHmqZpD5XfQ2cWTrdIhUG+fzAm2CDGrQlUmxpwCyCvJx8I3EbY0iaonEtWSfhNhXDkmlEOp8huZ2VGbk49X0B1sKYz6cr0F4jGg26RGxSUhI+iwVYa9sEfboj21sZSMHY2mfNsRzgcyM9l/VkKuPluol1Oq/lRuzJ2/3KejOLAPCcvXWcNocHMcD3fal7u+6x37iHGXg9keqFdiRkvy3C75eN6jZyfgG3M311sWTw3vOm66npacgbVO2OhDbyLT2TTKduK3q4XBdZ7BqDWjVNetbcXSDtcvPwvGT6P2TUsf";
        String secretKey = "CRu3Yc58KCPRo4PQpJa1yw==";
        String abc1 = AesUtils.decryptMsg(abc, secretKey);
        System.out.println("abc1"+abc1);*/
    }
}
