package com.dhb.cip.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;

public class CIPUtils {
	/**
	 * 参数规则排序
	 * @param map
	 */
	public static String getBuildPayParams(Map<String,String> map){
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		String s = "";
		for(String key : keys){
			if(map.get(key) != null && !map.get(key).equals("") && !map.get(key).equals("null")){
				if(!map.get(key).equals("\"null\"")){
					if(!key.equals("sign")&&!key.equals("signType")){
						s += key + "=" + map.get(key) + "&";
					}
				}
			}
        }
		return s.substring(0, s.length()-1);
	}
	/**
	 * 参数规则排序
	 * @param map
	 */
	public static String getObjectBuildPayParams(Map<String,Object> map){
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		String s = "";
		for(String key : keys){
			if(map.get(key) != null && !map.get(key).equals("") && !map.get(key).equals("null")){
				if(!map.get(key).equals("\"null\"")){
					if(!key.equals("sign")&&!key.equals("signType")){
						s += key + "=" + map.get(key) + "&";
					}
				}
			}
        }
		return s.substring(0, s.length()-1);
	}
	
	public void singlePay(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String orderId = System.currentTimeMillis()+"";
		Map<String,String> map = new HashMap<String,String>();
		map.put("serviceName", Constants.cip_serviceName_agentSinglePay);//服务名称
		map.put("version", Constants.cip_version);//版本号
		map.put("platform", "");//平台标示
		map.put("merchantId", YSBUtil.getReadProperties("cip", "cip.merchantId"));//商户号
		map.put("payType", Constants.cip_payType_16);//支付类型
		map.put("signType", Constants.cip_signMethod_RSA);//签名类型
		map.put("charset", Constants.cip_charset_UTF8);//参数编码字符集
		map.put("merBatchNo", orderId);//商户批次号
		map.put("txnTime", sdf.format(new Date()));//批次提交时间
		map.put("payeeAcct", "6214854513335397");//收款人账号
		map.put("payeeName", "潘泳辰");//收款人名称
		map.put("applyAmount", "0.01");//代付申请金额
		map.put("applyReason", "");//附言
		map.put("bankName", "招商银行股份有限公司");//开户行名称
		map.put("bankCode", "CMB");//开户行编号
		map.put("bankProvince", "");//开户行编号
		map.put("bankCity", "");//开户行市
		map.put("bankBranchName", "");//支行名称
		try {
			String waitSign = CIPUtils.getBuildPayParams(map);
			System.out.println("waitSign="+waitSign);
			String sign = RSA.sign(waitSign, YSBUtil.getReadProperties("cip", "cip.privateKey"), Constants.cip_charset_UTF8);
			System.out.println("sign="+sign);
			map.put("sign", sign);//签名
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = ANYZUtil.sendMsg(YSBUtil.getReadProperties("cip", "cip.singlePay.url"), map);
		System.out.println("result="+result);
		
		Map<String,String> respMap = JsonUtil.getJsonToMapStr(result);
		
		try {
//			System.out.println(RSA.getPlainText(respMap));
//			System.out.println(respMap.get("sign"));
			Boolean verifyResult = RSA.verifySign(CIPUtils.getBuildPayParams(respMap), respMap.get("sign"), YSBUtil.getReadProperties("cip", "cip.publicKey"), Constants.cip_charset_UTF8);
			System.out.println("verifyResult="+verifyResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void singlePayQuery(){
		String orderId = "1490608951627";
		Map<String,String> map = new HashMap<String,String>();
		map.put("serviceName", Constants.cip_serviceName_queryAgentSinglePay);//服务名称
		map.put("version",Constants.cip_version);//版本号
		map.put("platform",Constants.cip_version);//平台标示
		map.put("merchantId",YSBUtil.getReadProperties("cip", "cip.merchantId"));//商户号
		map.put("signType",Constants.cip_signMethod_RSA);//签名类型
		map.put("charset",Constants.cip_charset_UTF8);//编码字符集
		map.put("merBatchNo",orderId);//商户批次号
		try {
			String waitSign = CIPUtils.getBuildPayParams(map);
			System.out.println("waitSign="+waitSign);
			String sign = RSA.sign(waitSign, YSBUtil.getReadProperties("cip", "cip.privateKey"), Constants.cip_charset_UTF8);
			System.out.println("sign="+sign);
			map.put("sign", sign);//签名
			String result = ANYZUtil.sendMsg(YSBUtil.getReadProperties("cip", "cip.singlePay.query.url"), map);
			System.out.println("result="+result);
			Map<String,Object> respMap = JsonUtil.getJsonToMap(result);
			if(respMap.get("sign") !=null && !"".equals(respMap.get("sign").toString())){
				System.out.println("!!!!!="+CIPUtils.getObjectBuildPayParams(respMap));
				Boolean verifyResult = RSA.verifySign(CIPUtils.getObjectBuildPayParams(respMap), respMap.get("sign").toString(), YSBUtil.getReadProperties("cip", "cip.publicKey"), Constants.cip_charset_UTF8);
				System.out.println("verifyResult="+verifyResult);
			}else{
				System.out.println("retCode="+respMap.get("retCode"));
				System.out.println("retMsg="+respMap.get("retMsg"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CIPUtils cip = new CIPUtils();
		cip.singlePayQuery();

	}

}
