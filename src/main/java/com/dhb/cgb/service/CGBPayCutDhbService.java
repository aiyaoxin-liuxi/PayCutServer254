package com.dhb.cgb.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.dhb.controller.DHBPayController;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.entity.exception.CutException;
import com.dhb.service.PayCutInterface;


public class CGBPayCutDhbService implements PayCutInterface{
	private static final Log logger = LogFactory.getLog(DHBPayController.class);
	
	private CGBService cgbService;
	public SingleResp singleCut(OutRequestInfo reqInfo)throws Exception{
		logger.error("CGBPayCutService: not implement");
		throw new CutException("not implement");
	}
	@Transactional
	public SingleResp batchPay(BatchTranReq tranReq)throws Exception{
		return getCgbService().batchPay(tranReq);
	}
	
	@Override
	public SingleResp singlePay(OutRequestInfo info) throws Exception {
		SingleResp singleResp = new SingleResp();
		String toBankName = info.getBankName();
		if(toBankName.contains("广发银行")){
			singleResp =getCgbService().bankInTran(info);
		}else{
			singleResp =getCgbService().bankOutTran(info);
		}	
		singleResp.setTranNo(info.getTranNo());
		return singleResp;
	}
	@Override
	public SingleResp batchCut(BatchTranReq batchReq) throws Exception {
		logger.error("CGBPayCutService: not implement");
		throw new CutException("not implement");
	}
	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		
		return cgbService.querySingleTranStatus(info);
	}
	public CGBService getCgbService() {
		return cgbService;
	}
	public void setCgbService(CGBService cgbService) {
		this.cgbService = cgbService;
	}
	@Override
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void queryTranStatus(DhbBizJournal journal) {
		cgbService.queryTranStatus(journal);
		
	}
	
}
