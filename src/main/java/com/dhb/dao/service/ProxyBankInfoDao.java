package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.DhbBankInfo;

@Service
public class ProxyBankInfoDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public DhbBankInfo getPayBankInfoByChannleId(String channelId){
		String bankInfoSql = "select * from dhb_proxy_carry_bankacct where channelId=:channelId and bizType=:bizType";
		List<DhbBankInfo> bankInfoList = commonObjectDao.findList(bankInfoSql, DhbBankInfo.class, new Object[]{channelId,"2"});
		if(bankInfoList.size()>0){
			DhbBankInfo bankInfo = bankInfoList.get(0);
			return bankInfo;
		}
		return null;
	}
	public DhbBankInfo getCutBankInfoByChannleId(String channelId){
		String bankInfoSql = "select * from dhb_proxy_carry_bankacct where channelId=:channelId and bizType=:bizType";
		List<DhbBankInfo> bankInfoList = commonObjectDao.findList(bankInfoSql, DhbBankInfo.class, new Object[]{channelId,"1"});
		if(bankInfoList.size()>0){
			DhbBankInfo bankInfo = bankInfoList.get(0);
			return bankInfo;
		}
		return null;
	}
	public DhbBankInfo getBankInfoByChannleIdBizType(String channelId,String bizType){
		String bankInfoSql = "select * from dhb_proxy_carry_bankacct where channelId=:channelId and bizType=:bizType";
		List<DhbBankInfo> bankInfoList = commonObjectDao.findList(bankInfoSql, DhbBankInfo.class, new Object[]{channelId,bizType});
		if(bankInfoList.size()>0){
			DhbBankInfo bankInfo = bankInfoList.get(0);
			return bankInfo;
		}
		return null;
	}
}
