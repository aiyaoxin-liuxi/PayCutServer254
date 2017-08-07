package com.dhb.cip.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.anyz.service.ANYZUtil;
import com.dhb.cip.entity.CIPUtils;
import com.dhb.cip.entity.Constants;
import com.dhb.cip.entity.RSA;
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
import com.dhb.entity.SingleResp;
import com.dhb.service.PayCutInterface;
import com.dhb.util.ArithUtil;
import com.dhb.util.JsonUtil;
import com.dhb.ysb.service.YSBUtil;
import com.google.common.base.Strings;
import com.jnewsdk.util.ResourceUtil;
import com.jnewsdk.util.SignUtil;
import com.jnewsdk.util.StringUtils;

@Service("CIPPayCutService")
public class CIPPayCutService implements PayCutInterface{
	public static Logger logger = Logger.getLogger(CIPPayCutService.class);
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
	
	@Override
	public SingleResp singleCut(OutRequestInfo info) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp singlePay(OutRequestInfo reqInfo) throws Exception {
		// TODO Auto-generated method stub
		SingleResp singleResp = new SingleResp();
		Map<String,Object> cip_map_ret = null;
		Date now = new Date();
		String id = YSBUtil.getReadProperties("cip", "cip.merchantId")
				+ df.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",5, '0');
		//调用通道前先保存流水表信息
		//注：代付 frombankcode  是公司的账户
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Pay.getCode());
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
		journal.setCurrency("CNY");
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		//组装传递给通道方的报文
		Map<String,String> map = new HashMap<String,String>();
		map.put("serviceName", Constants.cip_serviceName_agentSinglePay);//服务名称
		map.put("version", Constants.cip_version);//版本号
		map.put("platform", "");//平台标示
		map.put("merchantId", YSBUtil.getReadProperties("cip", "cip.merchantId"));//商户号
		map.put("payType", Constants.cip_payType_16);//支付类型
		map.put("signType", Constants.cip_signMethod_RSA);//签名类型
		map.put("charset", Constants.cip_charset_UTF8);//参数编码字符集
		map.put("merBatchNo", journal.getId());//商户批次号
		map.put("txnTime", sdf.format(new Date()));//批次提交时间
		map.put("payeeAcct", reqInfo.getAccNo());//收款人账号
		map.put("payeeName", reqInfo.getAccName());//收款人名称
		map.put("applyAmount", String.valueOf(reqInfo.getBanlance()));//代付申请金额
		map.put("applyReason", reqInfo.getRemark());//附言
		map.put("bankName", reqInfo.getBankName());//开户行名称
		map.put("bankCode", "CMB");//开户行编号
		map.put("bankProvince", reqInfo.getBankProvince());//开户行省
		map.put("bankCity", reqInfo.getBankCity());//开户行市
		map.put("bankBranchName", reqInfo.getBankName());//支行名称
		try {
			String waitSign = CIPUtils.getBuildPayParams(map);
			logger.info("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+",待签名数据："+waitSign);
			String sign = RSA.sign(waitSign, YSBUtil.getReadProperties("cip", "cip.privateKey"), Constants.cip_charset_UTF8);
			logger.info("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+",签名数据："+sign);
			map.put("sign", sign);//签名
			String result = ANYZUtil.sendMsg(YSBUtil.getReadProperties("cip", "cip.singlePay.url"), map);
			logger.info("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果："+result);
			Map<String,String> respMap = JsonUtil.getJsonToMapStr(result);
			if(respMap.get("sign") !=null && !"".equals(respMap.get("sign").toString())){//通道调用成功
				//通道返回参数验签
				Boolean verifyResult = RSA.verifySign(CIPUtils.getBuildPayParams(respMap), respMap.get("sign"), YSBUtil.getReadProperties("cip", "cip.publicKey"), Constants.cip_charset_UTF8);
				if(verifyResult == true){//验签通过
					if(respMap.get("tradeStatus").toString().equals("00")){//初始提交
						singleResp.setCode(DhbTranStatus.Handling.getCode());
			     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
					}else if(respMap.get("tradeStatus").toString().equals("01")){//商户确认
						singleResp.setCode(DhbTranStatus.Handling.getCode());
			     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
					}else if(respMap.get("tradeStatus").toString().equals("02")){//商户取消
						singleResp.setCode(DhbTranStatus.Fail.getCode());
			     		singleResp.setMessage(respMap.get("retMsg").toString());
					}else if(respMap.get("tradeStatus").toString().equals("03")){//内部审核通过
						singleResp.setCode(DhbTranStatus.Handling.getCode());
			     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
					}else if(respMap.get("tradeStatus").toString().equals("04")){//内部审核拒绝
						singleResp.setCode(DhbTranStatus.Fail.getCode());
			     		singleResp.setMessage(respMap.get("retMsg").toString());
					}else if(respMap.get("tradeStatus").toString().equals("05")){//提交金融基础
						singleResp.setCode(DhbTranStatus.Handling.getCode());
			     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
					}else if(respMap.get("tradeStatus").toString().equals("06")){//代付完成
						singleResp.setCode(DhbTranStatus.Succ.getCode());
			     		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
					}else{
						singleResp.setCode(DhbTranStatus.Unkown.getCode());
			     		singleResp.setMessage(respMap.get("retMsg").toString());
					}
				}else{//验签失败
					logger.info("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+",签名验证结果：false");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
		     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
				}
				journal.setId(journal.getId());
	        	journal.setEndTime(new Date());
	        	journal.setHandleRemark(singleResp.getMessage());
	        	journal.setHandleStatus(singleResp.getCode());
	        	dhbBizJournalDao.updateStatusById(journal);
			}else{//通道调用失败
				logger.info("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道调用失败:"+result);
				journal.setId(journal.getId());
				journal.setEndTime(new Date());
	        	journal.setHandleRemark(respMap.get("retMsg").toString());
	        	journal.setHandleStatus(DhbTranStatus.Fail.getCode());
	        	dhbBizJournalDao.updateStatusById(journal);
	        	singleResp.setCode(DhbTranStatus.Fail.getCode());
	     		singleResp.setMessage(respMap.get("retMsg").toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("(CIP单笔代付接口：)订单号："+reqInfo.getTranNo()+","+e.getMessage());
		}
		singleResp.setTranNo(reqInfo.getTranNo());
		reqInfo.setProxyBizJournalId(id);
		return singleResp;
	}

	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		SingleResp singleResp = new SingleResp(); 
		singleResp.setCode(DhbTranStatus.Handling.getCode());
		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		DhbBizJournal journal=dhbBizJournalDao.getBizJournalByReqInfo(info);
		if(journal==null){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
			return singleResp;
		}
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
	}

	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		// TODO Auto-generated method stub
		String batchId = journal.getBatchId();
		if(Strings.isNullOrEmpty(batchId)){
			querySingleJournalStatus(journal);
		}else{
			String merchId = journal.getMerchId();
			List<DhbBizJournal>  list=dhbBizJournalDao.getJournalByBatchId(merchId, batchId);
			for(DhbBizJournal jour:list){
				querySingleJournalStatus(jour);
			}
		}
	}
	private void querySingleJournalStatus(DhbBizJournal journal){
		SingleResp singleResp = new SingleResp(); 
		String tranNo = journal.getId();
		//调用通道
		Map<String,String> map = new HashMap<String,String>();
		map.put("serviceName", Constants.cip_serviceName_queryAgentSinglePay);//服务名称
		map.put("version",Constants.cip_version);//版本号
		map.put("platform",Constants.cip_version);//平台标示
		map.put("merchantId",YSBUtil.getReadProperties("cip", "cip.merchantId"));//商户号
		map.put("signType",Constants.cip_signMethod_RSA);//签名类型
		map.put("charset",Constants.cip_charset_UTF8);//编码字符集
		map.put("merBatchNo",tranNo);//商户批次号
		try {
			String waitSign = CIPUtils.getBuildPayParams(map);
			logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",待签名数据："+waitSign);
			String sign = RSA.sign(waitSign, YSBUtil.getReadProperties("cip", "cip.privateKey"), Constants.cip_charset_UTF8);
			map.put("sign", sign);//签名
			logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",组装通道方报文（明文）："+JsonUtil.getMapToJsonStr(map));
			String result = ANYZUtil.sendMsg(YSBUtil.getReadProperties("cip", "cip.singlePay.query.url"), map);
			logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",通道方返回结果："+result);
			Map<String,Object> respMap = JsonUtil.getJsonToMap(result);
			if(respMap.get("sign") !=null && !"".equals(respMap.get("sign").toString())){//通道调用成功
				Boolean verifyResult = RSA.verifySign(CIPUtils.getObjectBuildPayParams(respMap), respMap.get("sign").toString(), YSBUtil.getReadProperties("cip", "cip.publicKey"), Constants.cip_charset_UTF8);
				if(verifyResult == true){//验签通过
					logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",签名验证结果：true");
					if(respMap.get("retCode").toString().equals("0000")){//调用成功
						if(respMap.get("tradeStatus").toString().equals("00")){//初始提交
							singleResp.setCode(DhbTranStatus.Handling.getCode());
				     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
						}else if(respMap.get("tradeStatus").toString().equals("01")){//商户确认
							singleResp.setCode(DhbTranStatus.Handling.getCode());
				     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
						}else if(respMap.get("tradeStatus").toString().equals("02")){//商户取消
							singleResp.setCode(DhbTranStatus.Fail.getCode());
				     		singleResp.setMessage(respMap.get("retMsg").toString());
						}else if(respMap.get("tradeStatus").toString().equals("03")){//内部审核通过
							singleResp.setCode(DhbTranStatus.Handling.getCode());
				     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
						}else if(respMap.get("tradeStatus").toString().equals("04")){//内部审核拒绝
							singleResp.setCode(DhbTranStatus.Fail.getCode());
				     		singleResp.setMessage(respMap.get("retMsg").toString());
						}else if(respMap.get("tradeStatus").toString().equals("05")){//提交金融基础
							singleResp.setCode(DhbTranStatus.Handling.getCode());
				     		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
						}else if(respMap.get("tradeStatus").toString().equals("06")){//代付完成
							singleResp.setCode(DhbTranStatus.Succ.getCode());
				     		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
						}else if(respMap.get("tradeStatus").toString().equals("07")){//代付失败
							singleResp.setCode(DhbTranStatus.Fail.getCode());
				     		singleResp.setMessage(respMap.get("retMsg").toString());
						}else{
							singleResp.setCode(DhbTranStatus.Unkown.getCode());
				     		singleResp.setMessage(respMap.get("retMsg").toString());
						}
					}else{
						singleResp.setCode(DhbTranStatus.Fail.getCode());
			     		singleResp.setMessage(respMap.get("retMsg").toString());
					}
				}else{//验签失败
					logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",签名验证结果：false");
					singleResp.setCode(DhbTranStatus.Fail.getCode());
		     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
				}
				if(!singleResp.getCode().equals(journal.getHandleStatus())){
		        	journal.setEndTime(new Date());
		        	journal.setHandleRemark(singleResp.getMessage());
		        	journal.setHandleStatus(singleResp.getCode());
		        	dhbBizJournalDao.updateStatusById(journal);
		        }
			}else{//通道调用失败
				logger.info("(CIP单笔代付查询接口：)订单号："+tranNo+",通道调用失败:"+result);
				journal.setEndTime(new Date());
	        	journal.setHandleRemark(respMap.get("retMsg").toString());
	        	journal.setHandleStatus(DhbTranStatus.Fail.getCode());
	        	dhbBizJournalDao.updateStatusById(journal);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("(CIP单笔代付查询接口：)订单号："+tranNo+","+e.getMessage());
		}
	}
	public void updateMerAccount(OutRequestInfo reqInfo,String id,String bizType,DhbOutMerchant merchant){
		logger.info("[代收付外放]记录商户流水ProxyBizJournal");//可在商户端和运营端的代收付流水记录中查到
		ProxyBizJournal journal = new ProxyBizJournal();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat tf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		ProxyCarryBankacct proxyCarryBankacct = dhbOutMerchantDao.selectBankByType(bizType);
		journal.setAmount(reqInfo.getBanlance());
		if(BizType.Pay.getCode().equals(bizType)){//代付交易，收款人是客户，付款方是平台账户
			journal.setAcpacctname(reqInfo.getAccName());
			journal.setAcpacctno(reqInfo.getAccNo());
			journal.setPayacctname(proxyCarryBankacct.getAcctName());
			journal.setPayacctno(proxyCarryBankacct.getAcctNo());
		}else if(BizType.Cut.getCode().equals(bizType)){//代收交易，收款人是平台账户，付款人是客户
			journal.setAcpacctname(proxyCarryBankacct.getAcctName());
			journal.setAcpacctno(proxyCarryBankacct.getAcctNo());
			journal.setPayacctname(reqInfo.getAccName());
			journal.setPayacctno(reqInfo.getAccNo());
		}
		
		journal.setBiz_type(bizType);
		journal.setMemo(id);
		journal.setMerch_id(reqInfo.getMerchId());
		journal.setOutid(reqInfo.getTranNo());
		journal.setChannelId(reqInfo.getChannelId());
		journal.setPhone(null);
		journal.setRecord_id(reqInfo.getRecordId());
		journal.setTrans_date(df.format(now));
		journal.setTrans_time(tf.format(now));
		journal.setResp_date(df.format(now));
		journal.setResp_time(tf.format(now));
		journal.setStatus(DhbTranStatus.Succ.getCode());
		journal.setRemark("交易成功");
		journal.setChargemode("0");
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
		logger.info("[代收付外放]流水号："+reqInfo.getTranNo()+",商户账户余额扣:"+journal.getAmount());
		//余额
		Double currentBalance = ArithUtil.sub(last_amount, merchant.getMerFee());
		//可用余额变动
		Double currentValidBalance = ArithUtil.sub(last_valid_amount, merchant.getMerFee());
		//减手续费
		currentBalance = ArithUtil.sub(currentBalance, journal.getAmount());
		currentValidBalance = ArithUtil.sub(currentValidBalance, journal.getAmount());
		//3.1变更一下商户虚拟账户余额
		proxyMerchAmt.setBalance(currentBalance);
		//3.2变更可用余额
		proxyMerchAmt.setValidBalance(currentValidBalance);
		dhbOutMerchantDao.updateMerAmt(proxyMerchAmt);
	}
}
