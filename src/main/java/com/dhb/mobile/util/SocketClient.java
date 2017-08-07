package com.dhb.mobile.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


public class SocketClient {
	public static Logger logger = Logger.getLogger(SocketClient.class);
	
	//前5位代表报文长度 然后加 71位报文头 再加报文内容
	public static String sendServer(String ip,Integer port,String sendXml) throws IOException{
//		String enCodeXml = new String(Base64.encodeBase64(sendXml.getBytes())).trim();
//
//		  String len = "00000" + (enCodeXml.length()+71);
//		  len = len.substring(len.length() - 5);
//		  String str = len+ "11111111111111111111111111111111111111111111111111111111111111111111111"+enCodeXml;
		  String str = sendXml;
		  logger.info("Socket发送给钱包的报文："+str);
		  
		  byte[] res = str.getBytes();
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
				logger.info("sjcz 钱包返回的报文:"+rcvLen);
				int l=Integer.parseInt(rcvLen.substring(0, 5));
//				System.out.println("l="+l+" sl="+rcvLen.length());
				if(l+5==rcvLen.length()){
					break;
				}
//				String temStr = rcvLen.substring(76);
//				System.out.println(temStr);
//				String reposeXml = new String(Base64.decodeBase64(temStr.getBytes()));
//			      
//				System.out.println("响应+++++"+reposeXml);
			}
		  String temStr = rcvLen.substring(76);
		  String resXml = new String(Base64.decodeBase64(temStr.getBytes()));
		  logger.info("sjcz 钱包返回的报文XML：" + resXml);
		  return rcvLen;
	}
//	public static byte[] sendServer1(String ip,Integer port,byte[] data){
//		try {
//			Socket client = new Socket(ip, port);
//			client.setSoTimeout(65*1000);
//			OutputStream os = client.getOutputStream();
//			DataOutputStream dos = new DataOutputStream(os);
//			// 发送报文长度
//			dos.writeShort(data.length);
//			// 发送报文
//			os.write(data);
//			
//			InputStream is = client.getInputStream();
//			DataInputStream dis = new DataInputStream(is);
//			// 接收报文长度
//			int dataLen = dis.readShort();
//			if (dataLen<=0) {
//				return new byte[]{};
//			}
//			// 接收报文
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			byte[] buff = new byte[1024];
//			int totalLen = 0;
//			do {
//				int recvLen = dis.read(buff);
//				baos.write(buff, 0, recvLen);
//				totalLen += recvLen;
//			} while(totalLen<dataLen);
//			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//			// 关闭
//			dis.close();
//			is.close();
//			client.close();
//			// 返回
//			return baos.toByteArray();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	public static String sendServer2(String ip,Integer port,byte data[]) throws IOException{
//		  Socket client = new Socket(ip,port);
//		  System.out.println(client.isClosed());
//		  OutputStream out = client.getOutputStream();
//		  out.write(data, 0, data.length);
//		  out.flush();
//		  System.out.println(client.isClosed());
//		  InputStream in = client.getInputStream();
//		  client.shutdownOutput();
//		  byte ibuf[] = new byte[4096];
//		  System.out.println(in.read());
//		  int lenStr = 0;
//		  String rcvLen = null;
//		  while(true){				
//			  lenStr = in.read(ibuf, 0, 4096);				
//				if(lenStr<0){
//					break;
//				}
//				rcvLen = new String(ibuf, 0, lenStr ,"utf-8");
//				System.out.println("rcvLen   msg="+rcvLen);
//			}
//		// 关闭
//					in.close();
//					out.close();
//					client.close();
//					// 返回
//		  logger.info("钱包后台返回的报文：" + rcvLen);
//		  return rcvLen;
//	}
}
