package com.dhb.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.ProxyMerchAmtDetail;
@Service
public class ProxyMerchAmtDetailDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public ProxyMerchAmtDetail save(ProxyMerchAmtDetail proxyMerchAmtDetail){	
		String sql = "insert into PROXY_MERCH_AMT_DETAIL(trans_code,merch_id,merch_name,trans_date,trans_time,"
			      + "in_amount,out_amount,balance,valid_balance,trace_no,"
			      + "ref_outid,ref_batchid)"
			      + " values(:trans_code,:merch_id,:merch_name,:trans_date,:trans_time,"
			      + ":in_amount,:out_amount,:balance,:valid_balance,:trace_no,"
			      + ":ref_outid,:ref_batchid)";
		commonObjectDao.saveOrUpdate(sql, proxyMerchAmtDetail);
		return proxyMerchAmtDetail;
	}
}
