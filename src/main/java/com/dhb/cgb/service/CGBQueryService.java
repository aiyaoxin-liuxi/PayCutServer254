package com.dhb.cgb.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.cgb.entity.BatchPayResp;
import com.dhb.cgb.entity.BatchPayRespItem;
import com.dhb.cgb.entity.QueryBatchStatus;
import com.dhb.cgb.entity.QueryHostStatus;
import com.dhb.cgb.entity.SingleQueryResp;
import com.dhb.controller.DHBPayController;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.entity.CGBTranCodeType;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.SingleResp;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.dhb.util.PropFileUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

@Service
public class CGBQueryService {
	private static final Log logger = LogFactory.getLog(DHBPayController.class);
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	private String getHeadStr(String uuId,String tranCode,Date now){
		
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding = \"GBK\"?>\r\n")
		.append("<BEDC>\r\n")
		.append("<Message>\r\n")
		.append("<commHead>\r\n")
		.append("<tranCode>").append(tranCode).append("</tranCode>\r\n")
		.append("<cifMaster>").append(PropFileUtil.getByFileAndKey("cgb.properties", "cifMaster")).append("</cifMaster>\r\n")
		.append("<entSeqNo>").append(uuId).append("</entSeqNo>\r\n")
		.append("<tranDate>").append(DateUtil.getDayForYYMMDD(now)).append("</tranDate>\r\n")
		.append("<tranTime>").append(DateUtil.getTimeForHHmmss(now)).append("</tranTime>\r\n")
		.append("<retCode>").append("").append("</retCode>\r\n")
		.append("<entUserId>").append(PropFileUtil.getByFileAndKey("cgb.properties", "entUserId")).append("</entUserId>\r\n")
		.append("<password>").append(PropFileUtil.getByFileAndKey("cgb.properties", "password")).append("</password>\r\n")
		.append("</commHead>\r\n");
		return sb.toString();
	}
	private String getEnd(){
		return "</Body>\r\n</Message>\r\n</BEDC>\r\n";
	}
	public void queryTranStatus(DhbBizJournal journal,String uuId){
		String batchId = journal.getBatchId();
		Date createTime = journal.getCreateTime();
		String dateStr = null;
		if(createTime!=null){
			dateStr = DateUtil.getDayForYYMMDD(createTime);
		}
		if(!Strings.isNullOrEmpty(batchId)){
				String merchId = journal.getMerchId();
				if(!Strings.isNullOrEmpty(merchId)){
					queryBatchTranStatus(uuId,batchId,"",dateStr,merchId);
				}
			
			
		}else{
			querySingleTranStatus(uuId,journal,dateStr);
			
		}
	}
	public SingleResp queryBatchTranStatus(String uuId,String batchId,String queryId,String dateStr,String merchId){
		SingleResp singleResp = new SingleResp();
		singleResp.setCode(DhbTranStatus.Fail.getCode());
		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		Date now = new Date();
		String head =getHeadStr(uuId,CGBTranCodeType.OutBatchQuery.getCode(),now);
		StringBuilder sb = new StringBuilder();
		sb.append(head)
		.append("<Body>\r\n")
		.append("<customerBatchNo>").append(batchId).append("</customerBatchNo>\r\n")
		.append("<customerSalarySeq>").append("").append("</customerSalarySeq>\r\n")
		.append(getEnd());
		String xml = sb.toString();
		logger.info("gcb querySingleTranStatus xml:\r\n"+xml);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setEncoding("GBK");
		param.setUrl(PropFileUtil.getByFileAndKey("cgb.properties", "url"));
		Map<String, String> params = Maps.newHashMap();
		params.put("cgb_data", xml);
		param.setParams(params);
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(headParam);
		HttpResponser resp=send.postParamByHttpClient(param);
		if(resp!=null){
			if(200==resp.getCode()){
				String content = resp.getContent();
				if(!Strings.isNullOrEmpty(content)){
					logger.info("queryBatchTranStatus resp: reqId("+uuId+"),queryId("+queryId+")\r\n "+content);
					BatchPayResp  batchResp=CGBParseXml.getInstance().getBacthPayResp(content);
					if(batchResp!=null){
						 String entSeqNo =batchResp.getEntSeqNo();
						 String retCode = batchResp.getRetCode();
						 List<BatchPayRespItem> list =batchResp.getItems();
						 if(uuId.equals(entSeqNo)){
							 if("000".equals(retCode)){
								 List<DhbBizJournal> jourList =dhbBizJournalDao.getJournalByBatchId(merchId, batchId);
								 Map<String,DhbBizJournal> maps = Maps.newHashMap();
								 for(DhbBizJournal journal:jourList){ 
									 maps.put(journal.getId(), journal);
								 }
								 for(BatchPayRespItem item:list){
									 String seq =item.getCustomerSalarySeq();
									 String hostStatus = item.getBankstatus();
									 DhbTranStatus status= QueryBatchStatus.getTranStatusByCode(hostStatus);
									
									 DhbBizJournal journal= maps.get(seq);
									 if(journal!=null){
										 String oldStatus = journal.getHandleStatus();
										 if(!status.getCode().equals(oldStatus)){
											 journal.setEndTime(new Date());
											 journal.setHandleStatus(status.getCode());
											 journal.setHandleRemark(status.getDescription());
											 dhbBizJournalDao.updateStatusById(journal);
										 }
									 }
									 if(queryId!=null&&queryId.equals(seq)){
											 singleResp.setCode(status.getCode());
											 singleResp.setMessage(status.getDescription());
									 }
								 }
								
							 }
						 }
					}
					
					
				}
				
			}
		}
		return singleResp;
	}
	public SingleResp querySingleTranStatus(String uuId,DhbBizJournal journal,String dateStr){
		SingleResp singleResp = new SingleResp();
		singleResp.setCode(DhbTranStatus.Fail.getCode());
		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		Date now = new Date();
		String head =getHeadStr(uuId,CGBTranCodeType.QueryStatus.getCode(),now);
		StringBuilder sb = new StringBuilder();
		String queryId = journal.getId();
		sb.append(head)
		.append("<Body>\r\n")
		.append("<origEntseqno>").append(queryId).append("</origEntseqno>\r\n")
		.append("<origEntdate>").append(dateStr).append("</origEntdate>\r\n")
		.append(getEnd());
		String xml = sb.toString();
		logger.info("gcb querySingleTranStatus xml:\r\n"+xml);
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setEncoding("GBK");
		param.setUrl(PropFileUtil.getByFileAndKey("cgb.properties", "url"));
		Map<String, String> params = Maps.newHashMap();
		params.put("cgb_data", xml);
		param.setParams(params);
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/x-www-form-urlencoded");
		param.setHeads(headParam);
		HttpResponser resp=send.postParamByHttpClient(param);
		if(resp!=null){
			if(200==resp.getCode()){
				String content = resp.getContent();
				if(!Strings.isNullOrEmpty(content)){
					logger.info("gcb querySingleTranStatus resp: reqId("+uuId+"),queryId("+queryId+")\r\n "+content);
					SingleQueryResp  singleQueryResp=CGBParseXml.getInstance().getSingleQueryResp(content);
					if(singleQueryResp!=null){
						 String entSeqNo =singleQueryResp.getEntSeqNo();
						 String retCode = singleQueryResp.getRetCode();
						 String hostStatus = singleQueryResp.getHostStatus();
						 if(uuId.equals(entSeqNo)){
							 if("000".equals(retCode)){
								 if(!Strings.isNullOrEmpty(hostStatus)){
									DhbTranStatus status= QueryHostStatus.getTranStatusByCode(hostStatus);
									singleResp.setCode(status.getCode());
									singleResp.setMessage(status.getDescription());
									journal.setHandleStatus(status.getCode());
									journal.setHandleRemark(status.getDescription());
									journal.setEndTime(new Date());
									 dhbBizJournalDao.updateStatusById(journal);
								 }
							 }
						 }
					}
					
					
				}
				
			}
		}
		return singleResp;
	}
	
	
}
