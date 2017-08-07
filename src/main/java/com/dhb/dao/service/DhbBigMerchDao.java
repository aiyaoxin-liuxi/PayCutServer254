package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.BigMerchInfo;

@Service
public class DhbBigMerchDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public BigMerchInfo getBigMerchInfo(String bigChannelId){
		String sql = "select bigMerchId,keySrc,channelId,url from dhb_big_merchId where channelId=:channelId";
		List<BigMerchInfo> list=commonObjectDao.findList(sql, BigMerchInfo.class, new Object[]{bigChannelId});
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
