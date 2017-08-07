//package com.mondial.psap.test;
//import java.security.Key;
//import java.security.KeyFactory;
//import java.security.MessageDigest;
//import java.security.Security;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Date;
//import java.util.Random;
//
//import javax.crypto.Cipher;
//
//import org.apache.commons.codec.binary.Base64;
//
//import sun.misc.BASE64Decoder;
//
//import com.dhb.mobile.entity.InAccount;
//import com.dhb.mobile.entity.InMsg;
//import com.dhb.mobile.entity.InPurchase;
//import com.dhb.mobile.entity.InQuan;
//import com.dhb.mobile.entity.InQuanbody;
//import com.dhb.util.DateUtil;
//import com.dhb.util.PropFileUtil;
//import com.dhb.util.XmlUtil;
//
//public class MobileMain {
//	public static void main(String[] args) throws Exception {
////		String id="13761534727";
////		String type="0000";
////		String authmode="1";
////		String tranamt="10";
////		String acqbin="29002003";
////		String merId="5";
////		String ordernum=System.currentTimeMillis()+"";
////		System.out.println("ordernum="+ordernum);
////		String pubKeyIndex="020";
////		String ticket="802522137111111122900200320160122072555";
////		String sign="";
////		MobileRequestInfo info = new MobileRequestInfo();
////		info.setAccountId(id);
////		info.setAccountType(type);
////		info.setAccountAuthMode(authmode);
////		info.setTransAmt(tranamt);
////		info.setAcqBin(acqbin);
////		info.setMerId(merId);
////		info.setOrderNum(ordernum);
////		info.setPubKeyIndex(pubKeyIndex);
////		info.setTicket(ticket);
////		info.setTraceNum("123456");
////		info.setSign(sign);
////		
////		InQuan quan = createXML(info,"");
////		String quanbody = XmlUtil.ObjectToXml(quan.getQuanbody(),"UTF-8");
////		String quanbodystr = quanbody.substring(quanbody.indexOf("<Quanbody>"));
////		System.out.println(quanbody);
////		String sha=sha1(quanbodystr).toUpperCase();
////		 
////		String str=addZeroForNum("111111", 24)+addZeroForNum("", 24)+sha+addZeroForNum("", 20)+addZeroForNum("", 20);
////		System.out.println("128=="+str.toUpperCase());
////		String key=PropFileUtil.getByFileAndKey("mobile.properties", "publicKey");
////		
//////		String hex = RSACoder.Bytes2HexString(str.getBytes());
//////		System.out.println("明文=="+hex);
////		byte[] signdata = encryptByPublicKey(str.getBytes(), key);
////		String secureData = RSACoder.toHexString(signdata).replaceAll(" ", "");
////		String sdxml = XmlUtil.ObjectToXml(createXML(info, secureData),"UTF-8").replace(" standalone=\"yes\"", "");
////		System.out.println(sdxml);
//////		byte[] b = SocketClient.sendServer("218.240.148.181", 18045,sdxml.getBytes());
////		SocketClient.sendServer1("218.240.148.181", 18045, sdxml);
//////		System.out.println(b);
//		
//		
//		
//		
//		String str="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPFF1YW4+PFF1YW5ib2R5PjxNc2c+PHZlcnNpb24+MS4wLjA8L3ZlcnNpb24+PHR5cGU+MTAwMzwvdHlwZT48ZmxhZz4wMTwvZmxhZz48L01zZz48VXNlcj48aWQ+PC9pZD48L1VzZXI+PEFjY291bnQ+PGlkPjEzNzYxNTM0NzI3PC9pZD48dHlwZT48L3R5cGU+PGF1dGhNb2RlPjwvYXV0aE1vZGU+PC9BY2NvdW50PjxGaW5hbmNlPjxhbW91bnQ+PC9hbW91bnQ+PHRvdGFsSW5jb21lPjwvdG90YWxJbmNvbWU+PC9GaW5hbmNlPjxQdXJjaGFzZT48YWNxQklOPjI5MDAyMDAzPC9hY3FCSU4+PGRhdGU+MjAxNjAxMjEwNTIyNTk8L2RhdGU+PHRyYWNlTnVtPjEyMzQ1NjwvdHJhY2VOdW0+PC9QdXJjaGFzZT48UmVzcD48cmVzcENvZGU+UzAyPC9yZXNwQ29kZT48cmVzcEluZm8+Tm8gZmllbGQgWy9RdWFuL1F1YW5ib2R5L1RpY2tldF0gaW4gdGhlIGlucHV0IFhNTC4gZXJyb3IhPC9yZXNwSW5mbz48L1Jlc3A+PFNUUFA+PGRhdGU+PC9kYXRlPjx0cmFjZU51bT48L3RyYWNlTnVtPjwvU1RQUD48ZXh0SW5mbz4xMzAyMTk4NTkxMSwxNDAxMDEsMTAwPC9leHRJbmZvPjxQdWJLZXlJbmRleD4wMjA8L1B1YktleUluZGV4PjxUaWNrZXQ+PC9UaWNrZXQ+PC9RdWFuYm9keT48U2VjdXJlRGF0YT41REQ2M0Y0Nzc5NUIwNDVFQzZGMzEyMkZENDlBRkM1Q0IyMDdBODgxODI3MkJCMTU0MEM1MzQyNzVDRkQ2QTk4MTgyRjgxQUZEOUE4MUI5NzU5MTRDNjFEOTZGNTNEMkRDODNBNDBDMjM2MkU1NDM4Rjk3MEQ0MUJCNTI4NEZCQkEyODkyRDVDM0YyMjZBNTg2OEYwNUZCMDQzRjM4Q0EyNzVBNkY0QzYxQjRGMkREODdGN0JERTBFMTgwOTM0MDUxODM4QjZBQzEyRDVDREFGOUEwQUM0MjA3N0M2NTM5MzlDRTU1N0ZERkI2QjE4RDcyQUMyMTRFRTkwMEY2Mzk1PC9TZWN1cmVEYXRhPjwvUXVhbj4K";
//		
//		
//	}
//	public static byte[] encryptByPublicKey(byte[] data, String key)throws Exception {
//		// 对公钥解密
//		byte[] keyBytes = decryptBASE64(key);
//
//		// 取得公钥
//		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
////		KeyFactory keyFactory = KeyFactory.getInstance("RSA/None/NoPadding");
//		
//		Key publicKey = keyFactory.generatePublic(x509KeySpec);
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());  
////		final Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//
//		// 对数据加密
////		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//		Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
//		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//		return cipher.doFinal(data);
//	}
//	public static byte[] decryptBASE64(String key) throws Exception {
//		return (new BASE64Decoder()).decodeBuffer(key);
//	}
//	public static String sha1(String data) throws Exception {
//		
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//
//        md.update(data.getBytes());
//
//        StringBuffer buf = new StringBuffer();
//
//        byte[] bits = md.digest();
//
//        for(int i=0;i<bits.length;i++){
//
//            int a = bits[i];
//            
//            if(a<0) a+=256;
//
//            if(a<16) buf.append("0");
//            buf.append(Integer.toHexString(a));
//        }
//        return buf.toString();
//
//    }
//
//	
//	/*数字不足位数左补0
//	  *
//	  * @param str
//	  * @param strLength
//	  */
//	public static String addZeroForNum(String str, int strLength) {
//		int strLen = str.length();
//		if (strLen < strLength) {
//			while (strLen < strLength) {
//				StringBuffer sb = new StringBuffer();
////				sb.append(" ").append(str);//左补0
//	    		sb.append(str).append(" ");//右补0
//				str = sb.toString();
//				strLen = str.length();
//			}
//		}
//		return str;
//	 }
//	public static InQuan createXML(MobileRequestInfo info,String data){
//		InQuan quan = new InQuan();
//		InQuanbody quanbody = new InQuanbody();
//		
//		InMsg msg = new InMsg();
//		msg.setVersion("1.0.0");
//		msg.setType("1003");
//		msg.setFlag("00");
//		quanbody.setMsg(msg);
//		
//		InAccount account = new InAccount();
//		account.setId(info.getAccountId());//钱包账号id
//		account.setType(info.getAccountType());//钱包账号类型
//		account.setAuthMode(info.getAccountAuthMode());//钱包账号授权模式
//		quanbody.setAccount(account);
//		
//		InPurchase purchase = new InPurchase();
//		purchase.setAcqBIN(info.getAcqBin());//受理方机构代码
//		purchase.setDate(DateUtil.formatYYYYMMDDHHMMSS(new Date()));//受理日期时间
//		purchase.setCurrency("156");//币种
//		purchase.setTransAmt(info.getAmtStr(info.getTransAmt(), 12));//交易金额
//		purchase.setMerId(info.getMerId());//商户号
//		purchase.setOrderNum(info.getOrderNum());//订单号
//		purchase.setTraceNum(info.getTraceNum());
//		quanbody.setPurchase(purchase);
//		
//		quanbody.setExtInfo("");
//		quanbody.setPubKeyIndex(info.getPubKeyIndex());
//		quanbody.setTicket(info.getTicket());
//		
//		quan.setQuanbody(quanbody);
//		String secureData=data;
//		quan.setSecureData(secureData);
//		return quan;
//	}
//	public static String getSixRandom() {
//		Random random = new Random();
//		String result="";
//		for(int i=0;i<6;i++){
//			result+=random.nextInt(10);
//		}
//		return result;
//	}
//}
