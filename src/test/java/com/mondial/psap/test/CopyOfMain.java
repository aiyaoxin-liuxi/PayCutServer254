package com.mondial.psap.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.util.DigestMD5;
import com.dhb.util.HttpHelp;
import com.dhb.util.MsgUtil;
import com.google.common.io.Files;


public class CopyOfMain {

	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String fileContent ="";
		String filePath="F:/eclipse/workspace/PayCutServer/batchFile/";
		String fileName ="808080211303350_20151208_000012_Q.txt";
		System.out.println(fileContent);
		String filepath = filePath + fileName;
		
		File file = new File(filepath);
		fileContent=Files.toString(file, Charset.forName("utf-8"));
		System.out.println(filepath);
			
		// 将批量代扣信息写入临时文件
		
		String url="http://sfj.chinapay.com/dac/BatchCutUTF8";
		String MerKeyPath="F:/eclipse/workspace/PayCutServer/key/cut/Pbatch/MerPrK_808080211303350_20151202153059.key";
		String merId="808080211303350";
		// 文件上传准备
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		BufferedReader reader = null;
		InputStream resInputStream = null;
		try {
			httpClient = new HttpClient();
			httpClient.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			postMethod = new PostMethod(url);

			byte[] temSen = null;
			try {
				temSen = MsgUtil.getBytes(file);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int temSenLength = temSen.length;
			System.out.println("temSen=[" + temSenLength + "]");
			String tian = new String(temSen, "UTF-8");
			System.out.println("tian=[" + tian + "]");

			// 对需要上传的字段签名
			String chkValue2 = null;
			chkValue2 = DigestMD5.MD5Sign(merId, fileName, fileContent.getBytes("UTF-8"), MerKeyPath);
			System.out.println("批量文件上传接口签名内容:" + chkValue2);

			httpClient.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			// 获得管理参数
			HttpConnectionManagerParams managerParams = httpClient
					.getHttpConnectionManager().getParams();
			// 设置连接超时时间(单位毫秒)
			managerParams.setConnectionTimeout(40000);
			// 设置读数据超时时间(单位毫秒)
			managerParams.setSoTimeout(120000);
			postMethod.setRequestHeader("Connection", "close");
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(1, false));
			NameValuePair[] data = { new NameValuePair("merId", merId),
					new NameValuePair("fileName", fileName),
					new NameValuePair("fileContent", tian),
					new NameValuePair("chkValue", chkValue2) };

			postMethod.setRequestBody(data);

			int statusCode = 0;
			try {
				statusCode = httpClient.executeMethod(postMethod);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				resInputStream = postMethod.getResponseBodyAsStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 接收返回报文
			reader = new BufferedReader(new InputStreamReader(resInputStream, "UTF-8"));
			String tempBf = null;
			StringBuffer html = new StringBuffer();
			while ((tempBf = reader.readLine()) != null) {

				html.append(tempBf);
			}
			String result = html.toString();
			System.out.println("批量文件上传接口返回报文result=[" + result + "]");

			// 拆分应答报文数据
			int dex = result.lastIndexOf("=");
			String tiakong = result.substring(0, dex + 1);
			System.out.println("验签明文：" + "[" + tiakong + "]");
			String ChkValue = result.substring(dex + 1);
			
			String str[] = result.split("&");
			for(int i = 0; i<str.length; i++){
				System.out.println("-----------------------------"+str[i]);
			}
			int Res_Code = str[0].indexOf("=");
			int Res_message = str[1].indexOf("=");

			String responseCode = str[0].substring(Res_Code + 1);
			String message = str[1].substring(Res_message + 1);
			System.out.println("responseCode=" + responseCode);
			System.out.println("message=" + message);
			System.out.println("chkValue=" + ChkValue);
		}catch(Exception e){
			
		} finally {
			
			// 释放httpclient
			if (postMethod != null) {
				postMethod.releaseConnection();

			}
			if (null != httpClient) {
				SimpleHttpConnectionManager manager = (SimpleHttpConnectionManager) httpClient
						.getHttpConnectionManager();
				if (null == manager) {
					httpClient.getHttpConnectionManager().closeIdleConnections(
							0);
				} else {
					manager.shutdown();
				}
			}
			if (reader != null) {
				reader.close();
			}
			if (resInputStream != null) {
				resInputStream.close();
			}
		}
	}
	
	public static void  goPage(String host,String url ,BufferedWriter bw) throws IOException{
		HttpHelp help = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(host+url);
		HttpResponser resp = help.getByHttpClient(param);
		if(200==resp.getCode()){
			handle(host, bw, resp);
		}else{
			goPage(host,url,bw);
		}
	}

	private static void handle(String host, BufferedWriter bw,
			HttpResponser resp) throws IOException {
		String context = resp.getContent();
		if(!com.google.common.base.Strings.isNullOrEmpty(context)){
			 Document doc = Jsoup.parse(context);
			 Elements data=doc.getElementsByClass("tbdata");
			 if(data.size()>0){
				 Elements trList=data.get(0).getElementsByTag("tr");
				 for(Element trE:trList){
					 Elements tdList=trE.getElementsByTag("td");
					 if(tdList.size()==5){
						 Element id= tdList.get(0);
						 Element BankCode= tdList.get(1);
						 Element BankName= tdList.get(2);
						 Element tel= tdList.get(3);
						 Element address= tdList.get(4);
						 String line = id.html()+"\t"+BankCode.html()+"\t"+BankName.html()+"\t"+tel.html()+"\t"+address.html()+"\r";
						 bw.write(line);
						 System.out.println(line);
					 }
					 
				 }
			 }
			
			/* Elements pageList=doc.getElementsByClass("pager");
			 if(pageList.size()>0){
				 Elements aList=pageList.get(0).getElementsByTag("a");
		    	 int length =aList.size();
		    	 if(length>=2){
		    		Element aE= aList.get(length-2);
		    		if("下一页".equals(aE.html())){
		    			String nextUrl=aE.attr("href");
		    			goPage(host,nextUrl,bw);
		    		}
		    	 }
			 }*/
		}
	}
}

