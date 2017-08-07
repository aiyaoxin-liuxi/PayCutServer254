package com.dhb.chinapay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.chinapay.entity.CpBatchFile;
import com.dhb.dao.CommonObjectDao;
import com.google.common.base.Strings;

@Service
public class ChinaPayDaoService {
	@Autowired
	private CommonObjectDao commonObjectDao;
	
	public String findOpenBankIdByBankName(String bankName){
		if(Strings.isNullOrEmpty(bankName)){
			return null;
		}
		String sql= "select bankcode from dhb_bankname_list where bankName=:bankName";
		String openBankId = commonObjectDao.findSingleVal(sql, new Object[]{bankName});
		return openBankId;
	}
	public void insertFile(CpBatchFile batchFile){
		String sql= "insert into cp_batch_file(batchId,fileName) values(:batchId,:fileName)";
		commonObjectDao.saveOrUpdate(sql, batchFile);
	}
	
	public String getFileNameByBatchId(String batchId){
		String sql= "select fileName from cp_batch_file where batchId=:batchId";
		return  commonObjectDao.findSingleVal(sql, new Object[]{batchId});
	}
}
