package com.dhb.chinapay.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dhb.chinapay.entity.ChinaCutResCodeStatusCode;
import com.dhb.chinapay.entity.ChinaPayResponseCode;
import com.dhb.chinapay.entity.CpPayStateType;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.service.PayCutInterface;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.dhb.util.Tools;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class ChinaPayCutService implements PayCutInterface{

	private SequenceDao sequenceDao;

    private ChinaPayDaoService chinaPayDaoService;

	private DhbBizJournalDao dhbBizJournalDao;
	
	private ProxyBankInfoDao proxyBankInfoDao;
	
	private CpService cpService;
	private static final Log logger = LogFactory.getLog(ChinaPayCutService.class);

public SingleResp singleCutQuery(String orderNo,String transDate){
	SingleResp singleResp = new SingleResp();
	String merId=PropFileUtil.getByFileAndKey("chinapay.properties", "cutMerchId");
	String transType="0003";
	String version="20100831";
	String priv1="";
	StringBuilder sb = new StringBuilder();
	sb.append(merId).append(transType).append(orderNo).append(transDate).append(version);
	String plainText = sb.toString();
	String merchKeyPath=PropFileUtil.getByFileAndKey("chinapay.properties","merchkeyForCutPath");
	String chkValue=ChinaPaySignUtil.sign(plainText,merId,merchKeyPath);
	HttpHelp http = new HttpHelp();
	HttpRequestParam param = new HttpRequestParam();
	param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","singleCutQueryUrl"));
	param.setEncoding("gbk");
	Map<String,String> params =Maps.newHashMap();
	param.setParams(params);
	params.put("merId", merId);
	params.put("transType", transType);
	params.put("orderNo", orderNo);
	params.put("transDate", transDate);
	params.put("version", version);
	params.put("priv1", priv1);
	params.put("chkValue", chkValue);
	HttpResponser response=http.postParamByHttpClient(param);
	if(response!=null){
		String result = response.getContent();
		Map<String, String> keyValMap = ChinaPaySignUtil.parseResponse(result);
		if (keyValMap != null) {
			String responseCode = keyValMap.get("responseCode");
			String returnChkValue = keyValMap.get("chkValue");
			if (!Strings.isNullOrEmpty(responseCode)) {
				ChinaCutResCodeStatusCode returnCode = ChinaCutResCodeStatusCode.fromCode(responseCode);
				int lastIndex = result.lastIndexOf("&");
				plainText = result.substring(0, lastIndex+10);
				logger.info("交易结果:" + returnCode.getDescription());
				 String pulicKeyPath=PropFileUtil.getByFileAndKey("chinapay.properties","publicKey");
				boolean isSucc=ChinaPaySignUtil.verifySign(plainText, returnChkValue,pulicKeyPath);
				if(isSucc){
					if("00".equals(responseCode)){
						String transStat= keyValMap.get("transStat");
						if(!Strings.isNullOrEmpty(transStat)){
							ChinaCutResCodeStatusCode transState =ChinaCutResCodeStatusCode.fromStatusCode(transStat);
							if("S".equals(transState.getHandleStatus())){
								singleResp.setCode(DhbTranStatus.Succ.getCode());
								singleResp.setMessage(DhbTranStatus.Succ.getDescription());
							}
							else if("U".equals(transState.getHandleStatus())){
								singleResp.setCode(DhbTranStatus.Handling.getCode());
								singleResp.setMessage(DhbTranStatus.Handling.getDescription());
							}else{
								singleResp.setCode(DhbTranStatus.Fail.getCode());
								singleResp.setMessage(DhbTranStatus.Fail.getDescription());
							}
							logger.info("transState:("+transState.getDescription()+")");
							
						}
					}
					
					
				}

			}
		}
		logger.info("singleCutQuery return code(" + response.getCode()
				+ "),result(" + result + ")");
	}
	return singleResp;
}
public SingleResp singlePayQuery(String merSeqId,String merDate) {
	SingleResp singleResp = new SingleResp();
	String merId = PropFileUtil.getByFileAndKey("chinapay.properties", "payMerchId");
	String version = "20090501";
	String signFlag = "1";
	StringBuilder sb = new StringBuilder();
	sb.append(merId).append(merSeqId).append(merDate).append(version)
			.append(signFlag);
	String plainText = sb.toString();
	String merchKeyPath=PropFileUtil.getByFileAndKey("chinapay.properties","merchkeyForPayPath");
	String chkValue = ChinaPaySignUtil.sign(plainText, merId,merchKeyPath);
	HttpHelp http = new HttpHelp();
	HttpRequestParam param = new HttpRequestParam();
	param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","chinaPaySingleQuery"));
	param.setEncoding("gbk");
	Map<String, String> params = Maps.newLinkedHashMap();
	param.setParams(params);
	params.put("merId", merId);
	params.put("merDate", merDate);
	params.put("merSeqId", merSeqId);
	params.put("version", version);
	params.put("signFlag", signFlag);
	params.put("chkValue", chkValue);
	HttpResponser response = http.postParamByHttpClient(param);
	if (response != null) {
		String result = response.getContent();
		if(200==response.getCode()){
			if(!Strings.isNullOrEmpty(result)){
				List<String> list = ChinaPaySignUtil.parseQueryResponse(result);
				if (list != null) {
					String code = list.get(0);
					int length = list.size();
					String returnChkValue = list.get(length - 1);
					int lastIndex = result.lastIndexOf("|");
					plainText = result.substring(0, lastIndex + 1);
					boolean verifySucc = false;
					if (!Strings.isNullOrEmpty(returnChkValue)) {
						String pulickeyPath = PropFileUtil.getByFileAndKey("chinapay.properties","publicKey");
						verifySucc = ChinaPaySignUtil.verifySign(plainText, returnChkValue,pulickeyPath);
						if(verifySucc){
							
						}
					}
					if ("000".equals(code)) {
						singleResp.setCode(DhbTranStatus.Succ.getCode());
						singleResp.setMessage(DhbTranStatus.Succ.getDescription());
					}
					if ("001".equals(code)) {
						logger.info("merId(" + merId + "),merSeqId(" + merSeqId
								+ ") 查询无结果");
					}
					if ("002".equals(code)) {
						logger.info("merId(" + merId + "),merSeqId(" + merSeqId
								+ ") 查询失败");
					}
					logger.info("chinaPay single pay return code("
							+ response.getCode() + "),result(" + result + ")");
				}
			}
		}

	}
	return singleResp;
}
@Override
public SingleResp singleCut(OutRequestInfo info) throws Exception {
	SingleResp singleResp = new SingleResp();
	String merId= PropFileUtil.getByFileAndKey("chinapay.properties", "cutMerchId"); //商户号
	Date date =new Date();
	String transDate =DateUtil.getDayForYYMMDD(date);//商户日期
	String orderNo=StringUtils.leftPad(getSequenceDao().getNextVal("chinaPay_seq")+"", 16, '0');//订单号
	String transType="0003"; //交易类型
	String bankName = info.getBankName();
	String openBankId=getChinaPayDaoService().findOpenBankIdByBankName(bankName); //开户行号
	if(openBankId==null){
		singleResp.setCode(DhbTranStatus.Fail.getCode());
		singleResp.setMessage("不支持此银行");
		return singleResp;
	}
	String cardType="0"; //卡号或存折号标识位(0表示卡,1表示折)
	String cardNo =info.getAccNo(); //银行卡号
	String usrName=Tools.toUnicodeString(info.getAccName());//持卡人姓名 需以unicode传值 如：天的unicode为\u5929
	String certType =info.getCertType(); //身份证01； 军官证02 ；	护照03 ；	户口簿04 ；	回乡证05 ；	其他06；
    String certId=info.getCertNo();//证件号
    String curyId="156";//币种 固定为156，表示货币单位为人民币
    String transAmt =String.valueOf((int)(info.getBanlance()*100));//整数，以分为单位
	String priv1 =Tools.toUnicodeString("");
	String version="20150304";
	String gateId="7008";
	String termType="";
	StringBuilder sb = new StringBuilder();
	/*merId + transDate + orderNo + transType + openBankId + cardType + cardNo +usrName + certType + certId + curyId + transAmt +  priv1+version + gateId + termType*/
	sb.append(merId).append(transDate).append(orderNo).append(transType).append(openBankId)
	  .append(cardType).append(cardNo).append(usrName).append(certType).append(certId)
	  .append(curyId).append(transAmt).append(priv1).append(version).append(gateId).append(termType);  
	String plainText=sb.toString();
	String merchKeyPath=PropFileUtil.getByFileAndKey("chinapay.properties","merchkeyForCutPath");
	
	String chkValue=ChinaPaySignUtil.sign(plainText,merId,merchKeyPath);
	HttpHelp http = new HttpHelp();
	HttpRequestParam param = new HttpRequestParam();
	param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","singleCutUrl"));
	param.setEncoding("gbk");
	Map<String,String> params =Maps.newLinkedHashMap();
	param.setParams(params);
	params.put("merId", merId);
	params.put("transDate", transDate);
	params.put("orderNo", orderNo);
	params.put("transType", transType);
	params.put("openBankId", openBankId);
	params.put("cardType", cardType);
	params.put("cardNo", cardNo);
	params.put("usrName", usrName);
	params.put("certType", certType);
	params.put("certId", certId);
	params.put("curyId", curyId);
	params.put("transAmt", transAmt);
	params.put("priv1", priv1);
	params.put("version", version);
	params.put("gateId", gateId);
	params.put("termType", termType);
	params.put("chkValue", chkValue);
	String channelId = info.getChannelId();
	DhbBankInfo bankInfo =getProxyBankInfoService().getCutBankInfoByChannleId(channelId);
	DhbBizJournal journal = new DhbBizJournal();
	journal.setId(orderNo);
	journal.setMerchId(info.getMerchId());
	journal.setBizType(BizType.Cut.getCode());
	journal.setChannelId(channelId);
	journal.setFromBankCardNo(bankInfo.getAcctNo());
	journal.setFromBankCode(bankInfo.getBankCode());
	journal.setFromBankName(bankInfo.getBankName());
	journal.setFromUserName(bankInfo.getAcctName());
	journal.setToBankCardNo(info.getAccNo());
	journal.setToBankCode(info.getBankCode());
	journal.setToBankName(info.getBankName());
	journal.setToUserName(info.getAccName());
	journal.setCurrency("CNY");
	journal.setMoney(info.getBanlance());
	journal.setMemo(info.getComments());
	journal.setCreateTime(date);
	journal.setRecordId(info.getRecordId());
	getDhbBizJournalService().insertJournal(journal);
	HttpResponser response=http.postParamByHttpClient(param);
	journal.setHandleRemark(DhbTranStatus.Fail.getDescription());
	journal.setHandleStatus(DhbTranStatus.Fail.getCode());
	if(response!=null){
		String result = response.getContent();
		if(200==response.getCode()){
				if (!Strings.isNullOrEmpty(result)) {
					Map<String, String> keyValMap = ChinaPaySignUtil
							.parseResponse(result);
					if (keyValMap != null) {
						String responseCode = keyValMap.get("responseCode");
						String returnChkValue = keyValMap.get("chkValue");
						if (!Strings.isNullOrEmpty(responseCode)) {
							ChinaCutResCodeStatusCode returnCode = ChinaCutResCodeStatusCode
									.fromCode(responseCode);
							int lastIndex = result.lastIndexOf("&");
							plainText = result.substring(0, lastIndex + 10);
							logger.info("交易结果:" + returnCode.getDescription());
							String pulickeyPath = PropFileUtil.getByFileAndKey(
									"chinapay.properties",
									"publickeyForCutPath");
							boolean isSucc = ChinaPaySignUtil.verifySign(
									plainText, returnChkValue, pulickeyPath);
							if (isSucc) {
								String transStat = keyValMap.get("transStat");
								if (!Strings.isNullOrEmpty(transStat)) {
									ChinaCutResCodeStatusCode transState = ChinaCutResCodeStatusCode
											.fromStatusCode(transStat);
									if ("S".equals(transState.getHandleStatus())) {
										journal.setHandleRemark(DhbTranStatus.Succ
												.getDescription());
										journal.setHandleStatus(DhbTranStatus.Succ
												.getCode());
									}
									if ("U".equals(transState.getHandleStatus())) {
										journal.setHandleRemark(DhbTranStatus.Handling
												.getDescription());
										journal.setHandleStatus(DhbTranStatus.Handling
												.getCode());
									}
									if (!Strings.isNullOrEmpty(transState
											.getDescription())) {
										journal.setHandleRemark(transState
												.getDescription());
									}

									logger.info("transState:("
											+ transState.getDescription() + ")");

								}

							}

						}
					}
				}
		}
		journal.setEndTime(new Date());
		getDhbBizJournalService().updateStatusById(journal);
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		logger.info("chinaPay single cut return code(" + response.getCode()
				+ "),result(" + result + ")");
	}
	return singleResp;
}

@Override
public SingleResp singlePay(OutRequestInfo info) throws Exception {
	SingleResp singleResp = new SingleResp();
	String merId = PropFileUtil.getByFileAndKey("chinapay.properties", "payMerchId");
	Date date = new Date();
	String merDate = DateUtil.getDayForYYMMDD(date);// 32位
	String merSeqId = StringUtils.leftPad(
			getSequenceDao().getNextVal("chinaPay_seq")+"", 16, '0');
	String cardNo = info.getAccNo(); // 32
	String usrName = info.getAccName(); // 100
	String openBank = info.getBankName(); // 50
	String prov = ""; // 20
	String city = ""; // 40
	String transAmt = String.valueOf((int)(info.getBanlance()*100));// //12 分
	String purpose = "稿费"; //
	String subBank = "";
	String flag = info.getAccType();// “00”对私，“01”对公
	if(Strings.isNullOrEmpty(flag)){
		flag ="00";
	}
	String version = "20090501";
	String signFlag = "1";
	StringBuilder sb = new StringBuilder();
	sb.append(merId).append(merDate).append(merSeqId).append(cardNo)
			.append(usrName).append(openBank).append(prov).append(city)
			.append(transAmt).append(purpose).append(subBank).append(flag)
			.append(version);

	String plainText = sb.toString();
	String merchKeyPath=PropFileUtil.getByFileAndKey("chinapay.properties","merchkeyForPayPath");
	
	String chkValue = ChinaPaySignUtil.sign(plainText, merId,merchKeyPath);
	HttpHelp http = new HttpHelp();
	HttpRequestParam param = new HttpRequestParam();
	param.setUrl(PropFileUtil.getByFileAndKey("chinapay.properties","singlePayUrl"));
	param.setEncoding("gbk");
	Map<String, String> params = Maps.newLinkedHashMap();
	param.setParams(params);
	params.put("merId", merId);
	params.put("merDate", merDate);
	params.put("merSeqId", merSeqId);
	params.put("cardNo", cardNo);
	params.put("usrName", usrName);
	params.put("openBank", openBank);
	params.put("prov", prov);
	params.put("city", city);
	params.put("transAmt", transAmt);
	params.put("purpose", purpose);
	params.put("flag", flag);
	params.put("subBank", subBank);
	params.put("version", version);
	params.put("signFlag", signFlag);
	params.put("chkValue", chkValue);
	String channelId = info.getChannelId();
	DhbBankInfo bankInfo =getProxyBankInfoService().getPayBankInfoByChannleId(channelId);
	DhbBizJournal journal = new DhbBizJournal();
	journal.setId(merSeqId);
	journal.setMerchId(info.getMerchId());
	journal.setBizType(BizType.Pay.getCode());
	journal.setChannelId(channelId);
	journal.setFromBankCardNo(bankInfo.getAcctNo());
	journal.setFromBankCode(bankInfo.getBankCode());
	journal.setFromBankName(bankInfo.getBankName());
	journal.setFromUserName(bankInfo.getAcctName());
	journal.setToBankCardNo(info.getAccNo());
	journal.setToBankCode(info.getBankCode());
	journal.setToBankName(info.getBankName());
	journal.setToUserName(info.getAccName());
	journal.setCurrency("CNY");
	journal.setMoney(info.getBanlance());
	journal.setMemo(info.getComments());
	journal.setCreateTime(date);
	journal.setRecordId(info.getRecordId());
	getDhbBizJournalService().insertJournal(journal);
	HttpResponser response = http.postParamByHttpClient(param);
	journal.setHandleRemark(DhbTranStatus.Fail.getDescription());
	journal.setHandleStatus(DhbTranStatus.Fail.getCode());
	if (response != null) {
		String result = response.getContent();
		if(200==response.getCode()){
			if (!Strings.isNullOrEmpty(result)) {
			Map<String, String> keyValMap = ChinaPaySignUtil.parseResponse(result);
			if (keyValMap != null) {
				String responseCode = keyValMap.get("responseCode");
				String returnChkValue = keyValMap.get("chkValue");
				if (!Strings.isNullOrEmpty(responseCode)) {
					ChinaPayResponseCode returnCode = ChinaPayResponseCode
							.fromCode(responseCode);
					int lastIndex = result.lastIndexOf("&");
					plainText = result.substring(0, lastIndex);
					logger.info("交易结果:" + returnCode.getDescription());
					String pulickeyPath = PropFileUtil.getByFileAndKey("chinapay.properties","publicKey");
					if(ChinaPaySignUtil.verifySign(plainText, returnChkValue,pulickeyPath)){
						String stat = keyValMap.get("responseCode");
						if("0000".equals(returnCode.getCode())){
							CpPayStateType state =CpPayStateType.getStateByCode(stat);
							if(state!=null){
								if("S".equals(state.getCode())){
									journal.setHandleRemark(DhbTranStatus.Succ.getDescription());
									journal.setHandleStatus(DhbTranStatus.Succ.getCode());
								}
								if("U".equals(state.getCode())){
									journal.setHandleRemark(DhbTranStatus.Handling.getDescription());
									journal.setHandleStatus(DhbTranStatus.Handling.getCode());
								}
								if(!Strings.isNullOrEmpty(state.getDiscription())){
									journal.setHandleRemark(state.getDiscription());
								}
							}
						}else{
							if(!Strings.isNullOrEmpty(returnCode.getDescription())){
								journal.setHandleRemark(returnCode.getDescription());
							}
						}
					}
					
				}
			}
			}
		}
		journal.setEndTime(new Date());
		getDhbBizJournalService().updateStatusById(journal);
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		logger.info("chinaPay single pay return code(" + response.getCode()
				+ "),result(" + result + ")");
	}

	return singleResp;
}

@Override
public SingleResp querySingleTranStatus(OutRequestInfo info) throws Exception {
	SingleResp singleResp = new SingleResp(); 
	DhbBizJournal journal =getDhbBizJournalService().getBizJournalByReqInfo(info);
	if(journal==null){
		throw new CutException("not find this record");
	}
	if(DhbTranStatus.Succ.getCode().equals(journal.getHandleStatus())){
		singleResp.setCode(DhbTranStatus.Succ.getCode());
		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
		return singleResp;
	}
	String batchId = journal.getBatchId();
	String seqNo = journal.getId();
	Date createTime = journal.getCreateTime();
	if(Strings.isNullOrEmpty(batchId)){
		if(BizType.Cut.getCode().equals(journal.getBizType())){
			singleResp =singleCutQuery(seqNo,  DateUtil.getDayForYYMMDD(createTime));
		}
		if(BizType.Pay.getCode().equals(journal.getBizType())){
			singleResp =singlePayQuery(seqNo,  DateUtil.getDayForYYMMDD(createTime));
		}
		 if(DhbTranStatus.Succ.getCode().equals(journal.getHandleStatus())){
			 journal.setEndTime(new Date());
			 journal.setHandleRemark(singleResp.getMessage());
			 journal.setHandleStatus(singleResp.getCode());
			 getDhbBizJournalService().updateStatusById(journal);
		 }
	}else{
		singleResp =cpService.getFileStatus(batchId, seqNo);
		if(DhbTranStatus.Succ.getCode().equals(singleResp.getCode())){
			cpService.getBatchResp(batchId, seqNo);
		}else{
			
		}
	}
	return singleResp;
}

@Override
public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
	
	return cpService.batchCut(batchReq);
}
public SequenceDao getSequenceDao() {
	return sequenceDao;
}
public void setSequenceDao(SequenceDao sequenceDao) {
	this.sequenceDao = sequenceDao;
}
public ChinaPayDaoService getChinaPayDaoService() {
	return chinaPayDaoService;
}
public void setChinaPayDaoService(ChinaPayDaoService chinaPayDaoService) {
	this.chinaPayDaoService = chinaPayDaoService;
}
public DhbBizJournalDao getDhbBizJournalService() {
	return dhbBizJournalDao;
}
public void setDhbBizJournalService(DhbBizJournalDao dhbBizJournalService) {
	this.dhbBizJournalDao = dhbBizJournalService;
}
public ProxyBankInfoDao getProxyBankInfoService() {
	return proxyBankInfoDao;
}
public void setProxyBankInfoService(ProxyBankInfoDao proxyBankInfoService) {
	this.proxyBankInfoDao = proxyBankInfoService;
}
public CpService getCpService() {
	return cpService;
}
public void setCpService(CpService cpService) {
	this.cpService = cpService;
}
@Override
public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
		throws Exception {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void queryTranStatus(DhbBizJournal journal) {
	cpService.queryTranStatus(journal);
	
}

}
