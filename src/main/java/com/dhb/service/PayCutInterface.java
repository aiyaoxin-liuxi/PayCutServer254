package com.dhb.service;

import java.util.List;

import com.dhb.entity.BatchTranReq;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;

public interface PayCutInterface {

	public SingleResp singleCut(OutRequestInfo info)throws Exception;
	public SingleResp singlePay(OutRequestInfo info)throws Exception;
	public SingleResp querySingleTranStatus(OutRequestInfo info)throws Exception;
	public List<SingleResp> queryBatchTranStatus(OutRequestInfo info)throws Exception;
	public SingleResp  batchPay(BatchTranReq batchReq)throws Exception;
	public SingleResp  batchCut(BatchTranReq batchReq)throws Exception;
	public void queryTranStatus(DhbBizJournal journal);
}
