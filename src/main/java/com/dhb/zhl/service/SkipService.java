package com.dhb.zhl.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbBigMerchDao;
import com.dhb.dao.service.DhbBizJournalDao;
import com.dhb.dao.service.KeyInfoDao;
import com.dhb.dao.service.ProxyBankInfoDao;
import com.dhb.entity.BatchTranReq;
import com.dhb.entity.BigMerchInfo;
import com.dhb.entity.DhbBankInfo;
import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.HttpRequestParam;
import com.dhb.entity.HttpResponser;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.SingleResp;
import com.dhb.util.DateUtil;
import com.dhb.util.HttpHelp;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class SkipService {
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private DhbBigMerchDao dhbBigMerchDao;
	@Autowired
	private DhbBizJournalDao dhbBizJournalDao;
	@Autowired
	private ProxyBankInfoDao proxyBankInfoDao;
	@Autowired
	private KeyInfoDao keyService;
	
	private String getSeq(){
		Date now = new Date();
		String uuid = "dhb"
				+ DateUtil.getDayForYYMMDD(now)
				+ DateUtil.getTimeForHHmmss(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("dhb_skip_seq")+"",3, '0');
		return uuid;
	}
	private String getBatchSeq(){
		Date now = new Date();
		String uuid = "dhbbatch"
				+ DateUtil.getDayForYYMMDD(now)
				+ DateUtil.getTimeForHHmmss(now)
				+ StringUtils.leftPad(sequenceDao.getNextVal("dhb_skip_batch_seq")+"",3, '0');
		return uuid;
	}
	public DhbBizJournal saveSingleJournal(OutRequestInfo info){
		Date date = new Date();
		String channelId = info.getChannelId();
		String bizType = info.getBizType();
		DhbBankInfo bankInfo =proxyBankInfoDao.getBankInfoByChannleIdBizType(channelId,bizType);
		DhbBizJournal journal = new DhbBizJournal();
		String uuid = getSeq();
		journal.setId(getSeq());
		journal.setMerchId(info.getMerchId());
		journal.setBizType(info.getBizType());
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
		journal.setBigChannelId(info.getBigChannelId());
		dhbBizJournalDao.insertJournal(journal);
		info.setTranNo(uuid);
		return journal;
	}
	public List<DhbBizJournal> saveBatchJournal(BatchTranReq batchReq){
		Date date = new Date();
		List<DhbBizJournal> list = Lists.newArrayList();
		String bizType = batchReq.getBizType();
		String batchId = getBatchSeq();
		for(OutRequestInfo info :batchReq.getInfo()){
			String channelId = info.getChannelId();
			DhbBankInfo bankInfo =proxyBankInfoDao.getBankInfoByChannleIdBizType(channelId,bizType);
			DhbBizJournal journal = new DhbBizJournal();
			String uuid = getSeq();
			journal.setId(getSeq());
			journal.setBatchId(batchId);
			journal.setMerchId(info.getMerchId());
			journal.setBizType(info.getBizType());
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
			journal.setBigChannelId(info.getBigChannelId());
			dhbBizJournalDao.insertJournal(journal);
			info.setTranNo(uuid);
			list.add(journal);
		}
		
		return list;
	}
	public SingleResp sendHttp(BatchTranReq batchReq,String url){
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(url);
		Map<String,String> headParam =  Maps.newHashMap();
		headParam.put("Content-Type", "application/json;charset=UTF-8");
		Gson g = new Gson();
	    String context = g.toJson(batchReq);
		param.setHeads(headParam);
		param.setContext(context);
		HttpResponser resp =send.postParamByHttpClient(param);
		if(resp!=null){
			if(200==resp.getCode()){
				String respContext = resp.getContent();
				if(!Strings.isNullOrEmpty(respContext)){
					SingleResp singleResp=g.fromJson(respContext,SingleResp.class);
					return singleResp;
				}
			}
		}
		return null;
	}
	public SingleResp sendHttp(OutRequestInfo reqInfo,String url){
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		Gson g = new Gson();
		String context = g.toJson(reqInfo);
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		
		if(resp!=null){
			if(200==resp.getCode()){
				String respContext = resp.getContent();
				if(!Strings.isNullOrEmpty(respContext)){
					SingleResp singleResp=g.fromJson(respContext,SingleResp.class);
					return singleResp;
				}
			}
		}
		return null;
	}
	public List<SingleResp> sendBatchHttp(OutRequestInfo reqInfo,String url){
		HttpHelp send = new HttpHelp();
		HttpRequestParam param = new HttpRequestParam();
		param.setUrl(url);
		Map<String,String> heads = Maps.newHashMap();
		heads.put("Content-Type", "application/json;charset=UTF-8");
		Gson g = new Gson();
		String context = g.toJson(reqInfo);
		param.setContext(context);
		param.setHeads(heads);
		HttpResponser resp=send.postParamByHttpClient(param);
		
		if(resp!=null){
			if(200==resp.getCode()){
				String respContext = resp.getContent();
				if(!Strings.isNullOrEmpty(respContext)){
					List<SingleResp> singleResp=g.fromJson(respContext,new TypeToken<List<SingleResp>>() {
			        }.getType());
					return singleResp;
				}
			}
		}
		return null;
	}
	public BigMerchInfo makeUpReq(BatchTranReq batchinfo,boolean isTran){
		String bigChannel = batchinfo.getBigChannelId();
		if(Strings.isNullOrEmpty(bigChannel)){
			return null;
		}
		BigMerchInfo bigInfo=dhbBigMerchDao.getBigMerchInfo(bigChannel);
		batchinfo.setMerchId(bigInfo.getBigMerchId());
		if(isTran){
			for(OutRequestInfo info:batchinfo.getInfo()){
				String merchId = bigInfo.getBigMerchId();
				Double money = info.getBanlance();
				String accNo = info.getAccNo();
				String tranNo = info.getTranNo();
				String secretKey = bigInfo.getKeySrc();
				String sign=keyService.getTranSign(merchId, money, accNo, tranNo, secretKey);
				info.setSign(sign);
				info.setMerchId(merchId);
			}
		
		}
		return bigInfo;
	}
	public BigMerchInfo makeUpReqInfo(OutRequestInfo info,boolean isTran){
		String bigChannel = info.getBigChannelId();
		String batchId = info.getBatchId();
		if(Strings.isNullOrEmpty(bigChannel)){
			return null;
		}
		BigMerchInfo bigInfo=dhbBigMerchDao.getBigMerchInfo(bigChannel);
		info.setMerchId(bigInfo.getBigMerchId());
		if(isTran){
			String merchId = bigInfo.getBigMerchId();
			Double money = info.getBanlance();
			String accNo = info.getAccNo();
			String tranNo = info.getTranNo();
			String secretKey = bigInfo.getKeySrc();
			String sign=keyService.getTranSign(merchId, money, accNo, tranNo, secretKey);
			info.setSign(sign);
		}else{
			String merchId = bigInfo.getBigMerchId();
			String tranNo = info.getTranNo();
			String secretKey = bigInfo.getKeySrc();
			if(Strings.isNullOrEmpty(batchId)){
				String sign=keyService.getQuerySign(merchId, tranNo,  secretKey);
				info.setSign(sign);
			}else{
				String sign=keyService.getQuerySign(batchId, tranNo,  secretKey);
				info.setSign(sign);
			}
		}
		return bigInfo;
	}
}
