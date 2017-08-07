package com.dhb.nfc.service;

import java.util.Map;

import org.apache.log4j.Logger;

import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;

public class CcbQrCodePayService {
	Logger logger = Logger.getLogger(CcbQrCodePayService.class);
	/**
	 * 中信扫码支付
	 * @param mapParam
	 * @return
	 */
	public Map<String,Object> qrCodePay(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.pay.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return JsonUtil.getJsonToMap(resp.getContent());
	}
	/**
	 * 中信扫码交易退货
	 * @param mapParam
	 * @return
	 */
	public Map<String,Object> qrCodeTradeRefund(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.refund.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return JsonUtil.getJsonToMap(resp.getContent());
	}
	/**
	 * 中信分账子账户入驻
	 * @param mapParam
	 * @return
	 */
	public Map<String,Object> merchJoin(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.merchJoin.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return JsonUtil.getJsonToMap(resp.getContent());
	}
	/**
	 * 中信分账子账户查询
	 * @param mapParam
	 * @return
	 */
	public Map<String,Object> merchSearch(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.merchSearch.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return JsonUtil.getJsonToMap(resp.getContent());
	}
	/**
	 * 中信对账文件下载接口
	 */
	public String transBill(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.transBill.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return resp.getContent();
	}
	/**
	 * 中信通道支付
	 * @param mapParam
	 * @return
	 */
	public String channelPay(Map<String,Object> mapParam){
		String json = JsonUtil.getMapToJson(mapParam);
		String url = YSBUtil.getReadProperties("sub_nfc","ccb.channelPay.url");
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(json);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户的报文："+json);
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info("订单号："+mapParam.get("order_no")+"发送商户结果："+resp.getContent());
		return resp.getContent();
	}
	
}
