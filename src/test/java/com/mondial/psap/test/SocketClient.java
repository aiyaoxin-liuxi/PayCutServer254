package com.mondial.psap.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketClient {
	public static Logger logger = Logger.getLogger(SocketClient.class);
	public static String sendServer(String ip,Integer port,String data) throws IOException{
		  logger.info("Socket发送给钱包的报文："+data);
		  
		  byte[] res = data.getBytes();
		  Socket client = new Socket(ip,port);
		  OutputStream out = client.getOutputStream();
		  out.write(res, 0, res.length);
		  out.flush();
		  InputStream in = client.getInputStream();
		  byte ibuf[] = new byte[4096];
		  int lenStr = 0;
		  String rcvLen = null;
		  while(true){				
			  lenStr = in.read(ibuf, 0, 4096);				
				if(lenStr<0){
					break;
				}
				rcvLen = new String(ibuf, 0, lenStr ,"utf-8");
				logger.info("Socket接收钱包的报文:"+rcvLen);
				int l=Integer.parseInt(rcvLen.substring(0, 5));
				if(l+5==rcvLen.length()){
					break;
				}
			}
//		  String temStr = rcvLen.substring(76);
//		  String resXml = new String(Base64.decodeBase64(temStr.getBytes()));
//		  logger.info("返回XML：" + resXml);
		  return rcvLen;
	}

}
