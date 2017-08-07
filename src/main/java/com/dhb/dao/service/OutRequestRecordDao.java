package com.dhb.dao.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.SequenceDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.DhbPayCut;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBatch;
import com.dhb.nfc.entity.NfcOrder;
import com.dhb.util.DateUtil;
import com.dhb.util.Tools;
@Service
public class OutRequestRecordDao {

	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private SequenceDao sequenceDao;
	private final static DateFormat df = new SimpleDateFormat("yyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
	@Transactional
	public void saveBatchReq(BatchTranReq tranReq){
		ProxyBatch proxyPayBatch = new ProxyBatch();
		Date now = new Date();	
		String batch_id = "batch"
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("Dhb_Proxy_Batch_Record_seq")+"",3, '0');
		proxyPayBatch.setBatchId(batch_id);
		proxyPayBatch.setMerchId(tranReq.getMerchId());
		proxyPayBatch.setBizType(tranReq.getBizType());
		proxyPayBatch.setChannelId(tranReq.getChannelId());
		proxyPayBatch.setTotalNum(tranReq.getTotalNum());
		proxyPayBatch.setTotalMoney(tranReq.getTotalBalance());
		//创建上传日期
		proxyPayBatch.setCreateTime(new Date());
		proxyPayBatch.setOutBatchId(tranReq.getBatchId());
		//已经提交
		String sql = "insert into Dhb_Proxy_Batch_Record(batchId,outBatchId,bizType,merchId,filename,"
				      + "channelId,totalNum,totalMoney,totalSuccNum,totalSuccMoney,"
				      + "createTime,reviewTime,reviewStatus,"
				      + "reviewComments,remark)"
				      + " values(:batchId,:outBatchId,:bizType,:merchId,:filename,"
				      + ":channelId,:totalNum,:totalMoney,:totalSuccNum,:totalSuccMoney,"
				      + ":createTime,:reviewTime,:reviewStatus,"
				      + ":reviewComments,:remark)";
		commonObjectDao.saveOrUpdate(sql,proxyPayBatch);
		tranReq.setBatchId(batch_id);
		for(OutRequestInfo info:tranReq.getInfo()){
			DhbPayCut payCut = new DhbPayCut();
			payCut.setAccName(info.getAccName());
			payCut.setAccNo(info.getAccNo());
			payCut.setBankcode(info.getBankCode());
			payCut.setBankName(info.getBankName());
			payCut.setBatchId(batch_id);
			payCut.setBizType(tranReq.getBizType());
			payCut.setChannelId(info.getChannelId());
			payCut.setCreateTime(now);
			payCut.setCurrency("CNY");
			payCut.setMemo(info.getComments());
			payCut.setMoney(Double.valueOf(info.getBanlance()));
			payCut.setMerchId(info.getMerchId());
			String seq = Tools.getUUID();
			payCut.setRecordId(seq);
			info.setRecordId(seq);
			info.setBatchId(batch_id);
			info.setBizType(tranReq.getBizType());
			payCut.setOutId(info.getTranNo());
			payCut.setOutBatchId(info.getBatchId());
			//payCut.setReviewStatus(ReviewType.Commit.getCode());
			String paySql = "insert into dhb_pay_cut(recordId,outId,merchId,bizType,channelId,"
					+ "bankcode,bankName,accNo,accName,identityNo,currency,money,memo,"
					+ "reviewStatus,createTime,batchId,outBatchId) "
					+ "values (:recordId,:outId,:merchId,:bizType,:channelId,"
					+ ":bankcode,:bankName,:accNo,:accName,:identityNo,:currency,:money,:memo,"
					+ ":reviewStatus,:createTime,:batchId,:outBatchId)";
			commonObjectDao.saveOrUpdate(paySql, payCut);
		}	
	}
	@Transactional
	public DhbPayCut saveSingleReq(OutRequestInfo info){	
			DhbPayCut payCut = new DhbPayCut();
			payCut.setAccName(info.getAccName());
			payCut.setIdentityNo(info.getCertNo());
			payCut.setAccNo(info.getAccNo());
			payCut.setBankcode(info.getBankCode());
			payCut.setBankName(info.getBankName());
			
			payCut.setBizType(info.getBizType());
			payCut.setChannelId(info.getChannelId());
			payCut.setCreateTime(new Date());
			payCut.setCurrency("CNY");
			payCut.setMemo(info.getComments());
			payCut.setMoney(Double.valueOf(info.getBanlance()));
			payCut.setMerchId(info.getMerchId());
			String seq = Tools.getUUID();
			payCut.setRecordId(seq);
			info.setRecordId(seq);
			payCut.setOutId(info.getTranNo());
			//payCut.setReviewStatus(ReviewType.Commit.getCode());
			String paySql = "insert into dhb_pay_cut(recordId,outId,merchId,bizType,channelId,"
					+ "bankcode,bankName,accNo,accName,identityNo,currency,money,memo,"
					+ "reviewStatus,createTime,batchId) "
					+ "values (:recordId,:outId,:merchId,:bizType,:channelId,"
					+ ":bankcode,:bankName,:accNo,:accName,:identityNo,:currency,:money,:memo,"
					+ ":reviewStatus,:createTime,:batchId)";
			commonObjectDao.saveOrUpdate(paySql, payCut);
			return payCut;
	}
	/**
	 * NFC
	 * 保存订单日志表信息
	 * @author pie
	 */
	@Transactional
	public void getSaveNFCorder(Map<String,Object> map){
		NfcOrder nfcOrder = new NfcOrder();
		if(map.get("order_no") != null){
			nfcOrder.setOrderNo(map.get("order_no").toString());
		}
		if(map.get("refund_order_no") != null){
			nfcOrder.setRefundOrderNo(map.get("refund_order_no").toString());
		}
		if(map.get("reversal_order_no") != null){
			nfcOrder.setRefundOrderNo(map.get("reversal_order_no").toString());
		}
		if(map.get("sub_merch_no") != null){
			nfcOrder.setSubMerchNo(map.get("sub_merch_no").toString());
		}
		if(map.get("merch_no") != null){
			nfcOrder.setMerchNo(map.get("merch_no").toString());
		}
		if(map.get("merch_no") != null){
			nfcOrder.setMerchNo(map.get("merch_no").toString());
		}
		if(map.get("merch_channel") != null){
			nfcOrder.setMerchChannel(map.get("merch_channel").toString());
		}
		if(map.get("nfc_type") != null){
			nfcOrder.setNfcType(map.get("nfc_type").toString());
		}
		if(map.get("nfc_merch") != null){
			nfcOrder.setNfcMerch(map.get("nfc_merch").toString());
		}
		if(map.get("total_fee") != null){
			nfcOrder.setTotalFee(map.get("total_fee").toString());
		}
		if(map.get("refund_fee") != null){
			nfcOrder.setRefundFee(map.get("refund_fee").toString());
		}
		if(map.get("refund_channe") != null){
			nfcOrder.setRefundChanne(map.get("refund_channe").toString());
		}
		if(map.get("currency") != null){
			nfcOrder.setCurrency(map.get("currency").toString());
		}
		if(map.get("notify_url") != null){
			nfcOrder.setNotifyUrl(map.get("notify_url").toString());
		}
		if(map.get("remark") != null){
			nfcOrder.setRemark(map.get("remark").toString());
		}
		String strDate = DateUtil.format(new Date());
		try {
			nfcOrder.setCreatedTime(DateUtil.strToDate(strDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sql = "insert into dhb_nfc_order(order_no,refund_order_no,sub_merch_no,"
				+ "merch_no,merch_channel,nfc_type,nfc_merch,total_fee,refund_fee,refund_channe,currency,notify_url,remark,created_time) "
				+ "values(:orderNo,:refundOrderNo,:subMerchNo,:merchNo,:merchChannel,:nfcType,:nfcMerch,:totalFee,:refundFee,:refundChanne,"
				+ ":currency,:notifyUrl,:remark,:createdTime)";
		commonObjectDao.saveOrUpdate(sql, nfcOrder);
	}
	
	@Transactional
	public void saveBatchReq(BatchTranReq tranReq,Map<String,String> map){
		ProxyBatch proxyPayBatch = new ProxyBatch();
		Date now = new Date();	
		String batch_id = "batch"
				+ df.format(now)
				+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("Dhb_Proxy_Batch_Record_seq")+"",3, '0');
		proxyPayBatch.setBatchId(batch_id);
		proxyPayBatch.setMerchId(tranReq.getMerchId());
		proxyPayBatch.setBizType(tranReq.getBizType());
		proxyPayBatch.setChannelId(tranReq.getChannelId());
		proxyPayBatch.setTotalNum(tranReq.getTotalNum());
		proxyPayBatch.setTotalMoney(tranReq.getTotalBalance());
		//创建上传日期
		proxyPayBatch.setCreateTime(new Date());
		proxyPayBatch.setOutBatchId(tranReq.getBatchId());
		//已经提交
		String sql = "insert into Dhb_Proxy_Batch_Record(batchId,outBatchId,bizType,merchId,filename,"
				      + "channelId,totalNum,totalMoney,totalSuccNum,totalSuccMoney,"
				      + "createTime,reviewTime,reviewStatus,"
				      + "reviewComments,remark)"
				      + " values(:batchId,:outBatchId,:bizType,:merchId,:filename,"
				      + ":channelId,:totalNum,:totalMoney,:totalSuccNum,:totalSuccMoney,"
				      + ":createTime,:reviewTime,:reviewStatus,"
				      + ":reviewComments,:remark)";
		commonObjectDao.saveOrUpdate(sql,proxyPayBatch);
		tranReq.setBatchId(batch_id);
		for(OutRequestInfo info:tranReq.getInfo()){
			DhbPayCut payCut = new DhbPayCut();
			payCut.setAccName(info.getAccName());
			payCut.setAccNo(info.getAccNo());
			payCut.setBankcode(info.getBankCode());
			payCut.setBankName(info.getBankName());
			payCut.setBatchId(batch_id);
			payCut.setBizType(tranReq.getBizType());
			payCut.setChannelId(tranReq.getChannelId());
			payCut.setCreateTime(now);
			payCut.setCurrency("CNY");
			payCut.setMemo(info.getRemark());
			payCut.setMoney(Double.valueOf(info.getBanlance()));
			payCut.setMerchId(tranReq.getMerchId());
			String seq = Tools.getUUID();
			payCut.setRecordId(seq);
//			info.setRecordId(seq);
			info.setChannelId(tranReq.getChannelId());
			map.put(info.getTranNo(), seq);
			info.setBatchId(batch_id);
			info.setBizType(tranReq.getBizType());
			payCut.setOutId(info.getTranNo());
			payCut.setOutBatchId(proxyPayBatch.getOutBatchId());
			//payCut.setReviewStatus(ReviewType.Commit.getCode());
			String paySql = "insert into dhb_pay_cut(recordId,outId,merchId,bizType,channelId,"
					+ "bankcode,bankName,accNo,accName,identityNo,currency,money,memo,"
					+ "reviewStatus,createTime,batchId,outBatchId) "
					+ "values (:recordId,:outId,:merchId,:bizType,:channelId,"
					+ ":bankcode,:bankName,:accNo,:accName,:identityNo,:currency,:money,:memo,"
					+ ":reviewStatus,:createTime,:batchId,:outBatchId)";
			commonObjectDao.saveOrUpdate(paySql, payCut);
		}	
	}
}
