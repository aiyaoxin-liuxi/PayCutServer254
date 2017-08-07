package com.dhb.kl.realname.service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.service.DhbOutMerchantDao;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBizJournal;
import com.dhb.entity.ProxyMerchAmt;
import com.dhb.entity.RealNameInfo;
import com.dhb.entity.SingleResp;
import com.dhb.kl.realname.entity.FourReqData;
import com.dhb.kl.realname.entity.KLReq;
import com.dhb.kl.realname.entity.KLRetData;
import com.dhb.kl.realname.entity.KLReturn;
import com.dhb.kl.realname.entity.SixReqData;
import com.dhb.kl.realname.entity.ThreeReqData;
import com.dhb.kl.util.DES;
import com.dhb.kl.util.RSA;
import com.dhb.util.ArithUtil;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

@Service
public class ZXRealNameService {
	private Logger logger = Logger.getLogger(ZXRealNameService.class);
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
	public void sign(KLReq req){
		Gson g = new Gson();
		 String key = "SD8JK29E";//东汇宝key=DHd1B4ZF
		 DES des=new DES(key);
		 try {
			 String data = g.toJson(req.getMap());
			 data=des.encrypt(data);
			 req.setReqData(data);
			 String sign=RSA.sign(data, PropFileUtil.getByFileAndKey("lk.properties", "PRIVATE_KEY"));
			 req.setSign(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static boolean verifyReturn(String result) {
    	Gson g = new Gson();
    	KLReturn klReturn = g.fromJson(result, KLReturn.class);
 		if(!"000000".equals(klReturn.getRetCode())){
 			System.err.println("--------ReturnCode:"+klReturn.getRetCode()+"--------—ReturnMsg:"+klReturn.getRetMsg());
 			return false;
 		}
 		boolean isSign = RSA.verify(String.valueOf(klReturn.getRetData()),String.valueOf(klReturn.getSign()), PropFileUtil.getByFileAndKey("lk.properties", "LKL_PUBLIC_KEY"));
	    //验证签名
 		return isSign;

    }
	/**
	 * 三要素
	 * @param reqInfo
	 * @return
	 */
	public SingleResp realName(OutRequestInfo reqInfo){
		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setAccNo(fromAccNo);
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		//查询该记录是否做了银行卡实名认证
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		KLReq req = new KLReq();
   		ThreeReqData reqData = new ThreeReqData();
   		reqData.setAccountNo(fromAccNo);
   		reqData.setIdCardCore(fromCertNo);
   		reqData.setName(fromAccName);
   		req.getMap().put("idCardCore", fromCertNo);
   		req.getMap().put("name", fromAccName);
   		req.getMap().put("accountNo", fromAccNo);
   		sign(req);
   		req.setCustomerId(PropFileUtil.getByFileAndKey("lk.properties", "customerId"));
		HttpRequestParam param = new HttpRequestParam();
		Map<String,String> heads = Maps.newHashMap();
		param.setUrl(PropFileUtil.getByFileAndKey("lk.properties", "url"));
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", req.getCustomerId());
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId", PropFileUtil.getByFileAndKey("lk.properties", "prdGrpId"));
		params.put("prdId",PropFileUtil.getByFileAndKey("lk.properties", "prdId"));
		param.setParams(params);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		String sHtmlTextToken = resp.getContent();
		logger.info("拉卡拉返回认证结果:"+sHtmlTextToken);
		
		//*****start  大网关外放   修改商户账户(余额和可用余额均  减去手续费])****************//
		DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
		if(merchant != null){
			updateMerAccount(reqInfo,merchant);
		}
        //*****end*********************************************************//
		//验证签名
		boolean verifyFlag = verifyReturn(sHtmlTextToken);
		if(!verifyFlag){
			logger.error("三元素实名认证：验证消息失败 "+verifyFlag);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
   			return singleResp;
		}
		//解析返回结果
		KLRetData retData = getResData(sHtmlTextToken);
		logger.info("三元素实名认证返回的结果："+retData);
		//“T”表示有效的， ”F”表示无效的， “N”表示无法认证的， ”P”表示网络连接超时
		String result = retData.getResult();
		if("T".equals(result)){
			logger.info("实名认证结果：result="+result+","+retData.getMessage());
			//认证成功后添加一条记录
			String insertSql ="insert into dhb_realName(certNo,accNo,userName) values(:certNo,:accNo,:userName)";
    		commonObjectDao.saveOrUpdate(insertSql, realName);
    		
			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
		}else{
			logger.error("实名认证结果：result="+result+","+retData.getMessage());
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			singleResp.setMessage(retData.getMessage());
   			return singleResp;
		}
	}
	public KLRetData getResData(String sHtmlTextToken){
    	Gson g = new Gson();
    	KLReturn klReturn = g.fromJson(sHtmlTextToken, KLReturn.class);
    	String reqTransData = klReturn.getRetData();
    	DES des = new DES("SD8JK29E");
		try {
			reqTransData=des.decrypt(reqTransData);
			logger.info("retData解析之后："+reqTransData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		KLRetData data = g.fromJson(reqTransData, KLRetData.class);
		return data;
    }
	public SingleResp fourRealName(OutRequestInfo reqInfo){
		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
		}
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String fromTel = reqInfo.getTel();
   		//四要素认证交易类型
   		String fromBusinessType = reqInfo.getBusinessType();
   		if(fromBusinessType == null || fromBusinessType.equals("")){
   			fromBusinessType = "0";
   		}
   		String sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName and tel=:tel and business_Type=:businessType";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setAccNo(fromAccNo);
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		realName.setTel(fromTel);
   		realName.setBusinessType(fromBusinessType);
   		//四要素认证创建时间
   		try {
			String strDate = DateUtil.format(new Date());
			realName.setCreatedTime(DateUtil.strToDate(strDate));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createdTime type convert error" + e.getMessage());
		}
   		//查询该记录是否做了银行卡实名认证
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName,fromTel,fromBusinessType});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		KLReq req = new KLReq();
   		FourReqData reqData = new FourReqData();
   		reqData.setAccountNo(fromAccNo);
   		reqData.setIdCardCore(fromCertNo);
   		reqData.setName(fromAccName);
   		reqData.setBankPreMobile(fromTel);
   		req.getMap().put("idCardCore", fromCertNo);
   		req.getMap().put("name", fromAccName);
   		req.getMap().put("accountNo", fromAccNo);
   		req.getMap().put("bankPreMobile", fromTel);
   		sign(req);
   		req.setCustomerId(PropFileUtil.getByFileAndKey("lk.properties", "customerId"));
		HttpRequestParam param = new HttpRequestParam();
		Map<String,String> heads = Maps.newHashMap();
		param.setUrl(PropFileUtil.getByFileAndKey("lk.properties", "url"));
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", req.getCustomerId());
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId", PropFileUtil.getByFileAndKey("lk.properties", "prdGrpId"));
		params.put("prdId",PropFileUtil.getByFileAndKey("lk.properties", "prdId4"));
		param.setParams(params);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		String sHtmlTextToken = resp.getContent();
		logger.info("拉卡拉返回认证结果:"+sHtmlTextToken);
		//*****start  大网关外放   修改商户账户(余额和可用余额均  减去手续费])****************//
		DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
		if(merchant != null){
			updateMerAccount(reqInfo,merchant);
		}
        //*****end*********************************************************//
		//验证签名
		boolean verifyFlag = verifyReturn(sHtmlTextToken);
		if(!verifyFlag){
			logger.error("四元素实名认证：验证消息失败 "+verifyFlag);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
   			return singleResp;
		}
		//解析返回结果
		KLRetData retData = getResData(sHtmlTextToken);
		logger.info("四元素实名认证返回的结果："+retData);
		//“T”表示有效的， ”F”表示无效的， “N”表示无法认证的， ”P”表示网络连接超时
		String result = retData.getResult();
		if("T".equals(result)){
			logger.info("四实名认证结果：result="+result+","+retData.getMessage());
			//认证成功后添加一条记录
			String insertSql ="INSERT INTO DHB_REALNAME T ( CERTNO, ACCNO, USERNAME, TEL, BUSINESS_TYPE, CREATED_TIME ) VALUES(:certNo,:accNo,:userName,:tel,:businessType,:createdTime)";
    		commonObjectDao.saveOrUpdate(insertSql, realName);
			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
		}else{
			logger.error("四实名认证结果：result="+result+","+retData.getMessage());
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			//singleResp.setCode(retData.getResult());//拉卡拉返回状态
   			singleResp.setMessage(retData.getMessage());//拉卡拉返回信息
   			//认证失败后添加一条记录
			//四要素---返回信息
   	   		String errText = singleResp.getMessage();
   			realName.setErrText(errText);
   			String insertSql = "INSERT INTO DHB_REALNAME_FAIL T ( CERTNO, ACCNO, USERNAME, TEL, BUSINESS_TYPE, CREATED_TIME, ERRTEXT ) VALUES(:certNo,:accNo,:userName,:tel,:businessType,:createdTime,:errText)";
   			commonObjectDao.saveOrUpdate(insertSql, realName);
   			return singleResp;
		}
	}
	public RealNameInfo selectByAccNo(String accNo){
		String sql = "select * from dhb_realName where accNo=:accNo";
		List<RealNameInfo> realnamelist = commonObjectDao.findList(sql, RealNameInfo.class, new Object[]{accNo});
		if(realnamelist.size()<=0){
			return null;
		}
		return realnamelist.get(0);
	}
	
	public SingleResp sixRealName(OutRequestInfo reqInfo){
		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String istest = PropFileUtil.getByFileAndKey("lk.properties", "istest");
		if("test".equals(istest)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
		}
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String fromTel = reqInfo.getTel();
   		String fromCvn2 = reqInfo.getCvn2();
   		String fromValidityTerm = reqInfo.getValidityTerm();
   		//四要素认证交易类型
   		String fromBusinessType = reqInfo.getBusinessType();
   		if(fromBusinessType == null || fromBusinessType.equals("")){
   			fromBusinessType = "0";
   		}
   		String sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName and tel=:tel and business_Type=:businessType and cvn2=:cvn2 and validityTerm=:validityTerm";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setAccNo(fromAccNo);
   		realName.setCertNo(fromCertNo);
   		realName.setUserName(fromAccName);
   		realName.setTel(fromTel);
   		realName.setBusinessType(fromBusinessType);
   		realName.setCvn2(fromCvn2);
   		realName.setValidityTerm(fromValidityTerm);
   		//四要素认证创建时间
   		try {
			String strDate = DateUtil.format(new Date());
			realName.setCreatedTime(DateUtil.strToDate(strDate));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createdTime type convert error" + e.getMessage());
		}
   		//查询该记录是否做了银行卡实名认证
   		String isPass=commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName,fromTel,fromBusinessType,fromCvn2,fromValidityTerm});
   		if(isPass!=null){
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
   		KLReq req = new KLReq();
   		SixReqData reqData = new SixReqData();
   		reqData.setAccountNo(fromAccNo);
   		reqData.setIdCardCore(fromCertNo);
   		reqData.setName(fromAccName);
   		reqData.setBankPreMobile(fromTel);
   		reqData.setCvn2(fromCvn2);
   		reqData.setValidityTerm(fromValidityTerm);
   		req.getMap().put("idCardCore", fromCertNo);
   		req.getMap().put("name", fromAccName);
   		req.getMap().put("accountNo", fromAccNo);
   		req.getMap().put("bankPreMobile", fromTel);
   		req.getMap().put("cvn2", fromCvn2);
   		req.getMap().put("validityTerm", fromValidityTerm);
   		sign(req);
   		req.setCustomerId(PropFileUtil.getByFileAndKey("lk.properties", "customerId"));
		HttpRequestParam param = new HttpRequestParam();
		Map<String,String> heads = Maps.newHashMap();
		param.setUrl(PropFileUtil.getByFileAndKey("lk.properties", "url"));
		heads.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(heads);
		Map<String, String> params= Maps.newHashMap();
		params.put("customerId", req.getCustomerId());
		params.put("sign", req.getSign());
		params.put("reqData",req.getReqData());
		params.put("prdGrpId", PropFileUtil.getByFileAndKey("lk.properties", "prdGrpId"));
		params.put("prdId",PropFileUtil.getByFileAndKey("lk.properties", "prdId6"));
		param.setParams(params);
		HttpResponser resp=HttpHelp.postParamByHttpClient(param);
		String sHtmlTextToken = resp.getContent();
		logger.info("拉卡拉返回认证结果:"+sHtmlTextToken);
		//*****start  大网关外放   修改商户账户(余额和可用余额均  减去手续费])****************//
		DhbOutMerchant merchant = dhbOutMerchantDao.selectByMerId(reqInfo.getMerchId());
		if(merchant != null){
			updateMerAccount(reqInfo,merchant);
		}
        //*****end*********************************************************//
		//验证签名
		boolean verifyFlag = verifyReturn(sHtmlTextToken);
		if(!verifyFlag){
			logger.error("六元素实名认证：验证消息失败 "+verifyFlag);
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
   			return singleResp;
		}
		//解析返回结果
		KLRetData retData = getResData(sHtmlTextToken);
		logger.info("六元素实名认证返回的结果："+retData);
		//“T”表示有效的， ”F”表示无效的， “N”表示无法认证的， ”P”表示网络连接超时
		String result = retData.getResult();
		if("T".equals(result)){
			logger.info("六元素实名认证结果：result="+result+","+retData.getMessage());
			//认证成功后添加一条记录
			String insertSql ="INSERT INTO DHB_REALNAME T ( CERTNO, ACCNO, USERNAME, TEL, BUSINESS_TYPE, CREATED_TIME, CVN2, VALIDITYTERM ) VALUES(:certNo,:accNo,:userName,:tel,:businessType,:createdTime,:cvn2,:validityTerm)";
    		commonObjectDao.saveOrUpdate(insertSql, realName);
			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
		}else{
			logger.error("六元素实名认证结果：result="+result+","+retData.getMessage());
			singleResp.setCode(DhbTranStatus.Fail.getCode());
   			//singleResp.setCode(retData.getResult());//拉卡拉返回状态
   			singleResp.setMessage(retData.getMessage());//拉卡拉返回信息
   			//认证失败后添加一条记录
			//六要素---返回信息
   	   		String errText = singleResp.getMessage();
   			realName.setErrText(errText);
   			String insertSql = "INSERT INTO DHB_REALNAME_FAIL T ( CERTNO, ACCNO, USERNAME, TEL, BUSINESS_TYPE, CREATED_TIME, ERRTEXT, CVN2, VALIDITYTERM ) VALUES(:certNo,:accNo,:userName,:tel,:businessType,:createdTime,:errText,:cvn2,:validityTerm)";
   			commonObjectDao.saveOrUpdate(insertSql, realName);
   			return singleResp;
		}
	}
	public void updateMerAccount(OutRequestInfo reqInfo,DhbOutMerchant merchant){
		logger.info("[银行卡认证接口外放]记录商户流水ProxyBizJournal");
		ProxyBizJournal journal = new ProxyBizJournal();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat tf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		String outid = "CONTRACTCHANGE"+
				df.format(now)+
				tf.format(now)+System.currentTimeMillis();
		journal.setAcpacctname(null);
		journal.setAcpacctno(null);
		journal.setPayacctname(reqInfo.getAccName());
		journal.setPayacctno(reqInfo.getAccNo());
		journal.setAmount(null);
		//账户签约业务类型
		journal.setBiz_type("3");
		journal.setMemo("签约");
		journal.setChargemode(null);
		journal.setFee(null);
		journal.setMerch_id(reqInfo.getMerchId());
		journal.setOutid(outid);
		journal.setChannelId("2");
		journal.setPhone(null);
		journal.setRecord_id(reqInfo.getRecordId());
		journal.setTrans_date(df.format(now));
		journal.setTrans_time(tf.format(now));
		journal.setResp_date(df.format(now));
		journal.setResp_time(tf.format(now));
		journal.setStatus(null);
		journal.setRemark(null);
		journal.setResp_date(null);
		journal.setResp_time(null);
		journal.setFee(merchant.getMerFee());
		dhbOutMerchantDao.saveJounal(journal);
		
		logger.info("[大网关外放]修改商户账户");
		ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
		//取上次余额
		Double last_amount = proxyMerchAmt.getBalance();
		//取上次可用余额
		Double last_valid_amount= proxyMerchAmt.getValidBalance();
		
		
		/*Double currentBalance = ArithUtil.sub(last_amount, journal.getAmount());
		//可用余额变动
		Double currentValidBalance = ArithUtil.sub(last_valid_amount, journal.getAmount());
		//减手续费
		//2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		currentBalance = ArithUtil.sub(currentBalance, merchant.getMerFee());
//		currentBalance = ArithUtil.sub(currentBalance, 0);
		//end of 2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		//可用余额减手续费
		//2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费
		currentValidBalance = ArithUtil.sub(currentValidBalance, merchant.getMerFee());
//		currentValidBalance = ArithUtil.sub(currentValidBalance, 0);
		//end of 2015/08/28 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费*/
		
		//余额
		Double currentBalance = ArithUtil.sub(last_amount, merchant.getMerFee());
		//可用余额变动
		Double currentValidBalance = ArithUtil.sub(last_valid_amount, merchant.getMerFee());
		//减手续费
		
		//3.1变更一下商户虚拟账户余额
		proxyMerchAmt.setBalance(currentBalance);
		//3.2变更可用余额
		proxyMerchAmt.setValidBalance(currentValidBalance);
		dhbOutMerchantDao.updateMerAmt(proxyMerchAmt);
	}
}
