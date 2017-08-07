package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.DhbMobileOrder;
import com.dhb.entity.DhbMobileTransaction;
import com.google.common.base.Strings;
@Service
public class DhbMobileTransactionDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	@Autowired
	private DhbMobileOrderDao dhbMobileOrderDao;
	
	public DhbMobileTransaction saveDhbTransaction(DhbMobileTransaction transaction){	
		
		String sql = "insert into DHB_MOBILE_TRANSACTION(id,moborderId,platTime,amt,state,mobile,ofCardId,ofCardNum,spOrderId,returl)"
			      + " values(:id,:moborderId,:platTime,:amt,:state,:mobile,:ofCardId,:ofCardNum,:spOrderId,:returl)";
		commonObjectDao.saveOrUpdate(sql, transaction);
		return transaction;
	}
	
	public void updateStatusById(DhbMobileTransaction transaction){
		String updateSql = "update DHB_MOBILE_TRANSACTION set retCode=:retCode, retTime=:retTime,state=:state,retMsg=:retMsg"
				+ ",ofTraceNum=:ofTraceNum" +
				",ofOrderCache=:ofOrderCache,ofCardName=:ofCardName"+
				",gameState=:gameState"+
				" where id=:id";
		commonObjectDao.saveOrUpdate(updateSql, transaction);
	
	}
	@Transactional
	public void updateOrderAndTrans(DhbMobileOrder order, DhbMobileTransaction trans){
		dhbMobileOrderDao.updateStatusById(order);
		updateStatusById(trans);
	}
	
	public List<DhbMobileTransaction> selectTransactionBysporderId(String sporderId){
		if(Strings.isNullOrEmpty(sporderId)){
			return null;
		}
		String sql = "select * from DHB_MOBILE_TRANSACTION where spOrderId =:spOrderId";
		return commonObjectDao.findList(sql, DhbMobileTransaction.class, new Object[]{sporderId});
	}
}
