package com.dhb.ysb.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dhb.ysb.entity.Constants;
import com.dhb.anyz.service.ANYZUtil;
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
import com.dhb.service.PayCutInterface;
import com.dhb.util.JsonUtil;
import com.google.common.base.Strings;
import com.jnewsdk.util.StringUtils;

public class YSBPayCutService implements PayCutInterface{
	public static Logger logger = Logger.getLogger(YSBPayCutService.class);
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat tf = new SimpleDateFormat("HHmmss");
	@Override
	public SingleResp singleCut(OutRequestInfo reqInfo) throws Exception {
		// TODO Auto-generated method stub
		SingleResp singleResp = new SingleResp();
		Map<String,Object> ysb_map_ret = null;
		Date now = new Date();
		String id = YSBUtil.getReadProperties("ysb", "accountId")
				+ df.format(now)
				//+ tf.format(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("hyt_seq")+"",5, '0');
		//调用通道前先保存流水表信息
		//注：代扣 frombankcode  自己的账户
		DhbBizJournal journal = new DhbBizJournal();
		String channelId = reqInfo.getChannelId();
		journal.setId(id);
		journal.setMerchId(reqInfo.getMerchId());
		journal.setBizType(BizType.Cut.getCode());
		journal.setChannelId(reqInfo.getChannelId());
		DhbBankInfo bankInfo =proxyBankInfoDao.getPayBankInfoByChannleId(channelId);
		journal.setFromBankCardNo(reqInfo.getAccNo());
		journal.setFromIdentityNo(reqInfo.getCertNo());
		journal.setFromBankName(reqInfo.getBankName());
		journal.setFromUserName(reqInfo.getAccName());
		journal.setFromBankCode(reqInfo.getBankCode());
		journal.setToBankCardNo(bankInfo.getAcctNo());
		journal.setToBankCode(bankInfo.getBankCode());
		journal.setToBankName(bankInfo.getBankName());
		journal.setToUserName(bankInfo.getAcctName());
		journal.setMoney(reqInfo.getBanlance());
		journal.setBatchId(reqInfo.getBatchId());
		journal.setCurrency("CNY");
		journal.setMemo(reqInfo.getComments());
		journal.setCreateTime(now);
		journal.setRecordId(reqInfo.getRecordId());
		dhbBizJournalDao.insertJournal(journal);
		//组装传递给通道方的报文
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
		map.put("subContractId",reqInfo.getSubContractId());
		map.put("orderId",journal.getId());
		map.put("purpose",reqInfo.getRemark());
		map.put("amount",String.valueOf(reqInfo.getBanlance()));
		map.put("phoneNo",reqInfo.getMobile());
		map.put("responseUrl",Constants.ysb_notifyUrl);
		String sign = YSBUtil.getAssembleSign(map);
		logger.info("(YSB单笔代扣接口：)订单号："+reqInfo.getTranNo()+",待签名数据："+sign);
		String mac = YSBUtil.GetMD5Code(sign);
		map.put("mac",mac);
		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut, map);
		if (StringUtils.isEmpty(msg)) {
			logger.info("(YSB单笔代付接口：)订单号："+reqInfo.getTranNo()+",报文发送失败或应答消息为空");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}else{
			logger.info("(YSB单笔代扣接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果："+msg);
			ysb_map_ret = JsonUtil.getJsonToMap(msg);
			if(ysb_map_ret.get("result_code").equals("0000")){//受理成功 但不代表交易成功
				singleResp.setCode(DhbTranStatus.Handling.getCode());
         		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
         		singleResp.setMessage(ysb_map_ret.get("result_msg").toString());
			}
		}
		//接收通道返回后，更新流水表的数据
		DhbBizJournal updatejournal = new DhbBizJournal();
		updatejournal.setId(journal.getId());
        updatejournal.setEndTime(new Date());
        updatejournal.setHandleRemark(singleResp.getMessage());
        updatejournal.setHandleStatus(singleResp.getCode());
		dhbBizJournalDao.updateStatusById(updatejournal);
		singleResp.setTranNo(reqInfo.getTranNo());
		return singleResp;
	}

	@Override
	public SingleResp singlePay(OutRequestInfo reqInfo) throws Exception {
		// TODO Auto-generated method stub
		SingleResp singleResp = new SingleResp();
		Map<String,Object> ysb_map_ret = null;
		Date now = new Date();
		String id = YSBUtil.getReadProperties("ysb", "accountId")
				+ df.format(now)
//				+ tf.format(now)
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
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));//商户编号
		map.put("name",reqInfo.getAccName()); //用户姓名
		map.put("cardNo",reqInfo.getAccNo()); //银行卡号
		map.put("orderId",journal.getId());//订单号
		map.put("purpose",reqInfo.getRemark());//付款目的
		map.put("amount",String.valueOf(reqInfo.getBanlance()));//金额
		map.put("responseUrl",Constants.ysb_notifyUrl);//响应地址,目前写死
		String sign = YSBUtil.getAssembleSign(map);
		logger.info("(YSB单笔代付接口：)订单号："+reqInfo.getTranNo()+",待签名数据："+sign);
		String mac = YSBUtil.GetMD5Code(sign);
		map.put("mac",mac);//数字签名
		//调用通道
		String url = YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singlePay;
		String msg = ANYZUtil.sendMsg(url, map);
		if (StringUtils.isEmpty(msg)) {
			logger.info("(YSB单笔代付接口：)订单号："+reqInfo.getTranNo()+",报文发送失败或应答消息为空");
			singleResp.setCode(DhbTranStatus.Fail.getCode());
     		singleResp.setMessage(DhbTranStatus.Fail.getDescription());
		}else{
			logger.info("(YSB单笔代付接口：)订单号："+reqInfo.getTranNo()+",通道方返回结果："+msg);
			ysb_map_ret = JsonUtil.getJsonToMap(msg);
			if(ysb_map_ret.get("result_code").equals("0000")){//受理成功 但不代表交易成功
				singleResp.setCode(DhbTranStatus.Handling.getCode());
         		singleResp.setMessage(DhbTranStatus.Handling.getDescription());
			}else{
				singleResp.setCode(DhbTranStatus.Fail.getCode());
         		singleResp.setMessage(ysb_map_ret.get("result_msg").toString());
			}
		}
		//接收通道返回后，更新流水表的数据
		DhbBizJournal updatejournal = new DhbBizJournal();
		updatejournal.setId(journal.getId());
        updatejournal.setEndTime(new Date());
        updatejournal.setHandleRemark(singleResp.getMessage());
        updatejournal.setHandleStatus(singleResp.getCode());
		dhbBizJournalDao.updateStatusById(updatejournal);
		singleResp.setTranNo(reqInfo.getTranNo());
		return singleResp;
	}

	@Override
	public SingleResp querySingleTranStatus(OutRequestInfo info)
			throws Exception {
		// TODO Auto-generated method stub
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
		Map<String,Object> ysb_map_ret = null;
		SingleResp singleResp = new SingleResp(); 
		String tranNo = journal.getId();
		//调用通道
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
		map.put("orderId",tranNo);
		String sign = YSBUtil.getAssembleSign(map);
		logger.info("(YSB单笔代收付查询接口：)订单号："+tranNo+",组装通道方报文（明文）："+sign);
		String mac = YSBUtil.GetMD5Code(sign);
		map.put("mac",mac);
		//根据业务业务类型不同，调用不同的地址
		String msg = "";
		if(journal.getBizType().equals(BizType.Cut.getCode())){//代扣
			msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut_select, map);
		}
		if(journal.getBizType().equals(BizType.Pay.getCode())){//代付
			msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singlePay_select, map);
		}
		logger.info("(YSB单笔代收付查询接口：)订单号："+tranNo+",通道方返回结果："+msg);
		ysb_map_ret = JsonUtil.getJsonToMap(msg);
		if(ysb_map_ret.get("result_code").equals("0000")){//表示查询操作成功
			//交易成功
			if(ysb_map_ret.get("status").equals("00")){//成功
				singleResp.setCode(DhbTranStatus.Succ.getCode());
				singleResp.setMessage(DhbTranStatus.Succ.getDescription());
			}else if(ysb_map_ret.get("status").equals("10")){//处理中
					singleResp.setCode(DhbTranStatus.Handling.getCode());
					singleResp.setMessage(DhbTranStatus.Handling.getDescription());
			}else if(ysb_map_ret.get("status").equals("20")){//失败
				singleResp.setCode(DhbTranStatus.Fail.getCode());
				singleResp.setMessage(ysb_map_ret.get("desc").toString());
			}
			journal.setEndTime(new Date());
        	journal.setHandleRemark(singleResp.getMessage());
        	journal.setHandleStatus(singleResp.getCode());
        	dhbBizJournalDao.updateStatusById(journal);
		}
		
	}
	public Map<String,Object> singleCutContract(Map<String,String> map){
		Map<String,String>  ysb_map_ret = null;
		Map<String,Object> map_ret = null;
		Map<String,String> rq_map = new LinkedHashMap<String,String>();
		rq_map.put("accountId",YSBUtil.getReadProperties("ysb", "accountId"));
		rq_map.put("contractId",YSBUtil.getReadProperties("ysb", "contractId"));
		rq_map.put("name",map.get("accName"));
		rq_map.put("phoneNo",map.get("mobile"));
		rq_map.put("cardNo",map.get("accNo"));
		rq_map.put("idCardNo",map.get("certNo"));
		rq_map.put("startDate",map.get("startDate"));//yyyyMMdd
		rq_map.put("endDate",map.get("endDate"));//yyyyMMdd
		rq_map.put("cycle",map.get("cycle"));
		rq_map.put("triesLimit",map.get("triesLimit"));
		String sign = YSBUtil.getAssembleSign(rq_map);
		logger.info("(YSB单笔代扣子协议录入接口：)订单号："+map.get("tranNo")+",组装通道方报文（明文）："+sign);
		String mac = YSBUtil.GetMD5Code(sign);
		rq_map.put("mac",mac);
		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_singleCut_contract, rq_map);
		logger.info("(YSB单笔代扣子协议录入接口：)订单号："+map.get("tranNo")+",通道方返回结果："+msg);
		ysb_map_ret = JsonUtil.getJsonToMapStr(msg);
		if(ysb_map_ret.get("result_code").equals("0000")){//表示成功
			map_ret = JsonUtil.getReturnMessageHead(map.get("tranNo"), DhbTranStatus.Succ.getCode(), DhbTranStatus.Succ.getDescription());
			map_ret.put("subContractId", ysb_map_ret.get("subContractId"));
		}else{
			map_ret = JsonUtil.getReturnMessageHead(map.get("tranNo"), DhbTranStatus.Fail.getCode(), ysb_map_ret.get("result_msg"));
		}
		return map_ret;
	}
	public Map<String,Object> subConstractExtension(Map<String,String> map){
		Map<String,String>  ysb_map_ret = null;
		Map<String,Object> map_ret = null;
		Map<String,String> rq_map = new LinkedHashMap<String,String>();
		rq_map.put("accountId", YSBUtil.getReadProperties("ysb", "accountId"));
		rq_map.put("contractId", YSBUtil.getReadProperties("ysb", "contractId"));
		rq_map.put("subContractId", map.get("subContractId"));
		rq_map.put("startDate", map.get("startDate"));
		rq_map.put("endDate", map.get("endDate"));
		String sign = YSBUtil.getAssembleSign(rq_map);
		logger.info("(YSB子协议延期接口：)订单号："+map.get("tranNo")+",组装通道方报文（明文）："+sign);
		String mac = YSBUtil.GetMD5Code(sign);
		rq_map.put("mac",mac);
		String msg = ANYZUtil.sendMsg(YSBUtil.getReadProperties("ysb", "url")+Constants.ysb_contract_extension, rq_map);
		logger.info("(YSB子协议延期接口：)订单号："+map.get("tranNo")+",通道方返回结果："+msg);
		ysb_map_ret = JsonUtil.getJsonToMapStr(msg);
		if(ysb_map_ret.get("result_code").equals("0000")){//表示成功
			map_ret = JsonUtil.getReturnMessageHead(map.get("tranNo"), DhbTranStatus.Succ.getCode(), DhbTranStatus.Succ.getDescription());
			map_ret.put("subContractId", map.get("subContractId"));
		}else{
			map_ret = JsonUtil.getReturnMessageHead(map.get("tranNo"), DhbTranStatus.Fail.getCode(), ysb_map_ret.get("result_msg"));
		}
		return map_ret;
	}
}
