package com.dhb.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dhb.dao.CommonObjectDao;
import com.dhb.dao.PageDao;
import com.dhb.dao.SequenceDao;
import com.dhb.dao.service.DhbOutMerchantDao;
import com.dhb.dao.service.ProxyMerchAmtDetailDao;
import com.dhb.entity.BizType;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.OutRequestInfo;
import com.dhb.entity.ProxyBizJournal;
import com.dhb.entity.ProxyCarryBankacct;
import com.dhb.entity.ProxyMerchAmt;
import com.dhb.entity.ProxyMerchAmtDetail;
import com.dhb.entity.SingleResp;
import com.dhb.entity.form.ProxyBizJournalParam;
import com.dhb.util.ArithUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
@Service
public class ProxyBizJournalService {
	private static final Log logger = LogFactory.getLog(ProxySendSalaryService.class);
	@Autowired
	private PageDao pageDao;
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private DhbOutMerchantDao dhbOutMerchantDao;
	@Autowired
	private SequenceDao sequenceDao;
	@Autowired
	private ProxyMerchAmtDetailDao proxyMerchAmtDetailDao;
	
	public void queryByForm(ProxyBizJournalParam proxyBizJournalParam) throws ParseException{
		
		List<Object> list = Lists.newLinkedList();
		StringBuilder sb = new StringBuilder();
		String startTime = proxyBizJournalParam.getStartDate();
		String endTime = proxyBizJournalParam.getEndDate();
		String bizType = proxyBizJournalParam.getBizType();
		String fromAccNo  =  proxyBizJournalParam.getFromAccNo();
		String fromAccName = proxyBizJournalParam.getFromAccName();
		String toAccNo 	   = proxyBizJournalParam.getToAccNo();
		String toAccName   = proxyBizJournalParam.getToAccName();
		String merchId = proxyBizJournalParam.getMerchId();
		sb.append("select * from dhb_biz_journal ")
		  .append("where 1=1 ");
		
		if(!Strings.isNullOrEmpty(merchId)){
			sb.append("and merchId=:merchId ");
			list.add(merchId);
		}
		if(!Strings.isNullOrEmpty(startTime)){
			sb.append("and createTime>=to_date(:createTime,'yyyyMMdd') ");
			list.add(startTime);
		}
		if(!Strings.isNullOrEmpty(endTime)){
			sb.append("and createTime<=to_date(:createTime,'yyyyMMdd') ");
			list.add(endTime);
		}
		if(!Strings.isNullOrEmpty(bizType)){
			sb.append("and bizType=:bizType ");
			list.add(bizType);
		}
	/*	private String fromBankCode;
		private String fromBankName;
		private String fromBankCardNo;
		private String fromUserName;
		private String fromIdentityNo;
		private String toBankCode;
		private String toBankName;
		private String toBankCardNo;
		private String toUserName;
		private String toIdentityNo;*/
		if(!Strings.isNullOrEmpty(fromAccNo)){
			sb.append("and fromBankCardNo=:fromBankCardNo ");
			list.add(bizType);
		}
		if(!Strings.isNullOrEmpty(fromAccName)){
			sb.append("and instr(fromUserName,:fromUserName)>0 ");
			list.add(fromAccNo);
		}
		if(!Strings.isNullOrEmpty(toAccNo)){
			sb.append("and toBankCardNo=:toBankCardNo ");
			list.add(toAccNo);
		}
		if(!Strings.isNullOrEmpty(toAccName)){
			sb.append("and instr(toUserName,:toUserName)>0 ");
			list.add(toAccName);
		}
		sb.append("order by createTime desc ");
		String sql = sb.toString();
		logger.debug("sql:("+sql+")");
		pageDao.getPage(sql, list.toArray(), proxyBizJournalParam.getPager());
	}
	
	/**
	 * 代付审核时  若通道返回成功或处理中扣款
	 * 之前是通道返回成功才会操作账户余额
	 * @param outid
	 * @param status
	 * @param remark
	 */
	@Transactional
	public void updateStatusForPay(OutRequestInfo reqInfo,String bizType,DhbOutMerchant merchant,SingleResp singleResp){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		ProxyBizJournal journal = new ProxyBizJournal();
		ProxyCarryBankacct proxyCarryBankacct = dhbOutMerchantDao.selectBankByType(bizType);
		journal.setAmount(reqInfo.getBanlance());
		if(BizType.Pay.getCode().equals(bizType)){//代付交易，收款人是客户，付款方是平台账户
			journal.setAcpacctname(reqInfo.getAccName());
			journal.setAcpacctno(reqInfo.getAccNo());
			journal.setPayacctname(proxyCarryBankacct.getAcctName());
			journal.setPayacctno(proxyCarryBankacct.getAcctNo());
		}
		
		journal.setBiz_type(bizType);
		journal.setMemo(reqInfo.getProxyBizJournalId());
		journal.setMerch_id(reqInfo.getMerchId());
		journal.setOutid(reqInfo.getTranNo());
		journal.setChannelId(reqInfo.getChannelId());
		journal.setPhone(null);
		journal.setRecord_id(reqInfo.getRecordId());
		journal.setTrans_date(df.format(now));
		journal.setTrans_time(tf.format(now));
		journal.setResp_date(df.format(now));
		journal.setResp_time(tf.format(now));
		journal.setStatus(singleResp.getCode());
		journal.setRemark(singleResp.getMessage());
		journal.setChargemode("0");
		journal.setFee(merchant.getMerFee());
		dhbOutMerchantDao.saveJounal(journal);
		
		logger.info("[大网关外放]修改商户账户");
		ProxyMerchAmt proxyMerchAmt = dhbOutMerchantDao.selectAmtByMerId(reqInfo.getMerchId());
		//取上次余额
		Double last_amount = proxyMerchAmt.getBalance();
		//取上次可用余额
		Double last_valid_amount= proxyMerchAmt.getValidBalance();
		
		if("1".equals(bizType) || "5".equals(bizType)){

		}else if("2".equals(bizType) || "6".equals(bizType)){
			//1.插入一条入账台账流水
			ProxyMerchAmtDetail detail = new ProxyMerchAmtDetail();
			detail.setBalance(last_amount);
			detail.setOut_amount(journal.getAmount());
			detail.setMerch_id(reqInfo.getMerchId());
			detail.setMerch_name(reqInfo.getMerchId());
			detail.setRef_outid(journal.getOutid());
			detail.setRef_batchid(journal.getBatchId());
			detail.setTrace_no(df.format(now) + 
					tf.format(now) + 
					StringUtils.leftPad(sequenceDao.getNextVal("proxt_amt_detail_seq")+"", 6));
			//台账流水中定义的代收交易码
			detail.setTrans_code("030200");
			detail.setTrans_date(journal.getTrans_date());
			detail.setTrans_time(journal.getTrans_time());
			detail.setValid_balance(last_valid_amount);
			proxyMerchAmtDetailDao.save(detail);
			//2015/09/14 李真河，根据客户手续费月结需求，此处发生交易时不再减手续费，此处也不再记流水
			//2017/02/21王小威  根据小黑需求 代付交易时扣除手续费
			//2.插入一条扣除手续费的台账流水
			ProxyMerchAmtDetail fee_detail = new ProxyMerchAmtDetail();
			fee_detail.setBalance(last_amount-journal.getAmount());
			fee_detail.setOut_amount(journal.getFee());
			fee_detail.setMerch_id(reqInfo.getMerchId());
			fee_detail.setMerch_name(reqInfo.getMerchId());
			fee_detail.setRef_outid(journal.getOutid());
			fee_detail.setRef_batchid(journal.getBatchId());
			fee_detail.setTrace_no(df.format(now) + 
					tf.format(now) + 
					StringUtils.leftPad(sequenceDao.getNextVal("proxt_amt_detail_seq")+"", 6));
			//台账流水中定义的代收手续费交易码
			fee_detail.setTrans_code("030201");
			fee_detail.setTrans_date(journal.getTrans_date());
			fee_detail.setTrans_time(journal.getTrans_time());
			fee_detail.setValid_balance(last_valid_amount);
			proxyMerchAmtDetailDao.save(fee_detail);
			
			//账户余额变动
			Double currentBalance = ArithUtil.sub(last_amount, journal.getAmount());
			//可用余额变动
			Double currentValidBalance = ArithUtil.sub(last_valid_amount, journal.getAmount());
			//减手续费
			currentBalance = ArithUtil.sub(currentBalance, journal.getFee());
			currentValidBalance = ArithUtil.sub(currentValidBalance, journal.getFee());
			//3.1变更一下商户虚拟账户余额
			proxyMerchAmt.setBalance(currentBalance);
			//3.2变更可用余额
			proxyMerchAmt.setValidBalance(currentValidBalance);
			dhbOutMerchantDao.updateMerAmt(proxyMerchAmt);
		}
	}
}
