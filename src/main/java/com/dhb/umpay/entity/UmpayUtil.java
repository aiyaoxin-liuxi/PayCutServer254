package com.dhb.umpay.entity;

import java.util.Map;

import org.apache.log4j.Logger;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.SingleResp;
import com.umpay.api.common.ReqData;
import com.umpay.api.exception.ReqDataException;
import com.umpay.api.exception.RetDataException;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.api.paygate.v40.Plat2Mer_v40;

public class UmpayUtil {
	/**用户签约 **/
	public static final String service_user_reg_uecp = "user_reg_uecp";
	/**文件交易接口   接收商户FTP通知接口 **/
	public static final String service_pay_req_ftp_uecp = "pay_req_ftp_uecp";
	/**文件交易接口   文件处理结果通知接口 **/
	public static final String service_file_result_notify_uecp = "file_result_notify_uecp";
	/**商户结果通知   扣款结果通知 **/
	public static final String service_pay_result_notify_uecp = "pay_result_notify_uecp";
	/**商户订单查询接口**/
	public static final String service_query_order_req_uecp = "query_order_req_uecp";
	/** 直连付款 **/
	public static final String service_transfer_direct_req = "transfer_direct_req";
	/**付款查询**/
	public static final String service_transfer_query = "transfer_query";
	/**直扣api**/
	public static final String service_pay_req_direct_uecp = "pay_req_direct_uecp";
	/**编码 UTF-8**/
	public static final String charset_u8 = "UTF-8";
	
	/**签名方式  RSA**/
	public static final String sign_type = "RSA";
	
	/**版本号 1.0**/
	public static final String version = "1.0";
	
	/**直扣**/
	public static final String busi_type_03 = "03";
	
	/**媒介类型**/
	public static final String media_type = "MOBILE";
	
	/**身份证**/
	public static final String identity_type = "1";
	
	/**银行账户的对私  0**/
	public static final String pub_pri_flag_0 = "0";
	/**银行账户的对公 1**/
	public static final String pub_pri_flag_1 = "1";
	
	/**银行卡类型    对私借记卡**/
	public static final String card_type_0 = "0";
	/**银行卡类型    对私信用卡**/
	public static final String card_type_1 = "1";
	
	public static Logger logger = Logger.getLogger(UmpayUtil.class);
	public static SingleResp signAndVerifyForUmpay(Map<String,String> map,String seqtranno){
		SingleResp resp = new SingleResp();
		try {
			ReqData reqDataPost = Mer2Plat_v40.makeReqDataByPost(map);
			Map<String,String> paramMap = reqDataPost.getField();
			logger.info("【umpay req】"+seqtranno+",上送报文："+paramMap);
//			String url = "http://106.120.215.234:19002/spay/pay/payservice.do";
			String url = reqDataPost.getUrl();
			String ret = ANYZUtil.sendMsg(url, paramMap);
			logger.info("【umpay resp html】返回报文html："+ret);
			Map<String,String> retMap = Plat2Mer_v40.getResData(ret);
			logger.info("【umpay resp Map】"+seqtranno+",返回报文解析后Map："+ret);
			String code = retMap.get("ret_code");
			String message = retMap.get("ret_msg");
			String state = retMap.get("trade_state");
			resp.setCode(code);
			resp.setMessage(message);
			resp.setTranNo(state);
			return resp;
		} catch (ReqDataException e) {
			e.printStackTrace();
//			resp.setCode(DhbTranStatus.Fail.getCode());
			resp.setCode(DhbTranStatus.Handling.getCode());
			resp.setMessage("签名异常UmpayUtil");
			return resp;
		} catch (RetDataException e) {
			e.printStackTrace();
//			resp.setCode(DhbTranStatus.Fail.getCode());
			//这样会触发定时器查询，再次确实这笔订单的支付状态
			resp.setCode(DhbTranStatus.Handling.getCode());
			resp.setMessage("验签异常UmpayUtil");
			return resp;
		}
	}

}
