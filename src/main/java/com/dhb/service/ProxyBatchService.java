package com.dhb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhb.dao.PageDao;
import com.dhb.entity.page.PageModel;
import com.dhb.entity.test.ProxyAcpParam;
import com.dhb.entity.test.ProxyAcpPay;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Service("proxyBatchService")
public class ProxyBatchService {
	@Autowired
	private PageDao pageDao;
	public void batchPay(ProxyAcpParam form){

		String startDate = form.getStartDate();
		String  endDate= form.getEndDate();
		String acctname = form.getAcctname();
		String acctno = form.getAcctno();
		String handle_status = form.getHandle_status();
		String review_status = form.getReview_status();
		List<Object> list = Lists.newLinkedList();
		StringBuilder sb = new StringBuilder();
		sb.append("select p.*,b.bank_name from posp.proxy_acp_pay p ,posp.bank_swift_code b ")
		  .append("where p.bankno=b.swift_code ");
		if(!Strings.isNullOrEmpty(acctname)){
		
			sb.append("and acctname=:acctname ");
			list.add(acctname);
		}
		if(!Strings.isNullOrEmpty(acctno)){
			
			sb.append("and acctno=:acctno ");
			list.add(acctno);
		}
		if(!Strings.isNullOrEmpty(handle_status)){
			
			sb.append("and handle_status =:handle_status");
			list.add(handle_status);
		}
		if(!Strings.isNullOrEmpty(review_status)){
			
			sb.append("and review_status =:review_status");
			list.add(review_status);
		}
		PageModel<ProxyAcpPay> pager = form.getPager();
		String sql =  sb.toString();
		Object [] param = list.toArray();
		pageDao.getPage(sql, param, pager);
	}
	
}
