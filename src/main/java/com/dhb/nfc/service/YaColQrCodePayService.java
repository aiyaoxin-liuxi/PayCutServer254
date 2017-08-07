package com.dhb.nfc.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.KeyInfoDao;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcMerchToPayMerch;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.service.ValidateService;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.util.XmlUtils;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;
import com.murong.ecp.app.merchant.atc.MerchantUtil;
import com.murong.ecp.app.merchant.atc.RSASignUtil;

public class YaColQrCodePayService {
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Resource
	private ValidateService validateService;
	@Autowired
	private KeyInfoDao keyInfoDao;
	
	public static Logger logger = Logger.getLogger(YaColQrCodePayService.class);
    /**
     * 二维码支付接口
     * @author pie
     * @date 2016-08-25
     * @param mapParam
     * @return
     * @throws ServletException 
     * @throws IOException 
     */
    public Map<String,Object> qrCodePay(Map<String,Object> mapParam) throws ServletException, IOException{
    	Map<String,Object> jsonRetMap = null;
    	//保存订单流水表
    	NfcOrderWater water = this.getShareOrderWater(mapParam);
    	//查询商户与通道商户对应表
		NfcMerchToPayMerch nfcMerchToPayMerch = keyInfoDao.getNfcMerchToPayMerch(water.getMerchNo(),water.getMerchChannel());
    	if(nfcMerchToPayMerch == null){
    		jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,"该商户在通道配置中未找到数据！！！请联我司运维人员");
    	}else{
    		if(mapParam.get("nfc_type").equals(Constants.nfc_passive)){
        		water.setStatus(Constants.nfc_pay_status_3);
        		water.setMessage(Constants.nfc_pay_status_3_context);
        	}
        	if(mapParam.get("nfc_type").equals(Constants.nfc_active)){
        		water.setStatus(Constants.nfc_pay_status_0);
        		water.setMessage(Constants.nfc_pay_status_0_context);
        	}
        	//计算应收手续费
        	NumberFormat nf = new DecimalFormat("########.######");
        	Double merch_fee = Double.valueOf(water.getTotalFee()) * Double.valueOf(mapParam.get("merch_rate").toString());
        	water.setMerchFee(nf.format(merch_fee));
    		water.setEndTime(null);
        	dhbBizJournalDao.addNFCorderWater(water);
        	//组装传递给通道方的报文
        	QrCodeUtil w = new QrCodeUtil();
    		Map<String,Object> map = new LinkedHashMap<String, Object>();
    		map.put("charset", YSBUtil.getReadProperties("nfc", "sdk.charset"));//字符集
    		map.put("version", Constants.wechat_version_1);//接口版本
    		map.put("signType", Constants.wechat_sing_type);//签名方式
    		map.put("service", Constants.wechat_service_newGatePayment);//业务类型
    		if(mapParam.get("nfc_type").equals(Constants.nfc_passive) && water.getNfcMerch().equals(Constants.wechat_nfc_merch)){
    			map.put("subtype", Constants.wechat_sub_type_12);//业务子类型--微信扫码支付
    		}
    		if(mapParam.get("nfc_type").equals(Constants.nfc_active) && water.getNfcMerch().equals(Constants.wechat_nfc_merch)){
    			map.put("subtype", Constants.wechat_sub_type_13);//业务子类型--微信小额支付
    		}
    		if(mapParam.get("nfc_type").equals(Constants.nfc_passive) && water.getNfcMerch().equals(Constants.alipay_nfc_merch)){
    			map.put("subtype", Constants.wechat_sub_type_22);//业务子类型--支付宝扫码支付
    		}
    		if(mapParam.get("nfc_type").equals(Constants.nfc_active) && water.getNfcMerch().equals(Constants.alipay_nfc_merch)){
    			map.put("subtype", Constants.wechat_sub_type_21);//业务子类型--支付宝被扫支付
    		}
    		map.put("offlineNotifyUrl", YSBUtil.getReadProperties("nfc", "yacol.notify_url"));//后台通知URL
    		map.put("clientIP", "");//客户端IP
    		map.put("requestId", water.getMerOrderNo());//请求号
    		map.put("purchaserId", "");//购买者标识
    		map.put("merchantId", nfcMerchToPayMerch.getPayMerchNo());//合作商户编号
    		map.put("merchantName", "");//合作商户展示名称
    		map.put("orderId", water.getMerOrderNo());//订单号
    		map.put("orderTime", DateUtil.formatYYYYMMDDHHMMSS(water.getCreatedTime()));//订单时间
    		map.put("totalAmount", Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee())));//订单总金额
    		map.put("currency", "CNY");//交易币种
    		map.put("validUnit", Constants.wechat_validUnit_00);//订单有效期单位
    		map.put("validNum", Constants.wechat_validNum);//订单有效期数量
    		
    		//////////////////////////////////////////////////////////////////////////////////////////////////////////
    		map.put("isRaw", "1");//是否为原生态
    		map.put("showUrl", "https://www.baidu.com/");//商品展示URL
    		map.put("productName", mapParam.get("product_name"));//商品名称
    		map.put("productId", "");//商品编码
    		map.put("productDesc", mapParam.get("product_desc"));//商品描述
    		map.put("backParam", "");//原样返回的商户数据
    		map.put("limitCreditPay", "");//是否限制信用卡
    		map.put("authCode", mapParam.get("auth_code"));//授权码
    		//组装请求参数待加密字符串
    		String getWatiSign = QrCodeUtil.getBuildPayParams(map);
    		String merCert = nfcMerchToPayMerch.getPayMerchNo() + ".p12";
    		//获取容器中加密证书位置
    		String merchantCertPath = w.init()+"\\"+YSBUtil.getReadProperties("nfc","sdk.merchantCertPath")+merCert;//MerchantConfig.getConfig().getMerchantCertPath();
    		merchantCertPath = merchantCertPath.replace("/", "\\");
    		merchantCertPath = merchantCertPath.replace(" ", "");
    		/**
    		 * 在LINUX下，此方法不可注释
    		 */
    		merchantCertPath = "/"+merchantCertPath.replace("\\", "/");
    		
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",获取容器中加密证书位置："+merchantCertPath);
    		//获取证书密码
    		String merchantCertPass = nfcMerchToPayMerch.getPayMerchKey();
    		RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);
    		String merchantSign = util.sign(getWatiSign,"GBK");//商户签名
    		String merchantCert = util.getCertInfo();//商户证书
    		//组装好准备发送的报文
    		String sendStr = getWatiSign + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
    		//发起http请求，并获取响应报文
    		String requestUrl = YSBUtil.getReadProperties("nfc","sdk.requestUrl");
    		String charset = YSBUtil.getReadProperties("nfc","sdk.charset");
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",发送给通道的数据："+sendStr);
            String result = MerchantUtil.sendAndRecv(requestUrl, sendStr, charset);
            logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",通道方返回结果："+result);
      		//拼写待验签的字符串
      		Map<String,Object> retMap = QrCodeUtil.getBuildRetParamsMap(util, result);
      		String waitStr = QrCodeUtil.getBuildPayParams(retMap);
      		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",拼写待验签的字符串："+waitStr);
      		//验证签名
    		boolean flag = false;
    		//获取容器中公钥证书的位置
    		String rootCertPath = w.init() + YSBUtil.getReadProperties("nfc","sdk.rootCertPath");
    		/**
    		 * 在LINUX下，此方法不可注释
    		 */
    		rootCertPath = "/"+rootCertPath.replace("\\", "/");
    		
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",获取容器中公钥证书的位置："+rootCertPath);
    		RSASignUtil rsautil = new RSASignUtil(rootCertPath);
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",验证签名（待验签数据）："+waitStr);
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",验证签名（服务器对报文签名的签名结果）："+retMap.get("serverSign").toString());
    		logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+",验证签名（服务器端的签名证书）："+retMap.get("serverCert").toString());
    	    flag = rsautil.verify(waitStr,retMap.get("serverSign").toString(),retMap.get("serverCert").toString(),"GBK");
    	    if (!flag) {
    	    	logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+"验签结果："+flag+",错误信息：验签错误");
    	    	jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,"通道返回参数验签失败");
    		}else{
    			logger.info("(雅酷时空微信支付接口：)订单号："+water.getOrderNo()+"验签结果："+flag);
    			//验签成功后进行业务判断
    			if(retMap.get("returnCode").equals("000000") && retMap.get("status").equals("SUCCESS")){//成功（对被扫只是生产可支付的二维码生成成功）
    				if(mapParam.get("nfc_type").equals(Constants.nfc_passive)){
    					water.setStatus(Constants.nfc_pay_status_3);
    					water.setMessage(Constants.nfc_pay_status_3_context);
    					jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),water.getStatus(),water.getMessage());
    					jsonRetMap.put("code_url", retMap.get("codeUrl"));
    					jsonRetMap.put("code_img_url", retMap.get("codeImageUrl"));
    					jsonRetMap.put("merch_fee", water.getMerchFee());
    				}
    				if(mapParam.get("nfc_type").equals(Constants.nfc_active)){
    					water.setStatus(Constants.nfc_pay_status_1);
    					water.setMessage(Constants.nfc_pay_status_1_context);
    					jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),water.getStatus(),water.getMessage());
    					jsonRetMap.put("merch_fee", water.getMerchFee());
    				}
    			}else{//失败
    				water.setStatus(Constants.nfc_pay_status_9);
    				water.setMessage(URLDecoder.decode(retMap.get("returnMessage").toString(), "UTF-8"));
    				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),water.getStatus(),water.getMessage());
    			}
    			//最后时间
    			String endDate = DateUtil.format(new Date());
    			try {
    				water.setEndTime(DateUtil.strToDate(endDate));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			validateService.getUpateNfcOrderWater(water,null);
    		}
    	}
//    	Map<String,Object> jsonRetMap = null;
//    	//保存订单流水表
//    	NfcOrderWater water = this.getShareOrderWater(mapParam);
//		water.setStatus(Constants.nfc_pay_status_3);
//		water.setMessage(Constants.nfc_pay_status_3_context);
//		water.setEndTime(null);
//    	dhbBizJournalDao.addNFCorderWater(water);
//    	//组装传递给通道方的报文
//    	Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_passive_pay);//接口类型
//		map.put("version", Constants.wechat_version_1 );//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", water.getMerOrderNo());//商户订单号--- 交易流水表ID
//		map.put("device_info", "");//设备号
//		map.put("body", mapParam.get("commodity_describe"));//商品描述mapParam
//		map.put("attach", "");//附加信息
//		map.put("total_fee", Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee())));//总金额mapParam
//		map.put("mch_create_ip", WechatUtil.getNetAddress());//终端IP
//		map.put("notify_url", YSBUtil.getReadProperties("nfc", "wecat_notify_url"));//通知地址
//		map.put("time_start", DateUtil.formatYYYYMMDDHHMMSS(water.getCreatedTime()));//订单生成时间
//		Date lastDate = new Date(water.getCreatedTime().getTime()+300000);
//		map.put("time_expire", DateUtil.formatYYYYMMDDHHMMSS(lastDate));//订单超时时间
//		map.put("op_user_id", "");//操作员
//		map.put("goods_tag", "");//商品标记
//		map.put("product_id", "");//商品ID
//		map.put("nonce_str", WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信被扫接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		//调用通道
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信被扫接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信被扫接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//交易成功
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_3,Constants.nfc_pay_status_3_context);
//				jsonRetMap.put("code_url", Xmlmap.get("code_url"));
//				jsonRetMap.put("code_img_url", Xmlmap.get("code_img_url"));
//			}else{//交易失败
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("err_msg").toString());
//			}
//		}else{//通讯失败
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("message").toString());
//		}
    	return jsonRetMap;
    }
    /**
     * 二维码主扫支付接口
     * @param mapParam
     * @return
     */
//    public Map<String,Object> wecatActivePay(Map<String,Object> mapParam){
//    	Map<String,Object> jsonRetMap = null;
//    	//保存订单流水表
//    	NfcOrderWater water = this.getShareOrderWater(mapParam);
//		water.setStatus(Constants.nfc_pay_status_0);
//		water.setMessage(Constants.nfc_pay_status_0_context);
//		water.setEndTime(null);
//    	dhbBizJournalDao.addNFCorderWater(water);
//    	//组装传递给通道方的报文
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wechat_active_pay);//接口类型
//		map.put("version",Constants.wechat_version_2);//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("groupno","");
//		map.put("out_trade_no", water.getMerOrderNo());//商户订单号
//		map.put("deviceInfo","");//终端设备号
//		map.put("body",mapParam.get("commodity_describe"));//商品描述mapParam
//		map.put("attach","");//商户附加信息
//		map.put("total_fee",Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee())));//总金额mapParam
//		map.put("mch_create_ip",WechatUtil.getNetAddress());//订单生成的机器IP
//		map.put("auth_code",mapParam.get("auth_code"));//扫码支付授权码
//		map.put("timeStart","");//订单生成时间
//		map.put("timeExpire","");//订单失效时间
//		map.put("opUserId","");//操作员帐号,默认为商户号
//		map.put("opShopId","");//门店编号
//		map.put("opDeviceId","");//设备编号
//		map.put("goodsTag","");//商品标记
//		map.put("nonce_str",WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信主扫接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		//调用通道
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信主扫接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信主扫接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//业务交易成功
//				if(Xmlmap.get("pay_result").toString().equals("0")){//支付成功
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context);
//					jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_1,Constants.nfc_pay_status_1_context);
//				}else{//支付失败
//					water.setStatus(Constants.nfc_pay_status_9);
//					water.setMessage(map.get("pay_info").toString());
//					jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("pay_info").toString());
//				}
//			}else{//业务交易失败
//				water.setStatus(Constants.nfc_pay_status_9);
//				water.setMessage(map.get("err_msg").toString());
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("err_msg").toString());
//			}
//		}else{//通讯失败
//			water.setStatus(Constants.nfc_pay_status_9);
//			water.setMessage(map.get("message").toString());
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("message").toString());
//		}
//		//最后时间
//		String endDate = DateUtil.format(new Date());
//		try {
//			water.setEndTime(DateUtil.strToDate(endDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		validateService.getUpateNfcOrderWater(water);
//    	return jsonRetMap;
//    }
    /**
     * 二维码冲正接口
     * @param mapParam
     * 当支付返回失败，或收银系统超时需要取消交易，可以调用该接口。接口逻辑： 支付失败的关单，支付成功的撤销支付。
     * 注意：24 小时内的订单才可以冲正，其他正常支付的单如需实现相同功能请调用退款接口。
     * @return
     */
//    public Map<String,Object> wecatReversal(Map<String,Object> mapParam){
//    	//如果通讯类型为退货，需要根据用户传过来的退货订单号去订单日志流水表中查找对应的通道订单号
//		NfcOrderWater dw = validateService.getNfcOrderWater(null, mapParam.get("reversal_order_no").toString());
//		if(dw == null){
//			return JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,mapParam.get("reversal_order_no").toString()+"：此订单不存在！");
//		}
//    	Map<String,Object> jsonRetMap = null;
//    	//保存订单流水表
//    	NfcOrderWater water = this.getShareOrderWater(mapParam);
//    	water.setRefundOrderNo(dw.getMerOrderNo());
//		water.setStatus(Constants.nfc_pay_status_0);
//		water.setMessage(Constants.nfc_pay_status_0_context);
//		water.setEndTime(null);
//    	dhbBizJournalDao.addNFCorderWater(water);
//    	//组装传递给通道方的报文
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wechat_reversal);//接口类型
//		map.put("version",Constants.wechat_version_2);//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", water.getRefundOrderNo());//商户订单号
//		map.put("nonce_str",WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信冲正接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		//调用通道
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信主扫接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信主扫接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//业务交易成功
//				water.setStatus(Constants.nfc_pay_status_4);
//				water.setMessage(Constants.nfc_pay_status_4_context);
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_4,Constants.nfc_pay_status_4_context);
//			}else{//业务交易失败
//				water.setStatus(Constants.nfc_pay_status_9);
//				water.setMessage(Xmlmap.get("err_msg").toString());
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("err_msg").toString());
//			}
//		}else{//通讯失败
//			water.setStatus(Constants.nfc_pay_status_9);
//			water.setMessage(Xmlmap.get("message").toString());
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("message").toString());
//		}
//		//最后时间
//		String endDate = DateUtil.format(new Date());
//		try {
//			water.setEndTime(DateUtil.strToDate(endDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		validateService.getUpateNfcOrderWater(water);
//    	return jsonRetMap;
//    } 
    /**
     * 二维码退货接口
     * @author pie
     * @date 2016-08-26
     * @param mapParam
     * @return
     * @throws ServletException 
     * @throws IOException 
     */
    public Map<String,Object> qrCodeTradeRefund(Map<String,Object> mapParam) throws ServletException, IOException{
    	Map<String,Object> jsonRetMap = null;
    	//如果通讯类型为退货，需要根据用户传过来的退货订单号去订单日志流水表中查找对应的通道订单号
		NfcOrderWater dw = validateService.getNfcOrderWater(null, mapParam.get("refund_order_no").toString());
		if(dw == null){
			return JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,mapParam.get("refund_order_no").toString()+"：此订单不存在！");
		}
		//查询商户与通道商户对应表
		NfcMerchToPayMerch nfcMerchToPayMerch = keyInfoDao.getNfcMerchToPayMerch(dw.getMerchNo(),dw.getMerchChannel());
    	if(nfcMerchToPayMerch == null){
    		jsonRetMap = JsonUtil.getReturnNFCMessageHead(dw.getOrderNo(),Constants.nfc_pay_status_9,"该商户在通道配置中未找到数据！！！请联我司运维人员");
    	}else{
    		//保存订单流水表
        	NfcOrderWater water = this.getShareOrderWater(mapParam);
        	water.setRefundOrderNo(dw.getMerOrderNo());
    		water.setStatus(Constants.nfc_pay_status_2);
    		water.setMessage(Constants.nfc_pay_status_2_context);
    		water.setEndTime(null);
        	dhbBizJournalDao.addNFCorderWater(water);
        	//组装传递给通道方的报文
        	QrCodeUtil w = new QrCodeUtil();
    		Map<String,Object> map = new LinkedHashMap<String, Object>();
    		map.put("charset", YSBUtil.getReadProperties("nfc", "sdk.charset"));//字符集
    		map.put("version", Constants.wechat_version_1);//接口版本
    		map.put("signType", Constants.wechat_sing_type);//签名方式
    		map.put("service", Constants.wechat_service_OrderRefund);//业务类型
    		map.put("requestId", water.getRefundOrderNo());//请求号
    		map.put("merchantId", nfcMerchToPayMerch.getPayMerchNo());//合作商户编号
    		map.put("orderId", water.getRefundOrderNo());//订单号
    		map.put("refundAmount", Integer.parseInt(ANYZUtil.fromYuanToFen(water.getRefundFee())));//退款金额
    		//组装请求参数待加密字符串
    		String getWatiSign = QrCodeUtil.getBuildPayParams(map);
    		String merCert = nfcMerchToPayMerch.getPayMerchNo() + ".p12";
    		//获取容器中加密证书位置
    		String merchantCertPath = w.init()+"\\"+YSBUtil.getReadProperties("nfc","sdk.merchantCertPath")+merCert;//MerchantConfig.getConfig().getMerchantCertPath();
    		merchantCertPath = merchantCertPath.replace("/", "\\");
    		merchantCertPath = merchantCertPath.replace(" ", "");
    		/**
    		 * 在LINUX下，此方法不可注释
    		 */
    		merchantCertPath = "/"+merchantCertPath.replace("\\", "/");
    		
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",获取容器中加密证书位置："+merchantCertPath);
    		//获取证书密码
    		String merchantCertPass = nfcMerchToPayMerch.getPayMerchKey();
    		RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);
    		String merchantSign = util.sign(getWatiSign,"GBK");//商户签名
    		String merchantCert = util.getCertInfo();//商户证书
    		//组装好准备发送的报文
    		String sendStr = getWatiSign + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
    		//发起http请求，并获取响应报文
    		String requestUrl = YSBUtil.getReadProperties("nfc","sdk.requestUrl");
    		String charset = YSBUtil.getReadProperties("nfc","sdk.charset");
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",发送给通道的数据："+sendStr);
            String result = MerchantUtil.sendAndRecv(requestUrl, sendStr, charset);
            logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",通道方返回结果："+result);
            //拼写待验签的字符串
      		Map<String,Object> retMap = QrCodeUtil.getBuildRetParamsMap(util, result);
      		String waitStr = QrCodeUtil.getBuildPayParams(retMap);
      		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",拼写待验签的字符串："+waitStr);
      		//验证签名
    		boolean flag = false;
    		//获取容器中公钥证书的位置
    		String rootCertPath = w.init() + YSBUtil.getReadProperties("nfc","sdk.rootCertPath");
    		/**
    		 * 在LINUX下，此方法不可注释
    		 */
    		rootCertPath = "/"+rootCertPath.replace("\\", "/");
    		
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",获取容器中公钥证书的位置："+rootCertPath);
    		RSASignUtil rsautil = new RSASignUtil(rootCertPath);
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",验证签名（待验签数据）："+waitStr);
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",验证签名（服务器对报文签名的签名结果）："+retMap.get("serverSign").toString());
    		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",验证签名（服务器端的签名证书）："+retMap.get("serverCert").toString());
    	    flag = rsautil.verify(waitStr,retMap.get("serverSign").toString(),retMap.get("serverCert").toString(),"GBK");
    	    if (!flag) {
    	    	logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+"验签结果："+flag+",错误信息：验签错误");
    	    	jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,"通道返回参数验签失败");
    		}else{
    			logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+"验签结果："+flag);
    			//验签成功后进行业务判断
    			if(retMap.get("returnCode").equals("000000") && retMap.get("status").equals("SUCCESS")){//成功
    				water.setStatus(Constants.nfc_pay_status_2);
    				water.setMessage(Constants.nfc_pay_status_2_context);
    				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),water.getStatus(),water.getMessage());
    			}else{//失败
    				water.setStatus(Constants.nfc_pay_status_9);
    				water.setMessage(URLDecoder.decode(retMap.get("returnMessage").toString(), "UTF-8"));
    				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),water.getStatus(),water.getMessage());
    			}
    			//最后时间
    			String endDate = DateUtil.format(new Date());
    			try {
    				water.setEndTime(DateUtil.strToDate(endDate));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			validateService.getUpateNfcOrderWater(water,null);
    		}
    	}
    	return jsonRetMap;
//    	//如果通讯类型为退货，需要根据用户传过来的退货订单号去订单日志流水表中查找对应的通道订单号
//		NfcOrderWater dw = validateService.getNfcOrderWater(null, mapParam.get("refund_order_no").toString());
//		if(dw == null){
//			return JsonUtil.getReturnNFCMessageHead(mapParam.get("order_no").toString(),Constants.nfc_pay_status_9,mapParam.get("refund_order_no").toString()+"：此订单不存在！");
//		}
//    	Map<String,Object> jsonRetMap = null;
//    	//保存订单流水表
//    	NfcOrderWater water = this.getShareOrderWater(mapParam);
//    	water.setRefundOrderNo(dw.getMerOrderNo());
//		water.setStatus(Constants.nfc_pay_status_0);
//		water.setMessage(Constants.nfc_pay_status_0_context);
//		water.setEndTime(null);
//    	dhbBizJournalDao.addNFCorderWater(water);
//    	//组装传递给通道方的报文
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wechat_trade_refund);//接口类型
//		map.put("version",Constants.wechat_version_2);//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", water.getRefundOrderNo());//商户订单号
//		map.put("transaction_id", "");//酷宝支付订单号
//		map.put("out_refund_no", water.getMerOrderNo());//商户退款单号
//		map.put("total_fee",Integer.parseInt(ANYZUtil.fromYuanToFen(water.getTotalFee())));//总金额
//		map.put("refund_fee",Integer.parseInt(ANYZUtil.fromYuanToFen(water.getRefundFee())));//退款金额
//		map.put("op_user_id",YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//操作员
//		map.put("refund_channe",water.getRefundChanne());//退款渠道 ORIGINAL-原路退款，BALANCE-余额
//		map.put("nonce_str", WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		//调用通道
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信退货接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//业务交易成功
//				water.setStatus(Constants.nfc_pay_status_2);
//				water.setMessage(Constants.nfc_pay_status_2_context);
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_2,Constants.nfc_pay_status_2_context);
//			}else{//业务交易失败
//				water.setStatus(Constants.nfc_pay_status_9);
//				water.setMessage(Xmlmap.get("err_msg").toString());
//				jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("err_msg").toString());
//			}
//		}else{//通讯失败
//			water.setStatus(Constants.nfc_pay_status_9);
//			water.setMessage(Xmlmap.get("message").toString());
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(water.getOrderNo(),Constants.nfc_pay_status_9,Xmlmap.get("message").toString());
//		}
//		//最后时间
//		String endDate = DateUtil.format(new Date());
//		try {
//			water.setEndTime(DateUtil.strToDate(endDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		validateService.getUpateNfcOrderWater(water);
//    	return jsonRetMap;
    } 
    /**
     * 二维码订单查询接口
     * @param mapParam
     * @return
     * @throws IOException 
     * @throws ServletException 
     */
    public void qrCodeTradeQuery(NfcOrderWater water) throws ServletException, IOException{
    	qrCodeQuery(water);
//    	//NfcOrderWater dw = validateService.getNfcOrderWater(null, water.getOrderNo());
//    	Map<String,Object> jsonRetMap = null;
//    	//组装传递给通道方的报文
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		//map.put("service",Constants.wechat_trade_query);//接口类型
//		map.put("version",Constants.wechat_version_2);//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", water.getMerOrderNo());//商户订单号
//		map.put("transaction_id", "");//酷宝支付交易号
//		map.put("nonce_str", WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		//调用通道
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//业务交易成功
//				if(Xmlmap.get("trade_state").toString().equals("SUCCESS")){//交易状态---支付成功
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context);
//				}else if(Xmlmap.get("trade_state").toString().equals("REFUND")){//交易状态---转入退款
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context_refund);
//				}else if(Xmlmap.get("trade_state").toString().equals("NOTPAY")){//交易状态---未支付
//					water.setStatus(Constants.nfc_pay_status_3);
//					water.setMessage(Constants.nfc_pay_status_3_context);
//				}else if(Xmlmap.get("trade_state").toString().equals("CLOSED")){//交易状态---已关闭
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context_close);
//				}else if(Xmlmap.get("trade_state").toString().equals("REVOKED")){//交易状态---已冲正
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context_reversal);
//				}else if(Xmlmap.get("trade_state").toString().equals("USERPAYING")){//交易状态---用户支付中
//					water.setStatus(Constants.nfc_pay_status_0);
//					water.setMessage(Constants.nfc_pay_status_0_context);
//				}else if(Xmlmap.get("trade_state").toString().equals("PAYERROR")){//交易状态---支付失败
//					water.setStatus(Constants.nfc_pay_status_9);
//					water.setMessage(Constants.nfc_pay_status_9_context);
//				}else{
//					logger.info("订单查询接口，通道方返回的交易状态："+Xmlmap.get("trade_state").toString()+"未在文档中");
//				}
//			}else{//业务交易失败
//				water.setStatus(Constants.nfc_pay_status_9);
//				water.setMessage(Xmlmap.get("err_msg").toString());
//			}
//		}else if(Xmlmap.get("status").toString().equals("400")){//您的请求过于频繁
//			//不做数据更新
//		}else{//通讯失败
//			water.setStatus(Constants.nfc_pay_status_9);
//			water.setMessage(Xmlmap.get("message").toString());
//		}
//		//最后时间
//		String endDate = DateUtil.format(new Date());
//		try {
//			water.setEndTime(DateUtil.strToDate(endDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		validateService.getUpateNfcOrderWater(water);
//    	return jsonRetMap;
    }
    /**
     * 二维码退货查询接口
     * @param mapParam
     * @return
     * @throws IOException 
     * @throws ServletException 
     */
    public void qrCodeRefundQuery(NfcOrderWater water) throws ServletException, IOException{
    	qrCodeQuery(water);
//    	Map<String,Object> jsonRetMap = null;
//    	//组装传递给通道方的报文
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		//map.put("service",Constants.wechat_refund_query);//接口类型
//		map.put("version",Constants.wechat_version_2);//版本号
//		map.put("charset", Constants.wechat_charset_02);//字符集
//		map.put("sign_type", Constants.wechat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no",water.getRefundOrderNo());//商户退货订单号
//		map.put("transaction_id", "");//酷宝支付订单号
//		map.put("out_refund_no","");//商户退款单号
//		map.put("refund_id","");//酷宝支付退款单号
//		map.put("nonce_str", WechatUtil.getUUID());//随机字符串
//		String getWatiSign = WechatUtil.getBuildPayParams(map);
//		String sign = WechatUtil.getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		logger.info("(雅酷时空微信退货订单查询接口：)订单号："+water.getOrderNo()+",组装给通道方报文："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		logger.info("(雅酷时空微信退货订单查询接口：)订单号："+water.getOrderNo()+",通道方返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		logger.info("(雅酷时空微信退货订单查询接口：)订单号："+water.getOrderNo()+",将通道返回的xml转换成map："+Xmlmap);
//		if(Xmlmap.get("status").toString().equals("0")){//通讯成功
//			if(Xmlmap.get("result_code").toString().equals("0")){//业务交易成功
//				//退款笔数
//				int refund_count = Integer.parseInt(Xmlmap.get("refund_count").toString()); 
//				logger.info("(雅酷时空微信退货订单查询接口：)订单号："+water.getMerOrderNo()+",退款笔数refund_count："+refund_count);
//				if(Xmlmap.get("refund_status_"+(refund_count-1)+"").toString().equals("SUCCESS")){//交易状态---退款成功
//					water.setStatus(Constants.nfc_pay_status_1);
//					water.setMessage(Constants.nfc_pay_status_1_context);
//				}else if(Xmlmap.get("refund_status_"+(refund_count-1)+"").toString().equals("FAIL")){//交易状态---退款失败
//					water.setStatus(Constants.nfc_pay_status_9);
//					water.setMessage(Constants.nfc_pay_status_9_context);
//				}else if(Xmlmap.get("refund_status_"+(refund_count-1)+"").toString().equals("PROCESSING")){//交易状态---退款处理中
//					water.setStatus(Constants.nfc_pay_status_2);
//					water.setMessage(Constants.nfc_pay_status_2_context);
//				}else if(Xmlmap.get("refund_status_"+(refund_count-1)+"").toString().equals("PROCESSING")){//交易状态---退款未确定
//					water.setStatus(Constants.nfc_pay_status_29);
//					water.setMessage(Constants.nfc_pay_status_29_context);
//				}else if(Xmlmap.get("refund_status_"+(refund_count-1)+"").toString().equals("CHANGE")){//交易状态---退款转入代发
//					water.setStatus(Constants.nfc_pay_status_5);
//					water.setMessage(Constants.nfc_pay_status_5_context);
//				}else{
//					logger.info("退货订单查询接口，通道方返回的交易状态："+Xmlmap.get("trade_state").toString()+"未在文档中");
//				}
//			}else{//业务交易失败
//				water.setStatus(Constants.nfc_pay_status_9);
//				water.setMessage(Xmlmap.get("err_msg").toString());
//			}
//		}else{//通讯失败
//			water.setStatus(Constants.nfc_pay_status_9);
//			water.setMessage(Xmlmap.get("message").toString());
//		}
//		//最后时间
//		String endDate = DateUtil.format(new Date());
//		try {
//			water.setEndTime(DateUtil.strToDate(endDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		validateService.getUpateNfcOrderWater(water);
//    	return jsonRetMap;
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
    /**
     * 二维码查询接口-公用方法
     * @param mapParam
     * @return
     * @throws ServletException 
     * @throws IOException 
     */
    public void qrCodeQuery(NfcOrderWater water) throws ServletException, IOException{
    	//查询商户与通道商户对应表
		NfcMerchToPayMerch nfcMerchToPayMerch = keyInfoDao.getNfcMerchToPayMerch(water.getMerchNo(),water.getMerchChannel());
    	NfcOrderWater nfcWater = new NfcOrderWater();
    	//组装传递给通道方的报文
    	QrCodeUtil w = new QrCodeUtil();
		Map<String,Object> map = new LinkedHashMap<String, Object>();
		map.put("charset", YSBUtil.getReadProperties("nfc", "sdk.charset"));//字符集
		map.put("version", Constants.wechat_version_1);//接口版本
		map.put("signType", Constants.wechat_sing_type);//签名方式
		map.put("service", Constants.wechat_service_OrderSearch);//业务类型
		if(water.getNfcType().equals(Constants.nfc_refund)){
			map.put("requestId", water.getRefundOrderNo());//请求号
			map.put("orderId", water.getRefundOrderNo());//订单号
		}else{
			map.put("requestId", water.getMerOrderNo());//请求号
			map.put("orderId", water.getMerOrderNo());//订单号
		}
		map.put("merchantId", nfcMerchToPayMerch.getPayMerchNo());//合作商户编号
		
		//组装请求参数待加密字符串
		String getWatiSign = QrCodeUtil.getBuildPayParams(map);
		String merCert = nfcMerchToPayMerch.getPayMerchNo() + ".p12";
		//获取容器中加密证书位置
		String merchantCertPath = w.init()+"\\"+YSBUtil.getReadProperties("nfc","sdk.merchantCertPath")+merCert;//MerchantConfig.getConfig().getMerchantCertPath();
		merchantCertPath = merchantCertPath.replace("/", "\\");
		merchantCertPath = merchantCertPath.replace(" ", "");
		/**
		 * 在LINUX下，此方法不可注释
		 */
		merchantCertPath = "/"+merchantCertPath.replace("\\", "/");
		
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",获取容器中加密证书位置："+merchantCertPath);
		//获取证书密码
		String merchantCertPass = nfcMerchToPayMerch.getPayMerchKey();
		RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);
		String merchantSign = util.sign(getWatiSign,"GBK");//商户签名
		String merchantCert = util.getCertInfo();//商户证书
		//组装好准备发送的报文
		String sendStr = getWatiSign + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
		//发起http请求，并获取响应报文
		String requestUrl = YSBUtil.getReadProperties("nfc","sdk.requestUrl");
		String charset = YSBUtil.getReadProperties("nfc","sdk.charset");
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",发送给通道的数据："+sendStr);
        String result = MerchantUtil.sendAndRecv(requestUrl, sendStr, charset);
        logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",通道方返回结果："+result);
        //拼写待验签的字符串
  		Map<String,Object> retMap = QrCodeUtil.getBuildRetParamsMap(util, result);
  		String waitStr = QrCodeUtil.getBuildPayParams(retMap);
  		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",拼写待验签的字符串："+waitStr);
  		//验证签名
		boolean flag = false;
		//获取容器中公钥证书的位置
		String rootCertPath = w.init() + YSBUtil.getReadProperties("nfc","sdk.rootCertPath");
		/**
		 * 在LINUX下，此方法不可注释
		 */
		rootCertPath = "/"+rootCertPath.replace("\\", "/");
		
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",获取容器中公钥证书的位置："+rootCertPath);
		RSASignUtil rsautil = new RSASignUtil(rootCertPath);
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",验证签名（待验签数据）："+waitStr);
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",验证签名（服务器对报文签名的签名结果）："+retMap.get("serverSign").toString());
		logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+",验证签名（服务器端的签名证书）："+retMap.get("serverCert").toString());
	    flag = rsautil.verify(waitStr,retMap.get("serverSign").toString(),retMap.get("serverCert").toString(),"GBK");
	    if (!flag) {
	    	logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+"验签结果："+flag+",错误信息：验签错误");
		}else{
			logger.info("(雅酷时空微信订单查询接口：)订单号："+water.getOrderNo()+"验签结果："+flag);
			//验签成功后进行业务判断
			if(retMap.get("returnCode").equals("000000")){//返回码成功
				if(retMap.get("status").equals("WP")){//等待付款
					nfcWater.setStatus(Constants.nfc_pay_status_3);
					nfcWater.setMessage(Constants.nfc_pay_status_3_context);
				}else if(retMap.get("status").equals("PD")){//支付完成
					nfcWater.setStatus(Constants.nfc_pay_status_1);
					nfcWater.setMessage(Constants.nfc_pay_status_1_context);
				}else if(retMap.get("status").equals("CZ")){//订单关闭
					nfcWater.setStatus(Constants.nfc_pay_status_1);
					nfcWater.setMessage(Constants.nfc_pay_status_1_context_close);
				}else if(retMap.get("status").equals("EX")){//订单过期
					nfcWater.setStatus(Constants.nfc_pay_status_19);
					nfcWater.setMessage(Constants.nfc_pay_status_19_context);
				}else if(retMap.get("status").equals("CA")){//交易取消
					nfcWater.setStatus(Constants.nfc_pay_status_9);
					nfcWater.setMessage(Constants.nfc_pay_status_9_context);
				}else if(retMap.get("status").equals("RQ")){//退款受理中
					nfcWater.setStatus(Constants.nfc_pay_status_2);
					nfcWater.setMessage(Constants.nfc_pay_status_2_context);
				}else if(retMap.get("status").equals("RE")){//订单退款
					nfcWater.setStatus(Constants.nfc_pay_status_1);
					nfcWater.setMessage(Constants.nfc_pay_status_1_context_refund_RE);
				}else if(retMap.get("status").equals("NE")){//订单不存在
					nfcWater.setStatus(Constants.nfc_pay_status_9);
					nfcWater.setMessage(URLDecoder.decode(retMap.get("returnMessage").toString(), "UTF-8"));
				}else if(retMap.get("status").equals("RK")){//风控拒绝
					nfcWater.setStatus(Constants.nfc_pay_status_9);
					nfcWater.setMessage(URLDecoder.decode(retMap.get("returnMessage").toString(), "UTF-8"));
				}else if(retMap.get("status").equals("RP")){//部分退款
					nfcWater.setStatus(Constants.nfc_pay_status_1);
					nfcWater.setMessage(Constants.nfc_pay_status_1_context_refund_RP);
				}else if(retMap.get("status").equals("RF")){//全部退款
					nfcWater.setStatus(Constants.nfc_pay_status_1);
					nfcWater.setMessage(Constants.nfc_pay_status_1_context_refund_RF);
				}
			}else if(retMap.get("returnCode").equals("E25856")){//此订单正在退款中
				nfcWater.setStatus(Constants.nfc_pay_status_2);
				nfcWater.setMessage(Constants.nfc_pay_status_2_context);
			}else{//失败
				nfcWater.setStatus(Constants.nfc_pay_status_9);
				nfcWater.setMessage(URLDecoder.decode(retMap.get("returnMessage").toString(), "UTF-8"));
			}
			if(!nfcWater.getStatus().equals(water.getStatus())){
				//最后时间
				String endDate = DateUtil.format(new Date());
				try {
					water.setStatus(nfcWater.getStatus());
					water.setMessage(nfcWater.getMessage());
					water.setEndTime(DateUtil.strToDate(endDate));
				} catch (Exception e) {
					e.printStackTrace();
				}
				validateService.getUpateNfcOrderWater(water,null);
	        }
		}
    }
}
