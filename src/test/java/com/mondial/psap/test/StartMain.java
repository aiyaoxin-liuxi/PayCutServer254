package com.mondial.psap.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sun.security.action.PutAllAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dhb.anyz.entity.Constants;
import com.dhb.anyz.service.ANYZUtil;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.service.QrCodeUtil;
import com.dhb.util.DateUtil;
import com.dhb.util.EncodeUtils;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.util.MD5;
import com.dhb.util.PropFileUtil;
import com.dhb.util.Tools;
import com.dhb.util.XmlUtils;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.SignUtil;
import com.jnewsdk.util.StringUtils;

public class StartMain {
	public void singlePayCGB(){
		String merchId ="111301000000000";
		double money =1.00;
	//String accNo = "135001513010000518";//对公
		String accNo = "6225680221002123251";//对私
	//	String accNo ="1234567890"; //行外
		String trano = Tools.getUUID();
		String key = merchId+String.format("%.2f", money)+accNo+trano;
		String certNo ="320924199302020863";
		String certType ="01";
		String accType ="01";
		//String accName = "李氏长江实业";//对公
		String accName = "网银测试";//对私
		//String accName = "收款企业";//行外
		//String bankName = "兴业银行";
		String bankName ="广发银行";
		String channelId ="4";
		String bankCode ="309391000011";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setBankCode(bankCode);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		System.out.println(context);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/singlePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void singlePay(){
		String trano = "PanTest"+System.currentTimeMillis()+"";
		String merchId ="111301000000000";
		String channelId ="3";
		String bankName = "中国工商银行";
		String bankCode ="103584099993";
		String accNo = "6222020200057200491";
		String accName = "潘泳辰";
		String accType ="00";
		double banlance =0.01;
		//String currency = "CNY";
		String certNo = "230206198906121115";
		String remark = "测试";
		
		String key = merchId+banlance+accNo+trano;

		OutRequestInfo info = new OutRequestInfo();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		
		info.setTranNo(trano);
		info.setMerchId(merchId);
		info.setChannelId(channelId);
		info.setBankName(bankName);
		info.setBankCode(bankCode);
		info.setAccNo(accNo);
		info.setAccName(accName);
		info.setAccType(accType);
		info.setBanlance(banlance);
		info.setCertNo(certNo);
		info.setRemark(remark);
		//info.setCertNo(certNo);
		//info.setCertType(certType);
		info.setSign(sign);
		
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpRequestParam param = new HttpRequestParam();
		//param.setUrl("http://localhost:8080/PayCutServer/dhb/singlePay");
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/singlePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void singlePayForJYT(){
		String merchId ="111301000000000";
		double money =1.00;
//		String accNo = "136001512010007819";
		String accNo = "6215581804002971745";//my卡号
		String trano = Tools.getUUID();
		String key = merchId+String.format("%.2f", money)+accNo+trano;
		System.out.println("key="+key);
//		String certNo ="320924199302020863";
		String certNo ="420624199109111824";//my身份证号
		String certType ="01";//身份证
		String accType ="01";//02对公
		String accName = "王小威";
		String bankName = "中国工商银行";
		String channelId ="2";
		String bankCode ="102528300614";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setBankCode(bankCode);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/singlePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void singleCut(){
		String traNo = "PanTest"+System.currentTimeMillis()+"";
		String merchId ="111301000000000";
		String channelId ="3";
		String bankName = "中国工商银行";
		String accNo = "6222020200057200491";
		String accName = "潘泳辰";
		String accType ="00";
		double banlance =0.01;
		//String currency = "CNY";
		String certType ="01";
		String certNo ="23020619890612115";
		String bankCode = "01020000";
		String remark = "测试银生宝代扣";
		String subContractId = "5820";
		
		String key = merchId+banlance+accNo+traNo;
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		
		OutRequestInfo info = new OutRequestInfo();
		info.setTranNo(traNo);
		info.setMerchId(merchId);
		info.setChannelId(channelId);
		info.setBankName(bankName);
		info.setAccNo(accNo);
		info.setAccName(accName);
		info.setAccType(accType);
		info.setBanlance(banlance);
		info.setCertType(certType);
		info.setCertNo(certNo);
		info.setBankCode(bankCode);
		info.setRemark(remark);
		info.setSubContractId(subContractId);
		info.setSign(sign);
		
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpRequestParam param = new HttpRequestParam();
		//param.setUrl("http://localhost:8080/PayCutServer/dhb/singleCut");
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/singleCut");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void batchCut(){
		String merchId ="111301000000000";
		double money =1.00;
		String accNo = "6214623621000040403";
		String trano = Tools.getUUID();
		String key = merchId+money+accNo+trano;
		String certNo ="320924199302020863";
		String certType ="01";
		String accType ="00";
		String accName = "孙苏阳";
		String bankName = "广发银行";
		String channelId ="2";
		OutRequestInfo info = new OutRequestInfo();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/singleCut");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void query(){
		String merchId ="111301000000000";
		double money =100.00;
		String accNo = "6217860100000372608";
		String trano = "f834d40ecc3a4d2fb54cf879a09acba2";
		String key = merchId+trano;
		String certNo ="341281198403050497";
		String certType ="01";
		String accType ="00";
		String accName = "郑和进";
		String bankName = "中国银行";
		String channelId ="3";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/queryTranStatus");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	
	public void bankCardRealName(){
		String traNo = "CONTRACTCHANGE20160721130520083326";
		String merchId ="111301000000000";
		String accNo = "6226200102086726";
		String accName = "潘泳辰";
		String certNo ="230206198906121115";
		String channelId ="1";
		
		String key = merchId+traNo;
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setCertNo(certNo);
		info.setChannelId(channelId);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setTranNo(traNo);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/bankCardRealName");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void RealName(){
		String merchId ="111301000000000";
		double money =100.00;
		//String accNo = "6212250200009623735";//蘭
		String accNo = "6212260200057627768";//zheng
		//String accNo="6228840101812164";
		String trano = "1234222234242343777775748";
		String key = merchId+trano;
		//String certNo = "130925198310145611";
		//String certNo ="110102198012293032";//lan
		String certNo ="341281198403050497";
		String certType ="01";
		String accType ="00";
		//String accName = "李猛";
		//String accName = "兰世韬";
		String accName ="郑和进";
		String bankName = "中国银行";
		String channelId ="2";
		String tel = "15901009876";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		info.setAccType(accType);
		info.setBankName(bankName);
		info.setBanlance(money);
		info.setCertNo(certNo);
		info.setCertType(certType);
		info.setChannelId(channelId);
		info.setTel(tel);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setSign(sign);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.238/PayCutServer/dhb/bankCardRealName");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void fourRealName(){
		String trano = "Pan_test"+System.currentTimeMillis();
//		String merchId ="222301000000001";
		String merchId ="111301000000000";
		String accNo = "6259618065834107";
		String accName ="杨既";
		String certNo ="510504198412072510";
		String tel = "18281179550";
		String channelId ="11";
//		String businessType="1";
		String businessType="";
		String key = merchId+trano;
		
		
		//double money =100.00;
		//String accNo = "6212250200009623735";//蘭
		//String accNo="6228840101812164";
		//String certNo = "130925198310145611";
		//String certNo ="110102198012293032";//lan
		//String certType ="01";
		//String accType ="00";
		//String accName = "李猛";
		//String accName = "兰世韬";
		//String bankName = "中国银行";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		info.setAccName(accName);
		info.setAccNo(accNo);
		//info.setAccType(accType);
		//info.setBankName(bankName);
		//info.setBanlance(money);
		info.setCertNo(certNo);
		//info.setCertType(certType);
		info.setChannelId(channelId);
		info.setTel(tel);
		info.setComments("测试");
		info.setMerchId(merchId);
		info.setBusinessType(businessType);
		info.setSign(sign);
		info.setTranNo(trano);
		Gson g = new Gson();
		String context= g.toJson(info);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/fourRealName");
		param.setUrl("http://218.240.148.238/PayCutServer/dhb/fourRealName");
//		param.setUrl("http://localhost:8080/PayCutServer/dhb/fourRealName");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	public void shortMessage_test(){
		Map<String,String> map = new HashMap<String, String>();
		String short_message_url = PropFileUtil.getByFileAndKey("short_message.properties", "short_message_url");
		map.put("mobiles", "15010001161");
		map.put("flag", "1");
		map.put("randnum", "123456");
		Gson g = new Gson();
		String json= g.toJson(map);
		
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(short_message_url);
		Map<String,String> heads = Maps.newHashMap();
		HttpHelp send = new HttpHelp();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
		
	}	
	public void shortMessage(){
		Map<String,String> map = new HashMap<String, String>();
		map.put("tranNo", System.currentTimeMillis()+"");
		map.put("mobiles", "15010001161");
		map.put("flag", "1");
		map.put("type", "0");
		Gson g = new Gson();
		String json= g.toJson(map);
		
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhbmobile/shortMessage");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpHelp send = new HttpHelp();
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * anyz单笔代收
	 */
	public void anyzSingleCut(){
		String tranNo = "Pantest"+System.currentTimeMillis()+"";
		String merchId ="222301000000001";
		String channelId ="11";
		String bankName = "中国工商银行总行";
		String accNo = "6222020200057200491";
		//String accName = "潘泳辰";
		String accName = "";
		String accType ="00";
		String bankProvince = "";
		String bankCity = "";
		double banlance =0.5;
		String currency = "CNY";
		String certType ="01";
//		String certNo ="230206198906121115";
		String certNo ="";
//		String tel = "15010001161";
		String tel = "";
		String bankCode =  "01020000";
		String remark = "测试爱农驿站生产环境";
		String businessType = "1";
		
		String key = merchId+String.format("%.2f", banlance)+accNo+tranNo;
		System.out.println("key="+key);
		OutRequestInfo info = new OutRequestInfo();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		System.out.println("sign="+sign);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("tranNo",tranNo);
		map.put("merchId",merchId);
		map.put("channelId",channelId);
		map.put("bankName",bankName);
		map.put("accNo",accNo);
		map.put("accName",accName);
		map.put("accType",accType);
		map.put("bankProvince",bankProvince);
		map.put("bankCity",bankCity);
		map.put("banlance",String.format("%.2f", banlance));
		map.put("currency",currency);
		map.put("certType",certType);
		map.put("certNo",certNo);
		map.put("bankCode",bankCode);
		map.put("remark",remark);
		map.put("businessType", businessType);
		map.put("sign", sign);
		
		String json = JsonUtil.getMapToJson(map);
	
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/singleCut");
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/singleCut");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
		
	}
	/**
	 * anyz单笔代付
	 */
	public void anyzSinglePay(){
		String tranNo = "Pantest"+System.currentTimeMillis()+"";
		String merchId ="222301000000001";
		String channelId ="11";
		String protocolNo = "";
		String bankName = "广发银行股份有限公司南京分行营业部";
		String bankCode = "03060000";
//		String accNo = "6222020200057200491";
		String accNo = "136001512010007877";
		
//		String accName = "潘泳辰";
		String accName = "北京中互联科技有限公司";
		String accType ="01";
		
		String bankProvince = "";
		String bankCity = "";
		double banlance =0.01;
		String currency = "CNY";
		String certType ="01";
		String certNo ="230206198906121115";
		String mobile = "15010001161";
		String remark = "测试爱农驿站生产环境";
		String businessType = "1";
		
		String key = merchId+String.format("%.2f", banlance)+accNo+tranNo;
		System.out.println("key="+key);
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		System.out.println("sign="+sign);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("tranNo",tranNo);
		map.put("merchId",merchId);
		map.put("channelId",channelId);
		map.put("protocolNo",protocolNo);
		map.put("bankName",bankName);
		map.put("bankCode",bankCode);
		map.put("accNo",accNo);
		map.put("accName",accName);
		map.put("accType",accType);
		map.put("bankProvince",bankProvince);
		map.put("bankCity",bankCity);
		map.put("banlance",String.format("%.2f", banlance));
		map.put("currency",currency);
		map.put("certType",certType);
		map.put("certNo",certNo);
		map.put("businessType", businessType);
		map.put("mobile",mobile);
		map.put("remark",remark);
		map.put("sign", sign);
		
		String json = JsonUtil.getMapToJson(map);
	
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer254/dhb/singlePay");
//		param.setUrl("http://218.240.148.180:8084/PayCutServer254/dhb/singlePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
		
	}
	
	/**
	 * 单笔代收代付查询
	 */
	public void SingleCutPaySelect(){
		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("tranNo", "Pantest1466389073851");
//		map.put("merchId", "111301000000004");
//		map.put("channelId", "3");
		map.put("tranNo", "Pantest1469445725018");//Pantest1469445784150
		map.put("merchId", "222301000000001");
		map.put("channelId", "11");
		String key = map.get("merchId").toString()+map.get("tranNo").toString();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		map.put("sign", sign);

		String json = JsonUtil.getMapToJson(map);
		
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/queryTranStatus");
//		param.setUrl("http://localhost:8080/PayCutServer/dhb/queryTranStatus");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	/**
	 * ysb单笔代收子协议号录入
	 * @throws UnsupportedEncodingException 
	 */
	public void ysbCutContract(){
		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("tranNo", "Pantest1466389073851");
//		map.put("merchId", "111301000000004");
//		map.put("channelId", "3");
		map.put("tranNo","PanTest"+System.currentTimeMillis());
		map.put("merchId","111301000000000");
		map.put("channelId","3");
		map.put("accNo","6222020200057200491");
		map.put("accName","潘泳辰");
		map.put("certNo","230206198906121115");
		map.put("mobile","15010001161");
		map.put("startDate","20160629");
		map.put("endDate","20170325");

		String key = map.get("merchId").toString()+map.get("tranNo").toString();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		map.put("sign", sign);

		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/singleCutContract");
//		param.setUrl("http://localhost:8080/PayCutServer/dhb/singleCutContract");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	/**
	 * ysb子协议延期接口
	 */
	public void subContractExtension(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("tranNo","PanTest"+System.currentTimeMillis());
		map.put("merchId","111301000000000");
		map.put("channelId","3");
		map.put("subContractId", "773752");
		map.put("startDate", "20170101");
		map.put("endDate", "20170601");
		String key = map.get("merchId").toString()+map.get("tranNo").toString();
		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
		map.put("sign", sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/subContractExtension");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/subContractExtension");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	public static String getaaa(int length){
		char[] cResult = new char[length];
		int[] flag =  { 0 , 0 , 0 };
		int i = 0;
		while(flag[0] == 0 || flag[1] == 0 || flag[2] == 0 || i < length){
			i = i % length;
			int f = (int) (Math.random() * 3 % 3);
			if(f == 0){
				cResult[i] = (char) ('A' + Math.random() * 26);
			}else if(f == 1){
				cResult[i] = (char) ('a' + Math.random() * 26);
			}else{
				cResult[i] = (char) ('0' + Math.random() * 10);
			}
			flag[f] = 1;
			i++;
		}
		
		return new String(cResult);
		
	}
	 public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	 public static final String LETTERCHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	 public static String generateMixString(int length) {
		  StringBuffer sb = new StringBuffer();
		  Random random = new Random();
		  for (int i = 0; i < length; i++) {
		   sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
		  }
		  return sb.toString();
		 }
	public static void main(String[] args) throws UnsupportedEncodingException {
		/**
		 * 余额查询
		 */
//		Map<String,String> map = new LinkedHashMap<String,String>();
//		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
//		String sign = YSBUtil.getAssembleSign(map);
//		System.out.println("待签名数据："+sign);
//		String mac = YSBUtil.GetMD5Code(sign);
//		map.put("mac",mac);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+"/delegatePay/queryBlance", map);
//		System.out.println("通道方返回的数据："+msg);

		StartMain start = new StartMain();
		
//		System.out.println(generateMixString(10));
//		System.out.println(Math.random() * 26);
		
//		start.subContractExtension();
		
//		System.out.println(URLDecoder.decode("%E5%AF%B9%E4%B8%8D%E8%B5%B7%EF%BC%8C%E8%AE%A2%E5%8D%95%E7%8A%B6%E6%80%81%E5%BC%82%E5%B8%B8.", "UTF-8"));
		start.nfcPay();
//		start.nfcChannelPay();
//		start.qjMerchJoin();
//		start.nfcQjTransBill();
//		start.qjNotifyUrl();
//		start.notifyUrl();
//		start.wecatActivePay();
//		start.wecatTradeRefund();
//		start.wecatReversal();
//		start.nfcQuery();
//		start.notifyUrl();
		////start.singlePayCGB();
//	    start.RealName();
//		start.fourRealName();
//		start.bankCardRealName();
//		start.shortMessage();
//		start.anyzSingleCut();
//		start.anyzSinglePay();
//		start.singlePayForJYT();
//		String merorderId=Tools.getUUID();//银行订单号
//		start.quickPay(merorderId);
//		start.sendsms("c800e484c6304c5bb5b4c396292b5bbc");
//		start.payConfirm("123456","c800e484c6304c5bb5b4c396292b5bbc");
//		start.queryOrder();
		
//		String dateString = "2016-08-10 17:33:47";
//		try {
//			Date date = DateUtil.strToDate(dateString);
//			Date endTime = new Date(date.getTime()+300000);
//			String cratetime = DateUtil.formatYYYYMMDDHHMMSS(endTime);
//			System.out.println(cratetime);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
//	public void quickPay(String merorderId){
//		String merchId="111301000000000";
//		
//		String accNo="6217000010038223692";//账号 银行卡号
//		Integer txnAmt= 1;//交易金额 分
//		String payFlag="1";//1借记卡 2信用卡
//		String certify_id="420624199109111824";//身份证号码
//		String customerNm="王小威";//姓名
//		String phoneNo="13021985911";//手机号
////		String expired="";//有效期
////		String cvv2="";//
//		String backUrl="http://106.2.217.58:443/PayCutServer/AnBackReturnServlet";//
//		
//		String key = merchId+merorderId+accNo;
//		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
//		
//		QuickRequestInfo info = new QuickRequestInfo();
//		info.setMerchId(merchId);
//		info.setMerOrderId(merorderId);
//		info.setAccNo(accNo);
//		info.setTxnAmt(txnAmt);
//		info.setPayFlag(payFlag);
//		info.setCertify_id(certify_id);
//		info.setCustomerNm(customerNm);
//		info.setTel(phoneNo);
////		info.setCvv2(cvv2);
////		info.setExpired(expired);
//		info.setBackUrl(backUrl);
//		info.setSign(sign);
////		info.setBankId("01050000");
//		
//		Gson g = new Gson();
//		String context= g.toJson(info);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://localhost:8080/PayCutServer/dhbquick/quickPay");
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "application/json;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println(resp.getContent());
//		
//	}
//
//	public void sendsms(String merorderId){
//		String merchId="111301000000000";
//		//String merorderId="377c55603ee14bb69dce00511d6443ba";
//		
//		String key = merchId+merorderId;
//		String sign =MD5.encrypt(key,"6m0gqnng1vv0wfes");
//		
//		QuickRequestInfo info = new QuickRequestInfo();
//		info.setMerchId(merchId);
//		info.setMerOrderId(merorderId);
//		info.setSign(sign);
//		
//		Gson g = new Gson();
//		String context= g.toJson(info);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://localhost:8080/PayCutServer/dhbquick/sendSMS");
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "application/json;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println(resp.getContent());
//		
//	}
//	public void payConfirm(String smsCode,String merorderId){
//		String merchId="111301000000000";
////		String merorderId="a0b228a7be2f444eab84b74eebc888f0";
////		String smsCode = "123456";
//		
//		String key = merchId+merorderId+smsCode;
//		String sign = MD5.encrypt(key, "6m0gqnng1vv0wfes");
//		
//		QuickRequestInfo info = new QuickRequestInfo();
//		info.setMerchId(merchId);
//		info.setMerOrderId(merorderId);
//		info.setSmsCode(smsCode);
//		info.setSign(sign);
//		
//		Gson g = new Gson();
//		String context= g.toJson(info);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://localhost:8080/PayCutServer/dhbquick/payConfirm");
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "application/json;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println(resp.getContent());
//		
//	}
//	public void queryOrder(){
//		String merchId="111301000000000";
//		String merorderId="c800e484c6304c5bb5b4c396292b5bbc";
//		
//		String key = merchId+merorderId;
//		String sign = MD5.encrypt(key, "6m0gqnng1vv0wfes");
//		
//		QuickRequestInfo info = new QuickRequestInfo();
//		info.setMerchId(merchId);
//		info.setMerOrderId(merorderId);
//		info.setSign(sign);
//		
//		Gson g = new Gson();
//		String context= g.toJson(info);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://localhost:8080/PayCutServer/dhbquick/queryOrder");
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "application/json;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println(resp.getContent());
//		
//	}

	/**
	 * nfc支付接口
	 */
	public void nfcPay(){
		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("sub_merch_no","111111000000000");
		map.put("merch_no","801100053991123");
		map.put("merch_channel","hrt");
		//map.put("clearType","0");
		map.put("nfc_type",com.dhb.nfc.entity.Constants.nfc_passive);
		map.put("nfc_merch",com.dhb.nfc.entity.Constants.wechat_nfc_merch);
		map.put("product_name","测试和融通扫码");
		map.put("product_desc","测试和融通扫码");
		map.put("total_fee","0.01");
		map.put("merch_rate","0.038");
		map.put("currency","CNY");
		map.put("notify_url","http://www.baidu.com");
		map.put("remark","测试和融通扫码");
//		map.put("auth_code","");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			System.out.println(waitSign.getBytes("UTF-8"));
			System.out.println(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "7eefcd6f7976ff11");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcPay");
//		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcPay");
//		param.setUrl("http://218.240.148.254/PayCutServer/dhb/nfcPay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * nfc通道支付
	 */
	public void nfcChannelPay(){
		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("sub_merch_no","111111000000000");
		map.put("merch_no","QT201701090001");
		map.put("merch_channel","qj");
		map.put("nfc_merch",com.dhb.nfc.entity.Constants.alipay_nfc_merch);
		map.put("total_fee","0.01");
		map.put("merch_rate","0.039");
		map.put("currency","CNY");
		map.put("notify_url","http://www.baidu.com");
		map.put("remark","测试二维码支付通道");
		map.put("clearType","0");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
//		sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			System.out.println(waitSign.getBytes("UTF-8"));
			System.out.println(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "7eefcd6f7976ff11");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcChannelPay");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcChannelPay");
//		param.setUrl("http://218.240.148.254/PayCutServer/dhb/nfcPay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * 全晶商户进件儿和商户资料修改接口
	 */
	public void qjMerchJoin(){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		dataMap.put("order_no","PanTest"+System.currentTimeMillis());
		dataMap.put("sub_merch_no","111111000000000");
		dataMap.put("mId", "111111");//商户ID，由全通支付分配：1、 此参数为空时，为新商户进件。2、 此参数不为空时，为修改商户资料。
		dataMap.put("merchantId", "112233445566778899");//合作方商户编号，由合作方分配。
		dataMap.put("merchantName", "测试");//商户名称，请与营业执照上的名称一致。
		dataMap.put("merchantAddress", "北京市朝阳区大郊亭桥");//商户地址
		dataMap.put("merchantType", "1");//商户类型：0—个人，1—公司
		dataMap.put("categoryForAlipay", "2016062900190300");//支付宝经营类目ID，参见“支付宝经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
		dataMap.put("categoryForWeChat", "19");//微信经营类目ID，参见“微信经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
		dataMap.put("contractName", "东汇宝支付有限公司");//当merchantType=0 时，为收款人姓名当merchantType=1 时，为公司全称
		dataMap.put("idCard", "");//商户身份证号码（当merchantType=0 时，此项必填，银行系统风控监管需要）
		dataMap.put("merchantLicense", "4561687684564986468468798748");//商户营业执照号码 （当merchantType=1 时，此项必填，银行系统风控监管需要）
		dataMap.put("accName", "东汇宝支付有限公司");//收款人账户名
		dataMap.put("bankName", "中国人民银行天津分行营业管理部");//收款人开户行名称
		dataMap.put("bankId", "﻿1110002774");//收款人开户行联行号
		dataMap.put("bankNumber", "11223344556677");//收款人银行帐号
		dataMap.put("mobileForBank", "15010001161");//收款人银行预留手机号码
		dataMap.put("t0DrawFee", "0.2");//T0 单笔交易手续费，如0.2 元/笔则填0.2
		dataMap.put("t0TradeRate", "0.006");//T0 交易手续费扣率，如0.6%笔则填0.006
		dataMap.put("t1DrawFee", "0.2");//T1 单笔交易手续费，如0.2 元/笔则填0.2
		dataMap.put("t1TradeRate", "0.006");//T1 交易手续费扣率，如0.6%笔则填0.006
		String waitSign = QrCodeUtil.getBuildPayParams(dataMap);
		String sign = "";
		try {
			System.out.println(waitSign.getBytes("UTF-8"));
			System.out.println(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "7eefcd6f7976ff11");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataMap.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(dataMap);
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcQjMerchJoin");
//		param.setUrl("http://localhost:8080/PayCutServer254/dhb/nfcQjMerchJoin");
//		param.setUrl("http://218.240.148.254/PayCutServer/dhb/nfcQjMerchJoin");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * 全晶对账数据重导接口
	 */
	public void nfcQjTransBill(){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		dataMap.put("order_no","PanTest"+System.currentTimeMillis());
		dataMap.put("sub_merch_no","111111000000000");
		dataMap.put("date_time","20170301");
		String waitSign = QrCodeUtil.getBuildPayParams(dataMap);
		String sign = "";
		try {
			System.out.println(waitSign.getBytes("UTF-8"));
			System.out.println(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "7eefcd6f7976ff11");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataMap.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(dataMap);
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcQjTransBill");
//		param.setUrl("http://localhost:8080/PayCutServer254/dhb/nfcQjTransBill");
//		param.setUrl("http://218.240.148.254/PayCutServer/dhb/nfcQjMerchJoin");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * 微信主扫支付接口
	 */
	public void wecatActivePay(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("refund_oeder_no", "");
		map.put("merch_no","888301000000001");
		map.put("nfc_type",com.dhb.nfc.entity.Constants.nfc_active);
		map.put("nfc_merch",com.dhb.nfc.entity.Constants.wechat_nfc_merch);
		map.put("total_fee","0.01");
		map.put("refund_fee","");
		map.put("refund_channe","");
		map.put("commodity_describe","微信主扫支付测试1");
		map.put("auth_code","130671673436510897");
		map.put("currency","CNY");
		map.put("notify_url","");
		map.put("remark","微信主扫支付测试1");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "6b37adb8641debea");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/subContractExtension");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcActivePay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * 微信退货接口
	 */
	public void wecatTradeRefund(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("refund_order_no", "PanTest1476261953759");
		map.put("merch_no","888301000000001");
		map.put("nfc_type",com.dhb.nfc.entity.Constants.nfc_refund);
		map.put("nfc_merch",com.dhb.nfc.entity.Constants.wechat_nfc_merch);
		map.put("total_fee","0.01");
		map.put("refund_fee","0.01");
		map.put("currency","CNY");
		map.put("remark","微信退货测试");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "6b37adb8641debea");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcTradeRefund");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcTradeRefund");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	/**
	 * 微信冲正接口
	 */
	public void wecatReversal(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("order_no","PanTest"+System.currentTimeMillis());
		map.put("reversal_order_no","PanTest1470797311614");
		map.put("merch_no","888301000000001");
		map.put("nfc_type",com.dhb.nfc.entity.Constants.nfc_reversal);
		map.put("nfc_merch",com.dhb.nfc.entity.Constants.wechat_nfc_merch);
		map.put("currency","CNY");
		map.put("remark","微信冲正测试1");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "6b37adb8641debea");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		System.out.println(waitSign);
		System.out.println(sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/subContractExtension");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcReversal");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	
	public void nfcQuery(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("order_no","PanTest1472456855374");
		map.put("sub_merch_no","111111000000000");
		map.put("merch_no","888301000000001");
		map.put("nfc_type",com.dhb.nfc.entity.Constants.nfc_query);
		map.put("nfc_merch","wechat");//com.dhb.nfc.wecat.entity.Constants.wecat_nfc_merch
		map.put("currency","CNY");
		map.put("remark","");
		String waitSign = QrCodeUtil.getBuildPayParams(map);
		String sign = "";
		//sign = YSBUtil.GetMD5Code(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")));
		try {
			sign = MD5.encrypt(EncodeUtils.base64Encode(waitSign.getBytes("UTF-8")), "7eefcd6f7976ff11");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("sign",sign);
		String json = JsonUtil.getMapToJson(map);
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/nfcQuery");
		param.setUrl("http://localhost:8080/PayCutServer/dhb/nfcQuery");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	
	/**
	 *模拟全晶一码付异步通知接收地址
	 */
	public void qjNotifyUrl(){
		String json = "{\"code\":0,\"data\":\"897ec6e9d13f374b426b365f715fbab0088ef8e9933c539d09a22658f28dfb5a9c09ff9ccca9230c0dac6c8cb07407364c9c0eb9112428f77560f7f9085a56bf01663006eb123f51d3fd207acb8d299e41b9ffeb9e81cd35c8e2eebc2d090ea262be9dbffef85fdbbb8cbbe3f36796b5762247b5838b539f1ab8146c24f4e84aa937007195fb1ef8d62e89cb2ba08722\",\"sign\":\"dbfdce85c978fb9b2ae78824c9b933da\",\"msg\":\"success\"}";
		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://localhost:8080/PayCutServer254/dhb/qjNotifyUrl");
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/qjNotifyUrl");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "text/plain;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	
	/**
	 * 模拟异步通知
	 */
	public void notifyUrl(){
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("tranNo","PanTest"+System.currentTimeMillis());
//		map.put("nfcPayType","0");
//		map.put("sign", "12345678");
//		map.put("out_transaction_id","bbb");
//		//String json = JsonUtil.getMapToJson(map);
//		String xml = XmlUtils.getMapToXml(map);
		String xml = "<xml><bank_type><![CDATA[CFT]]></bank_type>"+
				"<charset><![CDATA[UTF-8]]></charset>"+
				"<fee_type><![CDATA[CNY]]></fee_type>"+
				"<is_subscribe><![CDATA[N]]></is_subscribe>"+
				"<mch_id><![CDATA[7551000001]]></mch_id>"+
				"<nonce_str><![CDATA[1470130754997]]></nonce_str>"+
				"<openid><![CDATA[oywgtuKWbuy3crsXWtPEQfKl4x_E]]></openid>"+
				"<out_trade_no><![CDATA[pan_test_1470130571085]]></out_trade_no>"+
				"<out_transaction_id><![CDATA[4007962001201608020388472036]]></out_transaction_id>"+
				"<pay_result><![CDATA[0]]></pay_result>"+
				"<result_code><![CDATA[0]]></result_code>"+
				"<sign><![CDATA[B264E336D9934D528866DE987DB23CDD]]></sign>"+
				"<sign_type><![CDATA[MD5]]></sign_type>"+
				"<status><![CDATA[0]]></status>"+
				"<time_end><![CDATA[20160802173914]]></time_end>"+
				"<total_fee><![CDATA[1]]></total_fee>"+
				"<trade_type><![CDATA[pay.weixin.native]]></trade_type>"+
				"<transaction_id><![CDATA[7551000001201608024004858153]]></transaction_id>"+
				"<version><![CDATA[2.0]]></version>"+
				"</xml>";

		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://218.240.148.180:8084/PayCutServer/dhb/wecatNotifyUrl");
//		param.setUrl("http://123.125.77.168:8080/PayCutServer/dhb/wecatNotifyUrl");
//		param.setUrl("http://localhost:8080/PayCutServer/dhb/wecatNotifyUrl");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(map);
		param.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		System.out.println(resp.getContent());

	}
	
}
