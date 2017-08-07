package com.dhb.shande.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.DhbOutMerchantDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBizJournal;
import com.dhb.entity.ProxyCarryBankacct;
import com.dhb.entity.ProxyMerchAmt;
import com.dhb.entity.RealNameInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.service.PayCutInterface;
import com.dhb.shande.entity.ProductIdSD;
import com.dhb.shande.entity.RetDataSD;
import com.dhb.util.AmountUtil;
import com.dhb.util.ArithUtil;
import com.dhb.util.Tools;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.StringUtils;

import cn.com.sand.online.agent.service.sdk.ConfigurationManager;
import cn.com.sand.online.agent.service.sdk.DynamicPropertyHelper;
import cn.com.sand.online.agent.service.sdk.HttpUtil;

/**
 * 杉德
 *
 * <strong>ShanDePayCutService</strong>. <br> 
 * <strong>Description : 杉德</strong> <br>
 * <strong>Create on : 2017年6月2日 下午5:23:39</strong>. <br>
 * <p>
 * <strong>Copyright (C) zhl Co.,Ltd.</strong> <br>
 * </p>
 * @author zts zhaotisheng@qq.com <br>
 * @version <strong>zhl-0.1.0</strong> <br>
 * <br>
 * <strong>修改历史: .</strong> <br>
 * 修改人 修改日期 修改描述<br>
 * Copyright ©  zhl by zts Inc. All Rights Reserved
 * -------------------------------------------<br>
 * <br>
 * <br>


 */
public class ShanDePayCutService implements PayCutInterface {

	public static Logger logger = Logger.getLogger(ShanDePayCutService.class);
//	private static String transCode = "RTPM";	//实时代付
	private static String payStr = "【杉德实时代付】";	//实时代付
	private static String cutStr = "【杉德实时代扣】";	//实时代付
	
	private static double minAmount_cut=5;//代收最小5元
	
	private final static DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
    
    @Autowired
	private SequenceDao sequenceDao;
    @Autowired
	private CommonObjectDao commonObjectDao;
    @Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
    @Autowired
	private DhbBizJournalDao dhbBizJournalDao;
    
    @Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
    
    public DhbOutMerchantDao getDhbOutMerchantDao() {
		return dhbOutMerchantDao;
	}
	public void setDhbOutMerchantDao(DhbOutMerchantDao dhbOutMerchantDao) {
		this.dhbOutMerchantDao = dhbOutMerchantDao;
	}
	
   public static void main(String[] args) {
	System.out.println("1909877720170606142217000030".length());
}
	//实时代付
	public SingleResp singlePay(OutRequestInfo info) throws Exception {
		String transCode = "RTPM";
		SingleResp singleResp = new SingleResp();
		logger.debug(payStr+"###>>>enter shande  singlePay  start.......................");
		validParam(info,transCode);//1. 杉德的参数校验
		DhbBizJournal journal = saveJournal(info,payStr,BizType.Pay.getCode());//2.保存交易流水
		String data =null;
		//3.准备请求
		try {
			logger.debug(payStr+"###>>>enter 1");
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
			//设置商户号
			logger.debug(payStr+"###>>>enter 2");
			String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
			//读取配置中公共URL
			logger.debug(payStr+"###>>>enter 3");
			String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();	
			url += "agentpay";
			//创建http辅助工具
			HttpUtil httpUtil= new HttpUtil();
			String genRequest = genRequest(info,journal,payStr);
			logger.error(payStr+url+" <= url, 6上送字符串 :"+genRequest);
			//通过辅助工具发送交易请求，并获取响应报文
			data= httpUtil.post(url, merchId, transCode, genRequest);		
			logger.error(payStr+" 请求返回");
			//4.处理返回start.....
			singleResp.setTranNo(info.getTranNo());//DHBPayController 有用的，不设置会报错
		} catch (IOException e) {
			logger.debug(payStr+" 装载配置异常....");
			e.printStackTrace();
		}catch (Exception e) {
			logger.debug(payStr+" 其他异常....");
			e.printStackTrace();
		}
		return dealCommonResData(singleResp,data,payStr,journal);
	}
	//处理返回结果
	private SingleResp dealCommonResData(SingleResp singleResp, String data, String logStr,DhbBizJournal journal) {
		logger.debug(logStr+"###>>处理返回结果: start.....");
		
		if( null==(data)){//为空,handling  防止发出去请求，连接断掉情况...
			singleResp.setCode(DhbTranStatus.Handling.getCode());
			singleResp.setMessage("返回为空(shande)");
			if(null !=journal){
				logger.info(logStr+"retData: 返回为空,开始更新journal");
				updateDhbBizJournalById(singleResp, journal,logStr);
			}
			return singleResp;
		}
		logger.debug(logStr+"###>>>返回结果: " +data);
		RetDataSD bean = (RetDataSD) new Gson().fromJson((data), RetDataSD.class);
		//认证的 start
		String validateStatus = bean.getValidateStatus();
		if(null !=validateStatus){//认证  和公安身份认证
			if("0".equals(validateStatus)){//0-通过 1-认证失败 
				singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
				if(null !=bean.getCertPicture()){
					singleResp.setContent(bean.getCertPicture());
				}
				logger.debug(logStr+"###>>>返回结果: 认证成功" );
				return singleResp;
			}
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			//
			String respDesc = bean.getRespDesc(); 
			if("成功".equals(respDesc)){
				respDesc="请求成功";
			}
			
			respDesc=respDesc+",认证失败";
			singleResp.setMessage(respDesc);//respDesc
			logger.debug(logStr+"###>>>返回结果: "+respDesc );
			return singleResp;
		}
		//认证的 end
		//处理余额查询的  start
		if(null !=bean.getBalance()){
			singleResp.setBalance(bean.getBalance());
		}
		if(null !=bean.getCreditAmt()){
			singleResp.setCreditAmt(bean.getCreditAmt());
		}
		if(null !=bean.getTranFee()){//4.5　代付手续费查询
			singleResp.setTranFee(bean.getTranFee());
		}
		if(null !=bean.getContent()){//4.8　对账单申请
			singleResp.setContent(bean.getContent());
		}
		//tranFee
		//处理余额查询的  end
		
		String respCode = bean.getRespCode();
		if(!respCode.equals("0000")){
			String respDesc = bean.getRespDesc();
			//不确定的都统一设置为支付中，用定时器在去查一下，以防出现通道支付成功而没有通知的情况
			if(respCode.equals("0001")){//银行处理中 《杉德代收付平台接口规范-商户接入1.0.2.docx》
				return handingOper(respCode,respDesc,singleResp,journal,logStr);
			}else if(respCode.equals("0002")){//银行返回超时
				return handingOper(respCode,respDesc,singleResp,journal,logStr);
			}else if(respCode.equals("0003")){//银行处理异常
				return handingOper(respCode,respDesc,singleResp,journal,logStr);
			}else if(respCode.equals("0004")){//平台处理异常
				return handingOper(respCode,respDesc,singleResp,journal,logStr);
			}else if(respCode.equals("0005")){//系统繁忙，请稍后重试
				return handingOper(respCode,respDesc,singleResp,journal,logStr);
			}else{//其他情况算作失败 ref:《杉德代收付平台接口规范-商户接入1.0.2.docx》
				singleResp.setCode(DhbTranStatus.Fail.getCode());singleResp.setMessage(respCode+":"+respDesc);
				if(null !=journal){
			 		logger.debug(logStr+"开始更新journal");//ref:《杉德代收付平台接口规范-商户接入1.0.2.docx》
					updateDhbBizJournalById(singleResp, journal,logStr);
					return singleResp;
				}
			}
		}
		//以下是 respCode 为 0000的情况
		String resultFlag = bean.getResultFlag();
		if("0".equals(resultFlag)){//0-成功 1-失败 2-处理中(已发往银行)
			singleResp.setCode(DhbTranStatus.Succ.getCode());
	 		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
		}else if("1".equals(resultFlag)){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
	 		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}else if("2".equals(resultFlag)){
			singleResp.setCode(DhbTranStatus.Handling.getCode());
	 		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		}else{
			String balance = singleResp.getBalance();//余额
			String tranFee = singleResp.getTranFee();//手续费
			String content = singleResp.getContent();//对账单链接
			if(null !=balance || null !=tranFee || null !=content ){
				logger.error(logStr+"###>>>查询余额 或者 手续费 或申请对账单 ");
			}else{
				singleResp.setCode(DhbTranStatus.Unkown.getCode());
				if(null !=bean.getRespDesc()){
					singleResp.setMessage(bean.getRespDesc());
				}else{
					singleResp.setMessage(DhbTranStatus.Unkown.getDescription());
				}
				logger.info(logStr+"###>>>返回结果的 resultFlag 超出预期 " +data);
				
			}
		}
		if(null !=journal){
			logger.debug(logStr+"开始更新journal");
			updateDhbBizJournalById(singleResp, journal,logStr);
		}
		return singleResp;
	}

	private SingleResp handingOper(String respCode, String respDesc, SingleResp singleResp, DhbBizJournal journal,String logStr) {
		singleResp.setCode(DhbTranStatus.Handling.getCode());
 		singleResp.setMessage(respCode+":"+respDesc);
 		logger.debug(logStr+"开始更新journal");//《杉德代收付平台接口规范-商户接入1.0.2.docx》
		updateDhbBizJournalById(singleResp, journal,logStr);
 		return singleResp;
	}

	private void updateDhbBizJournalById(SingleResp singleResp,DhbBizJournal journal,String logStr){
		DhbBizJournal update = new DhbBizJournal();
		update.setId(journal.getId());
		update.setEndTime(new Date());
		update.setHandleRemark(singleResp.getMessage());
		update.setHandleStatus(singleResp.getCode());
		//这个地方由于没有svn信息，先这样写了，本来是应该有返回值的，这样才能知道是否插入成功
    	dhbBizJournalDao.updateStatusById(update);
    	logger.debug(logStr+"###>>>enter shande  更新journal  end.......................");
	}
	
	//保存交易流水
	private DhbBizJournal saveJournal(OutRequestInfo reqInfo, String logstr,String bizType) {
		Date now = new Date();
		String id = ResourceUtil.getString("dsfpconfig", "merch.no")+ df.format(now)/*+ tf.format(now)*/
				+ StringUtils.leftPad(sequenceDao.getNextVal("shandepay_orderId_seq")+"",6, '0');
		//调用通道前先保存流水表信息
		//注：代付 frombankcode  是公司的账户
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(bizType);
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		journal.setFromBankCardNo(bankInfo.getAcctNo());
		journal.setFromBankName(bankInfo.getBankName());
		journal.setFromUserName(bankInfo.getAcctName());
		journal.setFromBankCode(bankInfo.getBankCode());
		journal.setToBankCardNo(reqInfo.getAccNo());
		journal.setToBankCode(reqInfo.getBankCode());
		journal.setToBankName(reqInfo.getBankName());
		journal.setToUserName(reqInfo.getAccName());
		journal.setToIdentityNo(reqInfo.getCertNo());
		journal.setMoney(reqInfo.getBanlance());
		journal.setBatchId(reqInfo.getBatchId());
		journal.setCurrency("CNY");journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);journal.setRecordId(reqInfo.getRecordId());
		//这个地方由于没有svn信息，先这样写了，本来是应该有返回值的，这样才能知道是否插入成功
//		logger.debug("###>>>enter shande  singlePay  .......................");
		dhbBizJournalDao.insertJournal(journal);
		logger.debug(logstr+"###>>>enter shande  singlePay   插入流水end.......................");
		return journal;
	}

	/**
	 * @param transCode 
	 * 
	  * validParam(细化杉德的param check
	  * @Title: validParam
	  * @Description: TODO
	  * @param @param info
	  * @param @throws CutException    设定文件
	  * @return void    返回类型
	  * @throws
	 */
	private void validParam(OutRequestInfo info, String transCode) throws CutException {
		logger.debug("###>>>enter shande  validParam  start..");
		if(null !=transCode){
			if("RNAU".equals(transCode)){
				info.setAccType("00");//银行卡认证 目前只认证对私的
			}else if("RNPA".equals(transCode)){//公安实名
				info.setAccType("00");
				String accName = info.getAccName();
				if(StringUtils.isEmpty(accName)  ){
					throw new CutException("accName(姓名) 不能为空");
				}
				if(StringUtils.isEmpty(info.getCertNo())  ){
					throw new CutException("CertNo（身份证） 不能为空");
				}
				return;
			}
		}
		String accType = info.getAccType();
		if(StringUtils.isEmpty(accType)  ){
			throw new CutException("AccType 不能为空");
		}
		if(StringUtils.isEmpty(info.getAccNo())  ){
			throw new CutException("AccNo(银行卡号) 不能为空");
		}
		//参考文档《中互联资金代收付接口说明umpay版本.docx》
		//文档路径 https://192.168.0.200:8443/svn/dhb/java/代收付文档
		//00：对私    
		if(  !(accType.equals("00") || accType.equals("01"))  ){
			throw new CutException("AccType's value must be 00 or 01");
		}
		///////////////////////start
		ProductIdSD productId = null;
		BizType findByCode = BizType.findByCode(info.getBizType());
		if(findByCode.getCode().equals(BizType.Pay.getCode())){//代付
			if(accType.equals("00")){
				productId = ProductIdSD.PAY2PRIVATE;
			}else if(accType.equals("01")){
				productId= ProductIdSD.PAY2PUBLIC;
			}
			String accAttr = productId.getAccAttr();
			if("1".equals(accAttr)){
				if(StringUtils.isEmpty(info.getBankName())  ){
					throw new CutException("对公的时候，BankName必填");
				}
			}
		}else if(findByCode.getCode().equals(BizType.Cut.getCode())){//代收
			if(accType.equals("00")){
				productId = ProductIdSD.CUT2PRIVATE;
			}else if(accType.equals("01")){
				productId= ProductIdSD.CUT2PUBLIC;
			}
			// bankName(账户开户行名称)    bankInsCode (bankInsCode) 不能为空
			String accAttr = productId.getAccAttr();
			if("1".equals(accAttr)){//对公
				if(StringUtils.isEmpty(info.getBankName())  ){
					throw new CutException("对公的BankName必填");
				}
				if(StringUtils.isEmpty(info.getBankCode())  ){
					throw new CutException("对公的银联机构号(BankCode)必填");
				}
			}
			//开户省份编码	provNo  
			if(StringUtils.isEmpty(info.getBankProvince())  ){
				throw new CutException("代收的开户省份编码（bankProvince）必填");
			}
			if(StringUtils.isEmpty(info.getCertType())  ){
				throw new CutException("代收的证件类型（CertType）必填");
			}
//			info.getCertNo()
			if(StringUtils.isEmpty(info.getCertNo())  ){
				throw new CutException("代收的证件号码（CertNo）必填");
			}
			//最低限额 代收交易 minAmount_cut 元
//			double banlance = ;
			double sub = ArithUtil.sub(info.getBanlance(), minAmount_cut);
			if( !(sub>=0)){
				throw new CutException("代收的最小交易金额是"+minAmount_cut+"元");
			}
		}else if(findByCode.getCode().equals(BizType.Bank_verify.getCode())){
			//姓名+身份证+卡号
			logger.debug("###>>>enter 签约  validParam  start..");
			String accName = info.getAccName();
			if(StringUtils.isEmpty(accName)  ){
				throw new CutException("accName(姓名) 不能为空");
			}
			if(StringUtils.isEmpty(info.getCertNo())  ){
				throw new CutException("CertNo（身份证） 不能为空");
			}
//			if(info.getTranNo().length()>30){
//				throw new CutException("TranNo 不能大于30位");
//			}
		}else{
			
			throw new CutException("请联系管理员扩展在用");
		}
		///////////////////////end
		logger.debug("###>>>enter shande  validParam  end..");
	}

	public static String genRequest(OutRequestInfo info, DhbBizJournal journal,String logStr) throws CutException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("version", "01");
		String accType = info.getAccType();ProductIdSD productId = null;
		BizType findByCode = BizType.findByCode(info.getBizType());
		if(findByCode.getCode().equals(BizType.Pay.getCode())){//代付
			if(accType.equals("00")){
				productId = ProductIdSD.PAY2PRIVATE;
			}else if(accType.equals("01")){
				productId= ProductIdSD.PAY2PUBLIC;
			}
		}else if(findByCode.getCode().equals(BizType.Cut.getCode())){//代收
			if(accType.equals("00")){
				productId = ProductIdSD.CUT2PRIVATE;
			}else if(accType.equals("01")){
				productId= ProductIdSD.CUT2PUBLIC;
			}
			//代收 
			//开户省份编码	provNo  
			String bankProvince = info.getBankProvince();
			if(null !=bankProvince){
				jsonObject.put("provNo", bankProvince);
			}
			//开户证件类型	certType  0101	身份证 certType
			if("01".equals(info.getCertType())){//目前先支持身份证
				jsonObject.put("certType", "0101");
			}else{
				throw new CutException("【杉德】"+"请联系管理员扩展(certType)在其他证件类型");
			}
			//开户证件号码	certNo    cardId(如果是对公填法人身份证号码) 目前都是对私 certNo cardId先一个样了
		
			jsonObject.put("certNo", info.getCertNo());//certNo 和cardId 先用一个了
			jsonObject.put("cardId", info.getCertNo());
			
			//purpose用途说明	purpose   (暂时和remark一样)
			jsonObject.put("purpose", info.getRemark()==null?findByCode.getDescription():info.getRemark());
		}
		
		if(info.getBizType().equals(BizType.Bank_verify.getCode())){//实名认证
			jsonObject.put("productId", "00000002");//
			if(accType.equals("00")){
				jsonObject.put("accAttr", "0");
			}else if(accType.equals("01")){
				jsonObject.put("accAttr", "1");
			}
			jsonObject.put("accType", "4");
			jsonObject.put("certType", "0101");
			jsonObject.put("certNo", info.getCertNo());
		}else{
			jsonObject.put("productId", productId.getCode());//00000001 "00000004"
			//jsonObject.put("timeOut", "20161024120000");//5 timeOut 不填默认24小时
			jsonObject.put("tranAmt",StringUtils.leftPad(AmountUtil.yuan2Fen(info.getBanlance())+"",12,'0'));// 6    分
			jsonObject.put("currencyCode", "156");//币种 7   
			jsonObject.put("accAttr", productId.getAccAttr());//0-对私(默认)  1-对公		取值需与产品编码一致
			jsonObject.put("accType", /*"4"*/productId.getAccType());//9 账号类型            3-公司账户  4-银行卡//先是银行卡，后期可以合并到productId
			if("3".equals(productId.getAccType())){
				jsonObject.put("bankName", info.getBankName());//accAttr =1时必填
			}
			jsonObject.put("remark", info.getRemark()==null?findByCode.getDescription():info.getRemark());
		}
		
		if(null !=journal){
			jsonObject.put("tranTime", df.format(journal.getCreateTime()));//03 交易时间 (保存为流水的创建时间，查询的时候是用这个)
			jsonObject.put("orderCode", /*info.getTranNo()*/journal.getId());//订单号 (和以前的代码保持一致)
		}else{
			String format = df.format(new Date());
			jsonObject.put("tranTime", format);
			jsonObject.put("orderCode", format+ StringUtils.leftPad(new Random().nextInt(10000)+"",12,'0') );
		}
		jsonObject.put("accNo",info.getAccNo());//10  代收收款人账户号  代付就是扣款账号
		jsonObject.put("accName",info.getAccName());//收款人账户名
		logger.info(logStr+"上送 json:"+ jsonObject.toJSONString());
		return jsonObject.toJSONString();
	}
	
	//代付 override
	public SingleResp singleCut(OutRequestInfo info) throws Exception {
		logger.debug(cutStr+" start....");
		String transCode = "RTCO";
		SingleResp singleResp = new SingleResp();
		validParam(info,transCode);//1. 杉德的参数校验
		DhbBizJournal journal = saveJournal(info,cutStr,BizType.Cut.getCode());//2.保存交易流水
		String data=null;
		//3.准备请求 装载配置
		try {
			logger.debug(cutStr+" 1....");
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
			logger.debug(cutStr+" 2....");
			//设置商户号
			String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
			//读取配置中公共URL
			String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();	
			
			//拼接本交易URL
			url += "collection";
			//创建http辅助工具
			HttpUtil httpUtil= new HttpUtil();
			String genRequest = genRequest(info,journal,cutStr);
			//通过辅助工具发送交易请求，并获取响应报文
			logger.error(cutStr+url+" <= url, 上送字符串 :"+genRequest);
			data = httpUtil.post(url, merchId, transCode, genRequest);
			logger.error(cutStr+" 请求返回");
			//4.处理返回start.....
			singleResp.setTranNo(info.getTranNo());//DHBPayController 有用的，不设置会报错
		} catch (IOException e) {
			logger.debug(cutStr+" 装载配置异常....");
			e.printStackTrace();
		}catch (Exception e) {
			logger.debug(cutStr+" 其他异常....");
			e.printStackTrace();
		}
		return dealCommonResData(singleResp,data,cutStr,journal);
	}

	
	/**
	 * 外放查询接口
	 * 和以前的保持一致，这里不查询上游通道，
	 * 有个定时器，定时查询通道
	 */
	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info) throws Exception {
		//UmpayPayCutService copy from UmpayPayCutService
		
		SingleResp singleResp = new SingleResp(); 
		singleResp.setCode(DhbTranStatus.Handling.getCode());
		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		DhbBizJournal journal = dhbBizJournalDao.getBizJournalByReqInfo(info);
		if(journal==null){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription()+",该订单在大网关不存在");
			return singleResp;
		}
		logger.info("【外放交易查询接口】tranNo="+info.getTranNo()+",code"+journal.getHandleStatus()+",message"+journal.getHandleRemark());
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
	}

	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
		SingleResp singleResp = new SingleResp();
		singleResp.setMessage("此通道不支持批量");
		return singleResp;
	}

	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		SingleResp singleResp = new SingleResp();
		singleResp.setMessage("此通道不支持批量");
		return singleResp;
	}

	/**
	 * getBizType 1单笔   5批量
	 */
	public void queryTranStatus(DhbBizJournal journal) {
		String logStr="【杉德查询】";logger.debug(logStr+"start....");
		try {
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
		} catch (IOException e) {
			logger.debug(payStr+" 装载配置异常....");
			e.printStackTrace();
		}
		//设置商户号
		String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
		//读取配置中公共URL
		String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();	
		// 拼接本交易URL
		url += "queryOrder";		
		String bizType = journal.getBizType();
		if(BizType.Pay.getCode().equals(bizType) || BizType.Cut.getCode().equals(bizType) ){//单笔
			
			querySingleJournalStatus(journal,merchId,url,logStr);//
			
		}else if(BizType.Pay_batch.getCode().equals(bizType) || BizType.Cut_batch.getCode().equals(bizType) ){//批量
			
			List<DhbBizJournal>  list=dhbBizJournalDao.getJournalByBatchId(journal.getMerchId(), journal.getBatchId());
			for(DhbBizJournal eachjour:list){
				querySingleJournalStatus(eachjour,merchId,url,logStr+"批量：");//
			}
			
		}else{
			logger.error(logStr+" journal getBizType 异常，（wxw 说3应该不是代付工资，好像还有个4，  先不用管...）");
		}
	}
	private void querySingleJournalStatus(DhbBizJournal journal, String merchId, String url, String logStr) {
		HttpUtil httpUtil = new HttpUtil();
		// 通过辅助工具发送交易请求，并获取响应报文
		try {
			String genRequest4Query = genRequest4Query(journal,"");
			if(null ==genRequest4Query){
				logger.error(logStr+" 查询异常 BizType,无法区分 ：RecordId:"+journal.getRecordId()+", BizType:"+journal.getBizType());
				return ;
			}
			logger.error(logStr+url+" <= url, 查询上送字符串 :"+genRequest4Query);
			String data = httpUtil.post(url, merchId, "ODQU", genRequest4Query);
			
			SingleResp singleResp = new SingleResp();
			dealCommonResData(singleResp,data,logStr,journal);
		} catch (Exception e) {
			logger.error(logStr+" 查询异常");
			e.printStackTrace();
		}
	}
	private String genRequest4Query(DhbBizJournal journal, String flagStr) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderCode", journal.getId());//SINGLEPAY000000000003
		jsonObject.put("version", "01");
		//和以前代码保持一直 ，现在只是对私,...
		String bizType = journal.getBizType();
		ProductIdSD p=null;
		if(bizType.equals(BizType.Pay.getCode())){
			p=ProductIdSD.PAY2PRIVATE;
		}else if(bizType.equals(BizType.Cut.getCode())){
			p=ProductIdSD.CUT2PRIVATE;
		}else{
			return null;
		}
		jsonObject.put("productId", /*"00000004"*/ p.getCode());
		jsonObject.put("tranTime", /*"20170606105202"*/ df.format(journal.getCreateTime()));	
		//PTFQ
		if(flagStr.equals("PTFQ")){//4.5　代付手续费查询需要的参数
			jsonObject.put("tranAmt",StringUtils.leftPad(AmountUtil.yuan2Fen(journal.getMoney())+"",12,'0'));// 6    分
			jsonObject.put("currencyCode", "156");
			jsonObject.put("accAttr",p.getAccAttr());
			jsonObject.put("accType", p.getAccType());
			jsonObject.put("accNo",journal.getToBankCardNo());
		}
		return jsonObject.toJSONString();
	}

	public SingleResp queryBalance(){
		String logStr="【杉德商户余额查询】";
		SingleResp singleResp = new SingleResp();
		//装载配置
		try {
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
		} catch (IOException e1) {
			logger.error(logStr+"加载配置异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("加载配置异常)");
			e1.printStackTrace();
			return singleResp;
		}
		//设置商户号
		String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
		//读取配置中公共URL
		String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();
		//拼接本交易URL
		url += "queryBalance";
		//创建http辅助工具
		HttpUtil httpUtil= new HttpUtil();
		//通过辅助工具发送交易请求，并获取响应报文
		String data=null;
		String genRequest4QueryBalance = genRequest4QueryBalance();
		logger.info(logStr+url+" <= url, 上送字符串 :"+genRequest4QueryBalance);
		try {
			data = httpUtil.post(url, merchId, "MBQU", genRequest4QueryBalance);
			singleResp= dealCommonResData(singleResp,data,logStr,null);
		} catch (Exception e) {
			logger.error(logStr+"请求通道异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("请求通道异常");
			e.printStackTrace();
			return singleResp;
		}
		return singleResp;
	}
	//
	public  String genRequest4QueryBalance() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderCode",  Tools.getUUID().substring(0, 30));//上游需要一个单号，这里组装一个
		jsonObject.put("version", "01");
		jsonObject.put("productId", "00000004");//这个不是按demo的 beause：代扣转代付 是你们开通清算模式了
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		jsonObject.put("tranTime", df.format(new Date()));
		return jsonObject.toJSONString();
	}
	
	/**
	 * 4.5　代付手续费查询
	  * queryAgentpayFee(用户商户查询单笔订单的手续费
	  * 骑着驴子去旅游:
	  * 代付手续费查询 这个接口是你对发起一笔代付，
	  * 你不确定这笔叫手续费多少，
	  * 可以通过这个接口来查看，这笔金额代付成功，我们会收取多少手续费
	  * 骑着驴子去旅游:
		这个接口的作用就我前面描叙的
		我:
		哦 好的 明白了
	  * @Title: queryAgentpayFee
	  * @Description: TODO
	  * @param @param journal
	  * @param @return    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	public SingleResp queryAgentpayFee(OutRequestInfo resInfo){
		SingleResp singleResp = new SingleResp();
		String logStr="【杉德代付手续费查询】";
		try {
			//装载配置
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
		} catch (IOException e1) {
			logger.error(logStr+"加载配置异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("加载配置异常)");
			e1.printStackTrace();
			return singleResp;
		}		
		//设置商户号
		String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();		
		//读取配置中公共URL
		String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();
		//拼接本交易URL
		url += "queryAgentpayFee";
		//创建http辅助工具
		HttpUtil httpUtil= new HttpUtil();
		//通过辅助工具发送交易请求，并获取响应报文
		String genRequest4Query = genRequest4Query2(resInfo);//
		logger.error(logStr+url+" <= url, 上送字符串 :"+genRequest4Query);
		String data=null;
		try {
			data = httpUtil.post(url, merchId, "PTFQ", genRequest4Query);
		} catch (Exception e) {
			logger.error(logStr+"请求异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("请求异常)");
			e.printStackTrace();
			return singleResp;
		}
		//手续费	tranFee	N12	M	精确到分
		singleResp= dealCommonResData(singleResp,data,logStr,null);
		return singleResp;
	}
	private String genRequest4Query2(OutRequestInfo resInfo) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderCode", Tools.getUUID().substring(0, 30));//
		jsonObject.put("version", "01");
		jsonObject.put("productId", "00000004");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		jsonObject.put("tranTime", df.format(new Date()));
		jsonObject.put("tranAmt", StringUtils.leftPad(AmountUtil.yuan2Fen(resInfo.getBanlance())+"",12,'0'));
		jsonObject.put("currencyCode", "156");
		jsonObject.put("accAttr", "0");
		jsonObject.put("accType", "4");
		jsonObject.put("accNo", resInfo.getAccNo());
		return jsonObject.toJSONString();
	}
	
	
	/*4.8　对账单申请
	 */
	public SingleResp getClearFileContent(OutRequestInfo resInfo){
		SingleResp singleResp = new SingleResp();
		String logStr="【杉德对账单申请】";
		try {
			//装载配置
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
		} catch (IOException e1) {
			logger.error(logStr+"加载配置异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("加载配置异常)");
			e1.printStackTrace();
			return singleResp;
		}		
		//设置商户号
		String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();		
		//读取配置中公共URL
		String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();
		//拼接本交易URL
		url += "getClearFileContent";
		//创建http辅助工具
		HttpUtil httpUtil= new HttpUtil();		
		String genRequest4Query = genRequest4getClearFileContent(resInfo);//TODO
		logger.info(logStr+url+" <= url, 上送字符串 :"+genRequest4Query);
		String data=null;
		try {
			data = httpUtil.post(url, merchId, "CFCT", genRequest4Query);
		} catch (Exception e) {
			logger.error(logStr+"请求异常");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage("请求异常)");
			e.printStackTrace();
			return singleResp;
		}
		singleResp= dealCommonResData(singleResp,data,logStr,null);
		return singleResp;
	}
	private String genRequest4getClearFileContent(OutRequestInfo resInfo) {
		JSONObject jsonObject = new JSONObject();	
		jsonObject.put("version", "01");
		jsonObject.put("clearDate", /*"20170207"*/resInfo.getTimestamp());//timestamp 前置check
		jsonObject.put("busiType", /*"2"*/resInfo.getBizType());		
		jsonObject.put("fileType", "1");
		return jsonObject.toJSONString();
	}
	/**
	 * 身份证认证 二要素 idCardVerify 4.7　实名公安认证
	  * idCardVerify(
	  *
	  * @Title: idCardVerify
	  * @Description: TODO
	  * @param @param info
	  * @param @return
	  * @param @throws Exception    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	public SingleResp idCardVerify(OutRequestInfo info) throws Exception {
		String transCode = "RNPA";	//实名公安认证
		String logStr="【杉德实名公安认证】";
		SingleResp singleResp = new SingleResp();
		logger.debug(logStr+"###>>>enter shande    start.......................");
		validParam(info,transCode);//1. 杉德的参数校验
		//2 检查是否已经实名
		String isPass=checkVerifyExist(info);
   		if(isPass!=null){
   			logger.debug(logStr+"###>>>数据库中已经存在这条实名认证信息....");
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
		String data =null;	
		//3.准备请求
		try {
			logger.debug(logStr+"###>>>enter 1");
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
			//设置商户号
			logger.debug(logStr+"###>>>enter 2");
			String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
			//读取配置中公共URL
			String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();	
			url += "idCardVerify";
			//创建http辅助工具
			HttpUtil httpUtil= new HttpUtil();
			String genRequest = genRequest4IdVerify(info,logStr);
			logger.error(logStr+url+" <= url, 6上送字符串 :"+genRequest);
			//通过辅助工具发送交易请求，并获取响应报文
			data= httpUtil.post(url, merchId, transCode, genRequest);		
			logger.error(logStr+" 请求返回");
			//4.处理返回start.....
			singleResp.setTranNo(info.getTranNo());//DHBPayController 有用的，不设置会报错
		} catch (IOException e) {
			logger.debug(logStr+" 装载配置异常....");
			e.printStackTrace();
		}catch (Exception e) {
			logger.debug(logStr+" 其他异常....");
			e.printStackTrace();
		}
		SingleResp res = dealCommonResData(singleResp,data,logStr,null);
		if(res.getCode().equals(DhbTranStatus.Succ.getCode())){
			//认证成功后添加一条记录
			logger.debug(logStr+"成功，保存到数据库(dhb_realName) start...");
			saveVerify(info);
			logger.debug(logStr+"成功，保存到数据库(dhb_realName) end...");
		}
		return res;
	}
	private String genRequest4IdVerify(OutRequestInfo info, String logStr) {
		String format = df.format(new Date());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderCode", format+ StringUtils.leftPad(new Random().nextInt(10000)+"",12,'0') );
		jsonObject.put("version", "01");
		jsonObject.put("productId", "00000003");
		jsonObject.put("tranTime",format);
		jsonObject.put("name", info.getAccName());
		jsonObject.put("certType", 	"0101");
		jsonObject.put("certNo", info.getCertNo());
		jsonObject.put("returnPic", "1");
		return jsonObject.toJSONString();
	}
	/**
	 * 
	  * bankCardRealName(4.6　实名认证
        	服务地址：${URL}/realNameVerify
        	业务逻辑按照
        	com/dhb/kl/realname/service/ZXRealNameService.java
	  * @Title: bankCardRealName
	  * @Description: TODO
	  * @param @param info
	  * @param @return
	  * @param @throws Exception    设定文件
	  * @return SingleResp    返回类型
	  * @throws
	 */
	public SingleResp bankCardRealName(OutRequestInfo info) throws Exception {
		String transCode = "RNAU";	//银行卡实名认证
		String logStr="【杉德实名认证】";
		SingleResp singleResp = new SingleResp();
		logger.debug(logStr+"###>>>enter shande    start.......................");
		validParam(info,transCode);//1. 杉德的参数校验
		//2 检查是否已经实名
		String isPass=checkVerifyExist(info);
   		if(isPass!=null){
   			logger.debug(logStr+"###>>>数据库中已经存在这条实名认证信息....");
   			singleResp.setCode(DhbTranStatus.Succ.getCode());
   			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
   			return singleResp;
   		}
		String data =null;
		//3.准备请求
		try {
			logger.debug(logStr+"###>>>enter 1");
			ConfigurationManager.loadProperties(new String[] { "dsfpconfig"});
			//设置商户号
			logger.debug(logStr+"###>>>enter 2");
			String merchId= DynamicPropertyHelper.getStringProperty("merch.no","").get();
			//读取配置中公共URL
			String url =  DynamicPropertyHelper.getStringProperty("dsfp.url", "").get();	
			url += "realNameVerify";
			//创建http辅助工具
			HttpUtil httpUtil= new HttpUtil();
			String genRequest = genRequest(info,null,logStr);
			logger.error(logStr+url+" <= url, 6上送字符串 :"+genRequest);
			//通过辅助工具发送交易请求，并获取响应报文
			data= httpUtil.post(url, merchId, transCode, genRequest);		
			logger.error(logStr+" 请求返回");
			//4.处理返回start.....
			singleResp.setTranNo(info.getTranNo());//DHBPayController 有用的，不设置会报错
		} catch (IOException e) {
			logger.debug(logStr+" 装载配置异常....");
			e.printStackTrace();
		}catch (Exception e) {
			logger.debug(logStr+" 其他异常....");
			e.printStackTrace();
		}
		SingleResp res = dealCommonResData(singleResp,data,logStr,null);
		if(res.getCode().equals(DhbTranStatus.Succ.getCode())){
			//认证成功后添加一条记录
			logger.debug(logStr+"成功，保存到数据库(dhb_realName) start...");
			saveVerify(info);
			logger.debug(logStr+"成功，保存到数据库(dhb_realName) end...");
		}
		return res;
	}
	private void saveVerify(OutRequestInfo reqInfo) {
   		RealNameInfo realName = new RealNameInfo();
   		realName.setCertNo(reqInfo.getCertNo());
   		realName.setUserName(reqInfo.getAccName());
   		String accNo = reqInfo.getAccNo();
   		String insertSql ="";
   		if(null == accNo){
   			insertSql ="insert into dhb_realName(certNo,userName) values(:certNo,:userName)";
   		}else{
   			realName.setAccNo(accNo);
   	   		insertSql ="insert into dhb_realName(certNo,accNo,userName) values(:certNo,:accNo,:userName)";
   		}
   		commonObjectDao.saveOrUpdate(insertSql, realName);
   		
	}
	//逻辑来自ZXRealNameService
	private String checkVerifyExist(OutRequestInfo reqInfo) {
		SingleResp singleResp = new SingleResp(); 
   		singleResp.setTranNo(reqInfo.getTranNo());
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertNo = reqInfo.getCertNo();
   		String sql = "";
   		RealNameInfo realName = new RealNameInfo();
   		realName.setCertNo(fromCertNo);
	   	realName.setUserName(fromAccName);
	   	
   		if(null ==fromAccNo){//查询该记录是否做了银行卡实名认证 (二要素)
   			sql = "select 1 from dhb_realName where certNo=:certNo  and userName=:userName";
   	   		return commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccName});
   		}else{//查询该记录是否做了银行卡实名认证(三要素)
   			realName.setAccNo(fromAccNo);
   			sql = "select 1 from dhb_realName where certNo=:certNo and accNo=:accNo and userName=:userName";
   	   		return commonObjectDao.findSingleVal(sql, new Object[]{fromCertNo,fromAccNo,fromAccName});
   		}
   		
	}
	
	
	
	
}
