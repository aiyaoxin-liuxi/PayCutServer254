package com.dhb.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class BatchResp {

	private String batchId;
	private int errorCount=0;
	private String errorCode;
	private String errorMessage;
	private List<SingleResp> record = Lists.newArrayList();

	
	@Override
	public String toString() {
		return "BatchResp [batchId=" + batchId + ", errorCount=" + errorCount
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ ", record=" + record + "]";
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public List<SingleResp> getRecord() {
		return record;
	}

	public void setRecord(List<SingleResp> record) {
		this.record = record;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
