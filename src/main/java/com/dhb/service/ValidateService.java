package com.dhb.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.anyz.entity.Constants;
import com.dhb.anyz.service.ANYZUtil;
import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.service.KeyInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.nfc.entity.NfcOrderWater;
import com.dhb.nfc.service.QrCodeUtil;
import com.dhb.util.DateUtil;
import com.dhb.util.JsonUtil;
import com.dhb.util.StringUtil;
import com.google.common.base.Strings;

@Service
public class ValidateService {
	public static Logger logger = Logger.getLogger(ValidateService.class);
	@Autowired
	private KeyInfoDao keyService;
	@Autowired
	private CommonObjectDao commonObjectDao;
	public void validatePayReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{
		String channelId = reqInfo.getChannelId();
		if(Strings.isNullOrEmpty(channelId)){
			throw new CutException("channelId is not null");
		}
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId)){
			throw new CutException("merchid is not null");
		}
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo)){
			throw new CutException("tranNo is not null");
		}
		String toAccNo = reqInfo.getAccNo();
		if(Strings.isNullOrEmpty(toAccNo)){
			throw new CutException("toAccNo is not null");
		}
		String toAccName = reqInfo.getAccName();
		if(Strings.isNullOrEmpty(toAccName)){
			throw new CutException("toAccName is not null");
		}
		String bankName = reqInfo.getBankName();
		if(Strings.isNullOrEmpty(bankName)){
			throw new CutException("bankName is not null");
		}
		String bankCode = reqInfo.getBankCode();
		if(Strings.isNullOrEmpty(bankCode)){
			throw new CutException("bankCode is not null");
		}
		String accType = reqInfo.getAccType();
		if(Strings.isNullOrEmpty(accType)){
			throw new CutException("accType is not null");
		}
		Double banlance = reqInfo.getBanlance();
		if(banlance==null){
			throw new CutException("banlance is not null");
		}
		if(channelId.equals(Constants.ysb_channelId)){
			String remark = reqInfo.getRemark();
			if(Strings.isNullOrEmpty(remark) ){
				throw new CutException("remark is not null");
			}
		}
		if(channelId.equals(Constants.anyz_channelId)){
			String mobile = reqInfo.getMobile();
			if(Strings.isNullOrEmpty(mobile)){
				throw new CutException("mobile is not null");
			}
		}
		if("2".equals(reqInfo.getBusinessType())){
			if(!keyService.signTranForMd5(merchId, banlance, toAccNo,tranNo,reqInfo.getSign())){
				throw new CutException("accNo("+toAccNo+") sign error");
			}
		}else{
			String secretKey = reqInfo.getSign();
			if(!keyService.signTran(merchId, banlance, toAccNo,tranNo, secretKey)){
				throw new CutException("accNo("+toAccNo+") sign error");
			}
		}
		
	}
	public void validateCutReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{
		/**
		 * 公用验证参数
		 */
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo)){
			throw new CutException("tranNo is not null");
		}
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId)){
			throw new CutException("merchid is not null");
		}
		String channelId = reqInfo.getChannelId();
		if(Strings.isNullOrEmpty(channelId)){
			throw new CutException("channelId is not null");
		}
		//业务类型
		String busniessType = reqInfo.getBusinessType();
		String bankName = reqInfo.getBankName();
		if(Strings.isNullOrEmpty(bankName)){
			throw new CutException("bankName is not null");
		}
		String accNo = reqInfo.getAccNo();
		if(Strings.isNullOrEmpty(accNo)){
			throw new CutException("accNo is not null");
		}
		String accType = reqInfo.getAccType();
		if(Strings.isNullOrEmpty(accType)){
			throw new CutException("accType is not null");
		}
		Double banlance = reqInfo.getBanlance();
		if(banlance==null){
			throw new CutException("banlance is not null");	
		}
		String certType = reqInfo.getCertType();
		if(Strings.isNullOrEmpty(certType)){
			throw new CutException("certType is not null");
		}	  
		if(channelId.equals(Constants.anyz_channelId) && Constants.business_type_1.equals(busniessType)){
			//银行编码
			String bankCode = reqInfo.getBankCode();
			if(Strings.isNullOrEmpty(bankCode)){
				throw new CutException("bankCode is not null");
			}
			//银行账户名称，证件号，手机号不传
		}else if(channelId.equals(Constants.anyz_channelId) && (StringUtil.isEmpty(busniessType) || Constants.business_type_0.equals(busniessType))){
			//银行编码
			String bankCode = reqInfo.getBankCode();
			if(Strings.isNullOrEmpty(bankCode)){
				throw new CutException("bankCode is not null");
			}
			//银行账户名称，证件号，手机号必传
			String accName = reqInfo.getAccName();
			if(Strings.isNullOrEmpty(accName)){
				throw new CutException("accName is not null");
			}
			String certNo = reqInfo.getCertNo();
			if(Strings.isNullOrEmpty(certNo)){
				throw new CutException("certNo is not null");
			}
			String mobile = reqInfo.getMobile();
			if(Strings.isNullOrEmpty(mobile)){
				throw new CutException("mobile is not null");
			}
		}else{//不符合爱农渠道的业务
			String accName = reqInfo.getAccName();
			if(Strings.isNullOrEmpty(accName)){
				throw new CutException("accName is not null");
			}
			String certNo = reqInfo.getCertNo();
			if(Strings.isNullOrEmpty(certNo)){
				throw new CutException("certNo is not null");
			}
		}
		if(channelId.equals(Constants.ysb_channelId)){
			String subContractId = reqInfo.getSubContractId();
			if(Strings.isNullOrEmpty(subContractId)){
				throw new CutException("subContractId is not null");
			}
			String remark = reqInfo.getRemark();
			if(Strings.isNullOrEmpty(remark)){
				throw new CutException("remark is not null");
			}
		}
//		String secretKey = reqInfo.getSign();
//		if(!keyService.signTran(merchId, banlance, accNo,tranNo, secretKey)){
//			throw new CutException("accNo("+accNo+") sign error");
//		}
		if("2".equals(reqInfo.getBusinessType())){
			if(!keyService.signTranForMd5(merchId, banlance, accNo,tranNo,reqInfo.getSign())){
				throw new CutException("accNo("+accNo+") sign error");
			}
		}else{
			String secretKey = reqInfo.getSign();
			if(!keyService.signTran(merchId, banlance, accNo,tranNo, secretKey)){
				throw new CutException("accNo("+accNo+") sign error");
			}
		}
		
		
	}
	public void validateQueryReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{
		String channelId = reqInfo.getChannelId();
		if(Strings.isNullOrEmpty(channelId))
			throw new CutException("channelId is not null");
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo))
			throw new CutException("tranNo is not null");
		if("2".equals(reqInfo.getBusinessType())){
			if(!keyService.signQueryForMd5(merchId,tranNo,reqInfo.getSign())){
				throw new CutException("tranNo("+tranNo+") sign error");
			}
		}else{
			String secretKey = reqInfo.getSign();
			if(!keyService.signQuery(merchId,tranNo, secretKey)){
				throw new CutException("tranNo("+tranNo+") sign error");
			}
		}
//		String secretKey = reqInfo.getSign();
//		if(!keyService.signQuery(merchId,tranNo,  secretKey)){
//			throw new CutException("tranNo("+tranNo+") sign error");
//		}
	}
	public void validateRealNameReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{	
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo))
			throw new CutException("tranNo is not null");
		String accNo = reqInfo.getAccNo();
		if(Strings.isNullOrEmpty(accNo))
			throw new CutException("accNo is not null");
		String accName = reqInfo.getAccName();
		if(Strings.isNullOrEmpty(accName))
			throw new CutException("accName is not null");
		String certNo = reqInfo.getCertNo();
		if(Strings.isNullOrEmpty(certNo))
			throw new CutException("certNo is not null");
		String signSrc = merchId+tranNo+accNo+certNo;
		String sign = reqInfo.getSign();
		if(!keyService.sign(merchId, signSrc, sign)){
			throw new CutException("tranNo("+tranNo+") sign error");
		}
	}
	public void validate4RealNameReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{	
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo))
			throw new CutException("tranNo is not null");
		String accNo = reqInfo.getAccNo();
		if(Strings.isNullOrEmpty(accNo))
			throw new CutException("accNo is not null");
		String accName = reqInfo.getAccName();
		if(Strings.isNullOrEmpty(accName))
			throw new CutException("accName is not null");
		String certNo = reqInfo.getCertNo();
		String tel = reqInfo.getTel();
		if(Strings.isNullOrEmpty(tel))
			throw new CutException("tel is not null");
		if(Strings.isNullOrEmpty(certNo))
			throw new CutException("certNo is not null");
		//String signSrc = merchId+tranNo+accNo+accName+certNo+tel;
//		String secretKey = reqInfo.getSign();
//		if(!keyService.signQuery(merchId,tranNo,  secretKey)){
//			throw new CutException("tranNo("+tranNo+") sign error");
//		}
		if("2".equals(reqInfo.getBusinessType())){
			if(!keyService.signQueryForMd5(merchId,tranNo,reqInfo.getSign())){
				throw new CutException("accNo("+tranNo+") sign error");
			}
		}else{
			String secretKey = reqInfo.getSign();
			if(!keyService.signQuery(merchId,tranNo, secretKey)){
				throw new CutException("tranNo("+tranNo+") sign error");
			}
		}
	}
	public void validate6RealNameReqInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo))
			throw new CutException("tranNo is not null");
		String accNo = reqInfo.getAccNo();
		if(Strings.isNullOrEmpty(accNo))
			throw new CutException("accNo is not null");
		String accName = reqInfo.getAccName();
		if(Strings.isNullOrEmpty(accName))
			throw new CutException("accName is not null");
		String certNo = reqInfo.getCertNo();
		String tel = reqInfo.getTel();
		if(Strings.isNullOrEmpty(tel))
			throw new CutException("tel is not null");
		if(Strings.isNullOrEmpty(certNo))
			throw new CutException("certNo is not null");
		String cvn2 = reqInfo.getCvn2();
		if(Strings.isNullOrEmpty(cvn2))
			throw new CutException("cvn2 is not null");
		String validityTerm = reqInfo.getValidityTerm();
		if(Strings.isNullOrEmpty(validityTerm))
			throw new CutException("validityTerm is not null");
		//String signSrc = merchId+tranNo+accNo+accName+certNo+tel;
		String secretKey = reqInfo.getSign();
		if(!keyService.signQuery(merchId,tranNo,  secretKey)){
			throw new CutException("tranNo("+tranNo+") sign error");
		}
	}
	public boolean validateTranNoIsExist(OutRequestInfo reqInfo)
	{
		String merchId = reqInfo.getMerchId();
		String tranNo = reqInfo.getTranNo();
		
		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outId=:outId";
		String recordId = commonObjectDao.findSingleVal(sql, new Object[]{merchId,tranNo});
		if(recordId==null){
			return false;
		}else{
			return true;
		}
		
	}
	public boolean validateBatchIdIsExist(BatchTranReq reqInfo)
	{
		String merchId = reqInfo.getMerchId();
		String batchId = reqInfo.getBatchId();
		String channelId = reqInfo.getChannelId();
		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outBatchId=:outBatchId and channelId=:channelId";
		String recordId = commonObjectDao.findSingleVal(sql, new Object[]{merchId,batchId,channelId});
		if(recordId==null){
			return false;
		}else{
			for(OutRequestInfo Info :reqInfo.getInfo()){
				boolean b=validateTranNoIsExist(Info);
				if(b){
					return true;
				}
			}
			return false;
		}
		
	}
	public void validateBatchIsNull(BatchTranReq reqInfo)throws CutException
	{
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String tranNo = reqInfo.getBatchId();
		if(Strings.isNullOrEmpty(tranNo))
			throw new CutException("tranNo is not null");
		Double banlance = reqInfo.getTotalBalance();
		if(banlance==null)
			throw new CutException("banlance is not null");
		double sumMoney = 0l;
		List<OutRequestInfo>  list =reqInfo.getInfo();
		for(OutRequestInfo info:list){
			validatePayReqInfoIsNull(info);
			sumMoney+=info.getBanlance();
		}
		if(banlance!=sumMoney){
			throw new CutException("Totalbanlance is not accord with the sum of children banlance");
		}
	}
	public String validateShowMessageIsNull(Map<String,Object> map)
	{		Object tranNo = map.get("tranNo");
			Object mobiles = map.get("mobiles");
			Object flag = map.get("flag");
			Object type = map.get("type");
			Map<String,Object> jsonMap = null;
		try{
			if(tranNo == null || tranNo.toString().equals("")){
				jsonMap = JsonUtil.getReturnMessageHead(tranNo.toString(),DhbTranStatus.Fail.getCode(),"tranNo is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(mobiles == null || mobiles.toString().equals("")){
				jsonMap = JsonUtil.getReturnMessageHead(tranNo.toString(),DhbTranStatus.Fail.getCode(),"mobiles is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(flag == null || flag.toString().equals("")){
				jsonMap = JsonUtil.getReturnMessageHead(tranNo.toString(),DhbTranStatus.Fail.getCode(),"flag is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(type == null || type.toString().equals("")){
				jsonMap = JsonUtil.getReturnMessageHead(tranNo.toString(),DhbTranStatus.Fail.getCode(),"type is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
		}catch(Exception e){
			logger.error("验证发送短信接口参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnMessageHead(tranNo.toString(),DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	public String validateSingleCutContractIsNull(Map<String,Object> map){
		Map<String,Object> jsonMap = null;
		try{
			if(Strings.isNullOrEmpty(map.get("tranNo").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"tranNo is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("merchId").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"merchId is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("channelId").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"channelId is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("accNo").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"accNo is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("accName").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"accName is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("certNo").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"certNo is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("mobile").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"mobile is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("startDate").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"startDate is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("endDate").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"endDate is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			String secretKey = map.get("sign").toString();
			if(!keyService.signQuery(map.get("merchId").toString(),map.get("tranNo").toString(),secretKey)){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"sign error");
				return JsonUtil.getMapToJson(jsonMap);
			}
		}catch(Exception e){
			logger.error("单笔代扣子协议录入接口参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	
	public String validateSubContractExtensionIsNull(Map<String,Object> map){
		Map<String,Object> jsonMap = null;
		try{
			if(Strings.isNullOrEmpty(map.get("tranNo").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"tranNo is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("merchId").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"merchId is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("channelId").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"channelId is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("subContractId").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"subContractId is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("startDate").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"startDate is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(Strings.isNullOrEmpty(map.get("endDate").toString()) ){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"endDate is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			String secretKey = map.get("sign").toString();
			if(!keyService.signQuery(map.get("merchId").toString(),map.get("tranNo").toString(),secretKey)){
				jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),"sign error");
				return JsonUtil.getMapToJson(jsonMap);
			}
		}catch(Exception e){
			logger.error("子协议延期接口参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnMessageHead(map.get("tranNo").toString(),DhbTranStatus.Fail.getCode(),DhbTranStatus.Fail.getDescription());
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	
	private boolean checkTime(Date date){
		return DateUtil.isLessMinutes(date, 3);
	}
	/**
	 * NFC
	 * 验证NFC订单是否存在
	 * @author pie
	 */
	public boolean validateNFCorderNoIsExist(Map<String,Object> map)
	{
		String order_no = map.get("order_no").toString();
		String merch_no = map.get("merch_no").toString();
		
		String sql = "select order_no from dhb_nfc_order where order_no=:order_no and merch_no=:merch_no";
//		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outId=:outId";
		String orderNo = commonObjectDao.findSingleVal(sql, new Object[]{order_no,merch_no});
		if(orderNo==null){
			return false;
		}else{
			return true;
		}
		
	}
	//验证需要退货的订单是否存在
	public boolean validateNFCRefundorderNoIsExist(Map<String,Object> map)
	{
		String refund_order_no = map.get("refund_order_no").toString();
		String merch_no = map.get("merch_no").toString();
		
		String sql = "select order_no from dhb_nfc_order where order_no=:refund_order_no and merch_no=:merch_no";
//		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outId=:outId";
		String orderNo = commonObjectDao.findSingleVal(sql, new Object[]{refund_order_no,merch_no});
		if(orderNo==null){
			return false;
		}else{
			return true;
		}
		
	}
	/**
	 * NFC
	 * 根据NFC订单获取相应信息	 
	 */
	public NfcOrderWater getNfcOrderWater(String merOrderNo,String orderNo){
		NfcOrderWater water = null;
		String sql = "select w.order_no,w.merch_channel,to_char(w.total_fee,'FM999999999990.00') as total_fee,w.status,w.message,w.end_time,w.sub_merch_no,w.merch_no,w.notify_url,w.mer_order_no from dhb_nfc_order_water w where 1=1 ";
		if(merOrderNo !=null &&  merOrderNo.length() != 0){
			sql += " and w.mer_order_no=:merOrderNo";
			water= commonObjectDao.findOneObject(sql, NfcOrderWater.class, new Object[]{merOrderNo});
		}
		if(orderNo !=null &&  orderNo.length() != 0){
			sql += " and w.order_no=:orderNo";
			water= commonObjectDao.findOneObject(sql, NfcOrderWater.class, new Object[]{orderNo});
		}
		return water;
	}
	/**
	 * NFC
	 * 根据NFC订单修改订单交易流水表状态，描述，最终时间点
	 */
	public void getUpateNfcOrderWater(NfcOrderWater water,String type){
		String sql = "";
		if(type != null && !"".equals(type)){
			sql = "update dhb_nfc_order_water set status=:status,message=:message,end_time=:endTime,nfc_merch=:nfcMerch where mer_order_no=:merOrderNo";
		}else{
			sql = "update dhb_nfc_order_water set status=:status,message=:message,end_time=:endTime where mer_order_no=:merOrderNo";
		}
		commonObjectDao.saveOrUpdate(sql, water);
	}
	
	/**
	 * NFC
	 * 验证NFC交易接口请求参数
	 * @author pie
	 */
	public String validateNFCparamIsNull(Map<String,Object> map){		
		Map<String,Object> jsonMap = null;
		Object order_no = map.get("order_no");
		Object sub_merch_no = map.get("sub_merch_no");
		Object merch_no = map.get("merch_no");
		Object nfc_type = map.get("nfc_type");
		Object nfc_merch = map.get("nfc_merch");
		Object merch_channel = map.get("merch_channel");
		Object clearType = map.get("clearType");
		Object limit_pay = map.get("limit_pay");
		Object auth_code = map.get("auth_code");
		Object product_name = map.get("product_name");
		Object product_desc = map.get("product_desc");
		Object total_fee = map.get("total_fee");
		Object merch_rate = map.get("merch_rate");
		Object refund_fee = map.get("refund_fee");
		Object refund_order_no = map.get("refund_order_no");
		Object reversal_order_no = map.get("reversal_order_no");
		Object refund_channe = map.get("refund_channe");
		Object query_order_no = map.get("query_order_no");
		Object currency = map.get("currency");
		Object notify_url = map.get("notify_url");
		Object remark = map.get("remark");
		Object sign = map.get("sign");
		try{
			if(order_no == null || order_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"order_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(sub_merch_no == null || sub_merch_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sub_merch_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(merch_no == null || merch_no.toString().toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(nfc_type == null || nfc_type.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_type is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(nfc_merch == null || nfc_merch.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_merch is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(currency == null || currency.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"currency is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(com.dhb.nfc.entity.Constants.nfc_merch_channel_qj.equals(merch_channel)){
				if(clearType == null || clearType.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"clearType is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}
			//判断主扫/被扫/冲正/退货/查询
			if(nfc_type.equals(com.dhb.nfc.entity.Constants.nfc_passive)){//被扫
				if(product_name == null || product_name.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"product_name is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(product_desc == null || product_desc.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"product_desc is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(total_fee == null || total_fee.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}else if(ANYZUtil.isNumber(total_fee.toString()) == false){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is Invalid format");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(merch_rate == null || merch_rate.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_rate is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(notify_url == null || notify_url.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"notify_url is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else if(nfc_type.equals(com.dhb.nfc.entity.Constants.nfc_active)){//主扫
				if(product_name == null || product_name.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"product_name is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(product_desc == null || product_desc.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"product_desc is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(total_fee == null || total_fee.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}else if(ANYZUtil.isNumber(total_fee.toString()) == false){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is Invalid format");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(merch_rate == null || merch_rate.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_rate is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(auth_code == null || auth_code.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"auth_code is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else if(nfc_type.equals(com.dhb.nfc.entity.Constants.nfc_reversal)){//冲正
				if(reversal_order_no == null || reversal_order_no.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"reversal_order_no is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else if(nfc_type.equals(com.dhb.nfc.entity.Constants.nfc_refund)){//退货
				if(total_fee == null || total_fee.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}else if(ANYZUtil.isNumber(total_fee.toString()) == false){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is Invalid format");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(refund_fee == null || refund_fee.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"refund_fee is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}else if(ANYZUtil.isNumber(refund_fee.toString()) == false){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"refund_fee is Invalid format");
					return JsonUtil.getMapToJson(jsonMap);
				}
				if(refund_order_no == null || refund_order_no.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"refund_order_no is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
				//判断总金额和实际退款金额
				if(refund_fee.toString().compareTo(total_fee.toString()) > 0){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"refund_fee must be smaller than the total_fee");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else if(nfc_type.equals(com.dhb.nfc.entity.Constants.nfc_query)){
				
			}else{
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_type is error");
				return JsonUtil.getMapToJson(jsonMap);
			}
			//验证签名
			if(sign == null || sign.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}else{//验签
				//组装待签名数据
				String waitSign = QrCodeUtil.getBuildPayParams(map);
				if(!keyService.getNewNFCsignValidate(sign.toString(), waitSign, sub_merch_no.toString(), order_no.toString())){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is error");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}
		}catch(Exception e){
			logger.error("验证NFC交易请求参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"验证NFC交易请求参数异常");
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	/**
	 * NFC
	 * 验证NFC通道支付接口请求参数
	 * @author pie
	 */
	public String validateNFCchannelParamIsNull(Map<String,Object> map){
		Map<String,Object> jsonMap = null;
		Object order_no = map.get("order_no");
		Object sub_merch_no = map.get("sub_merch_no");
		Object merch_no = map.get("merch_no");
		Object merch_channel = map.get("merch_channel");
		Object clearType = map.get("clearType");
		Object limit_pay = map.get("limit_pay");
		Object nfc_type = map.get("nfc_type"); 
		Object openId = map.get("openId");
		Object buyerLogonId = map.get("buyerLogonId");
		Object buyerId = map.get("buyerId");
		Object nfc_merch = map.get("nfc_merch");
		Object total_fee = map.get("total_fee");
		Object merch_rate = map.get("merch_rate");
		Object currency = map.get("currency");
		Object notify_url = map.get("notify_url");
		Object product_name = map.get("product_name");
		Object remark = map.get("remark");
		Object sign = map.get("sign");
		try{
			if(order_no == null || order_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"order_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(sub_merch_no == null || sub_merch_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sub_merch_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(merch_no == null || merch_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(merch_channel == null || merch_channel.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_channel is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			
			if(nfc_type == null || nfc_type.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_type is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(nfc_merch == null || nfc_merch.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_merch is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(total_fee == null || total_fee.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}else if(ANYZUtil.isNumber(total_fee.toString()) == false){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"total_fee is Invalid format");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(merch_rate == null || merch_rate.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_rate is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(currency == null || currency.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"currency is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(notify_url == null || notify_url.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"notify_url is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(product_name == null || product_name.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"product_name is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(merch_channel.equals(com.dhb.nfc.entity.Constants.nfc_merch_channel_ccb)){
				if(nfc_merch.equals(com.dhb.nfc.entity.Constants.wechat_nfc_merch)){//微信
					if(openId == null || openId.toString().equals("")){
						jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"openId is not null");
						return JsonUtil.getMapToJson(jsonMap);
					}
				}else if(nfc_merch.equals(com.dhb.nfc.entity.Constants.alipay_nfc_merch)){//支付宝
					if((buyerLogonId == null || buyerLogonId.toString().equals("")) && (buyerId == null || buyerId.toString().equals(""))){
						jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"buyerLogonId and buyerId can't at the same time is null");
						return JsonUtil.getMapToJson(jsonMap);
					}
				}else{
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"nfc_merch is error");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else if(merch_channel.equals(com.dhb.nfc.entity.Constants.nfc_merch_channel_qj)){
				if(clearType == null || clearType.toString().equals("")){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"clearType is not null");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}else{
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"merch_channel is error");
				return JsonUtil.getMapToJson(jsonMap);
			}
			
			
			//验证签名
			if(sign == null || sign.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}else{//验签
				//组装待签名数据
				String waitSign = QrCodeUtil.getBuildPayParams(map);
				if(!keyService.getNewNFCsignValidate(sign.toString(), waitSign, sub_merch_no.toString(), order_no.toString())){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is error");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}
		}catch(Exception e){
			logger.error("验证NFC通道支付请求参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"验证NFC通道支付请求参数异常");
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	/**
	 * NFC-进件&&修改商户资料&&手动重导对账数据接口验签方法
	 * @author pie
	 */
	public String validateNFCqj(Map<String,Object> map){
		Map<String,Object> jsonMap = null;
		Object order_no = map.get("order_no");
		Object sub_merch_no = map.get("sub_merch_no");
		Object sign = map.get("sign");
		try{
			if(order_no == null || order_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"order_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			if(sub_merch_no == null || sub_merch_no.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sub_merch_no is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}
			//验证签名
			if(sign == null || sign.toString().equals("")){
				jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is not null");
				return JsonUtil.getMapToJson(jsonMap);
			}else{//验签
				//组装待签名数据
				String waitSign = QrCodeUtil.getBuildPayParams(map);
				if(!keyService.getNewNFCsignValidate(sign.toString(), waitSign, sub_merch_no.toString(), order_no.toString())){
					jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"sign is error");
					return JsonUtil.getMapToJson(jsonMap);
				}
			}
		}catch(Exception e){
			logger.error("验证NFC进件请求参数异常："+e.getMessage());
			//构建错误报文
			jsonMap = JsonUtil.getReturnNFCMessageHead(order_no.toString(),com.dhb.nfc.entity.Constants.nfc_pay_status_9,"验证NFC进件请求参数异常");
			return JsonUtil.getMapToJson(jsonMap);
		}
		return "success";
	}
	
	public SingleResp validateCutContractUmpayIsNull(OutRequestInfo req,SingleResp resp){
		resp.setCode(DhbTranStatus.Fail.getCode());
		try{
			if(Strings.isNullOrEmpty(req.getTranNo()) ){
				resp.setMessage("tranNo is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getMerchId()) ){
				resp.setMessage("merchId is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getChannelId()) ){
				resp.setMessage("channelId is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getAccNo()) ){
				resp.setMessage("accNo is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getAccName()) ){
				resp.setMessage("accName is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getAccType())){
				resp.setMessage("accType is not null");
				return resp;
			}
			if(!"00".equals(req.getAccType()) && !"01".equals(req.getAccType())){
				resp.setMessage("accType is not 00 or 01");
				return resp;
			}
			if("01".equals(req.getAccType())){//对公
				if(Strings.isNullOrEmpty(req.getCardName())){
					resp.setMessage("cardName is not null");
					return resp;
				}
				if(Strings.isNullOrEmpty(req.getBankCode())){
					resp.setMessage("bankCode is not null");
					return resp;
				}
			}
			if(Strings.isNullOrEmpty(req.getCardType())){
				resp.setMessage("cardType is not null");
				return resp;
			}
			if(!"01".equals(req.getCertType())){
				resp.setMessage("certType is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getCertNo()) ){
				resp.setMessage("certNo is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getMobile()) ){
				resp.setMessage("mobile is not null");
				return resp;
			}
			if(Strings.isNullOrEmpty(req.getSubContractId()) ){
				resp.setMessage("subContractId is not null");
				return resp;
			}
			String secretKey = req.getSign();
			if(!keyService.signQuery(req.getMerchId(),req.getTranNo(),secretKey)){
				resp.setMessage("sign error");
				return resp;
			}
		}catch(Exception e){
			e.printStackTrace();
			//构建错误报文
			resp.setMessage("Exception");
			return resp;
		}
		resp.setCode(DhbTranStatus.Succ.getCode());
		resp.setMessage(DhbTranStatus.Succ.getDescription());
		return resp;
	}
	
	public void validateCutReqUmpayInfoIsNull(OutRequestInfo reqInfo)throws CutException
	{
		/**
		 * 公用验证参数
		 */
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo)){
			throw new CutException("tranNo is not null");
		}
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId)){
			throw new CutException("merchid is not null");
		}
		String channelId = reqInfo.getChannelId();
		if(Strings.isNullOrEmpty(channelId)){
			throw new CutException("channelId is not null");
		}
		Double banlance = reqInfo.getBanlance();
		if(banlance==null){
			throw new CutException("banlance is not null");	
		}
		String mobile = reqInfo.getMobile();
		if(Strings.isNullOrEmpty(mobile)){
			throw new CutException("mobile is not null");
		}
		String subContractId = reqInfo.getSubContractId();
		if(Strings.isNullOrEmpty(subContractId)){
			throw new CutException("subContractId is not null");
		}
		String secretKey = reqInfo.getSign();
		if(!keyService.signTran(merchId, banlance, mobile,tranNo, secretKey)){
			throw new CutException("tranNo("+tranNo+") sign error");
		}
	}
	
	public void validateBatchIsNullForUmpay(BatchTranReq reqInfo)throws CutException
	{
		String merchId = reqInfo.getMerchId();
		if(Strings.isNullOrEmpty(merchId))
			throw new CutException("merchid is not null");
		String batchId = reqInfo.getBatchId();
		if(Strings.isNullOrEmpty(batchId))
			throw new CutException("batchId is not null");
		String channelId = reqInfo.getChannelId();
		if(Strings.isNullOrEmpty(channelId))
			throw new CutException("channelId is not null");
		Double banlance = reqInfo.getTotalBalance();
		if(banlance==null)
			throw new CutException("TotalBalance is not null");
		Integer num = reqInfo.getTotalNum();
		if(num == null)
			throw new CutException("TotalNum is not null");
		
		double sumMoney = 0l;
		List<OutRequestInfo>  list =reqInfo.getInfo();
		for(OutRequestInfo info:list){
			validatePayReqInfoIsNullForUmpay(info,merchId);
			sumMoney+=info.getBanlance();
		}
		if(banlance!=sumMoney){
			throw new CutException("Totalbanlance is not accord with the sum of children banlance");
		}
	}
	private void validatePayReqInfoIsNullForUmpay(OutRequestInfo reqInfo,String merId)throws CutException
	{
		String tranNo = reqInfo.getTranNo();
		if(Strings.isNullOrEmpty(tranNo)){
			throw new CutException("tranNo is not null");
		}
		String mobile = reqInfo.getMobile();
		if(Strings.isNullOrEmpty(mobile)){
			throw new CutException("mobile is not null");
		}
		Double banlance = reqInfo.getBanlance();
		if(banlance==null){
			throw new CutException("banlance is not null");
		}
		String SubContractId = reqInfo.getSubContractId();
		if(Strings.isNullOrEmpty(SubContractId)){
			throw new CutException("SubContractId is not null");
		}
		String secretKey = reqInfo.getSign();
		if(!keyService.signTran(merId, banlance, SubContractId,tranNo, secretKey)){
			throw new CutException("tranNo("+tranNo+") sign error");
		}
	}
}
