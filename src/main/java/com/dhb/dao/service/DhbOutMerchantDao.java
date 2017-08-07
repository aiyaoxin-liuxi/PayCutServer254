package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.DhbOutMerchant;
import com.dhb.entity.ProxyBizJournal;
import com.dhb.entity.ProxyCarryBankacct;
import com.dhb.entity.ProxyMerchAmt;
@Service
public class DhbOutMerchantDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public DhbOutMerchant selectByMerId(String merchantid){
		String sql = "select * from DHB_OUTMERCHANT where MERCHANTID=:merchantid";
		List<DhbOutMerchant> list = commonObjectDao.findList(sql, DhbOutMerchant.class, new Object[]{merchantid});
		if(list.size()>0){
			DhbOutMerchant order = list.get(0);
			return order;
		}
		return null;
	}
	
	public ProxyMerchAmt selectAmtByMerId(String merchantid){
		String sql = "select * from PROXY_MERCH_AMT where MERCH_ID=:merchantid";
		List<ProxyMerchAmt> list = commonObjectDao.findList(sql, ProxyMerchAmt.class, new Object[]{merchantid});
		if(list.size()>0){
			ProxyMerchAmt order = list.get(0);
			return order;
		}
		return null;
	}
	public ProxyCarryBankacct selectBankByType(String acctType){
		String sql = "select * from PROXY_CARRY_BANKACCT where ACCT_TYPE=:acctType";
		List<ProxyCarryBankacct> list = commonObjectDao.findList(sql, ProxyCarryBankacct.class, new Object[]{acctType});
		if(list.size()>0){
			ProxyCarryBankacct order = list.get(0);
			return order;
		}
		return null;
	}
	public void saveJounal(ProxyBizJournal journal){
		String sql = "insert into PROXY_BIZ_JOURNAL(outid,merch_id,trans_date,trans_time,biz_type,"
			      + "record_id,amount,payacctname,payacctno,acpacctname,"
			      + "acpacctno,memo,resp_date,resp_time,status,remark,channelId,chargemode,fee)"
			      + " values(:outid,:merch_id,:trans_date,:trans_time,:biz_type,"
			      + ":record_id,:amount,:payacctname,:payacctno,:acpacctname,"
			      + ":acpacctno,:memo,:resp_date,:resp_time,:status,:remark,:channelId,:chargemode,:fee)";
		commonObjectDao.saveOrUpdate(sql, journal);
	}
	public void updateMerAmt(ProxyMerchAmt merchAmt){
		String updateSql = "update PROXY_MERCH_AMT set BALANCE=:balance,"
				+ "VALID_BALANCE=:validBalance where MERCH_ID=:merchId";
		commonObjectDao.saveOrUpdate(updateSql, merchAmt);
	}
}
