package com.dhb.cgb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.CommonObjectDao;
import com.google.common.base.Strings;

@Service
public class CGBDaoService {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public String getErrorInfo(String errorCode){
		if(Strings.isNullOrEmpty(errorCode)){
			return null;
		}
		String sql ="select ei.message from cgb_error_info ei where ei.code=:code";
		String recordId = commonObjectDao.findSingleVal(sql, new Object[]{errorCode});
		return recordId;
	}
	
	

}
