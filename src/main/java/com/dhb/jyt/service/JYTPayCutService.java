package com.dhb.jyt.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.jyt.entity.JYTResp;
import com.dhb.jyt.entity.JYTTranType;
import com.dhb.service.PayCutInterface;
import com.dhb.service.ProxySinglePayCutThreadService;
import com.dhb.util.PropFileUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;


public class JYTPayCutService implements PayCutInterface{
	public static Logger logger = Logger.getLogger(JYTPayCutService.class);
	
	private CommonObjectDao commonObjectDao;
	
	private SequenceDao sequenceDao;
	private ProxySinglePayCutThreadService proxySinglePayCutThreadService;
	private DhbBizJournalDao dhbBizJournalService;
	private ProxyBankInfoDao proxyBankInfoService;
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
   
	public SingleResp singleCut(OutRequestInfo reqInfo)throws Exception{
		String forTest= PropFileUtil.getByFileAndKey("jyt.properties", "forTest");
		if("test".equals(forTest)){
			return forTest(reqInfo);
		}
		SingleResp singleResp = new SingleResp(); 
		StringBuffer xml = new StringBuffer();
		Date now = new Date();
		String tranType = JYTTranType.SingleCut.getCode();
		String id = JYTHelp.getInstance().getMerchId()
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
		xml.append(head);
		String fromBankName = reqInfo.getBankName();
		String fromAccNo = reqInfo.getAccNo();
		String fromAccName = reqInfo.getAccName();
		String fromCertType = reqInfo.getCertType();
		String fromCertNo = reqInfo.getCertNo();
		String tel = reqInfo.getTel();
		if(Strings.isNullOrEmpty(tel)){
   			tel ="";
   		}
		Double meney = reqInfo.getBanlance();
		String accType = reqInfo.getAccType();
   		if(Strings.isNullOrEmpty(accType)){
   			accType ="00";
   		}
		xml.append("<body><mer_viral_acct></mer_viral_acct><agrt_no></agrt_no>")
		   .append("<bank_name>").append(fromBankName).append("</bank_name>")
		   .append("<account_no>").append(fromAccNo).append("</account_no>")
		   .append("<account_name>").append(fromAccName).append("</account_name>")
		   .append("<account_type>").append(accType).append("</account_type>")
		   .append("<brach_bank_province>").append("").append("</brach_bank_province>")
		   .append("<brach_bank_city>").append("").append("</brach_bank_city>")
		   .append("<brach_bank_name>").append("").append("</brach_bank_name>")
		   .append("<tran_amt>").append(meney).append("</tran_amt>")
		   .append("<currency>").append("CNY").append("</currency>")
		   .append("<bsn_code>").append("14900").append("</bsn_code>")//其他费用14900
		   .append("<cert_type>").append(fromCertType).append("</cert_type>")
		   .append("<cert_no>").append(fromCertNo).append("</cert_no>")
		   .append("<mobile>").append(tel).append("</mobile>").append("<remark>").append("").append("</remark>")
		   .append("<reserve></reserve>")
		   .append("</body></message>");
		String toXml = xml.toString();
		logger.info("JYT singleCut xml:"+toXml);
		String mac=JYTHelp.getInstance().signMsg(toXml);
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Cut.getCode());
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =getProxyBankInfoService().getCutBankInfoByChannleId(channelId);
		journal.setFromBankCardNo(fromAccNo);
		journal.setFromIdentityNo(fromCertNo);
		journal.setFromBankName(fromBankName);
		journal.setFromUserName(fromAccName);
		journal.setToBankCardNo(bankInfo.getAcctNo());
		journal.setToBankCode(bankInfo.getBankCode());
		journal.setToBankName(bankInfo.getBankName());
		journal.setToUserName(bankInfo.getAcctName());
		journal.setMoney(meney);
		journal.setBatchId(reqInfo.getBatchId());
		journal.setCurrency("CNY");
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalService.insertJournal(journal);
        String respXml = JYTHelp.getInstance().sendMsg(toXml, mac);
        logger.info("JYT singleCut resp:"+respXml);
        JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
        if(jresp!=null){
        	String respCode = jresp.getRespCode();
        	String respDesc = jresp.getRespDesc();
        	 if("S0000000".equals(respCode)){
        		  String tranState=jresp.getTranState();
             	if("01".equals(tranState)){
             		singleResp.setCode(DhbTranStatus.Succ.getCode());
             		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
             	}
             	else if("02".equals(tranState)){
             		singleResp.setCode(DhbTranStatus.Handling.getCode());
             		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
             	}else{
             		singleResp.setCode(DhbTranStatus.Fail.getCode());
             		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
             	}	
             }else if("E0000000".equals(respCode)){
             	singleResp.setCode(DhbTranStatus.Handling.getCode());
             	singleResp.setMessage(DhbTranStatus.Handling.getDescription());
             }else{
             	singleResp.setCode(DhbTranStatus.Fail.getCode());
             	singleResp.setMessage(respDesc);
             }
             DhbBizJournal updatejournal = new DhbBizJournal();
             updatejournal.setId(id);
             updatejournal.setEndTime(new Date());
             updatejournal.setHandleRemark(singleResp.getMessage());
             updatejournal.setHandleStatus(singleResp.getCode());
             dhbBizJournalService.updateStatusById(updatejournal);
        }
       /* String respCode = JYTHelp.getInstance().getMsgRespCode(respXml);
        String tranCode=JYTHelp.getInstance().getMsgState(respXml);*/
       
		return singleResp;
	}
    public Double getBalance(String accNo){
    	Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
   		String tranType = JYTTranType.SinglePay.getCode();
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		StringBuffer xml = new StringBuffer();
   		xml.append(head);
   
   		xml.append("<body><mer_viral_acct>").append(accNo)
   		   .append("</mer_viral_acct></body></message>");
   		String toXml = xml.toString();
   		logger.info("JYT getBalance xml:"+toXml);
   		String mac=JYTHelp.getInstance().signMsg(toXml);
   	    try {
			JYTHelp.getInstance().sendMsg(toXml, mac);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
 
    public SingleResp forTest(OutRequestInfo reqInfo){
 		String fromBankName = reqInfo.getBankName();
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertType = reqInfo.getCertType();
   		String fromCertNo = reqInfo.getCertNo();
   		Double meney = reqInfo.getBanlance();
   		String tel = reqInfo.getTel();
   		if(Strings.isNullOrEmpty(tel)){
   			tel ="";
   		}
   		String accType = reqInfo.getAccType();
   		if(Strings.isNullOrEmpty(accType)){
   			accType ="00";
   		}
   		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
    	SingleResp singleResp = new SingleResp();
    	singleResp.setCode(DhbTranStatus.Succ.getCode());
    	singleResp.setMessage(DhbTranStatus.Succ.getDescription());
    	DhbBizJournal journal = new DhbBizJournal();
   		String channelId = reqInfo.getChannelId();
   		journal.setId(id);
   		journal.setMerchId(reqInfo.getMerchId());
   		journal.setBizType(reqInfo.getBizType());
   		journal.setChannelId(reqInfo.getChannelId());
   		DhbBankInfo bankInfo =getProxyBankInfoService().getPayBankInfoByChannleId(channelId);
   		journal.setFromBankCardNo(fromAccNo);
   		journal.setFromIdentityNo(fromCertNo);
   		journal.setFromBankName(fromBankName);
   		journal.setFromUserName(fromAccName);
   		journal.setToBankCardNo(bankInfo.getAcctNo());
   		journal.setToBankCode(bankInfo.getBankCode());
   		journal.setToBankName(bankInfo.getBankName());
   		journal.setToUserName(bankInfo.getAcctName());
   		journal.setMoney(meney);
   		journal.setBatchId(reqInfo.getBatchId());
   		journal.setCurrency("CNY");
   		journal.setMemo(reqInfo.getComments());
   		journal.setCreateTime(now);
   		journal.setRecordId(reqInfo.getRecordId());
   		dhbBizJournalService.insertJournal(journal);
   		journal.setHandleRemark(singleResp.getMessage());
   		journal.setHandleStatus(singleResp.getCode());
     dhbBizJournalService.updateStatusById(journal);
    	return singleResp;
    }
   	public SingleResp singlePay(OutRequestInfo reqInfo)throws Exception{
   		String forTest= PropFileUtil.getByFileAndKey("jyt.properties", "forTest");
		if("test".equals(forTest)){
			return forTest(reqInfo);
		}
   		SingleResp singleResp = new SingleResp(); 
   		StringBuffer xml = new StringBuffer();
   		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
   		String tranType = JYTTranType.SinglePay.getCode();
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head);
   		String fromBankName = reqInfo.getBankName();
   		String fromAccNo = reqInfo.getAccNo();
   		String fromAccName = reqInfo.getAccName();
   		String fromCertType = reqInfo.getCertType();
   		String fromCertNo = reqInfo.getCertNo();
   		Double meney = reqInfo.getBanlance();
   		String tel = reqInfo.getTel();
   		if(Strings.isNullOrEmpty(tel)){
   			tel ="";
   		}
   		String accType = reqInfo.getAccType();
   		if(Strings.isNullOrEmpty(accType)){
   			accType ="00";
   		}
   		xml.append("<body><mer_viral_acct></mer_viral_acct><agrt_no></agrt_no>")
   		   .append("<bank_name>").append(fromBankName).append("</bank_name>")
   		   .append("<account_no>").append(fromAccNo).append("</account_no>")
   		   .append("<account_name>").append(fromAccName).append("</account_name>")
   		   .append("<account_type>").append(accType).append("</account_type>")
   		   .append("<brach_bank_province>").append("").append("</brach_bank_province>")
   		   .append("<brach_bank_city>").append("").append("</brach_bank_city>")
   		   .append("<brach_bank_name>").append("").append("</brach_bank_name>")
   		   .append("<tran_amt>").append(meney).append("</tran_amt>")
   		   .append("<currency>").append("CNY").append("</currency>")
   		   .append("<bsn_code>").append("09200").append("</bsn_code>")//其他费用
   		   .append("<cert_type>").append(fromCertType).append("</cert_type>")
   		   .append("<cert_no>").append(fromCertNo).append("</cert_no>")
   		   .append("<mobile>").append(tel).append("</mobile>").append("<remark>").append("").append("</remark>")
   		   .append("<reserve></reserve>")
   		   .append("</body></message>");
   		String toXml = xml.toString();
   		logger.info("JYT singlePay xml:"+toXml);
   		String mac=JYTHelp.getInstance().signMsg(toXml);
   		DhbBizJournal journal = new DhbBizJournal();
   		String channelId = reqInfo.getChannelId();
   		journal.setId(id);
   		journal.setMerchId(reqInfo.getMerchId());
   		journal.setBizType(BizType.Pay.getCode());
   		journal.setChannelId(reqInfo.getChannelId());
   		DhbBankInfo bankInfo =getProxyBankInfoService().getPayBankInfoByChannleId(channelId);
   		journal.setFromBankCardNo(fromAccNo);
   		journal.setFromIdentityNo(fromCertNo);
   		journal.setFromBankName(fromBankName);
   		journal.setFromUserName(fromAccName);
   		journal.setToBankCardNo(bankInfo.getAcctNo());
   		journal.setToBankCode(bankInfo.getBankCode());
   		journal.setToBankName(bankInfo.getBankName());
   		journal.setToUserName(bankInfo.getAcctName());
   		journal.setMoney(meney);
   		journal.setBatchId(reqInfo.getBatchId());
   		journal.setCurrency("CNY");
   		journal.setMemo(reqInfo.getComments());
   		journal.setCreateTime(now);
   		journal.setRecordId(reqInfo.getRecordId());
   		dhbBizJournalService.insertJournal(journal);
           String respXml = JYTHelp.getInstance().sendMsg(toXml, mac);
           logger.info("JYT singlePay resp:"+respXml);
         /*  String respCode = JYTHelp.getInstance().getMsgRespCode(respXml);
           String tranCode=JYTHelp.getInstance().getMsgState(respXml);*/
           JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
           if(jresp!=null){
           		String respCode = jresp.getRespCode();
           		String respDesc = jresp.getRespDesc();
           	 	if("S0000000".equals(respCode)){
           	 		String tranState=jresp.getTranState();
                	if("01".equals(tranState)){
                		singleResp.setCode(DhbTranStatus.Succ.getCode());
                		singleResp.setMessage(DhbTranStatus.Succ.getDescription());
                	}
                	else if("02".equals(tranState)){
                		singleResp.setCode(DhbTranStatus.Handling.getCode());
                		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
                	}else{
                		singleResp.setCode(DhbTranStatus.Fail.getCode());
                		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
                	}	
                }else if("E0000000".equals(respCode)){
                	singleResp.setCode(DhbTranStatus.Handling.getCode());
                	singleResp.setMessage(DhbTranStatus.Handling.getDescription());
                }else{
                	singleResp.setCode(DhbTranStatus.Fail.getCode());
                	singleResp.setMessage(respDesc);
                }
                DhbBizJournal updatejournal = new DhbBizJournal();
                updatejournal.setId(id);
                updatejournal.setEndTime(new Date());
                updatejournal.setHandleRemark(singleResp.getMessage());
                updatejournal.setHandleStatus(singleResp.getCode());
                dhbBizJournalService.updateStatusById(updatejournal);
           }
   		return singleResp;
   	}
	
	public SingleResp batchPay(BatchTranReq tranReq)throws Exception{
		return handleBatch(tranReq);
	}

	public SingleResp batchCut(BatchTranReq tranReq)throws Exception{
		
		return handleBatch(tranReq);
	}
	
	private SingleResp handleBatch(BatchTranReq tranReq)throws Exception{
		
		SingleResp resp = new SingleResp();
		int threadCount = tranReq.getInfo().size();
		CountDownLatch threadsSignal = new CountDownLatch(threadCount);
		List<SingleResp> list = Lists.newArrayList();
		for(OutRequestInfo reqInfo:tranReq.getInfo()){
			reqInfo.setBatchId(tranReq.getBatchId());
			SingleResp singleResp = new SingleResp();
			list.add(singleResp);
			proxySinglePayCutThreadService.toSinglePayCut(reqInfo, this, threadsSignal, singleResp);
		}
		threadsSignal.await();
		resp.setCode(DhbTranStatus.Handling.getCode());
		resp.setMessage(DhbTranStatus.Handling.getDescription());
		return resp;
	
	}
	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info) throws Exception {
		
		SingleResp singleResp = new SingleResp(); 
		singleResp.setCode(DhbTranStatus.Handling.getCode());
		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
		DhbBizJournal journal=dhbBizJournalService.getBizJournalByReqInfo(info);
		if(journal==null){
			singleResp.setCode(DhbTranStatus.Fail.getCode());
			singleResp.setMessage(DhbTranStatus.Fail.getDescription());
			return singleResp;
		}
		singleResp.setCode(journal.getHandleStatus());
		singleResp.setMessage(journal.getHandleRemark());
		return singleResp;
		/*
		SingleResp singleResp = new SingleResp(); 
   		StringBuffer xml = new StringBuffer();
		String merchId = info.getMerchId();
		String tranNo = info.getTranNo();
		String sql = "select recordId from dhb_pay_cut where merchId=:merchId and outId=:outId";
		String recordId = getCommonObjectDao().findSingleVal(sql, new Object[]{merchId,tranNo});
		String journalSql = "select id,bizType,handleStatus from dhb_biz_journal where recordId=:recordId";
		DhbBizJournal journal = getCommonObjectDao().findOneObject(journalSql, DhbBizJournal.class, new Object[]{recordId});
		if(journal==null){
			throw new CutException("not find this record");
		}
		String bizType=journal.getBizType();
		String queryId = journal.getId();
		String handleStatus = journal.getHandleStatus();
		if(DhbTranStatus.Succ.getCode().equals(handleStatus)){
			singleResp.setCode(DhbTranStatus.Succ.getCode());
			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
			return singleResp;
		}
		BizType currentType= BizType.findByCode(bizType);
		String tranType = null;
		if(BizType.Cut.equals(currentType)){
			tranType = JYTTranType.QuerySingleCut.getCode();
		}
		if(BizType.Pay.equals(currentType)){
			tranType = JYTTranType.QuerySinglePay.getCode();
		}
		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head).append("<body><ori_tran_flowid>").append(queryId)
		   .append("</ori_tran_flowid>")
		   .append("</body></message>");
   		String toXml = xml.toString();
		logger.info("JYT querySingleTranStatus xml:"+toXml);
		String mac=JYTHelp.getInstance().signMsg(toXml);
	
        String respXml = JYTHelp.getInstance().sendMsg(toXml, mac);
        logger.info("JYT querySingleTranStatus resp:"+respXml);
        JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
        if(jresp!=null){
        	String respCode = jresp.getRespCode();
        	String respMessage = jresp.getRespDesc();
        	 if("S0000000".equals(respCode)){
        		 String tranRespCode = jresp.getTranRespCode();
        		 String tranStates = jresp.getTranState();
        		 String tranRespMessage= jresp.getTranRespDesc();
             	if("S0000000".equals(tranRespCode)){
            		if("01".equals(tranStates)){
            			singleResp.setCode(DhbTranStatus.Succ.getCode());
            			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
            		}
            		else if("00".equals(tranStates)){
            			singleResp.setCode(DhbTranStatus.Handling.getCode());
            			 singleResp.setMessage(DhbTranStatus.Handling.getDescription());
            		}else{
            			singleResp.setCode(DhbTranStatus.Fail.getCode());
            			 singleResp.setMessage(tranRespMessage);
            		}	
            	}else{
            		 singleResp.setCode(DhbTranStatus.Fail.getCode());
            		 singleResp.setMessage(tranRespMessage);
            	}
            }else{
             	singleResp.setCode(DhbTranStatus.Fail.getCode());
             	singleResp.setMessage(respMessage);
             }
        }
        if(!singleResp.getCode().equals(journal.getHandleStatus())){
        	journal.setEndTime(new Date());
        	journal.setHandleRemark(singleResp.getMessage());
        	journal.setHandleStatus(singleResp.getCode());
        	dhbBizJournalService.updateStatusById(journal);
        }
		return singleResp;
	
	*/}



	public CommonObjectDao getCommonObjectDao() {
		return commonObjectDao;
	}


	public void setCommonObjectDao(CommonObjectDao commonObjectDao) {
		this.commonObjectDao = commonObjectDao;
	}


	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}


	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}


	public ProxyBankInfoDao getProxyBankInfoService() {
		return proxyBankInfoService;
	}


	public void setProxyBankInfoService(ProxyBankInfoDao proxyBankInfoService) {
		this.proxyBankInfoService = proxyBankInfoService;
	}


	public DhbBizJournalDao getDhbBizJournalService() {
		return dhbBizJournalService;
	}


	public void setDhbBizJournalService(DhbBizJournalDao dhbBizJournalService) {
		this.dhbBizJournalService = dhbBizJournalService;
	}
	public ProxySinglePayCutThreadService getProxySinglePayCutThreadService() {
		return proxySinglePayCutThreadService;
	}
	public void setProxySinglePayCutThreadService(
			ProxySinglePayCutThreadService proxySinglePayCutThreadService) {
		this.proxySinglePayCutThreadService = proxySinglePayCutThreadService;
	}
	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		String merchId = info.getMerchId();
		String batchId = info.getBatchId();
		List<DhbBizJournal> list=dhbBizJournalService.getJournalByBatchId(merchId, batchId);
		List<SingleResp> lists = Lists.newArrayList();
		for(DhbBizJournal journal:list){
			SingleResp single = new SingleResp();
			single.setCode(journal.getHandleStatus());
			single.setMessage(journal.getHandleRemark());
			single.setTranNo(journal.getId());
			lists.add(single);
		}
		logger.info("batchQuery batchId("+batchId+"),"+lists.toString());
		return lists;
	}
	private void querySingleJournalStatus(DhbBizJournal journal){
		StringBuffer xml = new StringBuffer();
		SingleResp singleResp = new SingleResp(); 
		String bizType=journal.getBizType();
		String queryId = journal.getId();
		BizType currentType= BizType.findByCode(bizType);
		String tranType = null;
		if(BizType.Cut.equals(currentType)){
			tranType = JYTTranType.QuerySingleCut.getCode();
		}
		if(BizType.Pay.equals(currentType)){
			tranType = JYTTranType.QuerySinglePay.getCode();
		}
		Date now = new Date();
   		String id = JYTHelp.getInstance().getMerchId()
   				+ df.format(now)
   				+ tf.format(now)
   				+ StringUtils.leftPad(getSequenceDao().getNextVal("hyt_seq")+"",6, '0');
   		String head = JYTHelp.getInstance().getMsgHeadXml(tranType, id);
   		xml.append(head).append("<body><ori_tran_flowid>").append(queryId)
		   .append("</ori_tran_flowid>")
		   .append("</body></message>");
   		String toXml = xml.toString();
		logger.info("JYT querySingleTranStatus xml:"+toXml);
		String mac=JYTHelp.getInstance().signMsg(toXml);
	
        String respXml=null;
		try {
			respXml = JYTHelp.getInstance().sendMsg(toXml, mac);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.info("JYT querySingleTranStatus resp:"+respXml);
        JYTResp jresp = JYTHelp.getInstance().getResp(respXml);
        singleResp.setCode(DhbTranStatus.Handling.getCode());
        if(jresp!=null){
        	String respCode = jresp.getRespCode();
        	String respMessage = jresp.getRespDesc();
        	 if("S0000000".equals(respCode)){
        		 String tranRespCode = jresp.getTranRespCode();
        		 String tranStates = jresp.getTranState();
        		 String tranRespMessage= jresp.getTranRespDesc();
             	if("S0000000".equals(tranRespCode)){
            		if("01".equals(tranStates)){
            			singleResp.setCode(DhbTranStatus.Succ.getCode());
            			singleResp.setMessage(DhbTranStatus.Succ.getDescription());
            		}
            		else if("00".equals(tranStates)){
            			singleResp.setCode(DhbTranStatus.Handling.getCode());
            			 singleResp.setMessage(DhbTranStatus.Handling.getDescription());
            		}else{
            			singleResp.setCode(DhbTranStatus.Fail.getCode());
            			 singleResp.setMessage(tranRespMessage);
            		}	
            	}else if("E0000000".equals(tranRespCode)){
                 	singleResp.setCode(DhbTranStatus.Handling.getCode());
                 	singleResp.setMessage(DhbTranStatus.Handling.getDescription());
                 }else{
            		 singleResp.setCode(DhbTranStatus.Fail.getCode());
            		 singleResp.setMessage(tranRespMessage);
            	}
            }else if("E0000000".equals(respCode)){
             	singleResp.setCode(DhbTranStatus.Handling.getCode());
             	singleResp.setMessage(DhbTranStatus.Handling.getDescription());
             }else{
             	singleResp.setCode(DhbTranStatus.Fail.getCode());
             	singleResp.setMessage(respMessage);
             }
        }
        if(!singleResp.getCode().equals(journal.getHandleStatus())){
        	journal.setEndTime(new Date());
        	journal.setHandleRemark(singleResp.getMessage());
        	journal.setHandleStatus(singleResp.getCode());
        	dhbBizJournalService.updateStatusById(journal);
        }
	}
	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		String batchId = journal.getBatchId();
		if(Strings.isNullOrEmpty(batchId)){
			querySingleJournalStatus(journal);
		}else{
			String merchId = journal.getMerchId();
			List<DhbBizJournal>  list=dhbBizJournalService.getJournalByBatchId(merchId, batchId);
			for(DhbBizJournal jour:list){
				querySingleJournalStatus(jour);
			}
		}
	
		
	}
	
}
