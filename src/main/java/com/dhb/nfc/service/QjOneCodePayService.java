package com.dhb.nfc.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.KeyInfoDao;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcMerchToPayMerch;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.service.ValidateService;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.util.MD5;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;

public class QjOneCodePayService {
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Resource
	private ValidateService validateService;
	@Autowired
	private KeyInfoDao keyInfoDao;
	
	public static Logger logger = Logger.getLogger(QjOneCodePayService.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 全晶一码付接口
	 * @param mapParam
	 * @return
	 */
	public Map<String,Object> oneCodePay(Map<String,Object> mapParam){
		Map<String,Object> jsonRetMap = null;
    	//保存订单流水表
    	NfcOrderWater water = this.getShareOrderWater(mapParam);
    	water.setStatus(Constants.nfc_pay_status_3);
		water.setMessage(Constants.nfc_pay_status_3_context);
    	//计算应收手续费
    	NumberFormat nf = new DecimalFormat("########.######");
    	Double merch_fee = Double.valueOf(water.getTotalFee()) * Double.valueOf(mapParam.get("merch_rate").toString());
    	water.setMerchFee(nf.format(merch_fee));
		water.setEndTime(null);
    	dhbBizJournalDao.addNFCorderWater(water);
    	//组装传递给通道方的报文
    	String orderId = water.getMerOrderNo();
		int fee = Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee()));//分
		String time = sdf.format(new Date());
    	Map<String,Object> dataMap = new HashMap<String, Object>();
		dataMap.put("orderId", orderId);
		dataMap.put("fee", fee);
		dataMap.put("time", time);
		dataMap.put("clearType", mapParam.get("clearType"));
		String data = QrCodeUtil.getBuildPayParams(dataMap);
		logger.info("(全晶一码付接口：)订单号："+water.getOrderNo()+",data数据："+data);
		logger.info("(全晶一码付接口：)订单号："+water.getOrderNo()+",data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		logger.info("(全晶一码付接口：)订单号："+water.getOrderNo()+",sign加密数据："+MD5.encrypt(data));
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("mId", water.getMerchNo());
		map.put("data", AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		map.put("sign", MD5.encrypt(data));
		map.put("resType", YSBUtil.getReadProperties("nfc","qj.resType"));
		String json = JsonUtil.getMapToJson(map);
		logger.info("(全晶一码付接口：)订单号："+water.getOrderNo()+",发送给通道的数据："+json);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(YSBUtil.getReadProperties("nfc","qj.url"));
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "text/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		logger.info("(全晶一码付接口：)订单号："+water.getOrderNo()+",通道方返回结果json："+resp.getContent());
		Map<String,Object> retMap = JsonUtil.getJsonToMap(resp.getContent());
		if(retMap.get("code").toString().equals("0")){//接口调用成功
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_3,Constants.nfc_pay_status_3_context);
			jsonRetMap.put("code_url", "");
			jsonRetMap.put("code_img_url",retMap.get("url").toString());
			jsonRetMap.put("merch_fee", water.getMerchFee());
		}else{//接口调用失败
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,retMap.get("msg").toString());
			water.setStatus(Constants.nfc_pay_status_9);
			water.setMessage(retMap.get("msg").toString());
		}
		//最后时间
		String endDate = DateUtil.format(new Date());
		try {
			water.setEndTime(DateUtil.strToDate(endDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		validateService.getUpateNfcOrderWater(water,null);
		return jsonRetMap;
	}
	/**
	 * 全晶通道支付接口
	 */
	public String channelPay(Map<String,Object> mapParam){
		//保存订单流水表
    	NfcOrderWater water = this.getShareOrderWater(mapParam);
    	//计算应收手续费
    	NumberFormat nf = new DecimalFormat("########.######");
    	Double merch_fee = Double.valueOf(water.getTotalFee()) * Double.valueOf(mapParam.get("merch_rate").toString());
    	water.setStatus(Constants.nfc_pay_status_3);
		water.setMessage(Constants.nfc_pay_status_3_context);
    	water.setMerchFee(nf.format(merch_fee));
		water.setEndTime(null);
    	dhbBizJournalDao.addNFCorderWater(water);
    	//组装传递给通道方的报文
    	String orderId = water.getMerOrderNo();
		int fee = Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee()));//分
		String time = sdf.format(new Date());
		int payType = 9;
		if(water.getNfcMerch().equals(Constants.wechat_nfc_merch)){
			payType = 0;
		}
		if(water.getNfcMerch().equals(Constants.alipay_nfc_merch)){
			payType = 1;
		}
    	Map<String,Object> dataMap = new HashMap<String, Object>();
		dataMap.put("orderId", orderId);
		dataMap.put("fee", fee);
		dataMap.put("time", time);
		dataMap.put("payType", payType);
		dataMap.put("clearType", mapParam.get("clearType"));
		String data = QrCodeUtil.getBuildPayParams(dataMap);
		logger.info("(全晶公众号支付接口：)订单号："+water.getOrderNo()+",data数据："+data);
		logger.info("(全晶公众号支付接口：)订单号："+water.getOrderNo()+",data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc", "qj.key")));
		logger.info("(全晶公众号支付接口：)订单号："+water.getOrderNo()+",sign加密数据："+MD5.encrypt(data));
		Map<String,String> map = new LinkedHashMap<String, String>();
		map.put("mId", water.getMerchNo());
		map.put("data", AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc", "qj.key")));
		map.put("sign", MD5.encrypt(data));
		map.put("resType", YSBUtil.getReadProperties("nfc","qj.resType"));
		map.put("url", YSBUtil.getReadProperties("nfc","qj.channelPayUrl"));
		String json = JsonUtil.getMapToJsonStr(map);
		logger.info("(全晶公众号支付接口：)订单号："+water.getOrderNo()+",发送给通道的数据："+json);
//		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("nfc","qj.channelPayUrl"), map);
//		logger.info("(全晶公众号支付接口：)订单号："+water.getOrderNo()+",通道方返回结果html："+msg);
		return json;
	}
	/**
	 * 全晶商户进件或者修改商户资料接口
	 */
	public Map<String,Object> MerchJoin(Map<String,Object> mapParam){
		Map<String,Object> jsonRetMap = null;
		Map<String,Object> dataMap = new HashMap<String, Object>();
		dataMap.put("mId", mapParam.get("mId"));//商户ID，由全通支付分配：1、 此参数为空时，为新商户进件。2、 此参数不为空时，为修改商户资料。
		dataMap.put("merchantId", mapParam.get("merchantId"));//合作方商户编号，由合作方分配。
		dataMap.put("merchantName", mapParam.get("merchantName"));//商户名称，公司类型商户：商户名称必须与营业执照上主体一致。个人类型商户：商户名称必须为“个体户+法人名称”。
		dataMap.put("shortName", mapParam.get("shortName"));//商户简称，公司类型商户：以公司名称命名、以公司商标或商标加其他关键字命名。个人类型商户：以营业执照名称命名、以法人名称+销售商品命名、以实体店店名命名。
		dataMap.put("merchantAddress", mapParam.get("merchantAddress"));//商户地址
		dataMap.put("merchantType", mapParam.get("merchantType"));//商户类型：0—个人，1—公司
		dataMap.put("categoryForAlipay", mapParam.get("categoryForAlipay"));//支付宝经营类目ID，参见“支付宝经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
		dataMap.put("categoryForWeChat", mapParam.get("categoryForWeChat"));//微信经营类目ID，参见“微信经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
		dataMap.put("contractName", mapParam.get("contractName"));//当merchantType=0 时，为收款人姓名当merchantType=1 时，为为公司营业执照主体名称
		dataMap.put("idCard", mapParam.get("idCard"));//商户身份证号码（当merchantType=0 时，此项必填，银行系统风控监管需要）
		dataMap.put("merchantLicense", mapParam.get("merchantLicense"));//商户营业执照号码 （当merchantType=1 时，此项必填，银行系统风控监管需要）
		dataMap.put("accName", mapParam.get("accName"));//收款人账户名
		dataMap.put("bankName", mapParam.get("bankName"));//收款人开户行名称
		dataMap.put("bankId", mapParam.get("bankId"));//收款人开户行联行号
		dataMap.put("bankNumber", mapParam.get("bankNumber"));//收款人银行帐号
		dataMap.put("mobileForBank", mapParam.get("mobileForBank"));//收款人银行预留手机号码
		dataMap.put("t0DrawFee", mapParam.get("t0DrawFee"));//T0 单笔交易手续费，如0.2 元/笔则填0.2
		dataMap.put("t0TradeRate", mapParam.get("t0TradeRate"));//T0 交易手续费扣率，如0.6%笔则填0.006
		dataMap.put("t1DrawFee", mapParam.get("t1DrawFee"));//T1 单笔交易手续费，如0.2 元/笔则填0.2
		dataMap.put("t1TradeRate", mapParam.get("t1TradeRate"));//T1 交易手续费扣率，如0.6%笔则填0.006
		dataMap.put("provinceCode", mapParam.get("provinceCode"));//省份编码
		dataMap.put("cityCode", mapParam.get("cityCode"));//城市编码
		dataMap.put("districtCode", mapParam.get("districtCode"));//县区编码
		String data = QrCodeUtil.getBuildPayParams(dataMap);
		logger.info("(全晶商户进件或者修改商户资料接口：)订单号："+mapParam.get("order_no")+",data数据："+data);
		logger.info("(全晶商户进件或者修改商户资料接口：)订单号："+mapParam.get("order_no")+",data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		logger.info("(全晶商户进件或者修改商户资料接口：)订单号："+mapParam.get("order_no")+",sign加密数据："+MD5.encrypt(data));
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));
		map.put("data",AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
		map.put("sign",MD5.encrypt(data));
		map.put("resType",YSBUtil.getReadProperties("nfc","qj.resType"));
		String json = JsonUtil.getMapToJson(map);
		logger.info("(全晶商户进件或者修改商户资料接口：)订单号："+mapParam.get("order_no")+",发送给通道的数据："+json);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(YSBUtil.getReadProperties("nfc","qj.merchJoin"));
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "text/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		logger.info("全晶商户进件或者修改商户资料接口：)订单号："+mapParam.get("order_no")+",通道返回结果json："+resp.getContent());
		Map<String,Object> jsonMap = JsonUtil.getJsonToMap(resp.getContent());
		if(jsonMap.get("code").toString().equals("0")){//表示入驻成功，同步返回全通支付商户编号mId 和合作方商户编号merchantId
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_1,Constants.nfc_pay_status_1_context);
		}else if(jsonMap.get("code").toString().equals("12")){//表示入驻结果将通过【支付结果/商户入驻结果回调通知】接口返回
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_0,jsonMap.get("msg").toString());
		}else{
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,jsonMap.get("msg").toString());
		}
		jsonRetMap.put("mId", jsonMap.get("mId"));
		jsonRetMap.put("merchantId", jsonMap.get("merchantId"));
		return jsonRetMap;
	}
	/**
	 * 全晶图片上传接口
	 */
	public Map<String,Object> QjUploadFile(Map<String,Object> mapParam){
		Map<String,Object> jsonRetMap = null;
		Map<String,String> dataMap = new HashMap<String,String>();
		dataMap.put("cooperatorId",mapParam.get("merch_no").toString());//合作方ID，由全通支付分配
		dataMap.put("merchantId",mapParam.get("merchantId").toString());//合作方商户编号，由合作方分配
		dataMap.put("mobile",mapParam.get("mobile").toString());//商户手机号码，必须与入驻接口的收款人银行预留手机号码一致
		dataMap.put("fileType",mapParam.get("fileType").toString());//证件图片文件类型：1011-身份证正面,1012-身份证反面
		dataMap.put("suffix",mapParam.get("suffix").toString());//证件图片文件扩展名(如: png,jpg)
		dataMap.put("resType",YSBUtil.getReadProperties("nfc","qj.resType"));//请求类型：0—测试环境，1—生产环境
		dataMap.put("startIndex",mapParam.get("startIndex").toString());//证件图片字节的起始索引（从0开始）
		dataMap.put("endIndex",mapParam.get("endIndex").toString());//证件图片字节的截止索引
		dataMap.put("totalLength",mapParam.get("totalLength").toString());//证件图片文件总的字节大小(即文件内容的字符串长度的一半)
		dataMap.put("content",mapParam.get("content").toString());//证件图片文件的内容(十六进制形式的字符串，所以必须是偶数位)
		dataMap.put("checkValue",mapParam.get("checkValue").toString());//1个字节的校验值，十六进制形式(对本次上传的文件内容的各个字节的进行异或的结果)
		logger.info("(全晶图片上传接口：)订单号："+mapParam.get("order_no")+",发送给通道的数据："+ANYZUtil.getWebForm(dataMap));
		String result = ANYZUtil.sendMsg(YSBUtil.getReadProperties("nfc","qj.UploadFile"),dataMap);
		logger.info("全晶图片上传接口：)订单号："+mapParam.get("order_no")+",通道返回结果json："+result);
		Map<String,Object> jsonMap = JsonUtil.getJsonToMap(result);
		if(jsonMap.get("code").toString().equals("0")){//证件图片文件上传成功
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_1,Constants.nfc_pay_status_1_context);
		}else{
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,jsonMap.get("msg").toString());
		}
		return jsonRetMap;
	}
	/**
	 * 全晶商户入驻结果查询接口
	 */
	public Map<String,Object> QjmerchJoinResult(Map<String,Object> mapParam){
		Map<String,Object> jsonRetMap = null;
		Map<String,String> dataMap = new HashMap<String, String>();
		dataMap.put("cooperatorId",mapParam.get("merch_no").toString());
		dataMap.put("merchantId",mapParam.get("merchantId").toString());
		String sign = MD5.encrypt(dataMap.get("cooperatorId")+dataMap.get("merchantId"));
		dataMap.put("sign",sign);
		dataMap.put("resType",YSBUtil.getReadProperties("nfc","qj.resType"));
		String json = JsonUtil.getMapToJsonStr(dataMap);
		logger.info("(全晶商户入驻结果查询接口：)订单号："+mapParam.get("order_no")+",发送给通道的数据："+json);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(YSBUtil.getReadProperties("nfc","qj.merchJoinResult"));
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "text/json;charset=UTF-8");
		param.setContext(json);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		logger.info("全晶商户入驻结果查询接口：)订单号："+mapParam.get("order_no")+",通道返回结果json："+resp.getContent());
		Map<String,Object> jsonMap = JsonUtil.getJsonToMap(resp.getContent());
		if(jsonMap.get("code").toString().equals("0")){//接口调用成功
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_1,Constants.nfc_pay_status_1_context);
		}else if(jsonMap.get("code").toString().equals("12")){//账号审核中
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_0,jsonMap.get("msg").toString());
		}else{
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,jsonMap.get("msg").toString());
		}
		jsonRetMap.put("mId", jsonMap.get("mId"));//全通支付商户编号mId
		jsonRetMap.put("merchantId", jsonMap.get("merchantId"));//合作方商户编号merchantId
		return jsonRetMap;
	}
	
    /**
     * 构建共用订单流水对象
     * @param mapParam
     * @return
     */
    public NfcOrderWater getShareOrderWater(Map<String,Object> mapParam){
    	NfcOrderWater water = new NfcOrderWater();
    	water.setMerOrderNo(QrCodeUtil.getUUID());
    	if(mapParam.get("order_no") != null){
    		water.setOrderNo(mapParam.get("order_no").toString());
		}
    	if(mapParam.get("refund_order_no") != null){
    		water.setRefundOrderNo(mapParam.get("refund_order_no").toString());
		}
    	if(mapParam.get("sub_merch_no") != null){
			water.setSubMerchNo(mapParam.get("sub_merch_no").toString());
		}
    	if(mapParam.get("merch_no") != null){
			water.setMerchNo(mapParam.get("merch_no").toString());
		}
		if(mapParam.get("merch_no") != null){
			water.setMerchNo(mapParam.get("merch_no").toString());
		}
		if(mapParam.get("merch_channel") != null){
			water.setMerchChannel(mapParam.get("merch_channel").toString());
		}
		if(mapParam.get("nfc_type") != null){
			water.setNfcType(mapParam.get("nfc_type").toString());
		}
		if(mapParam.get("nfc_merch") != null){
			water.setNfcMerch(mapParam.get("nfc_merch").toString());
		}
		if(mapParam.get("total_fee") != null){
			water.setTotalFee(mapParam.get("total_fee").toString());
		}
		if(mapParam.get("refund_fee") != null){
			water.setRefundFee(mapParam.get("refund_fee").toString());
		}
		if(mapParam.get("refund_channe") != null){
			water.setRefundChanne(mapParam.get("refund_channe").toString());
		}
		if(mapParam.get("currency") != null){
			water.setCurrency(mapParam.get("currency").toString());
		}
		if(mapParam.get("notify_url") != null){
			water.setNotifyUrl(mapParam.get("notify_url").toString());
		}
		if(mapParam.get("remark") != null){
			water.setRemark(mapParam.get("remark").toString());
		}
		String strDate = DateUtil.format(new Date());
		try {
			water.setCreatedTime(DateUtil.strToDate(strDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return water;
    }
}
