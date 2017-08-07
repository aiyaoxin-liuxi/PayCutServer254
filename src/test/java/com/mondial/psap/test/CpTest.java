package com.mondial.psap.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dhb.entity.BatchTranReq;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.util.HttpHelp;
import com.dhb.util.MD5;
import com.dhb.util.Tools;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class CpTest {
	@Test
	public void testsinglePayCGB(){
		String merchId ="111101000000000";
		double money =1.00;
		//String accNo = "135001513010000518";//对公
		//String accNo = "6225680221002123251";//对私
		String accNo ="6214623621000040403"; //行外
		String trano = Tools.getUUID();
		String key = merchId+String.format("%.2f", money)+accNo+trano;
		String certNo ="320924199302020863";
		String certType ="01";
		String accType ="00";
		//String accName = "李氏长江实业";//对公
		//String accName = "网银测试";//对私
	    String accName = "孙苏阳";//行外
		String bankName = "广发银行";
		//String bankName ="广发银行";
		String channelId ="2";
		String bankCode ="306584001261";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"ykw0n5149fob59xt");
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
	@Test
	public void testsBatchCutCGB(){
		BatchTranReq batchRea = new BatchTranReq();
		String batchId = Tools.getUUID();
		batchRea.setBatchId(batchId);
		batchRea.setChannelId("3");
		batchRea.setTotalBalance(2.00);
		batchRea.setTotalNum(2);
		String merchId ="111301000000000";
		batchRea.setMerchId(merchId);
		double money =1.00;
	   //String accNo = "135001513010000518";//对公
		String accNo = "6214623621000040403";//对私
		//String accNo ="1234567890"; //行外
		String trano = Tools.getUUID();
		String key = merchId+String.format("%.2f", money)+accNo+trano;
		String certNo ="320924199302020863";
		String certType ="01";
		String accType ="01";
		//String accName = "李氏长江实业";//对公
		String accName = "孙苏阳";//对私
	   // String accName = "收款企业";//行外
		//String bankName = "兴业银行";
		String bankName ="广发银行";
		String channelId ="3";
		String bankCode ="306584001261";
		OutRequestInfo info = new OutRequestInfo();
		batchRea.getInfo().add(info);
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
		
	
	     OutRequestInfo info1 = new OutRequestInfo();
	 	double money1 =1.00;
		   //String accNo = "135001513010000518";//对公
			//String accNo1 = "6225680221002123251";//对私
			String accNo1 ="6230580000034982590"; //行外
			String trano1 = Tools.getUUID();
			String key1 = merchId+String.format("%.2f", money1)+accNo1+trano1;
			String certNo1 ="320924199302020863";
			String certType1 ="01";
			String accType1 ="01";
			//String accName = "李氏长江实业";//对公
			//String accName1 = "网银测试";//对私
		   String accName1 = "孙苏阳";//行外
			String bankName1 = "平安银行";
			//String bankName1 ="广发银行";
			String channelId1 ="3";
			String bankCode1 ="103584099993";
	 	batchRea.getInfo().add(info1);
		String sign1 =MD5.encrypt(key1,"6m0gqnng1vv0wfes");
		info1.setAccName(accName1);
		info1.setAccNo(accNo1);
		info1.setAccType(accType1);
		info1.setBankName(bankName1);
		info1.setBanlance(money1);
		info1.setCertNo(certNo1);
		info1.setCertType(certType1);
		info1.setChannelId(channelId1);
		info1.setComments("测试");
		info1.setMerchId(merchId);
		info1.setSign(sign1);
		info1.setBankCode(bankCode1);
		info1.setTranNo(trano1);
		Gson g = new Gson();
		String context= g.toJson(batchRea);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/batchCut");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	@Test
	public void testquery(){
		String merchId ="111101000000000";
		double money =100.00;
		String accNo = "6217860100000372608";
		String trano = "09225102868644acb40acecb27919f97";
		String key = merchId+trano;
		String certNo ="341281198403050497";
		String certType ="01";
		String accType ="00";
		String accName = "郑和进";
		String bankName = "中国银行";
		String channelId ="2";
		OutRequestInfo info = new OutRequestInfo();
		
		String sign =MD5.encrypt(key,"ykw0n5149fob59xt");
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
	
	public static void main(String[] args) {
		String aaa = "mId=QT201701090001&orgOrderId=4271&qtOrderId=1740&orderId=9146f500284343a9b323001ef5476bd4&fee=1&time=20170111091803";
		String[] msg_ret = aaa.split("&");
		Map<String,String> msg_data = new HashMap<String, String>();
		for(int i = 0 ; i < msg_ret.length ; i++){
			String[] msg_ret_two = msg_ret[i].split("=");
			msg_data.put(msg_ret_two[0], msg_ret_two[1]);
		}
    	String orderid = msg_data.get("orderid").toString();
    	System.out.println(orderid);
    	String fee = msg_data.get("fee").toString();
    	System.out.println(fee);
	}
}
