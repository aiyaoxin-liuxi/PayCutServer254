package com.dhb.entity;

import java.util.Date;


public class CGBTranRecord {
	private String recordId;//batch是为子id
	private String id;
	private String merchId;
	private String batchId;
	private String journalId;//batch 为流水id
	private String tranCode;
	private String outAccName;
	private String outAcc;
	private String outAccBank;
	private String inAccName;
	private String inAcc;
	private String inAccBank;
	private String inBankCode;
	private double amount;
	private String remark;
	private Date startTime;
	private Date endTime;
	private String respStatus;
	private String respComment;
	private double frBalance;
	private double toBalance;
	private double handleFee;
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	public String getOutAccName() {
		return outAccName;
	}
	public void setOutAccName(String outAccName) {
		this.outAccName = outAccName;
	}
	public String getOutAcc() {
		return outAcc;
	}
	public void setOutAcc(String outAcc) {
		this.outAcc = outAcc;
	}
	public String getOutAccBank() {
		return outAccBank;
	}
	public void setOutAccBank(String outAccBank) {
		this.outAccBank = outAccBank;
	}
	public String getInAccName() {
		return inAccName;
	}
	public void setInAccName(String inAccName) {
		this.inAccName = inAccName;
	}
	public String getInAcc() {
		return inAcc;
	}
	public void setInAcc(String inAcc) {
		this.inAcc = inAcc;
	}
	public String getInAccBank() {
		return inAccBank;
	}
	public void setInAccBank(String inAccBank) {
		this.inAccBank = inAccBank;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public double getFrBalance() {
		return frBalance;
	}
	public void setFrBalance(double frBalance) {
		this.frBalance = frBalance;
	}
	public double getToBalance() {
		return toBalance;
	}
	public void setToBalance(double toBalance) {
		this.toBalance = toBalance;
	}
	public double getHandleFee() {
		return handleFee;
	}
	public void setHandleFee(double handleFee) {
		this.handleFee = handleFee;
	}

	public String getRespStatus() {
		return respStatus;
	}
	public void setRespStatus(String respStatus) {
		this.respStatus = respStatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getRespComment() {
		return respComment;
	}
	public void setRespComment(String respComment) {
		this.respComment = respComment;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getInBankCode() {
		return inBankCode;
	}
	public void setInBankCode(String inBankCode) {
		this.inBankCode = inBankCode;
	}
	public String getJournalId() {
		return journalId;
	}
	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

}
