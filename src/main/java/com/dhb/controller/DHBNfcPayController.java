package com.dhb.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dhb.anyz.service.ANYZUtil;
import com.dhb.dao.service.KeyInfoDao;
import com.dhb.dao.service.OutRequestRecordDao;
import com.dhb.nfc.entity.AESUtil;
import com.dhb.nfc.entity.Constants;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.nfc.service.CcbQrCodePayService;
import com.dhb.nfc.service.HrtQrCodePayService;
import com.dhb.nfc.service.QjOneCodePayService;
import com.dhb.nfc.service.QrCodeUtil;
import com.dhb.nfc.service.RfQrCodePayService;
import com.dhb.nfc.service.YaColQrCodePayService;
import com.dhb.quartz.NFCQjTransBillService;
import com.dhb.service.ValidateService;
import com.dhb.util.DateUtil;
import com.dhb.util.EncodeUtils;
import com.dhb.util.JsonUtil;
import com.dhb.util.MD5;
import com.dhb.util.XmlUtils;
import com.dhb.ysb.service.YSBUtil;
import com.google.gson.JsonSyntaxException;
import com.murong.ecp.app.merchant.atc.RSASignUtil;

@Controller
@RequestMapping(value="/dhb")
public class DHBNfcPayController {
	public static Logger logger = Logger.getLogger(DHBNfcPayController.class);
	@Resource
	private YaColQrCodePayService yaColQrCodePayService;
	@Resource
	private HrtQrCodePayService hrtWechatPayService;
	@Resource
	private QjOneCodePayService qjOneCodePayService;
	@Resource
	private CcbQrCodePayService ccbQrCodePayService;
	@Resource
	private RfQrCodePayService rfQrCodePayService;
	@Resource
	private NFCQjTransBillService nfcQjTransBillService;
	@Resource
	private ValidateService validateService;
	@Resource
	private OutRequestRecordDao outRequestRecordService;
	@Autowired
	private KeyInfoDao keyInfoDao;
	/**
	 * NFC支付
	 * @author pie
     * @date 2016-08-25
	 * @param json
	 * @return
	 * @throws ServletException 
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcPay",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcPay(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(无线通讯支付接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCparamIsNull(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				String nfc_merch = jsonMap.get("nfc_merch").toString();
				String nfc_type = jsonMap.get("nfc_type").toString();
				String merch_channel = jsonMap.get("merch_channel").toString();
				logger.info("收到请求(无线通讯支付接口)：客户端->服务端(" + orderNo + ")参数：" + json);
				//验证订单是否存在
				if(validateService.validateNFCorderNoIsExist(jsonMap) == true){//验证订单号是否存在
					logger.info("无线通讯支付接口订单号重复请更换:"+orderNo);
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯支付接口订单号重复请更换");
					return JsonUtil.getMapToJson(jsonRetMap);
				}
				//保存订单日志表
				outRequestRecordService.getSaveNFCorder(jsonMap);
				if(nfc_merch.equals(Constants.wechat_nfc_merch)){//微信
					//暂时根据不同的商户号区分所调用的通道服务
					if(merch_channel.equals(Constants.nfc_merch_channel_hrt)){//和融通被扫
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = hrtWechatPayService.qrCodePassivePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else if(merch_channel.equals(Constants.nfc_merch_channel_qj)){//全晶被扫
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = qjOneCodePayService.oneCodePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else if(merch_channel.equals(Constants.nfc_merch_channel_ccb)){//中信银行
						jsonRetMap = ccbQrCodePayService.qrCodePay(jsonMap);
					}else if(merch_channel.equals(Constants.nfc_merch_channel_rf)){//融服
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = rfQrCodePayService.qrCodePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道配置有误，请联系运维人员！");
					}
				}else if(nfc_merch.equals(Constants.alipay_nfc_merch)){//支付宝
					if(jsonMap.get("merch_channel").toString().equals(Constants.nfc_merch_channel_hrt)){
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = hrtWechatPayService.qrCodePassivePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else if(jsonMap.get("merch_channel").toString().equals(Constants.nfc_merch_channel_qj)){//全晶被扫
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = qjOneCodePayService.oneCodePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else if(merch_channel.equals(Constants.nfc_merch_channel_ccb)){//中信银行
						jsonRetMap = ccbQrCodePayService.qrCodePay(jsonMap);
					}else if(merch_channel.equals(Constants.nfc_merch_channel_rf)){//融服
						if(nfc_type.equals(Constants.nfc_passive)){
							jsonRetMap = rfQrCodePayService.qrCodePay(jsonMap);
						}else{
							jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
						}
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道配置有误，请联系运维人员！");
					}
				}else{
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"请检查参数【nfc_merch】【nfc_type】");
				}
			}else{
				logger.info("无线通讯支付接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("无线通讯支付接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯支付接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("无线通讯支付接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯支付接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
		logger.info("耗时：无线通讯支付接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(无线通讯支付接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
		return JsonUtil.getMapToJson(jsonRetMap);
	}
	/**
	 * NFC通道支付
	 * @author pie
     * @date 2017-01-17
	 * @param json
	 * @return
	 * @throws ServletException 
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcChannelPay",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcChannelPay(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(无线通讯通道支付接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String returnString = "";
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCchannelParamIsNull(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				String merch_channel = jsonMap.get("merch_channel").toString();
				String nfc_merch = jsonMap.get("nfc_merch").toString();
				String nfc_type = jsonMap.get("nfc_type").toString();
				logger.info("收到请求(无线通讯通道支付接口)：客户端->服务端(" + orderNo + ")参数：" + json);
				//验证订单是否存在
				if(validateService.validateNFCorderNoIsExist(jsonMap) == true){//验证订单号是否存在
					logger.info("无线通讯通道支付接口订单号重复请更换:"+orderNo);
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯通道支付接口订单号重复请更换");
					return JsonUtil.getMapToJson(jsonRetMap);
				}
				//保存订单日志表
				outRequestRecordService.getSaveNFCorder(jsonMap);
				if(nfc_type.equals(Constants.nfc_channelPay)){
					if(merch_channel.equals(Constants.nfc_merch_channel_qj)){
						returnString = qjOneCodePayService.channelPay(jsonMap);
						logger.info("耗时：无线通讯通道支付接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
						logger.info("收到请求(无线通讯通道支付接口)：服务端->客户端(" + orderNo + ")参数：" + returnString);
						return returnString;
					}else if(merch_channel.equals(Constants.nfc_merch_channel_ccb)){
						returnString = ccbQrCodePayService.channelPay(jsonMap);
						logger.info("耗时：无线通讯通道支付接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
						logger.info("收到请求(无线通讯通道支付接口)：服务端->客户端(" + orderNo + ")参数：" + returnString);
						return returnString;
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道配置有误，请联系运维人员！");
						return JsonUtil.getMapToJson(jsonRetMap);
					}
				}else if(nfc_merch.equals(Constants.alipay_nfc_merch) && nfc_type.equals(Constants.nfc_channelPay)){
					if(merch_channel.equals(Constants.nfc_merch_channel_qj)){
						returnString = qjOneCodePayService.channelPay(jsonMap);
						logger.info("耗时：无线通讯通道支付接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
						logger.info("收到请求(无线通讯通道支付接口)：服务端->客户端(" + orderNo + ")参数：" + returnString);
						return returnString;
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道配置有误，请联系运维人员！");
						return JsonUtil.getMapToJson(jsonRetMap);
					}
				}else{
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"请检查参数【nfc_merch】【nfc_type】");
					return JsonUtil.getMapToJson(jsonRetMap);
				}
			}else{
				logger.info("无线通讯通道支付接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("无线通讯通道支付接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯通道支付接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("无线通讯通道支付接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯通道支付接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-全通支付商户进件或者修改商户资料接口
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcQjMerchJoin",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcQjMerchJoin(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(全通支付商户进件或者修改商户资料接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				jsonRetMap = qjOneCodePayService.MerchJoin(jsonMap);
				logger.info("耗时：全通支付商户进件或者修改商户资料接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(全通支付商户进件或者修改商户资料接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				return JsonUtil.getMapToJson(jsonRetMap);
			}else{
				logger.info("全通支付商户进件或者修改商户资料接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("全通支付商户进件或者修改商户资料接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全通支付商户进件或者修改商户资料接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("全通支付商户进件或者修改商户资料接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全通支付商户进件或者修改商户资料接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-全通图片上传接口
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcQjUploadFile",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcQjUploadFile(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(全通图片上传接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				jsonRetMap = qjOneCodePayService.QjUploadFile(jsonMap);
				logger.info("耗时：全通图片上传接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(全通图片上传接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				return JsonUtil.getMapToJson(jsonRetMap);
			}else{
				logger.info("全通图片上传接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("全通图片上传接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全通图片上传接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("全通图片上传接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全通图片上传接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-全晶商户入驻结果查询接口
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcQjmerchJoinResult",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcQjmerchJoinResult(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(全晶商户入驻结果查询接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				jsonRetMap = qjOneCodePayService.QjmerchJoinResult(jsonMap);
				logger.info("耗时：全晶商户入驻结果查询接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(全晶商户入驻结果查询接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				return JsonUtil.getMapToJson(jsonRetMap);
			}else{
				logger.info("全晶商户入驻结果查询接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("全晶商户入驻结果查询接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全晶商户入驻结果查询接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("全晶商户入驻结果查询接口收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"全晶商户入驻结果查询接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-对账数据重导接口(对外提供，方便重新导入)
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcTransBill",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcTransBill(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(对账数据重导接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String returnString = "";
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				if(jsonMap.get("merch_channel") != null){
					if(jsonMap.get("merch_channel").toString().equals(Constants.nfc_merch_channel_qj)){
						returnString = nfcQjTransBillService.status(jsonMap.get("date_time").toString());
					}else if(jsonMap.get("merch_channel").toString().equals(Constants.nfc_merch_channel_rf)){
						returnString = rfQrCodePayService.transBill(jsonMap);
					}else if(jsonMap.get("merch_channel").toString().equals(Constants.nfc_merch_channel_ccb)){
						returnString = ccbQrCodePayService.transBill(jsonMap);
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"对账数据重导接口merch_channel,请检查");
						return JsonUtil.getMapToJson(jsonRetMap);
					}
				}else{
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"对账数据重导接口merch_channel is null");
					return JsonUtil.getMapToJson(jsonRetMap);
				}
				logger.info("耗时：对账数据重导接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(对账数据重导接口)：服务端->客户端(" + orderNo + ")参数：" + returnString);
				return returnString;
			}else{
				logger.info("对账数据重导接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("对账数据重导接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"对账数据重导接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("对账数据重导接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"对账数据重导接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-中信银行分账子账户入驻接口
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcCcbMerchJoin",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcCcbMerchJoin(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(中信银行分账子账户入驻接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				jsonRetMap = ccbQrCodePayService.merchJoin(jsonMap);
				logger.info("耗时：中信银行分账子账户入驻接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(中信银行分账子账户入驻接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				return JsonUtil.getMapToJson(jsonRetMap);
			}else{
				logger.info("中信银行分账子账户入驻接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("中信银行分账子账户入驻接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"中信银行分账子账户入驻接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("中信银行分账子账户入驻接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"中信银行分账子账户入驻接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	/**
	 * NFC-中信银行分账子账户查询接口 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcCcbMerchSearch",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcCcbMerchSearch(@RequestBody String json) throws ServletException, IOException{
		logger.info("收到请求(中信银行分账子账户查询接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCqj(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				jsonRetMap = ccbQrCodePayService.merchSearch(jsonMap);
				logger.info("耗时：中信银行分账子账户查询接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
				logger.info("收到请求(中信银行分账子账户查询接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
				return JsonUtil.getMapToJson(jsonRetMap);
			}else{
				logger.info("中信银行分账子账户查询接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("中信银行分账子账户查询接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"中信银行分账子账户查询接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("中信银行分账子账户查询接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"中信银行分账子账户查询接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
	}
	
	
	/**
	 * NFC退货
	 *@author pie
     * @date 2016-08-26
	 * @param json
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcTradeRefund",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcTradeRefund(@RequestBody String json){
		logger.info("收到请求(无线通讯退货接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCparamIsNull(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				String nfc_merch = jsonMap.get("nfc_merch").toString();
				String nfc_type = jsonMap.get("nfc_type").toString();
				logger.info("收到请求(无线通讯退货接口)：客户端->服务端(" + orderNo + ")参数：" + json);
				//验证订单是否存在
				if(validateService.validateNFCorderNoIsExist(jsonMap) == true){//验证订单号是否存在
					logger.info("无线通讯退货接口订单号重复请更换:"+orderNo);
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯退货接口订单号重复请更换");
					return JsonUtil.getMapToJson(jsonRetMap);
				}else{
					if(validateService.validateNFCRefundorderNoIsExist(jsonMap) == false){//退货的订单不存在，无法退货
						logger.info("无线通讯通道支付接口退货的订单不存在，无法退货:"+orderNo);
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯通道支付接口退货的订单不存在，无法退货");
						return JsonUtil.getMapToJson(jsonRetMap);
					}
				}
				//根据订单号，查询订单交易流水表数据
				NfcOrderWater water = validateService.getNfcOrderWater(null,jsonMap.get("refund_order_no").toString());
				String channel = water.getMerchChannel();
				//保存订单日志表
				jsonMap.put("merch_channel", channel);
				//保存订单日志表
				outRequestRecordService.getSaveNFCorder(jsonMap);
				if(nfc_merch.equals(Constants.wechat_nfc_merch) && nfc_type.equals(Constants.nfc_refund)){//微信
					if(channel.equals(Constants.nfc_merch_channel_ccb)){
						jsonRetMap = ccbQrCodePayService.qrCodeTradeRefund(jsonMap);
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
					}
				}else if(nfc_merch.equals(Constants.alipay_nfc_merch) && nfc_type.equals(Constants.nfc_refund)){//支付宝
					if(channel.equals(Constants.nfc_merch_channel_ccb)){
						jsonRetMap = ccbQrCodePayService.qrCodeTradeRefund(jsonMap);
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"该商户的通道不支持此功能，请联系运维人员！");
					}
				}else{
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"请检查参数【nfc_merch】【nfc_type】");
				}
			}else{
				logger.info("无线通讯退货接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("无线通讯退货接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯退货接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("无线通讯退货接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯退货接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
		logger.info("耗时：无线通讯退货接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(无线通讯退货接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
		return JsonUtil.getMapToJson(jsonRetMap);
	}
	/**
	 * 冲正
	 * @param json
	 * @return
	 */
//	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcReversal",produces = {"application/json;charset=UTF-8"})
//	public @ResponseBody String nfcReversal(@RequestBody String json){
//		logger.info("收到请求(无线通讯冲正接口)：参数：" + json);
//		long l = System.currentTimeMillis();
//		Map<String,Object> jsonRetMap = null;
//		String orderNo = "";
//		try{
//			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
//			//验证接收参数
//			String ret = validateService.validateNFCparamIsNull(jsonMap);
//			if(ret.equals("success")){
//				orderNo = jsonMap.get("order_no").toString();
//				String nfc_merch = jsonMap.get("nfc_merch").toString();
//				String nfc_type = jsonMap.get("nfc_type").toString();
//				logger.info("收到请求(无线通讯冲正接口)：客户端->服务端(" + orderNo + ")参数：" + json);
//				//验证订单是否存在
//				if(validateService.validateNFCorderNoIsExist(jsonMap)){//验证订单号是否存在
//					logger.info("无线通讯冲正接口订单号重复请更换:"+orderNo);
//					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯冲正接口订单号重复请更换");
//					return JsonUtil.getMapToJson(jsonRetMap);
//				}
//				//保存订单日志表
//				outRequestRecordService.getSaveNFCorder(jsonMap);
//				if(nfc_merch.equals(Constants.wechat_nfc_merch) && nfc_type.equals(Constants.nfc_reversal)){//微信
//					jsonRetMap = wecatPayService.wecatReversal(jsonMap);
//				}else if(nfc_merch.equals("allipay")){//支付宝
//					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"暂不开放");
//				}else{
//					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"请检查参数【nfc_merch】【nfc_type】");
//				}
//			}else{
//				logger.info("无线通讯冲正接口验证失败:"+orderNo);
//				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
//			}
//		}catch(JsonSyntaxException e){
//			logger.error("无线通讯冲正接口转换JSON异常："+orderNo+","+e.getMessage());
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯冲正接口转换JSON异常");
//			return JsonUtil.getMapToJson(jsonRetMap);
//		}catch(Exception e){
//			e.printStackTrace();
//			logger.error(e.getMessage(), e);
//			logger.error("无线通讯冲正接口接收参数异常："+orderNo+","+e.getMessage());
//			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯冲正接口接收参数异常");
//			return JsonUtil.getMapToJson(jsonRetMap);
//		}
//		logger.info("耗时：无线通讯冲正接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
//		logger.info("收到请求(无线通讯冲正接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
//		return JsonUtil.getMapToJson(jsonRetMap);
//	}
	/**
	 * 本地订单查询
	 * @param json
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", value = "/nfcQuery",produces = {"application/json;charset=UTF-8"})
	public @ResponseBody String nfcQuery(@RequestBody String json){
		logger.info("收到请求(无线通讯订单查询接口)：参数：" + json);
		long l = System.currentTimeMillis();
		Map<String,Object> jsonRetMap = null;
		String orderNo = "";
		try{
			Map<String,Object> jsonMap = JsonUtil.getJsonToMap(json);
			//验证接收参数
			String ret = validateService.validateNFCparamIsNull(jsonMap);
			if(ret.equals("success")){
				orderNo = jsonMap.get("order_no").toString();
				String nfc_merch = jsonMap.get("nfc_merch").toString();
				String nfc_type = jsonMap.get("nfc_type").toString();
				logger.info("收到请求(无线通讯订单查询接口)：客户端->服务端(" + orderNo + ")参数：" + json);
				//验证订单是否存在
				if(!validateService.validateNFCorderNoIsExist(jsonMap)){//验证订单号是否存在
					logger.info("无线通讯订单查询接口订单号为NULL:"+orderNo);
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯订单查询接口订单号为NULL");
					return JsonUtil.getMapToJson(jsonRetMap);
				}
				if((nfc_merch.equals(Constants.wechat_nfc_merch) || nfc_merch.equals(Constants.alipay_nfc_merch)) && nfc_type.equals(Constants.nfc_query)){//微信支付宝
					//直接调用数据库查询
					NfcOrderWater water = validateService.getNfcOrderWater(null, jsonMap.get("order_no").toString());
					if(water == null){
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"查无此单");
					}else{
						jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,water.getStatus(),water.getMessage());
					}
				}else{
					jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"请检查参数【nfc_merch】【nfc_type】");
				}
			}else{
				logger.info("无线通讯订单查询接口验证失败:"+orderNo);
				return JsonUtil.getMapToJson(JsonUtil.getJsonToMap(ret));
			}
		}catch(JsonSyntaxException e){
			logger.error("无线通讯订单查询接口转换JSON异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯订单查询接口转换JSON异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("无线通讯订单查询接口接收参数异常："+orderNo+","+e.getMessage());
			jsonRetMap = JsonUtil.getReturnNFCMessageHead(orderNo,Constants.nfc_pay_status_9,"无线通讯订单查询接口接收参数异常");
			return JsonUtil.getMapToJson(jsonRetMap);
		}
		logger.info("耗时：无线通讯订单查询接口("+ orderNo +"):"+(System.currentTimeMillis()-l));
		logger.info("收到请求(无线通讯订单查询接口)：服务端->客户端(" + orderNo + ")参数：" + JsonUtil.getMapToJson(jsonRetMap));
		return JsonUtil.getMapToJson(jsonRetMap);
	}
	/**
	 * 雅酷时空异步通知接收地址
	 * @author pie
     * @date 2016-08-26
	 * @param from_submit
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST,value = "/yacolNotifyUrl")
	public String yacolNotifyUrl(HttpServletRequest request, HttpServletResponse response){
		String encoding="";
		String returnParam = "";
		String charset = request.getParameter("charset");
		String version = request.getParameter("version");
        String serverCert = request.getParameter("serverCert");
        String serverSign = request.getParameter("serverSign");
        String signType = request.getParameter("signType");
        String service = request.getParameter("service");
        String status = request.getParameter("status");         
        String returnCode = request.getParameter("returnCode");
        String returnMessage = request.getParameter("returnMessage");
        String tradeNo = request.getParameter("tradeNo");
        String purchaserId = request.getParameter("purchaserId");          
        String orderId = request.getParameter("orderId");
        String orderTime = request.getParameter("orderTime");
        String totalAmount = request.getParameter("totalAmount");
        String bankAbbr = request.getParameter("bankAbbr");
        String payTime = request.getParameter("payTime");  
        String acDate = request.getParameter("acDate");
        String fee = request.getParameter("fee");
        String backParam = request.getParameter("backParam");             
        String merchantId = request.getParameter("merchantId");
        logger.info("NFC异步通知所接收参数charset="+charset);
        logger.info("NFC异步通知所接收参数version="+version);
        logger.info("NFC异步通知所接收参数serverCert="+serverCert);
        logger.info("NFC异步通知所接收参数serverSign="+serverSign);
        logger.info("NFC异步通知所接收参数signType="+signType);
        logger.info("NFC异步通知所接收参数service="+service);
        logger.info("NFC异步通知所接收参数status="+status);
        logger.info("NFC异步通知所接收参数returnCode="+returnCode);
        logger.info("NFC异步通知所接收参数returnMessage="+returnMessage);
        logger.info("NFC异步通知所接收参数tradeNo="+tradeNo);
        logger.info("NFC异步通知所接收参数purchaserId="+purchaserId);
        logger.info("NFC异步通知所接收参数orderId="+orderId);
        logger.info("NFC异步通知所接收参数orderTime="+orderTime);
        logger.info("NFC异步通知所接收参数totalAmount="+totalAmount);
        logger.info("NFC异步通知所接收参数bankAbbr="+bankAbbr);
        logger.info("NFC异步通知所接收参数payTime="+payTime);
        logger.info("NFC异步通知所接收参数acDate="+acDate);
        logger.info("NFC异步通知所接收参数fee="+fee);
        logger.info("NFC异步通知所接收参数backParam="+backParam);
        logger.info("NFC异步通知所接收参数merchantId="+merchantId);
        try{
        	//编码设置
        	if("00".equals(charset)){
                request.setCharacterEncoding(Constants.wechat_charset_00);
                encoding=Constants.wechat_charset_00;
            }else if("01".equals(charset)) {
                request.setCharacterEncoding(Constants.wechat_charset_01);
                encoding=Constants.wechat_charset_01;
            }else if("02".equals(charset)) {
                request.setCharacterEncoding(Constants.wechat_charset_02);
                encoding=Constants.wechat_charset_02;
            }else{
                request.setCharacterEncoding(Constants.wechat_charset_00);
                encoding=Constants.wechat_charset_00;
            }
        	//根据订单号，查询订单交易流水表数据
    		NfcOrderWater water = validateService.getNfcOrderWater(orderId,null);
    		if(water == null){
    			logger.info("(雅酷时空微信异步通知接收：)订单号不存在！！！");
    			returnParam = "fail";
    		}else{
    			//去订单流水表中查询业务状态是否为处理过的，如果已处理返回success,如果未处理进行处理
        		if(water.getStatus().equals(Constants.nfc_pay_status_1) || water.getStatus().equals(Constants.nfc_pay_status_9)){//交易完成或交易失败
        			returnParam = "success";
        		}else{
        			//获取签名数据进行验签
        			Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
        	        dataMap.put("charset",charset);
        	        dataMap.put("version",version);
        	        dataMap.put("signType",signType);
        	        dataMap.put("service",service);
        	        dataMap.put("status",status);
        	        dataMap.put("returnCode",returnCode);
        	        dataMap.put("returnMessage",returnMessage);                             
        	        dataMap.put("tradeNo",tradeNo);
        	        dataMap.put("purchaserId",purchaserId);
        	        dataMap.put("merchantId",merchantId);
        	        dataMap.put("orderId",orderId);
        	        dataMap.put("orderTime",orderTime);
        	        dataMap.put("totalAmount",totalAmount);
        	        dataMap.put("bankAbbr",bankAbbr);
        	        dataMap.put("payTime",payTime);
        	        dataMap.put("acDate",acDate);
        	        dataMap.put("fee",fee); 
        	        dataMap.put("backParam",backParam);
        	        //拼写待验签的字符串
        	  		String waitStr = QrCodeUtil.getBuildPayParams(dataMap);
        	  		logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+",拼写待验签的字符串："+waitStr);
        	  		//获取容器中公钥证书的位置
        	  		QrCodeUtil w = new QrCodeUtil();
        	  		String rootCertPath = w.init() + YSBUtil.getReadProperties("nfc","sdk.rootCertPath");
        	  		/**
        			 * 在LINUX下，此方法不可注释
        			 */
        			rootCertPath = "/"+rootCertPath.replace("\\", "/");
        			
        	  		logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+",获取容器中公钥证书的位置："+rootCertPath);
        	  		RSASignUtil rsautil = new RSASignUtil(rootCertPath);
        	  		logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+",验证签名（待验签数据）："+waitStr);
        			logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+",验证签名（服务器对报文签名的签名结果）："+serverSign);
        			logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+",验证签名（服务器端的签名证书）："+serverCert);
        			boolean flag = false;
        			flag = rsautil.verify(waitStr,serverSign,serverCert,encoding);
        			if (!flag) {
        		    	logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+"验签结果："+flag+",错误信息：验签错误");
    					returnParam = "fail";
        			}else{
        				logger.info("(雅酷时空微信异步通知接收：)订单号："+water.getOrderNo()+"验签结果："+flag);
        				if(status.equals("SUCCESS") && returnCode.equals("000000")){
        					water.setStatus(Constants.nfc_pay_status_1);
        					water.setMessage(Constants.nfc_pay_status_1_context);
        				}else{
        					water.setStatus(Constants.nfc_pay_status_9);
        					water.setMessage(returnMessage);
        				}
        				returnParam = "success";
        				validateService.getUpateNfcOrderWater(water,null);
        				//组装异步通知参数 准备发送给商户
    					Map<String,Object> notityMap = new LinkedHashMap<String, Object>();
    					notityMap.put("order_no", water.getOrderNo());
    					notityMap.put("total_fee", water.getTotalFee());
    					notityMap.put("result_code", water.getStatus());
    					notityMap.put("message", water.getMessage());
    					notityMap.put("end_time", DateUtil.format(water.getEndTime()));
    					String paramStr = QrCodeUtil.getBuildPayParams(notityMap);
    					String signStr = "";
    					String key = keyInfoDao.getNfcKeySrc(water.getMerchNo());
    					signStr = MD5.encrypt(EncodeUtils.base64Encode(paramStr.getBytes("UTF-8")),key);
    					Map<String,Object> m = new LinkedHashMap<String, Object>();
    					m.put("order_no", water.getOrderNo());
    					m.put("data", paramStr+"&sign="+signStr);
    					QrCodeUtil.getSendNotifyUrl(water.getNotifyUrl(),m);
        			}
        		}
    		}
        }catch(Exception ex){
        	ex.printStackTrace();
			logger.error("雅酷时空微信异步通知"+ex.getMessage(), ex);
			returnParam = "fail";
        }
		return returnParam;
	}
	/**
	 * 和融通异步通知接收地址
	 * @author pie
     * @date 2016-09-14
	 * @param XML
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "text/xml", value = "/NotifyUrl_XML",produces = {"text/xml;charset=UTF-8"})
	public @ResponseBody String NotifyUrl_XML(@RequestBody String xml){
		logger.info("接收无线通讯被扫（和融通）异步通知XML："+xml);
		String returnParam = "";
		Map<String,Object> map = new HashMap<String, Object>();
		String orderid = "";
		Map<String,Object> Xmlmap = XmlUtils.getXmlToMap(xml);
		map.put("unno", Xmlmap.get("unno"));
		map.put("mid", Xmlmap.get("mid"));
		//查询商户与通道商户对应表
//    	NfcMerchToPayMerch nfcMerchToPayMerch = keyInfoDao.getNfcMerchToPayMerch_(Xmlmap.get("mid").toString(),Constants.nfc_merch_channel_hrt);

		
		try{
			orderid = Xmlmap.get("orderid").toString();
			//根据订单号，查询订单交易流水表数据
			NfcOrderWater water = validateService.getNfcOrderWater(orderid,null);
			//去订单流水表中查询业务状态是否为处理过的，如果已处理返回success,如果未处理进行处理
			if(water.getStatus().equals(Constants.nfc_pay_status_1) || water.getStatus().equals(Constants.nfc_pay_status_9)){//交易完成或交易失败
				//returnParam = "success";
				map.put("orderid", Xmlmap.get("orderid"));
				map.put("respstatus", "S");
				String returnWaitSign = QrCodeUtil.getBuildPayParams(map);
				String sign_ = QrCodeUtil.getSingParam(returnWaitSign,YSBUtil.getReadProperties("nfc","hrt.key"));
				map.put("sign", sign_);
				returnParam = XmlUtils.getMapToXml(map);
				logger.info("无线通讯被扫（和融通）中互联返回异步通知XML："+"orderid="+orderid+",returnParam="+returnParam);
			}else{
				/**
				 * 根据签名数据进行验签
				 */
				//获取签名数据
				String sign = Xmlmap.get("sign").toString();
				//组装待签名数据
				String waitSign = QrCodeUtil.getBuildPayParams(Xmlmap);
				//将待签名数据加密
				String signParam = QrCodeUtil.getSingParam(waitSign,YSBUtil.getReadProperties("nfc","hrt.key"));
				//比较通道方与我方的签名数据
				if(sign.equals(signParam)){
					logger.info("无线通讯被扫（和融通）异步通知XML验签通过！！！"+"orderid="+orderid+",order_no="+water.getOrderNo());
					//根据订单修改存入数据
					if(Xmlmap.get("status").toString().equals("S")){//支付成功
						water.setStatus(Constants.nfc_pay_status_1);
						water.setMessage(Constants.nfc_pay_status_1_context);
					}else if(Xmlmap.get("status").toString().equals("E")){//支付失败
						water.setStatus(Constants.nfc_pay_status_9);
						water.setMessage(Xmlmap.get("errdesc").toString());
					}else if(Xmlmap.get("status").toString().equals("R")){//不确定（处理中）
						water.setStatus(Constants.nfc_pay_status_3);
						water.setMessage(Constants.nfc_pay_status_3_context);
					}
					//最后时间
					String endDate = DateUtil.format(new Date());
					try {
						water.setEndTime(DateUtil.strToDate(endDate));
					} catch (Exception e) {
						e.printStackTrace();
					}
//					returnParam = "success";
					/**
					 * 返回给和融通的XML
					 */
					map.put("orderid", Xmlmap.get("orderid"));
					map.put("respstatus", "S");
					String returnWaitSign = QrCodeUtil.getBuildPayParams(map);
					String sign_ = QrCodeUtil.getSingParam(returnWaitSign,YSBUtil.getReadProperties("nfc","hrt.key"));
					map.put("sign", sign_);
					returnParam = XmlUtils.getMapToXml(map);
					logger.info("无线通讯被扫（和融通）中互联返回异步通知XML："+"orderid="+orderid+",returnParam="+returnParam);
					
					validateService.getUpateNfcOrderWater(water,null);
					//组装异步通知参数 准备发送给商户
					Map<String,Object> notityMap = new LinkedHashMap<String, Object>();
					notityMap.put("order_no", water.getOrderNo());
					notityMap.put("total_fee", water.getTotalFee());
					notityMap.put("result_code", water.getStatus());
					notityMap.put("message", water.getMessage());
					notityMap.put("end_time", DateUtil.format(water.getEndTime()));
					String paramStr = QrCodeUtil.getBuildPayParams(notityMap);
					String signStr = "";
					String key = keyInfoDao.getNfcKeySrc(water.getSubMerchNo());
					signStr = MD5.encrypt(EncodeUtils.base64Encode(paramStr.getBytes("UTF-8")),key);
					Map<String,Object> m = new LinkedHashMap<String, Object>();
					m.put("order_no", water.getOrderNo());
					m.put("data", paramStr+"&sign="+signStr);
					QrCodeUtil.getSendNotifyUrl(water.getNotifyUrl(),m);
				}else{
					logger.info("无线通讯被扫（和融通）异步通知XML验签失败！！！"+orderid);
//					returnParam = "fail";
					map.put("orderid", Xmlmap.get("orderid"));
					map.put("respstatus", "E");
					String returnWaitSign = QrCodeUtil.getBuildPayParams(map);
					String sign_ = QrCodeUtil.getSingParam(returnWaitSign,YSBUtil.getReadProperties("nfc","hrt.key"));
					map.put("sign", sign_);
					returnParam = XmlUtils.getMapToXml(map);
					logger.info("无线通讯被扫（和融通）中互联返回异步通知XML："+"orderid="+orderid+",returnParam="+returnParam);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			logger.error("无线通讯被扫（和融通）异步通知XML取值异常："+orderid+","+e.getMessage());
//			returnParam = "fail";
			map.put("orderid", Xmlmap.get("orderid"));
			map.put("respstatus", "E");
			String returnWaitSign = QrCodeUtil.getBuildPayParams(map);
			String sign_ = QrCodeUtil.getSingParam(returnWaitSign,YSBUtil.getReadProperties("nfc","hrt.key"));
			map.put("sign", sign_);
			returnParam = XmlUtils.getMapToXml(map);
			logger.info("无线通讯被扫（和融通）中互联返回异步通知XML："+"orderid="+orderid+",returnParam="+returnParam);
		}
		
//		try{
//			Map<String,Object> map = XmlUtils.getXmlToMap(xml);
//			out_trade_no = map.get("out_trade_no").toString();
//			//根据订单号，查询订单交易流水表数据
//			NfcOrderWater water = validateService.getNfcOrderWater(out_trade_no,null);
//			//去订单流水表中查询业务状态是否为处理过的，如果已处理返回success,如果未处理进行处理
//			if(water.getStatus().equals(Constants.nfc_pay_status_1) || water.getStatus().equals(Constants.nfc_pay_status_9)){//交易完成或交易失败
//				returnParam = "success";
//			}else{
//				//根据签名数据进行验签
//				//获取签名数据
//				String sign = map.get("sign").toString();
//				//组装待签名数据
//				String waitSign = WechatUtil.getBuildPayParams(map);
//				//将待签名数据加密
//				String signParam = WechatUtil.getSingParam(waitSign);
//				//比较通道方与我方的签名数据
//				if(sign.equals(signParam)){
//					logger.info("无线通讯被扫异步通知XML验签通过！！！"+"out_trade_no="+out_trade_no+",order_no="+water.getOrderNo());
//					//根据订单修改存入数据
//					if(map.get("status").toString().equals("0")){//通讯成功
//						if(map.get("result_code").toString().equals("0")){//业务成功
//							if(map.get("pay_result").toString().equals("0")){//支付成功
//								water.setStatus(Constants.nfc_pay_status_1);
//								water.setMessage(Constants.nfc_pay_status_1_context);
//								payFlag = "00";
//							}else{//支付失败
//								water.setStatus(Constants.nfc_pay_status_9);
//								water.setMessage(map.get("pay_info").toString());
//							}
//						}else{//业务失败
//							water.setStatus(Constants.nfc_pay_status_9);
//							water.setMessage(map.get("err_msg").toString());
//						}
//					}else{//通讯失败
//						water.setStatus(Constants.nfc_pay_status_9);
//						water.setMessage(map.get("message").toString());
//					}
//					//最后时间
//					String endDate = DateUtil.format(new Date());
//					try {
//						water.setEndTime(DateUtil.strToDate(endDate));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					validateService.getUpateNfcOrderWater(water);
//					returnParam = "success";
//					//组装异步通知参数 准备发送给商户
//					if(payFlag.equals("00")){
//						result_code = Constants.nfc_pay_status_1;
//						message = Constants.nfc_pay_status_1_context;
//					}else{
//						result_code = Constants.nfc_pay_status_9;
//						message = water.getMessage();
//					}
//					Map<String,Object> notityMap = new LinkedHashMap<String, Object>();
//					notityMap.put("order_no", water.getOrderNo());
//					notityMap.put("total_fee", water.getTotalFee());
//					notityMap.put("result_code", result_code);
//					notityMap.put("message", message);
//					notityMap.put("end_time", DateUtil.format(water.getEndTime()));
//					String paramStr = WechatUtil.getBuildPayParams(notityMap);
//					String signStr = "";
//					try {
//						String key = keyInfoDao.getNfcKeySrc(water.getMerchNo());
//						signStr = MD5.encrypt(EncodeUtils.base64Encode(paramStr.getBytes("UTF-8")),key);
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					Map<String,Object> m = new LinkedHashMap<String, Object>();
//					m.put("order_no", water.getOrderNo());
//					m.put("data", paramStr+"&sign="+signStr);
//					WechatUtil.getSendNotifyUrl(water.getNotifyUrl(),m);
//				}else{
//					logger.info("无线通讯被扫异步通知XML验签失败！！！"+out_trade_no);
//					returnParam = "fail";
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			logger.error(e.getMessage(), e);
//			logger.error("无线通讯被扫异步通知XML取值异常："+out_trade_no+","+e.getMessage());
//			returnParam = "fail";
//		}
		return returnParam;
	}
	/**
	 * 全晶一码付异步通知接收地址
	 * @param json
	 * @return 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = "text/plain", value = "/qjNotifyUrl",produces = {"text/plain;charset=UTF-8"})
	public @ResponseBody String qjNotifyUrl(@RequestBody String json){
		logger.info("收到请求(全晶一码付异步通知接收json)：参数：" + json);
		String returnString = "999999";
		String orderId = "";
		try{
			Map<String,Object> map = JsonUtil.getJsonToMap(json);
			String code = map.get("code").toString();
			String data = map.get("data").toString();
			String msg = map.get("msg").toString();
			String merchType = map.get("type").toString();
			//验签data和sign
			String dataString = AESUtil.decrypt(data, YSBUtil.getReadProperties("nfc","qj.key"));
			logger.info("(全晶一码付异步通知解密data数据后的值为：)：参数：" + dataString);
			String[] msg_ret = dataString.split("&");
			Map<String,String> msg_data = new HashMap<String, String>();
			for(int i = 0 ; i < msg_ret.length ; i++){
				String[] msg_ret_two = msg_ret[i].split("=");
				msg_data.put(msg_ret_two[0], msg_ret_two[1]);
			}
	    	if(merchType.equals("12")){//商户入驻结果通知
	    		String merchCode = msg_data.get("code");
	    		String mId = msg_data.get("mId");
	    		String merchantId = msg_data.get("merchantId");
	    		String merchMsg = msg_data.get("msg");
	    		Map<String,Object> merchJoinNotifyMap = new HashMap<String, Object>();
	    		if(merchCode.equals("0")){
	    			logger.info("全晶商户入驻异步通知：商户入驻成功！！！");
					merchJoinNotifyMap.put("merchantId", merchantId);
					merchJoinNotifyMap.put("mId", mId);
					merchJoinNotifyMap.put("result_code", Constants.nfc_pay_status_1);
					merchJoinNotifyMap.put("message", merchMsg);
	    		}else{
	    			logger.info("全晶商户入驻异步通知：商户入驻失败！！！");
					merchJoinNotifyMap.put("merchantId", merchantId);
					merchJoinNotifyMap.put("mId", mId);
					merchJoinNotifyMap.put("result_code", Constants.nfc_pay_status_9);
					merchJoinNotifyMap.put("message", merchMsg);
	    		}
	    		//组装异步通知参数 准备发送给商户
				String paramStr = QrCodeUtil.getBuildPayParams(merchJoinNotifyMap);
				String signStr = "";
				String key = keyInfoDao.getNfcKeySrc("111111000000000");//一码付POSP商户号---目前写死了
				String url = keyInfoDao.getNfcMerchChannel("111111000000000");
				signStr = MD5.encrypt(EncodeUtils.base64Encode(paramStr.getBytes("UTF-8")),key);
				merchJoinNotifyMap.put("sign", signStr);
				QrCodeUtil.getSendNotifyUrl(url,merchJoinNotifyMap);
				returnString = "000000";
	    	}
	    	if(merchType.equals("1")){//支付结果通知
	    		orderId = msg_data.get("orderId").toString();
		    	String fee = msg_data.get("fee").toString();
		    	String type = msg_data.get("type").toString();
		    	if("wechatPay".equals(type)){
		    		type = "wechat";
		    	}else if("aliPay".equals(type)){
		    		type = "alipay";
		    	}
	    		//根据订单号，查询订单交易流水表数据
				NfcOrderWater water = validateService.getNfcOrderWater(orderId,null);
				String merOrderNo = water.getMerOrderNo();
				String totalFee = ANYZUtil.fromYuanToFen(water.getTotalFee());
				//我方流水表中的订单号和交易金额与通道方发来的参数内容进行比对
				if(orderId.equals(merOrderNo) && fee.equals(totalFee)){
					logger.info("全晶一码付异步通知："+"orderid="+orderId+",订单号与金额验证通过！！！");
					//查询商户与通道商户对应表
			    	//NfcMerchToPayMerch nfcMerchToPayMerch = keyInfoDao.getNfcMerchToPayMerch_(msg_data.get("mid").toString(),Constants.nfc_merch_channel_qj);
			    	if(water.getStatus().equals(Constants.nfc_pay_status_1) || water.getStatus().equals(Constants.nfc_pay_status_9)){
						//该订单的状态为成功或失败，无需做处理
			    		logger.info("全晶一码付异步通知："+"orderid="+orderId+",该订单的状态为成功或失败，无需做处理！！！");
			    		returnString = "000000";
					}else{
						if("0".equals(code)){//成功
							logger.info("全晶一码付异步通知："+"orderid="+orderId+",type="+type);
							water.setStatus(Constants.nfc_pay_status_1);
							water.setMessage(Constants.nfc_pay_status_1_context);
							water.setNfcMerch(type);
							logger.info("全晶一码付异步通知："+"orderid="+orderId+",订单交易成功");
						}else{//失败
							logger.info("全晶一码付异步通知："+"orderid="+orderId+",type="+type);
							water.setStatus(Constants.nfc_pay_status_9);
							water.setMessage(msg);
							water.setNfcMerch(type);
							logger.info("全晶一码付异步通知："+"orderid="+orderId+",订单交易失败");
						}
						//最后时间
						String endDate = DateUtil.format(new Date());
						try {
							water.setEndTime(DateUtil.strToDate(endDate));
						} catch (Exception e) {
							e.printStackTrace();
						}
						validateService.getUpateNfcOrderWater(water,type);
						//组装异步通知参数 准备发送给商户
						Map<String,Object> notityMap = new LinkedHashMap<String, Object>();
						notityMap.put("order_no", water.getOrderNo());
						notityMap.put("total_fee", water.getTotalFee());
						notityMap.put("result_code", water.getStatus());
						notityMap.put("message", water.getMessage());
						notityMap.put("end_time", DateUtil.format(water.getEndTime()));
						String paramStr = QrCodeUtil.getBuildPayParams(notityMap);
						String signStr = "";
						String key = keyInfoDao.getNfcKeySrc(water.getSubMerchNo());
						signStr = MD5.encrypt(EncodeUtils.base64Encode(paramStr.getBytes("UTF-8")),key);
						Map<String,Object> m = new LinkedHashMap<String, Object>();
						m.put("order_no", water.getOrderNo());
						m.put("data", paramStr+"&sign="+signStr);
						QrCodeUtil.getSendNotifyUrl(water.getNotifyUrl(),m);
						returnString = "000000";
					}
				}else{
					//订单号与金额验证失败
					logger.info("全晶一码付异步通知："+"orderid="+orderId+",订单号与金额验证失败！！！");
					return returnString;
				}
	    	}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("orderid="+orderId+"全晶一码付异步通知处理异常："+e.getMessage());
			return returnString;
		}
		return returnString;
	}
}
