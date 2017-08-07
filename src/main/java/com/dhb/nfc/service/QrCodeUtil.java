package com.dhb.nfc.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jsoup.helper.HttpConnection.Request;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.entity.Constants;
import com.dhb.service.ValidateService;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.JsonUtil;
import com.dhb.util.MD5;
import com.dhb.util.XmlUtil;
import com.dhb.util.XmlUtils;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.murong.ecp.app.merchant.atc.MerchantConfig;
import com.murong.ecp.app.merchant.atc.RSASignUtil;

public class QrCodeUtil {
	public static Logger logger = Logger.getLogger(QrCodeUtil.class);
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
     * 获取项目所在目录
     */
    public String init() throws ServletException {
        String contentPath = this.getClass().getResource("/").getPath();
        contentPath = contentPath.substring(1, contentPath.length()-17);
        return contentPath;
    }
	/**
	 * 获得一个UUID
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}
	/**
	 * 获得指定数目的UUID
	 * @param number
	 * int 需要获得的UUID数量
	 * @return String[] UUID数组
	 */
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}
		return ss;
	}
	/**
	 * 参数规则排序
	 * @param map
	 */
	public static String getBuildPayParams(Map<String,Object> map){
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		String s = "";
		for(String key : keys){
			if(map.get(key) != null && !map.get(key).equals("")){
				if(!key.equals("sign")&&!key.equals("serverCert")&&!key.equals("serverSign")){
					s += key + "=" + map.get(key) + "&";
				}
			}
        }
		return s.substring(0, s.length()-1);
	}
	/**
	 * 同步返回验签固定规则
	 * @param map
	 */
	public static Map<String,Object> getBuildRetParamsMap(RSASignUtil util,String res){
		Map<String,Object> retMap = new LinkedHashMap<String,Object>();
        retMap.put("version",(String)util.getValue(res,"version"));
		retMap.put("charset",(String)util.getValue(res,"charset"));
		retMap.put("signType",(String)util.getValue(res,"signType"));
		retMap.put("status",(String)util.getValue(res,"status"));
		retMap.put("returnCode",(String)util.getValue(res,"returnCode"));
		retMap.put("returnMessage",(String)util.getValue(res,"returnMessage"));
		retMap.put("merchantId",(String)util.getValue(res,"merchantId"));
		retMap.put("orderId",(String)util.getValue(res,"orderId"));
		retMap.put("tradeNo",(String)util.getValue(res,"tradeNo"));
		retMap.put("codeUrl",(String)util.getValue(res,"codeUrl"));
		retMap.put("codeImageUrl",(String)util.getValue(res,"codeImageUrl"));
		retMap.put("tokenId",(String)util.getValue(res,"tokenId"));
		retMap.put("payInfo",(String)util.getValue(res,"payInfo"));
		retMap.put("appId",(String)util.getValue(res,"appId"));
		retMap.put("serverCert",(String)util.getValue(res,"serverCert"));
		retMap.put("serverSign",(String)util.getValue(res,"serverSign"));
		//退货
		retMap.put("refundAmount",(String)util.getValue(res,"refundAmount"));
		retMap.put("service",(String)util.getValue(res,"service"));
		//订单查询
		retMap.put("orderTime",(String)util.getValue(res,"orderTime"));
		retMap.put("totalAmount",(String)util.getValue(res,"totalAmount"));
		retMap.put("bankAbbr",(String)util.getValue(res,"bankAbbr"));
		retMap.put("purchaserId",(String)util.getValue(res,"purchaserId"));
		retMap.put("payTime",(String)util.getValue(res,"payTime"));
		retMap.put("fee",(String)util.getValue(res,"fee"));
		retMap.put("backParam",(String)util.getValue(res,"backParam"));
		return retMap;
	}
	
	/**
	 *  根据规则参数生成密文
	 * @param 
	 */
	public static String getSingParam(String param,String key){
		String s = param + "&key=" + key;
		return YSBUtil.GetMD5Code(s);
	}
	/**
	 * 订单生成的机器IP
	 * @param 
	 * @throws DocumentException
	 */
	public static String getNetAddress(){
		String ip = "";
		try{
			//获取计算机名称
			//String name = InetAddress.getLocalHost().getHostName(); 
			// 获取IP地址 
			ip = InetAddress.getLocalHost().getHostAddress(); 
		}catch(UnknownHostException e){
			System.out.println("异常：" + e);            
			e.printStackTrace();
		}
		return ip;
	}
	/**
	 * 发送异步通知给商户
	 * @param 目前机制只发送一次
	 */
	public static void getSendNotifyUrl(String notifyUrl,Map<String,Object> map){
		HttpRequestParam http = new HttpRequestParam();
		http.setUrl(notifyUrl);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		http.setContext(JsonUtil.getMapToJson(map));
		logger.info("异步通知发送商户的报文："+JsonUtil.getMapToJson(map));
		http.setHeads(heads);
		HttpResponser resp=HttpHelp.postParamByHttpClient(http);
		logger.info(map.get("order_no")+"异步通知发送商户结果："+resp.getContent());
	}
	/**
	 * 判断是否为json
	 * @param json
	 * @return
	 */
	public static boolean isGoodJson(String json) {    
	   try {    
	       new JsonParser().parse(json);  
	       return true;    
	   } catch (JsonParseException e) {    
		   logger.info("bad json: " + json);    
	       return false;    
	   }    
	}
	
	public static void main(String[] args) throws DocumentException, ServletException {
		// TODO Auto-generated method stub
//		WechatUtil w = new WechatUtil();
//		System.out.println(w.init());
		/**
		 * 下单请求
		 */
		//D:\j2ee\tomcat7\apache-tomcat-7.0.57\webapps\PayCutServer
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("charset", "00");//字符集
//		map.put("version", "1.0");//接口版本
//		map.put("signType", "RSA");//签名方式
//		map.put("service", "newGatePayment");//业务类型
//		map.put("subtype", "12");//业务子类型
//		map.put("offlineNotifyUrl", YSBUtil.getReadProperties("nfc", "wechat_notify_url"));//后台通知URL
//		map.put("clientIP", "");//客户端IP
//		map.put("requestId", "pan_test_"+System.currentTimeMillis());//请求号
//		map.put("purchaserId", "");//购买者标识
//		map.put("merchantId", YSBUtil.getReadProperties("nfc", "wechat_notify_url"));//合作商户编号
//		map.put("merchantName", "");//合作商户展示名称
//		map.put("orderId", "pan_test_"+System.currentTimeMillis());//订单号
//		map.put("orderTime", DateUtil.formatYYYYMMDDHHMMSS(new Date()));//订单时间
//		map.put("totalAmount", "1");//订单总金额
//		map.put("currency", "CNY");//交易币种
//		map.put("validUnit", "00");//订单有效期单位
//		map.put("validNum", "5");//订单有效期数量
//		map.put("isRaw", "");//是否为原生态
//		map.put("showUrl", "");//商品展示URL
//		map.put("productName", "测试被扫");//商品名称
//		map.put("productId", "");//商品编码
//		map.put("productDesc", "测试被扫");//商品描述
//		map.put("backParam", "");//原样返回的商户数据
//		map.put("limitCreditPay", "");//是否限制信用卡
//		map.put("authCode", "");//授权码
//		String getWatiSign = getBuildPayParams(map);
//		String merCert = YSBUtil.getReadProperties("nfc","sdk.merchantId") + ".p12";
//
//		String path = "D:\\j2ee\\tomcat7\\apache-tomcat-7.0.57\\webapps\\PayCutServer";
//		System.out.println("1:"+path);
////		String path = Class.class.getResource("/").getPath();
////		System.out.println(path);
//		//System.out.print(Class.class.getClass().getResource("/").getPath());
////		String merchantCertPath = path+"\\"+YSBUtil.getReadProperties("nfc","sdk.merchantCertPath")+merCert;//MerchantConfig.getConfig().getMerchantCertPath()
//		
//		String merchantCertPath = "D:\\j2ee\\tomcat7\\apache-tomcat-7.0.57\\webapps\\PayCutServer\\WEB-INF\\cert\\888010057120001.p12";
//		String merchantCertPass = YSBUtil.getReadProperties("nfc","sdk.merchantCertPass");
//		System.out.println("2:"+merchantCertPath);
//		System.out.println("3:"+merchantCertPass);
//		RSASignUtil util = new RSASignUtil(merchantCertPath,merchantCertPass);
//		String merchantSign = util.sign(getWatiSign,"GBK");//商户签名
//		String merchantCert = util.getCertInfo();//商户证书
//		
//		String buf = getWatiSign + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
//		System.out.println(buf);
		/**
		 * 扫描支付接口---被扫
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_passive_pay);//接口类型
//		map.put("version", Constants.wecat_version_1);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", "pan_test_"+System.currentTimeMillis());//商户订单号
//		map.put("device_info", "");//设备号
//		map.put("body", "测试微信扫码生成二维码");//商品描述
//		map.put("attach", "");//附加信息
//		map.put("total_fee", 1);//总金额
//		map.put("mch_create_ip", WecatUtil.getNetAddress());//终端IP
//		map.put("notify_url", YSBUtil.getReadProperties("nfc", "wecat_notify_url"));//通知地址
//		map.put("time_start", "");//订单生成时间
//		map.put("time_expire", "");//订单超时时间
//		map.put("op_user_id", "");//操作员
//		map.put("goods_tag", "");//商品标记
//		map.put("product_id", "");//商品ID
//		map.put("nonce_str", WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
//		String xml = "<xml><bank_type><![CDATA[CFT]]></bank_type>"
//				+ "<charset><![CDATA[UTF-8]]></charset>"
//				+ "<coupon_fee><![CDATA[0]]></coupon_fee>"
//				+ "<fee_type><![CDATA[CNY]]></fee_type>"
//				+ "<is_subscribe><![CDATA[N]]></is_subscribe>"
//				+ "<mch_id><![CDATA[7551000001]]></mch_id>"
//				+ "<nonce_str><![CDATA[1470124167106]]></nonce_str>"
//				+ "<openid><![CDATA[oywgtuKWbuy3crsXWtPEQfKl4x_E]]></openid>"
//				+ "<out_trade_no><![CDATA[pan_test_1470124064033]]></out_trade_no>"
//				+ "<out_transaction_id><![CDATA[4007962001201608020379378561]]></out_transaction_id>"
//				+ "<pay_result><![CDATA[0]]></pay_result>"
//				+ "<result_code><![CDATA[0]]></result_code>"
//				+ "<sign><![CDATA[4D80F49D75CA0EA2268193A1B65BA20D]]></sign>"
//				+ "<sign_type><![CDATA[MD5]]></sign_type>"
//				+ "<status><![CDATA[0]]></status>"
//				+ "<time_end><![CDATA[20160802154853]]></time_end>"
//				+ "<total_fee><![CDATA[1]]></total_fee>"
//				+ "<trade_type><![CDATA[pay.weixin.native]]></trade_type>"
//				+ "<transaction_id><![CDATA[7551000001201608027479136623]]></transaction_id>"
//				+ "<version><![CDATA[2.0]]></version>"
//				+ "</xml>	";
//				Map<String,Object> map = XmlUtils.getXmlToMap(xml);
//				System.out.println("map:"+map);
//				System.out.println("sign:"+map.get("sign"));
//				String waitSign = WecatUtil.getBuildPayParams(map);
//				System.out.println("waitSign:"+waitSign);
//				String signParam = WecatUtil.getSingParam(waitSign);
//				System.out.println("signParam:"+signParam);
		
		/**
		 * 扫描支付接口---主扫
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_active_pay);//接口类型
//		map.put("version",Constants.wecat_version_2);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("groupno","");
//		map.put("out_trade_no", "pan_test_"+System.currentTimeMillis());//商户订单号
//		map.put("deviceInfo","");//终端设备号
//		map.put("body","微信扫码主扫测试");//商品描述
//		map.put("attach","");//商户附加信息
//		map.put("total_fee",1);//totalFee
//		map.put("mch_create_ip",WecatUtil.getNetAddress());//订单生成的机器IP
//		map.put("auth_code","130013935697536109");//扫码支付授权码
//		map.put("timeStart","");//订单生成时间
//		map.put("timeExpire","");//订单失效时间
//		map.put("opUserId","");//操作员帐号,默认为商户号
//		map.put("opShopId","");//门店编号
//		map.put("opDeviceId","");//设备编号
//		map.put("goodsTag","");//商品标记
//		map.put("nonce_str",WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		/**
		 * 冲正接口 当支付返回失败，或收银系统超时需要取消交易，可以调用该接口。接口逻辑： 支付失败的关单，支付成功的撤销支付。注意：24 小时内的订单才可以冲正，其他正常支付的单如需实现相同功能请调用退款接口。
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_reversal);//接口类型
//		map.put("version",Constants.wecat_version_2);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", "pan_test_1470190651322");//商户订单号
//		map.put("nonce_str",WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		/**
		 * 订单查询接口
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_trade_query);//接口类型
//		map.put("version",Constants.wecat_version_2);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no", "pan_test_1470190651322");//商户订单号
//		map.put("transaction_id", "");//酷宝支付交易号
//		map.put("nonce_str", WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		/**
		 * 退款接口
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_trade_refund);//接口类型
//		map.put("version",Constants.wecat_version_2);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no","pan_test_1470130571085");//商户订单号
//		map.put("transaction_id", "");//酷宝支付订单号
//		map.put("out_refund_no","pan_test_"+System.currentTimeMillis());//商户退款单号
//		map.put("total_fee",1);//总金额
//		map.put("refund_fee",1);//退款金额
//		map.put("op_user_id",YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//操作员
//		map.put("refund_channe","ORIGINAL");//退款渠道 ORIGINAL-原路退款，BALANCE-余额
//		map.put("nonce_str", WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		/**
		 * 退款查询接口
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("service",Constants.wecat_refund_query);//接口类型
//		map.put("version",Constants.wecat_version_2);//版本号
//		map.put("charset", Constants.wecat_charset);//字符集
//		map.put("sign_type", Constants.wecat_sing_type);//签名方式
//		map.put("mch_id", YSBUtil.getReadProperties("nfc", "wecat_mch_id"));//商户号
//		map.put("out_trade_no","pan_test_1470130571085");//商户订单号
//		map.put("transaction_id", "");//酷宝支付订单号
//		map.put("out_refund_no","");//商户退款单号
//		map.put("refund_id","");//酷宝支付退款单号
//		map.put("nonce_str", WecatUtil.getUUID());//随机字符串
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","wecat_req_url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		
		/**
		 * 和融通  扫码支付（获取二维码） 有异步通知
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("unno",YSBUtil.getReadProperties("nfc","hrt.unno"));
//		map.put("payway","WXPAY");
//		map.put("mid",YSBUtil.getReadProperties("nfc","hrt.mid"));
//		map.put("orderid",YSBUtil.getReadProperties("nfc","hrt.unno")+QrCodeUtil.getUUID().substring(0,26));
//		map.put("amount","0.01");
//		map.put("subject","测试和融通二维码被扫——公众号接口");
//		map.put("desc","测试和融通二维码被扫-公众号接口");
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign,YSBUtil.getReadProperties("nfc","hrt.key"));
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		System.out.println("通道(和融通)请求XML："+context);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","hrt.passivePayUrl"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		heads.put("Accept", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道(和融通)返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道(和融通)返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		
		/**
		 * 和融通  交易查询
		 */
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("unno",YSBUtil.getReadProperties("nfc","hrt.unno"));
//		map.put("mid",YSBUtil.getReadProperties("nfc","hrt.mid"));
//		map.put("orderid","pan_test_14737541291991");
//		String getWatiSign = getBuildPayParams(map);
//		String sign = getSingParam(getWatiSign);
//		map.put("sign", sign);//签名
//		String context = XmlUtils.getMapToXml(map);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","hrt.OrderSearchUrl"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/xml;charset=UTF-8");
//		heads.put("Accept", "text/xml;charset=UTF-8");
//		param.setContext(context);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道(和融通)返回结果XML："+resp.getContent());
//		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(resp.getContent());
//		System.out.println("将通道(和融通)返回的xml转换成map:"+Xmlmap);
//		String json = JsonUtil.getMapToJson(Xmlmap);
//		System.out.println("将map转换成json:"+json);
		/**
		 * 全晶一码付
		 */
//		String orderId = "pan_tesi"+System.currentTimeMillis();
//		int fee = 1;//分
//		String time = sdf.format(new Date());
//		Map<String,Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("orderId", orderId);
//		dataMap.put("fee", fee);
//		dataMap.put("time", time);
//		dataMap.put("clearType", 0);
//		String data = QrCodeUtil.getBuildPayParams(dataMap);
//		//String data = "orderId=112233445566778899&fee=1&time="+time+"&clearType=0";
//		logger.info("(全晶一码付接口：)订单号："+orderId+",data数据："+data);
//		logger.info("(全晶一码付接口：)订单号："+orderId+",data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
//		logger.info("(全晶一码付接口：)订单号："+orderId+",sign加密数据："+MD5.encrypt(data));
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("mId", YSBUtil.getReadProperties("nfc","qj.mId"));
//		map.put("data", AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
//		map.put("sign", MD5.encrypt(data));
//		map.put("resType", "1");
//		String json = JsonUtil.getMapToJson(map);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","qj.url"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/json;charset=UTF-8");
//		param.setContext(json);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果json："+resp.getContent());
		/**
		 * 全晶对账查询
		 */
//		Map<String,Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));
//		dataMap.put("date","20170129");
//		String sign = dataMap.get("cooperatorId").toString() + dataMap.get("date").toString();
//		dataMap.put("sign", MD5.encrypt(sign));
//		dataMap.put("resType", "0");
//		
//		String json = JsonUtil.getMapToJson(dataMap);
//		
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","qj.transBill"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/json;charset=UTF-8");
//		param.setContext(json);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果json："+resp.getContent());
		/**
		 * 全晶商户进件或者修改商户资料接口
		 */
//		Map<String,Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("mId", "111111");//商户ID，由全通支付分配：1、 此参数为空时，为新商户进件。2、 此参数不为空时，为修改商户资料。
//		dataMap.put("merchantId", "112233445566778899");//合作方商户编号，由合作方分配。
//		dataMap.put("merchantName", "测试");//商户名称，请与营业执照上的名称一致。
//		dataMap.put("merchantAddress", "北京市朝阳区大郊亭桥");//商户地址
//		dataMap.put("merchantType", "1");//商户类型：0—个人，1—公司
//		dataMap.put("categoryForAlipay", "2016062900190300");//支付宝经营类目ID，参见“支付宝经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
//		dataMap.put("categoryForWeChat", "19");//微信经营类目ID，参见“微信经营类目”。注意：微信和支付宝分别有各自的经营类目列表，两者不可混淆。
//		dataMap.put("contractName", "东汇宝支付有限公司");//当merchantType=0 时，为收款人姓名当merchantType=1 时，为公司全称
//		dataMap.put("idCard", "");//商户身份证号码（当merchantType=0 时，此项必填，银行系统风控监管需要）
//		dataMap.put("merchantLicense", "4561687684564986468468798748");//商户营业执照号码 （当merchantType=1 时，此项必填，银行系统风控监管需要）
//		dataMap.put("accName", "东汇宝支付有限公司");//收款人账户名
//		dataMap.put("bankName", "中国人民银行天津分行营业管理部");//收款人开户行名称
//		dataMap.put("bankId", "﻿1110002774");//收款人开户行联行号
//		dataMap.put("bankNumber", "11223344556677");//收款人银行帐号
//		dataMap.put("mobileForBank", "15010001161");//收款人银行预留手机号码
//		dataMap.put("t0DrawFee", "0.2");//T0 单笔交易手续费，如0.2 元/笔则填0.2
//		dataMap.put("t0TradeRate", "0.006");//T0 交易手续费扣率，如0.6%笔则填0.006
//		dataMap.put("t1DrawFee", "0.2");//T1 单笔交易手续费，如0.2 元/笔则填0.2
//		dataMap.put("t1TradeRate", "0.006");//T1 交易手续费扣率，如0.6%笔则填0.006
//		String data = QrCodeUtil.getBuildPayParams(dataMap);
//		logger.info("(全晶商户进件或者修改商户资料接口：),data数据："+data);
//		logger.info("(全晶商户进件或者修改商户资料接口：),data加密数据："+AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
//		logger.info("(全晶商户进件或者修改商户资料接口：),sign加密数据："+MD5.encrypt(data));
//		Map<String,Object> map = new LinkedHashMap<String, Object>();
//		map.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));
//		map.put("data",AESUtil.encrypt(data,YSBUtil.getReadProperties("nfc","qj.key")));
//		map.put("sign",MD5.encrypt(data));
//		map.put("resType",YSBUtil.getReadProperties("nfc","qj.resType"));
//		String json = JsonUtil.getMapToJson(map);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl(YSBUtil.getReadProperties("nfc","qj.merchJoin"));
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/json;charset=UTF-8");
//		param.setContext(json);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果json："+resp.getContent());
		/**
		 * 全晶图片上传接口
		 */
		Map<String,String> dataMap = new HashMap<String, String>();
		dataMap.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));//合作方ID，由全通支付分配
		dataMap.put("merchantId","11223344");//合作方商户编号，由合作方分配
		dataMap.put("mobile","15010001161");//商户手机号码，必须与入驻接口的收款人银行预留手机号码一致
		dataMap.put("fileType","1011");//证件图片文件类型：1011-身份证正面,1012-身份证反面
		dataMap.put("suffix","png");//证件图片文件扩展名(如: png,jpg)
		dataMap.put("resType","0");//请求类型：0—测试环境，1—生产环境
		dataMap.put("startIndex","0");//证件图片字节的起始索引（从0开始）
		dataMap.put("endIndex","56");//证件图片字节的截止索引
		dataMap.put("totalLength","121");//证件图片文件总的字节大小(即文件内容的字符串长度的一半)
		dataMap.put("content","121");//证件图片文件的内容(十六进制形式的字符串，所以必须是偶数位)
		dataMap.put("checkValue","1212");//1个字节的校验值，十六进制形式(对本次上传的文件内容的各个字节的进行异或的结果)
////		String json = JsonUtil.getMapToJsonStr(dataMap);
////		System.out.println("json="+json);
////		HttpHelp send = new HttpHelp();
////		HttpRequestParam param = new HttpRequestParam();
////		param.setUrl("http://wpay.transgem.cn/WechatPay/UploadFile");
////		Map<String,String> heads = Maps.newHashMap();
////		heads.put("Content-Type", "text/json;charset=UTF-8");
////		param.setContext(json);
////		param.setHeads(heads);
////		HttpResponser resp=send.postParamByHttpClient(param);
////		System.out.println("通道返回结果json："+resp.getContent());
//		
		System.out.println(ANYZUtil.getWebForm(dataMap));
		String result = ANYZUtil.sendMsg("http://wpay.transgem.cn/WechatPay/UploadFile",dataMap);
		System.out.println(result);
		
		/**
		 * 商户入驻结果查询接口
		 */
//		Map<String,String> dataMap = new HashMap<String, String>();
//		dataMap.put("cooperatorId",YSBUtil.getReadProperties("nfc","qj.mId"));
//		dataMap.put("merchantId","11223344");
//		String sign = MD5.encrypt(dataMap.get("cooperatorId")+dataMap.get("merchantId"));
//		dataMap.put("sign",sign);
//		dataMap.put("resType","0");
//		String json = JsonUtil.getMapToJsonStr(dataMap);
//		System.out.println("json="+json);
//		HttpHelp send = new HttpHelp();
//		HttpRequestParam param = new HttpRequestParam();
//		param.setUrl("http://wpay.transgem.cn/WechatPay/GetJoinResult");
//		Map<String,String> heads = Maps.newHashMap();
//		heads.put("Content-Type", "text/json;charset=UTF-8");
//		param.setContext(json);
//		param.setHeads(heads);
//		HttpResponser resp=send.postParamByHttpClient(param);
//		System.out.println("通道返回结果json："+resp.getContent());
	}
}
