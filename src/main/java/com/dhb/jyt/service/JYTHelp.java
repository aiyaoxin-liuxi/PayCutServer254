package com.dhb.jyt.service;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dhb.jyt.entity.AppException;
import com.dhb.jyt.entity.JYTResp;
import com.dhb.util.CryptoUtils;
import com.dhb.util.DESHelper;
import com.dhb.util.DateTimeUtils;
import com.dhb.util.HttpClient431Util;
import com.dhb.util.PropFileUtil;
import com.dhb.util.RSAHelper;
import com.dhb.util.StringUtil;
import com.google.common.base.Strings;


public class JYTHelp  {
	
	private static JYTHelp jytHelp = new JYTHelp();
	private JYTHelp(){
		
	}
	public static JYTHelp getInstance(){
		return jytHelp;
	}
	protected static final Logger log = Logger.getLogger(JYTHelp.class);
	//测试环境
	public static String APP_SERVER_URL = PropFileUtil.getByFileAndKey("jyt.properties", "APP_SERVER_URL");
	
	public static String RealName_SERVER_URL = PropFileUtil.getByFileAndKey("jyt.properties", "RealName_SERVER_URL");
	//测试环境测试商户
	//public static String MERCHANT_ID = PropFileUtil.getByFileAndKey("jyt.properties", "MERCHANT_ID");	//替换为自己的商户号
	
	public static String RESP_MSG_PARAM_SEPARATOR = "&";
	
	/**返回报文merchant_id字段前缀*/
	public static String RESP_MSG_PARAM_PREFIX_MERCHANT_ID = "merchant_id=";
	
	/**返回报文xml_enc字段前缀*/
	public static String RESP_MSG_PARAM_PREFIX_XML_ENC = "xml_enc=";
	/**返回报文xml_enc字段前缀*/
	public static String RESP_MSG_PARAM_PREFIX_KEY_ENC = "key_enc=";
	
	/**返回报文sign字段前缀*/
	public static String RESP_MSG_PARAM_PREFIX_SIGN = "sign=";
	
	//替换为自己的客户端私钥
	//测试环境
	public static String CLIENT_PRIVATE_KEY=PropFileUtil.getByFileAndKey("jyt.properties", "CLIENT_PRIVATE_KEY");
	
	//替换匹配的服务端公钥
	//测试环境
	public static String SERVER_PUBLIC_KEY=PropFileUtil.getByFileAndKey("jyt.properties", "SERVER_PUBLIC_KEY");
	
	
	public RSAHelper rsaHelper = new RSAHelper();
	
	public String getMerchId(){
		return PropFileUtil.getByFileAndKey("jyt.properties", "MERCHANT_ID");
	}
	
	{
		try {
			rsaHelper.initKey(PropFileUtil.getByFileAndKey("jyt.properties", "CLIENT_PRIVATE_KEY"), PropFileUtil.getByFileAndKey("jyt.properties", "SERVER_PUBLIC_KEY"), 2048);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取交易流水号
	 * <p> Create Date: 2014-5-10 </p>
	 * @return
	 */
	public String getTranFlow(){
		return getMerchId()+RandomStringUtils.randomNumeric(18);
	}
	
	
	/**
	 * 获得报文头字符串
	 * <p> Create Date: 2014-5-10 </p>
	 * @param tranCode
	 * @return
	 */
	public String getMsgHeadXml(String tranCode,String tranNo){
		StringBuffer headXml = new StringBuffer();
		headXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><head><version>1.0.0</version>");
		headXml.append("<tran_type>01</tran_type>")
			   .append("<merchant_id>").append(getMerchId()).append("</merchant_id>");
		headXml.append("<tran_date>").append(DateTimeUtils.getNowDateStr(DateTimeUtils.DATE_FORMAT_YYYYMMDD))
		       .append("</tran_date>");
		headXml.append("<tran_time>").append(DateTimeUtils.getNowDateStr(DateTimeUtils.DATETIME_FORMAT_HHMMSS))
		       .append("</tran_time><tran_flowid>").append(tranNo)
		       .append("</tran_flowid><tran_code>").append(tranCode).append("</tran_code>");
		headXml.append("</head>");
		
		return headXml.toString();
	}
	
	public String sendMsg(String xml,String sign) throws Exception{
	/*	log.info("上送报文："+xml);
		log.info("上送签名："+sign);*/
		
		byte[] des_key = DESHelper.generateDesKey() ;
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("merchant_id", getMerchId());
		paramMap.put("xml_enc", encryptXml(xml,des_key));
		paramMap.put("key_enc", encryptKey(des_key));
		paramMap.put("sign", sign);
	
		// 获取执行结果
		
		String res = HttpClient431Util.doPost(paramMap, APP_SERVER_URL);
		
		if(res  == null){
			log.error("服务器连接失败");
			
			throw new AppException("测试异常");
		}else{
			log.info("连接服务器成功,返回结果"+res);
		}
		String []respMsg = res.split(RESP_MSG_PARAM_SEPARATOR);
		
		String merchantId = respMsg[0].substring(RESP_MSG_PARAM_PREFIX_MERCHANT_ID.length());
		String respXmlEnc = respMsg[1].substring(RESP_MSG_PARAM_PREFIX_XML_ENC.length());
		String respKeyEnc = respMsg[2].substring(RESP_MSG_PARAM_PREFIX_KEY_ENC.length());
		String respSign = respMsg[3].substring(RESP_MSG_PARAM_PREFIX_SIGN.length());
		
		byte respKey[] = decryptKey(respKeyEnc) ;
		
		String respXml = decrytXml( respXmlEnc,respKey ) ;
		
		
	/*	log.info("返回报文merchantId:"+merchantId);
		log.info("返回报文XML:"+respXml);
		log.info("返回报文签名:"+respSign);*/
		
		if(!verifyMsgSign(respXml,respSign)){
			throw new AppException("延签错误");
		}
		
		return respXml;
	}
	public String sendRealNameMsg(String xml,String sign) throws Exception{
/*		log.info("上送报文："+xml);
		log.info("上送签名："+sign);*/
		
		byte[] des_key = DESHelper.generateDesKey() ;
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("merchant_id", getMerchId());
		paramMap.put("xml_enc", encryptXml(xml,des_key));
		paramMap.put("key_enc", encryptKey(des_key));
		paramMap.put("sign", sign);
	
		// 获取执行结果
		
		String res = HttpClient431Util.doPost(paramMap, RealName_SERVER_URL);
		
		if(res  == null){
			log.error("服务器连接失败");
			
			throw new AppException("测试异常");
		}else{
			//log.info("连接服务器成功,返回结果"+res);
		}
		String []respMsg = res.split(RESP_MSG_PARAM_SEPARATOR);
		
		String merchantId = respMsg[0].substring(RESP_MSG_PARAM_PREFIX_MERCHANT_ID.length());
		String respXmlEnc = respMsg[1].substring(RESP_MSG_PARAM_PREFIX_XML_ENC.length());
		String respKeyEnc = respMsg[2].substring(RESP_MSG_PARAM_PREFIX_KEY_ENC.length());
		String respSign = respMsg[3].substring(RESP_MSG_PARAM_PREFIX_SIGN.length());
		
		byte respKey[] = decryptKey(respKeyEnc) ;
		
		String respXml = decrytXml( respXmlEnc,respKey ) ;
		
		
	/*	log.info("返回报文merchantId:"+merchantId);
		log.info("返回报文XML:"+respXml);
		log.info("返回报文签名:"+respSign);*/
		
		if(!verifyMsgSign(respXml,respSign)){
			throw new AppException("延签错误");
		}
		
		return respXml;
	}
	
	
	public String getMsgRespCode(String respMsg) throws Exception{
        SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new InputSource(new StringReader(respMsg)));
		
		//解析报文头
		Node packageHead = doc.selectSingleNode("/message/head");
		
		Node respCodeNode = packageHead.selectSingleNode("resp_code");
		
		return respCodeNode.getText();
	}
	
	public String getMsgState(String respMsg) throws Exception{
        SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new InputSource(new StringReader(respMsg)));
		
		//解析报文头
		Node packageHead = doc.selectSingleNode("/message/body");
		if(packageHead==null)
			return null;
		
		Node tranStateNode = packageHead.selectSingleNode("tranState");
		if(tranStateNode==null)
			return null;
		
		return tranStateNode.getText();
	}
	public String getRespCode(String respMsg) throws Exception{
        SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new InputSource(new StringReader(respMsg)));
		
		//解析报文头
		Node packageHead = doc.selectSingleNode("/message/body");
		if(packageHead==null)
			return null;
		
		Node tranStateNode = packageHead.selectSingleNode("tran_resp_code");
		if(tranStateNode==null)
			return null;
		
		return tranStateNode.getText();
	}
	public String getRespMessage(String respMsg) throws Exception{
        SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new InputSource(new StringReader(respMsg)));
		
		//解析报文头
		Node packageHead = doc.selectSingleNode("/message/body");
		if(packageHead==null)
			return null;
		
		Node tranStateNode = packageHead.selectSingleNode("tran_resp_desc");
		if(tranStateNode==null)
			return null;
		
		return tranStateNode.getText();
	}
	public JYTResp getResp(String respMsg){
		if(Strings.isNullOrEmpty(respMsg)){
			return null;
		}
		Digester digester=new Digester();
		digester.addObjectCreate("message", JYTResp.class);
		digester.addBeanPropertySetter("message/head/tran_code", "tranCode");
		digester.addBeanPropertySetter("message/head/resp_code", "respCode");
		digester.addBeanPropertySetter("message/head/resp_desc", "respDesc");
		digester.addBeanPropertySetter("message/body/tran_state", "tranState");
		digester.addBeanPropertySetter("message/body/tran_resp_code", "tranRespCode");
		digester.addBeanPropertySetter("message/body/tran_resp_desc", "tranRespDesc");
		digester.addBeanPropertySetter("message/body/remark", "message");
		digester.addBeanPropertySetter("message/body/is_bcn_and_idn_conform", "isBcnAndidnConform");
		StringReader reader = new StringReader(respMsg.trim());
		JYTResp parse =null;
		try {
			parse = (JYTResp) digester.parse(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parse;
	}
	
	private String encryptKey( byte[] key){
		
		byte[] enc_key = null ;
		try {
			enc_key = rsaHelper.encryptRSA(key, false, "UTF-8") ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return StringUtil.bytesToHexString(enc_key) ;
	}
	
	private byte[] decryptKey(String hexkey){
		byte[] key = null ;
		byte[] enc_key = StringUtil.hexStringToBytes(hexkey) ;
		
		try {
			key = rsaHelper.decryptRSA(enc_key, false, "UTF-8") ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return key ;
	}
	
	private String encryptXml( String xml, byte[] key){
		
		String enc_xml = CryptoUtils.desEncryptToHex(xml, key) ;
		
		return enc_xml;
	}
	
	public String decrytXml(String xml_enc, byte[] key) {
		String xml = CryptoUtils.desDecryptFromHex(xml_enc, key) ;
		return xml;
	}

	public boolean verifyMsgSign(String xml, String sign)
	{
		byte[] bsign = StringUtil.hexStringToBytes(sign) ;
		
		boolean ret = false ;
		try {
			ret = rsaHelper.verifyRSA(xml.getBytes("UTF-8"), bsign, false, "UTF-8") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public String signMsg( String xml ){
		String hexSign = null ;
		
		try {
			byte[] sign = rsaHelper.signRSA(xml.getBytes("UTF-8"), false, "UTF-8") ;
			
			hexSign = StringUtil.bytesToHexString(sign) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hexSign;
	}
	
	public boolean verifySign(byte[] plainBytes, byte[] signBytes){
		boolean flag = false;
		try {
			flag = rsaHelper.verifyRSA(plainBytes, signBytes, false, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	public static void main(String[] args) {
		String res="merchant_id=301060120001&xml_enc=f0de9a8ca1277dc7741c1e1b06c273ae7306928294124bceb6492c06d4e1bcdadc81c031f765633dc1e08b33dd0c9fd63cf12c369383c4662d5cbdb562d07b518e96875e2eccb68bb9072231ef959ea34410ab15cf9874bab92495891b7e25ec823e81be7e23ef77ca7543d275aca8574d446f5437fd57828b26bec43d7e3c51a30f5d136649675276c8a38de7e33877f12c6569b3bb51fb7052b20492e9e0d2c289913550fa9213bb3476388bc0b08c356c033976aa863dd554d5c39f3b578569f51492db41d6088356634a6cbe7a24ada656bed51bd5ca95443375f31c4dad256f65e891920441dd642cad08d493cd8ce2e0d11044d4d2e90046d303f047d6f802fea10f96f0c93470d5486d7acbe8f072e4aca38b428c2d86c7b880d944e8712355afecd39a3caa9f3bda11fe3e5052c38ae0dcafa1b3c11fbc2cf43c98e46fc897fe02f85d85bfcad9b7788deb301d059998b94bb3d90287b8b462ddf186a4036acac4057d4f6b2ebb4bff3a7a261f06e0a22b4ffb705bfce263370e3b9174e784a9c1fca191&key_enc=610190b0af9f182180bf3301c3ea777471ca93e5e28f4c9c19403ae9ce036508ff20ec955c81ea1c8ff31cc977107fa3099cec4ee9f234899b17058c95df930724443d34fe602470d8a8844f0d9781b9f6dca6f40af5de66cd10bacf8e6cd617f1e4960dd44a7c529e4ebfe66255fc5e37bedd1dd97d0a34da49f0931ef9964a5e25ec2577ab3fcd4116334f4598496924618c29bafcff81477ba08878534194744494f97099b70723f6dc819cb877340e790a12cec42ff06e97ce3a4035e6892e05368a00370da137d7c868f2194067c4883e3aac8e549a45cb98212c0e9b8682e6fbcffeb624cb989dd01a9efd7c2144732d5c4832941b3650d3a2ecc9817d&sign=a754404987194534f5223adb95d5917ed16808f04c5a3b6f88ff297a6e2929e9b35ff1f0240cf395d8d7d5b29b9366078e1054240fd1d7152e21cf610f61f587107902810778a1c12ec64b5e8770806502f0dbcce481f45518260818f68adc354dcf3dd515a3a052150fc2dce9882ff481585ae8506b24ffa2a358b22d7fe85bdbf4daead065a7ed1882dbf27300fefb1847133db37cf58b15035b3694351510633eb9ae8d2cb50516d1ef32b412dea85149b0a7975f1c5e4d02e4e8ef965f8f21230bd2d78b8b817d31a67c725ff270846290ee397f9ed8ec8fd6e4d79525620d8bc2bb7e4c52d09f8ba26e7713d699877399ef4825a043381efcd1c6a74ec2";
       String []respMsg = res.split(RESP_MSG_PARAM_SEPARATOR);
		
		String merchantId = respMsg[0].substring(RESP_MSG_PARAM_PREFIX_MERCHANT_ID.length());
		String respXmlEnc = respMsg[1].substring(RESP_MSG_PARAM_PREFIX_XML_ENC.length());
		String respKeyEnc = respMsg[2].substring(RESP_MSG_PARAM_PREFIX_KEY_ENC.length());
		String respSign = respMsg[3].substring(RESP_MSG_PARAM_PREFIX_SIGN.length());
		
		byte respKey[] = JYTHelp.getInstance().decryptKey(respKeyEnc) ;
		
		String respXml = JYTHelp.getInstance().decrytXml( respXmlEnc,respKey ) ;
		
		
		log.info("返回报文merchantId:"+merchantId);
		log.info("返回报文XML:"+respXml);
		log.info("返回报文签名:"+respSign);
		
		if(!JYTHelp.getInstance().verifyMsgSign(respXml,respSign)){
			throw new AppException("延签错误");
		}
	}
	
}
