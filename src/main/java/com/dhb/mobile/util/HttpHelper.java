package com.dhb.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
public class HttpHelper {
	public static final String GET = "GET";
	public static final String POST = "POST";
	private static final Logger logger = Logger.getLogger(HttpHelper.class);
	
	public static String doHttpGetResponse(String urlStr, String method,
			String charSet, String postStr, String timeOut) {
		if (method == null
				|| (!GET.equalsIgnoreCase(method) && !POST
						.equalsIgnoreCase(method))) {
			logger.error("无效http方法[" + method + "]");
			return null;
		}
		logger.info("HTTP访问：urlStr=" + urlStr + ",method=" + method
				+ ",charSet=" + charSet + ",postStr=" + postStr + ",timeOut="
				+ timeOut);
		String myUrlStr = "";
		if (GET.equalsIgnoreCase(method)) {
			if (-1 == urlStr.indexOf("?")) {
				myUrlStr = urlStr + "?" + postStr;
			} else {
				myUrlStr = urlStr + "&" + postStr;
			}
		} else {
			myUrlStr = urlStr;
		}
		logger.info("HTTP访问:urlStr="+myUrlStr);
		URL url = null;
		try {
			url = new URL(myUrlStr);
		} catch (MalformedURLException e) {
			logger.error("访问" + urlStr + "异常", e);
			return null;
		}
		if ("https".equalsIgnoreCase(urlStr.substring(0, 5))) {
			SSLContext sslContext = null;
			try {
				sslContext = SSLContext.getInstance("TLS");
				X509TrustManager xtmArray[] = { new HttpX509TrustManager() };
				sslContext.init(null, xtmArray, new SecureRandom());
			} catch (GeneralSecurityException gse) {
				gse.printStackTrace();
			}
			if (sslContext != null) {
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
						.getSocketFactory());
			}
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HttpHostnameVerifier());
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			logger.error("打开网络连接异常", e);
			return null;
		}
		System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
		System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
		try {
			httpURLConnection.setRequestMethod(method.toUpperCase());
		} catch (ProtocolException e) {
			logger.error("HTTP请求方式不支持,METHOD=" + method, e);
			return null;
		}
		if (POST.equalsIgnoreCase(method)) {
			httpURLConnection.setDoOutput(true);
			PrintWriter printWriter = null;
			try {
				printWriter = new PrintWriter(new OutputStreamWriter(
						httpURLConnection.getOutputStream(), charSet));
			} catch (UnsupportedEncodingException e) {
				logger.error("POST方式打开输出流异常", e);
				return null;
			} catch (IOException e) {
				logger.error("POST方式打开输出流异常", e);
				return null;
			}
			printWriter.write(postStr);
			printWriter.flush();
		}
		InputStream inputStream = null;
		try {
			inputStream = httpURLConnection.getInputStream();
		} catch (IOException e) {
			logger.error("打开输入流异常", e);
			return null;
		}

		int statusCode = 0;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			statusCode = httpURLConnection.getResponseCode();
			int data = 0;
			while ((data = inputStream.read()) != -1) {
				byteArrayOutputStream.write(data);
			}
		} catch (IOException e) {
			logger.error("读取HTTP响应数据异常", e);
			return null;
		}
		byte[] returnBytes = byteArrayOutputStream.toByteArray();
		String returnStr = null;
		try {
			returnStr = new String(returnBytes, charSet);
		} catch (UnsupportedEncodingException e) {
			logger.error("二进制数据转换成字符串异常", e);
			return null;
		}

		// 返回
		return returnStr;
	}

}
