package com.dhb.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** 
 * <p>Description: [将请求转发到前置机的具体实现]</p>
 * Created on 2013-7-18
 * @author  YQZL--银企直连
 * @version 1.0 
 * Copyright (c) 2013 宇信易诚--新企业网银--银企直连 
 */ 
public class HttpConnUtil {



    /**
     * 通过流的方式，推送数据到网银
     * 
     * @param urlStr 网银URL
     * @param formData 原表单数
     * @param signedData 已签名数
     * @param encryptPwdType 密码加密类别
     * @param timeOut 过期时间
     * @param isAuth 与网银的连接是否提供客户端证??
     * @return 
     */
    public static String getHttpDocument(String url, String cgb_data, int timeOut) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(timeOut);
        conn.getOutputStream().write(("cgb_data="+cgb_data).getBytes());
        String document = getDataFromAppServerNewLine(conn, false);
        conn.disconnect();
        return document;
    }

    public static String getDataFromAppServerNewLine(HttpURLConnection conn, boolean isNewLine) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String buffer = "";
        String sumBuffer = "";
        while (buffer != null) {
            try {
                buffer = br.readLine();
                if (buffer != null) {
                    if (isNewLine) {
                        sumBuffer += "\n";
                    }
                    sumBuffer += buffer;
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                break;
            }
        }
        if (isNewLine) {
            sumBuffer = sumBuffer.trim();
        }
        return new String(sumBuffer.getBytes(), "GBK").toString();
    }

public static void main(String[] args) {
	try {
		String data = "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\"?>"+
				"<BEDC>"+
				"    <Message>"+
				"        <commHead>"+
				"            <tranCode>0011</tranCode>"+
				"            <cifMaster>1000002822</cifMaster>"+
				"            <entSeqNo>22222222222222222222</entSeqNo>"+
				"            <tranDate>20151119</tranDate>"+
				"            <tranTime>142227</tranTime>"+
				"            <retCode></retCode>"+
				"            <entUserId>100001</entUserId>"+
				"            <password>&lt;![CDATA[s8m3w0z0v9]]&gt;</password>"+
				"        </commHead>"+
				"        <Body>"+
				"            <traceNo></traceNo>"+
				"            <outAccName>银企客户接入专用四十</outAccName>"+
				"            <outAcc>101001513010006954</outAcc>"+
				"            <outAccBank>广发银行南京分行营业部</outAccBank>"+
				"            <inAccName>李氏长江实业</inAccName>"+
				"            <inAcc>135001513010000518</inAcc>"+
				"            <inAccBank>广发银行</inAccBank>"+
				"            <inAccAdd></inAccAdd>"+
				"            <amount>1.0</amount>"+
				"            <remark></remark>"+
				"            <date>20151119</date>"+
				"            <comment>测试</comment>"+
				"            <creNo></creNo>"+
				"            <frBalance></frBalance>"+
				"            <toBalance></toBalance>"+
				"            <handleFee></handleFee>"+
				"        </Body>"+
				"    </Message>"+
				"</BEDC>";
		System.out.println(getHttpDocument("http://192.168.0.100:9528/CGBClient/BankAction",data,3000));;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}