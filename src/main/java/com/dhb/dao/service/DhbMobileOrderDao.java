package com.dhb.dao.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.DhbMobileOrder;
import com.dhb.mobile.entity.InAccount;
import com.dhb.mobile.entity.InPurchase;
import com.dhb.mobile.entity.InQuan;
import com.dhb.mobile.util.Constant;
import com.dhb.util.Tools;
@Service
public class DhbMobileOrderDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public DhbMobileOrder saveMobileOrder(InQuan quan){	
		InAccount account = quan.getQuanbody().getAccount();
		InPurchase purchase = quan.getQuanbody().getPurchase();
		DhbMobileOrder order = new DhbMobileOrder();
		String id = Tools.getUUID();
		order.setId(id);
		order.setMerchid(purchase.getMerId());
		order.setMobile(purchase.getPhnumber());
		order.setAmt(Long.parseLong(purchase.getTransAmt()));
		order.setChannelId("1");
		order.setPlatTime(new Date());
		order.setState(Constant.QB_STATE_NEW);
		order.setAccId(account.getId());
		order.setAccType(account.getType());
		order.setAccAuthMode(account.getAuthMode());
		order.setAcqBin(purchase.getAcqBIN());
		order.setpDate(purchase.getDate());
		order.setpTraceNum(purchase.getTraceNum());
		order.setCurrency("156");
		order.setOrderInfo(purchase.getOrderInfo());
		order.setOrderNum(purchase.getOrderNum());
		order.setExtInfo(quan.getQuanbody().getExtInfo());
		order.setPubkeyIndex(quan.getQuanbody().getPubKeyIndex());
		order.setTicket(quan.getQuanbody().getTicket());
		
		
		String sql = "insert into DHB_MOBILE_ORDER(id,merchid,mobile,amt,channelId,"
			      + "platTime,state,accId,accType,accAuthMode,"
			      + "acqBin,pDate,pTraceNum,"
			      + "currency,orderInfo,orderNum,extInfo,pubkeyIndex,ticket)"
			      + " values(:id,:merchid,:mobile,:amt,:channelId,"
			      + ":platTime,:state,:accId,:accType,:accAuthMode,"
			      + ":acqBin,:pDate,:pTraceNum,"
			      + ":currency,:orderInfo,:orderNum,:extInfo,:pubkeyIndex,:ticket)";
		commonObjectDao.saveOrUpdate(sql, order);
		return order;
	}
	
	public void updateStatusById(DhbMobileOrder order){
		
		String updateSql = "update DHB_MOBILE_ORDER set state=:state,"
				+ "memo=:memo where id=:id";
		commonObjectDao.saveOrUpdate(updateSql, order);
	
	}
	public DhbMobileOrder selectByOrderId(String id){
		String sql = "select * from DHB_MOBILE_ORDER where id=:id";
		List<DhbMobileOrder> list = commonObjectDao.findList(sql, DhbMobileOrder.class, new Object[]{id});
		if(list.size()>0){
			DhbMobileOrder order = list.get(0);
			return order;
		}
		return null;
	}
}
