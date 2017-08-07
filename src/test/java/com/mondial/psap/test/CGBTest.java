package com.mondial.psap.test;

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

public class CGBTest {
	@Test
	public void testsinglePayCGB(){
		String merchId ="111301000000001";
		double money =1.00;
//		String accNo = "9550880200064300139";//对公
//		String accNo = "6214620421000001874";//对私 
		String accNo ="6216610100001856976"; //行外
		String trano = Tools.getUUID();
		String key = merchId+String.format("%.2f", money)+accNo+trano;
		String certNo ="16307129-2";
		String certType ="30";
		String accType ="01";//00：对私 01：对公
		String accName = "显什蚜呆GCH靖靖徊船";//对公
//		String accName = "郝蛮替";//对私
//	    String accName = "收款企业";//行外
//		String bankName = "中国工商银行";
		String bankName ="广发银行";
		String channelId ="1";
		String bankCode ="102100099996";
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
	@Test
	public void testsBatchPayCGB(){
		BatchTranReq batchRea = new BatchTranReq();
		String batchId = Tools.getUUID();
		batchRea.setBatchId(batchId);
		batchRea.setChannelId("1");
		batchRea.setTotalBalance(1.00);
		batchRea.setTotalNum(1);
		String merchId ="111301000000000";
		batchRea.setMerchId(merchId);
		double money =1.00;
//	   String accNo = "9550880200064300139";//对公
		String accNo = "9550880200139300176";//对私
		//String accNo ="1234567890"; //行外
		String trano = Tools.getUUID();
		String key = merchId+money+"0"+accNo+trano;
		String certNo ="16307129-2";
		String certType ="30";
		String accType ="01";
//		String accName = "显什蚜呆GCH靖靖徊船";//对公
		String accName = "UAT银企客户接入027";//对私
	   // String accName = "收款企业";//行外
		//String bankName = "兴业银行";
		String bankName ="广发银行";
		String channelId ="1";
		String bankCode ="102100099996";
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
		
	
//	     OutRequestInfo info1 = new OutRequestInfo();
//	 	double money1 =1.00;
//	   //String accNo = "135001513010000518";//对公
//		String accNo1 = "6214620421000001874";//对私
////		String accNo1 ="1234567890"; //行外
//		String trano1 = Tools.getUUID();
//		String key1 = merchId+money1+"0"+accNo1+trano1;
//		String certNo1 ="16307129-2";
//		String certType1 ="30";
//		String accType1 ="00";
//		//String accName = "李氏长江实业";//对公
//		String accName1 = "郝蛮替";//对私
////	   String accName1 = "收款企业";//行外
////		String bankName1 = "兴业银行";
//		String bankName1 ="广发银行";
//		String channelId1 ="1";
//		String bankCode1 ="309391000011";
//	 	batchRea.getInfo().add(info1);
//		String sign1 =MD5.encrypt(key1,"6m0gqnng1vv0wfes");
//		info1.setAccName(accName1);
//		info1.setAccNo(accNo1);
//		info1.setAccType(accType1);
//		info1.setBankName(bankName1);
//		info1.setBanlance(money1);
//		info1.setCertNo(certNo1);
//		info1.setCertType(certType1);
//		info1.setChannelId(channelId1);
//		info1.setComments("测试");
//		info1.setMerchId(merchId);
//		info1.setSign(sign1);
//		info1.setBankCode(bankCode1);
//		info1.setTranNo(trano1);
		Gson g = new Gson();
		String context= g.toJson(batchRea);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl("http://localhost:8080/PayCutServer/dhb/batchPay");
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		System.out.println(resp.getContent());
	}
	@Test
	public void testquery(){
		String merchId ="111301000000000";
		double money =1.00;
		String accNo = "9550880200064300139";
		String trano = "69fea95af9d4450282caddfee8388e18";
		String key = merchId+trano;
		String certNo ="16307129-2";
		String certType ="30";
		String accType ="00";
		String accName = "显什蚜呆GCH靖靖徊船";//对公
		String bankName ="广发银行";
		String channelId ="1";
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
}
