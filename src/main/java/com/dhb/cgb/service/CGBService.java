package com.dhb.cgb.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.cgb.entity.BankOutResp;
import com.dhb.cgb.entity.BatchPayResp;
import com.dhb.cgb.entity.BatchPayRespItem;
import com.dhb.cgb.entity.CommHead;
import com.dhb.cgb.entity.OutBEDC;
import com.dhb.cgb.entity.OutBatchBEDC;
import com.dhb.cgb.entity.OutBatchBody;
import com.dhb.cgb.entity.OutBatchMessage;
import com.dhb.cgb.entity.OutBody;
import com.dhb.cgb.entity.OutMessage;
import com.dhb.cgb.entity.Record;
import com.dhb.cgb.entity.Records;
import com.dhb.controller.DHBPayController;
import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.CGBTranCodeType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.entity.form.InBEDC;
import com.dhb.entity.form.InBody;
import com.dhb.entity.form.InMessage;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.dhb.util.XmlUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
@Service
public class CGBService {
	private static final Log logger = LogFactory.getLog(DHBPayController.class);
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private CGBDaoService cgbDaoService;
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private CGBQueryService cgbQueryService;
	public SingleResp bankInTran(OutRequestInfo reqInfo)throws CutException{
		SingleResp singleResp = new SingleResp();
		singleResp.setTranNo(reqInfo.getTranNo());
		InBEDC data = new InBEDC();
		InMessage message = new InMessage();
		data.setMessage(message);
		CommHead head = new CommHead();
		InBody body = new InBody();
		message.setCommHead(head);
		message.setBody(body);
		//head
		head.setCifMaster(PropFileUtil.getByFileAndKey("cgb.properties", "cifMaster"));
		
		Date now = new Date();
		String uuid = "cgb"
				+ DateUtil.getDayForYYMMDD(now)
				+ DateUtil.getTimeForHHmmss(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("cgb_dhb_seq")+"",3, '0');
		head.setEntSeqNo(uuid);
		head.setEntUserId(PropFileUtil.getByFileAndKey("cgb.properties", "entUserId"));
		head.setPassword(PropFileUtil.getByFileAndKey("cgb.properties", "password"));
		head.setRetCode("");
		head.setTranCode(CGBTranCodeType.BankInTran.getCode());

		String date =DateUtil.getDayForYYMMDD(now);
		head.setTranDate(date);
		head.setTranTime(DateUtil.getTimeForHHmmss(now));
		String channelId = reqInfo.getChannelId();
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		//body
		body.setAmount(reqInfo.getBanlance());
		body.setComment(reqInfo.getComments());
		body.setCreNo("");
		body.setDate(date);
		body.setInAcc(reqInfo.getAccNo());
		body.setInAccBank(reqInfo.getBankName());
		body.setInAccName(reqInfo.getAccName());
		body.setOutAcc(bankInfo.getAcctNo());
		body.setOutAccBank(bankInfo.getBankName());
		body.setOutAccName(bankInfo.getAcctName());
		String xml=XmlUtil.ObjectToXml(data,"GBK");
		logger.info("bankInTran request: reqId("+uuid+")\r\n "+xml);
	
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setEncoding("GBK");
		String url = PropFileUtil.getByFileAndKey("cgb.properties", "url");
		param.setUrl(url);
		Map<String, String> params = Maps.newHashMap();
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded");
		params.put("cgb_data", xml);
		param.setParams(params);
		param.setHeads(headParam);
		
		// conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		DhbBizJournal journal = new DhbBizJournal();
		journal.setId(uuid);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Pay.getCode());
		journal.setChannelId(channelId);
		journal.setFromBankCardNo(bankInfo.getAcctNo());
		journal.setFromBankCode(bankInfo.getBankCode());
		journal.setFromBankName(bankInfo.getBankName());
		journal.setFromUserName(bankInfo.getAcctName());
		journal.setToBankCardNo(reqInfo.getAccNo());
		journal.setToBankCode(reqInfo.getBankCode());
		journal.setToBankName(reqInfo.getBankName());
		journal.setToUserName(reqInfo.getAccName());
		journal.setCurrency("CNY");
		journal.setMoney(reqInfo.getBanlance());
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		HttpResponser resp=send.postParamByHttpClient(param);

		handleSinglePay(journal,resp);
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
	}
	public SingleResp bankOutTran(OutRequestInfo reqInfo)throws CutException{
		SingleResp singleResp = new SingleResp();
		singleResp.setTranNo(reqInfo.getTranNo());
		OutBEDC data = new OutBEDC();
		OutMessage message = new OutMessage();
		data.setMessage(message);
		CommHead head = new CommHead();
		OutBody body = new OutBody();
		message.setCommHead(head);
		message.setBody(body);
		//head
		head.setCifMaster(PropFileUtil.getByFileAndKey("cgb.properties", "cifMaster"));
		Date now = new Date();
		String uuid = "cgb"
				+ DateUtil.getDayForYYMMDD(now)
				+ DateUtil.getTimeForHHmmss(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("cgb_dhb_seq")+"",3, '0');
		head.setEntSeqNo(uuid);
		head.setEntUserId(PropFileUtil.getByFileAndKey("cgb.properties", "entUserId"));
		head.setPassword(PropFileUtil.getByFileAndKey("cgb.properties", "password"));
		head.setRetCode("");
		head.setTranCode(CGBTranCodeType.BankOutTran.getCode());
	
		String date =DateUtil.getDayForYYMMDD(now);
		head.setTranDate(date);
		head.setTranTime(DateUtil.getTimeForHHmmss(now));
		String channelId = reqInfo.getChannelId();
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		//body
		body.setOutAcc(bankInfo.getAcctNo());
		body.setOutAccName(bankInfo.getAcctName());
		body.setInAcc(reqInfo.getAccNo());
		body.setInAccBank(reqInfo.getBankName());
		body.setInAccName(reqInfo.getAccName());
		Double amout = reqInfo.getBanlance();
		body.setAmount(amout);
		body.setComment(reqInfo.getComments());
		body.setPaymentBankid(reqInfo.getBankCode());
		String xml=XmlUtil.ObjectToXml(data,"GBK");
		logger.info("bankOutTran request: reqId("+uuid+")\r\n "+xml);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setEncoding("GBK");
		param.setUrl(PropFileUtil.getByFileAndKey("cgb.properties", "url"));
		Map<String, String> params = Maps.newHashMap();
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(headParam);
		params.put("cgb_data", xml);
		param.setParams(params);
		DhbBizJournal journal = new DhbBizJournal();
		journal.setId(uuid);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Pay.getCode());
		journal.setChannelId(channelId);
		journal.setFromBankCardNo(bankInfo.getAcctNo());
		journal.setFromBankCode(bankInfo.getBankCode());
		journal.setFromBankName(bankInfo.getBankName());
		journal.setFromUserName(bankInfo.getAcctName());
		journal.setToBankCardNo(reqInfo.getAccNo());
		journal.setToBankCode(reqInfo.getBankCode());
		journal.setToBankName(reqInfo.getBankName());
		journal.setToUserName(reqInfo.getAccName());
		journal.setCurrency("CNY");
		journal.setMoney(reqInfo.getBanlance());
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		HttpResponser resp=send.postParamByHttpClient(param);
		handleSinglePay(journal,resp);
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
	return singleResp;
	}
	
	private CommHead getHead(String tranCode){
		CommHead head = new CommHead();
		head.setCifMaster(PropFileUtil.getByFileAndKey("cgb.properties", "cifMaster"));
		Date now = new Date();
		String uuid = null;
	
			 uuid = "cgb"
						+ DateUtil.getDayForYYMMDD(now)
						+ DateUtil.getTimeForHHmmss(now)
						+ StringUtils.leftPad(sequenceDao.getNextVal("cgb_dhb_seq")+"",3, '0');
		head.setEntSeqNo(uuid);
		head.setEntUserId(PropFileUtil.getByFileAndKey("cgb.properties", "entUserId"));
		head.setPassword(PropFileUtil.getByFileAndKey("cgb.properties", "password"));
		head.setRetCode("");
		head.setTranCode(tranCode);
		String date =DateUtil.getDayForYYMMDD(now);
		head.setTranDate(date);
		head.setTranTime(DateUtil.getTimeForHHmmss(now));
		return head;
	}
	
	public SingleResp batchPay(BatchTranReq tranReq)throws Exception{
		SingleResp batchResp = new SingleResp();
		OutBatchBEDC data = new OutBatchBEDC();
		OutBatchMessage message = new OutBatchMessage();
		data.setMessage(message);
		//
		CommHead head = getHead(CGBTranCodeType.OutBatchPay.getCode());
		Date now = new Date();
		String uuid = head.getEntSeqNo();
		OutBatchBody body = new OutBatchBody();
		message.setCommHead(head);
		message.setBody(body);
		//head
		String channelId = tranReq.getChannelId();
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		String batchId = tranReq.getBatchId();
		
		body.setAccountNo(bankInfo.getAcctNo());
		body.setAllCount(tranReq.getTotalNum());
		body.setAllSalary(tranReq.getTotalBalance());
		body.setCustomerBatchNo(batchId);
		Records records = new Records();
		List<Record> list = Lists.newArrayList();
	    records.setRecord(list);;
		body.setRecords(records);
		int recordLength = tranReq.getInfo().size();
		Map<String,DhbBizJournal> journalMap = Maps.newHashMap();
		for(OutRequestInfo info :tranReq.getInfo()){
			Record record = new Record();
			list.add(record);
			record.setBankCode(info.getBankCode());
			record.setComment(info.getComments());
			String seq =  "cgb"
					+ DateUtil.getDayForYYMMDD(now)
					+ DateUtil.getTimeForHHmmss(now)
					+ StringUtils.leftPad(sequenceDao.getNextVal("cgb_dhb_seq")+"",3, '0');
			record.setCustomerSalarySeq(seq);
			record.setInacc(info.getAccNo());
			record.setInaccbank(info.getBankName());
			record.setInaccadd("");
			record.setInaccname(info.getAccName());
			record.setRemark("");
			record.setSalary(info.getBanlance());
			if(info.getBankName().contains("广发银行")){
				record.setTransType("0");
			}else{
				record.setTransType("1");
			}
			DhbBizJournal journal = new DhbBizJournal();
			journal.setId(seq);
			journalMap.put(seq, journal);
			journal.setMerchId(info.getMerchId());
			journal.setBizType(BizType.Pay.getCode());
			journal.setChannelId(channelId);
			journal.setBatchId(batchId);
			journal.setFromBankCardNo(bankInfo.getAcctNo());
			journal.setFromBankCode(bankInfo.getBankCode());
			journal.setFromBankName(bankInfo.getBankName());
			journal.setFromUserName(bankInfo.getAcctName());
			journal.setToBankCardNo(info.getAccNo());
			journal.setToBankCode(info.getBankCode());
			journal.setToBankName(info.getBankName());
			journal.setToUserName(info.getAccName());
			journal.setMoney(info.getBanlance());
			journal.setCurrency("CNY");
			journal.setMemo(info.getComments());
			journal.setCreateTime(now);
			journal.setRecordId(info.getRecordId());
			dhbBizJournalDao.insertJournal(journal);
		}
		String xml=XmlUtil.ObjectToXml(data,"GBK");
		logger.info("batchPay request: reqId("+uuid+")\r\n "+xml);
	
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setEncoding("GBK");
		param.setUrl(PropFileUtil.getByFileAndKey("cgb.properties", "url"));
		Map<String, String> params = Maps.newHashMap();
		params.put("cgb_data", xml);
		param.setParams(params);
		HttpResponser resp=send.postParamByHttpClient(param);
		batchResp.setCode(DhbTranStatus.Fail.getCode());
		batchResp.setMessage(DhbTranStatus.Fail.getDescription());
		if(resp!=null){
			if(200==resp.getCode()){
				String content = resp.getContent();
				if(content!=null){
					logger.info("batchPay resp: reqId("+uuid+")\r\n "+content);
					BatchPayResp  bankOutResp=CGBParseXml.getInstance().getBacthPayResp(content);
					String seqNo =bankOutResp.getEntSeqNo();
					String respCode =bankOutResp.getRetCode();
					//batchResp.setErrorCode(DhbTranStatus.Succ.getCode());
					if(uuid.equals(seqNo)){
						if("000".equals(respCode)){
							batchResp.setCode(DhbTranStatus.Handling.getCode());
							batchResp.setMessage(DhbTranStatus.Handling.getDescription());
							//int allErrCount = bankOutResp.getAllErrCount();
							//batchResp.setErrorCount(allErrCount);
							Date endTime = new Date();
							for(BatchPayRespItem item:bankOutResp.getItems()){
								DhbBizJournal errorJour=journalMap.get(item.getCustomerSalarySeq());
								if(errorJour!=null){
									SingleResp single = new SingleResp();
									//batchResp.getRecord().add(single);
									String errorMessage=item.getRetMes();
									if(Strings.isNullOrEmpty(errorMessage)){
										errorMessage = DhbTranStatus.Handling.getDescription();
										single.setCode(DhbTranStatus.Handling.getCode());
										single.setMessage(errorMessage);
										single.setTranNo(item.getCustomerSalarySeq());
									}else{
										single.setCode(DhbTranStatus.Fail.getCode());
										single.setMessage(errorMessage);
										single.setTranNo(item.getCustomerSalarySeq());
									}
									
									errorJour.setEndTime(endTime);
									errorJour.setHandleStatus(single.getCode());
									errorJour.setHandleRemark(single.getMessage());
									dhbBizJournalDao.updateStatusById(errorJour);
									journalMap.remove(item.getCustomerSalarySeq());
								}
							}
							/*if(recordLength>allErrCount){
								for(DhbBizJournal jour:journalMap.values()){
									jour.setEndTime(endTime);
									jour.setHandleStatus(DhbTranStatus.Handling.getCode());
									jour.setHandleRemark(DhbTranStatus.Handling.getDescription());
									dhbBizJournalService.updateStatusById(jour);
								}
							}else{
								if(bankOutResp.getItems()==null||bankOutResp.getItems().size()==0){
									for(DhbBizJournal jour:journalMap.values()){
										jour.setEndTime(endTime);
										jour.setHandleStatus(DhbTranStatus.Fail.getCode());
										jour.setHandleRemark(DhbTranStatus.Fail.getDescription());
										dhbBizJournalService.updateStatusById(jour);
									}
								}
							}*/
						}else{
							String errorMessage=cgbDaoService.getErrorInfo(respCode);
							logger.error("errorMessage("+errorMessage+")");
							if(Strings.isNullOrEmpty(errorMessage)){
								errorMessage = DhbTranStatus.Fail.getDescription();
							}
							batchResp.setMessage(errorMessage);
							   // batchResp.setErrorCount(recordLength);
								for(DhbBizJournal jour:journalMap.values()){
									jour.setEndTime(new Date());
									jour.setHandleStatus(DhbTranStatus.Fail.getCode());
									jour.setHandleRemark(errorMessage);
									dhbBizJournalDao.updateStatusById(jour);
								}
							
						}
					}else{
						logger.error("reqId("+uuid+")!= respId("+seqNo+")");
					}
				}
				
			}else{
				for(DhbBizJournal jour:journalMap.values()){
					jour.setEndTime(new Date());
					jour.setHandleStatus(DhbTranStatus.Fail.getCode());
					jour.setHandleRemark(DhbTranStatus.Fail.getDescription());
					dhbBizJournalDao.updateStatusById(jour);
				}
			}
		}
		
		return batchResp;
	}
	private void handleSinglePay(DhbBizJournal journal,HttpResponser resp){
		journal.setHandleRemark(DhbTranStatus.Fail.getDescription());
		journal.setHandleStatus(DhbTranStatus.Fail.getCode());
		String uuid =journal.getId();
		if(resp!=null){
			if(200==resp.getCode()){
				String content = resp.getContent();
				if(content!=null){
					logger.info("bankOutTran resp: reqId("+uuid+")\r\n "+content);
					BankOutResp  bankOutResp=CGBParseXml.getInstance().getBankOutResp(content);
					if(bankOutResp!=null){
						String seqNo =bankOutResp.getEntSeqNo();
						String respCode =bankOutResp.getRetCode();
						if(uuid.equals(seqNo)){
							if("000".equals(respCode)){
								journal.setHandleRemark(DhbTranStatus.Handling.getDescription());
								journal.setHandleStatus(DhbTranStatus.Handling.getCode());
							}else{
								journal.setHandleStatus(DhbTranStatus.Fail.getCode());
								String errorMessage=cgbDaoService.getErrorInfo(respCode);
								logger.info("errorMessage:("+errorMessage+")");
								if(Strings.isNullOrEmpty(errorMessage)){
									journal.setHandleRemark(DhbTranStatus.Fail.getDescription());
								}else{
									journal.setHandleRemark(errorMessage);
								}
							}
						}else{
							logger.error("reqId("+uuid+")!= respId("+seqNo+")");
						}
					}
					
				}
				
			}else{
				journal.setHandleRemark(DhbTranStatus.Fail.getDescription());
				journal.setHandleStatus(DhbTranStatus.Fail.getCode());
			}
		}
		journal.setEndTime(new Date());
		dhbBizJournalDao.updateStatusById(journal);
	}
	
	public SingleResp querySingleTranStatus(OutRequestInfo info)throws Exception{
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
	private String getSeq(Date now){
		String uuid = "cgb"
				+ DateUtil.getDayForYYMMDD(now)
				+ DateUtil.getTimeForHHmmss(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("cgb_dhb_seq")+"",3, '0');
		return uuid;
	}
	public void queryTranStatus(DhbBizJournal journal) {
		Date now = new Date();
		String uuId = getSeq(now);
		cgbQueryService.queryTranStatus(journal, uuId);
	}
	public CommonObjectDao getCommonObjectDao() {
		return commonObjectDao;
	}
	public void setCommonObjectDao(CommonObjectDao commonObjectDao) {
		this.commonObjectDao = commonObjectDao;
	}
	
}
