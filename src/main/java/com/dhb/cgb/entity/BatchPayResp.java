package com.dhb.cgb.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class BatchPayResp {
	private String entSeqNo;
	private String retCode;
	private String customerBatchNo;
	private String accountNo;
	private int allCount;
	private double allSalary;
	private int allErrCount;
	private List<BatchPayRespItem> items = Lists.newArrayList();
	public String getEntSeqNo() {
		return entSeqNo;
	}
	public void setEntSeqNo(String entSeqNo) {
		this.entSeqNo = entSeqNo;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getCustomerBatchNo() {
		return customerBatchNo;
	}
	public void setCustomerBatchNo(String customerBatchNo) {
		this.customerBatchNo = customerBatchNo;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public int getAllCount() {
		return allCount;
	}
	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}
	public double getAllSalary() {
		return allSalary;
	}
	public void setAllSalary(double allSalary) {
		this.allSalary = allSalary;
	}
	public int getAllErrCount() {
		return allErrCount;
	}
	public void setAllErrCount(int allErrCount) {
		this.allErrCount = allErrCount;
	}
	public List<BatchPayRespItem> getItems() {
		return items;
	}
	public void setItems(List<BatchPayRespItem> items) {
		this.items = items;
	}
	
	public void addItems(BatchPayRespItem item) {
		this.getItems().add(item);
	}

}
