package com.dhb.zhl.service;

import java.util.Date;
import java.util.List;

import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BigMerchInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.DhbTranStatus;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.service.PayCutInterface;
import com.google.common.base.Strings;

public class ZHLPayCutService implements PayCutInterface{
	
	private DhbBizJournalDao dhbBizJournalService;
	
	private ProxyBankInfoDao proxyBankInfoService;
	
	private SkipService skipService;
	@Override
	public SingleResp singleCut(OutRequestInfo info) throws Exception {
		SingleResp resp = new SingleResp();
		resp.setCode(DhbTranStatus.Fail.getCode());
		resp.setMessage(DhbTranStatus.Fail.getDescription());
		DhbBizJournal journal = skipService.saveSingleJournal(info);
		BigMerchInfo merchinfo =skipService.makeUpReqInfo(info, true);
		if(merchinfo==null){
			journal.setHandleRemark(DhbTranStatus.Fail.getCode());
			journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
			journal.setEndTime(new Date());
			dhbBizJournalService.updateStatusById(journal);
			return resp;
		}
		String toUrl =merchinfo.getUrl()+"/dhb/singleCut";
		SingleResp httpResp=skipService.sendHttp(info, toUrl);
		if(httpResp==null){
			httpResp = resp;
		}
		resp.setCode(httpResp.getCode());
		resp.setMessage(httpResp.getMessage());
		journal.setHandleRemark(httpResp.getMessage());
		journal.setHandleStatus(httpResp.getCode());
		journal.setEndTime(new Date());
		journal.setId(httpResp.getTranNo());
		dhbBizJournalService.updateStatusById(journal);
		return resp;
	}

	@Override
	public SingleResp singlePay(OutRequestInfo info) throws Exception {
		SingleResp resp = new SingleResp();
		resp.setCode(DhbTranStatus.Fail.getCode());
		resp.setMessage(DhbTranStatus.Fail.getDescription());
		DhbBizJournal journal = skipService.saveSingleJournal(info);
		BigMerchInfo merchinfo =skipService.makeUpReqInfo(info, true);
		if(merchinfo==null){
			journal.setHandleRemark(DhbTranStatus.Fail.getCode());
			journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
			journal.setEndTime(new Date());
			dhbBizJournalService.updateStatusById(journal);
			return resp;
		}
		String toUrl =merchinfo.getUrl()+"/dhb/singlePay";
		SingleResp httpResp=skipService.sendHttp(info, toUrl);
		if(httpResp==null){
			httpResp = resp;
		}
		resp.setCode(httpResp.getCode());
		resp.setMessage(httpResp.getMessage());
		journal.setHandleRemark(httpResp.getMessage());
		journal.setHandleStatus(httpResp.getCode());
		journal.setEndTime(new Date());
		dhbBizJournalService.updateStatusById(journal);
		return resp;
	}

	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		SingleResp resp = new SingleResp();
		resp.setCode(DhbTranStatus.Fail.getCode());
		resp.setMessage(DhbTranStatus.Fail.getDescription());
		DhbBizJournal journal = dhbBizJournalService.getBizJournalByReqInfo(info);
		info.setBatchId(null);
		BigMerchInfo merchinfo =skipService.makeUpReqInfo(info, false);
		if(merchinfo==null){
			journal.setHandleRemark(DhbTranStatus.Fail.getCode());
			journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
			journal.setEndTime(new Date());
			dhbBizJournalService.updateStatusById(journal);
			return resp;
		}
		String toUrl =merchinfo.getUrl()+"/dhb/querySingleTranStatus";
		SingleResp httpResp=skipService.sendHttp(info, toUrl);
		if(httpResp==null){
			httpResp = resp;
		}
		resp.setCode(httpResp.getCode());
		resp.setMessage(httpResp.getMessage());
		journal.setHandleRemark(httpResp.getMessage());
		journal.setHandleStatus(httpResp.getCode());
		journal.setEndTime(new Date());
		journal.setId(httpResp.getTranNo());
		dhbBizJournalService.updateStatusById(journal);
		return resp;
	}

	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SingleResp batchPay(BatchTranReq batchReq) throws Exception {
		SingleResp resp = new SingleResp();
		resp.setCode(DhbTranStatus.Fail.getCode());
		resp.setMessage(DhbTranStatus.Fail.getDescription());
		skipService.saveBatchJournal(batchReq);
		BigMerchInfo merchinfo =skipService.makeUpReq(batchReq, true);
		if(merchinfo==null){
			DhbBizJournal journal = new DhbBizJournal();
			journal.setHandleRemark(DhbTranStatus.Fail.getCode());
			journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
			journal.setEndTime(new Date());
			dhbBizJournalService.updateStatusByBatchId(journal);
			return resp;
		}
		String toUrl =merchinfo.getUrl()+"/dhb/batchPay";
		SingleResp httpResp=skipService.sendHttp(batchReq, toUrl);
		if(httpResp==null){
			httpResp = resp;
		}
		resp.setCode(httpResp.getCode());
		resp.setMessage(httpResp.getMessage());
		DhbBizJournal journal = new DhbBizJournal();
		journal.setHandleRemark(httpResp.getMessage());
		journal.setHandleStatus(httpResp.getCode());
		journal.setEndTime(new Date());
		journal.setId(httpResp.getTranNo());
		dhbBizJournalService.updateStatusByBatchId(journal);
		return resp;
	}

	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		SingleResp resp = new SingleResp();
		resp.setCode(DhbTranStatus.Fail.getCode());
		resp.setMessage(DhbTranStatus.Fail.getDescription());
		skipService.saveBatchJournal(batchReq);
		BigMerchInfo merchinfo =skipService.makeUpReq(batchReq, true);
		if(merchinfo==null){
			DhbBizJournal journal = new DhbBizJournal();
			journal.setHandleRemark(DhbTranStatus.Fail.getCode());
			journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
			journal.setEndTime(new Date());
			dhbBizJournalService.updateStatusByBatchId(journal);
			return resp;
		}
		String toUrl =merchinfo.getUrl()+"/dhb/batchCut";
		SingleResp httpResp=skipService.sendHttp(batchReq, toUrl);
		if(httpResp==null){
			httpResp = resp;
		}
		resp.setCode(httpResp.getCode());
		resp.setMessage(httpResp.getMessage());
		DhbBizJournal journal = new DhbBizJournal();
		journal.setHandleRemark(httpResp.getMessage());
		journal.setHandleStatus(httpResp.getCode());
		journal.setEndTime(new Date());
		journal.setBatchId(batchReq.getBatchId());
		dhbBizJournalService.updateStatusByBatchId(journal);;
		return resp;
	}

	public DhbBizJournalDao getDhbBizJournalService() {
		return dhbBizJournalService;
	}

	public void setDhbBizJournalService(DhbBizJournalDao dhbBizJournalService) {
		this.dhbBizJournalService = dhbBizJournalService;
	}

	public ProxyBankInfoDao getProxyBankInfoService() {
		return proxyBankInfoService;
	}

	public void setProxyBankInfoService(ProxyBankInfoDao proxyBankInfoService) {
		this.proxyBankInfoService = proxyBankInfoService;
	}

	public SkipService getSkipService() {
		return skipService;
	}

	public void setSkipService(SkipService skipService) {
		this.skipService = skipService;
	}

	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		String batchId = journal.getBatchId();
		OutRequestInfo info = new OutRequestInfo();
		info.setBigChannelId(journal.getBigChannelId());
		info.setTranNo(journal.getId());
		BigMerchInfo merchinfo=skipService.makeUpReqInfo(info, false);
		if(Strings.isNullOrEmpty(batchId)){
			if(merchinfo==null){
				handleFail(journal);
				return ;
			}
			String toUrl =merchinfo.getUrl()+"/dhb/querySingleTranStatus";
			SingleResp httpResp=skipService.sendHttp(info, toUrl);
			if(httpResp==null){
				//handleFail(journal);
				return;
			}
			journal.setHandleRemark(httpResp.getMessage());
			journal.setHandleStatus(httpResp.getCode());
			journal.setEndTime(new Date());
			journal.setId(httpResp.getTranNo());
			dhbBizJournalService.updateStatusById(journal);
		}else{
			info.setBatchId(batchId);
			String toUrl =merchinfo.getUrl()+"/dhb/queryBatchTranStatus";
			List<SingleResp> lists=skipService.sendBatchHttp(info, toUrl);
			if(lists==null){
				return;
			}
			for(SingleResp single:lists){
				DhbBizJournal jour = new DhbBizJournal();
				jour.setId(single.getTranNo());
				jour.setHandleRemark(single.getMessage());
				jour.setHandleStatus(single.getCode());
				jour.setEndTime(new Date());
				dhbBizJournalService.updateStatusById(journal);
			}
		}
		
	}
   private void handleFail(DhbBizJournal journal){
	   journal.setHandleRemark(DhbTranStatus.Fail.getCode());
		journal.setHandleStatus(DhbTranStatus.Fail.getDescription());
		journal.setEndTime(new Date());
		dhbBizJournalService.updateStatusById(journal);
   }
}
