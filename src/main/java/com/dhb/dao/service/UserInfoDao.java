package com.dhb.dao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.dhb.entity.UserInfo;
import com.google.common.base.Strings;

@Service
public class UserInfoDao {
	@Autowired
	private CommonObjectDao commonObjectDao;
	public List<UserInfo> getUserInfo(String cardno){
		if(Strings.isNullOrEmpty(cardno)){
			return null;
		}
		String sql = "select * from USER_INFO@link2 where USER_STAT in(2,8,9) and card_no=:cardno";
		return commonObjectDao.findList(sql, UserInfo.class,new Object[]{cardno});
	}
	public List<UserInfo> getUserInfoByaccId(String accid){
		if(Strings.isNullOrEmpty(accid)){
			return null;
		}
		String sql = "select * from USER_INFO@link2 where USER_STAT=2 and ACCT_ID=:accid";
		return commonObjectDao.findList(sql, UserInfo.class,new Object[]{accid});
	}
}
