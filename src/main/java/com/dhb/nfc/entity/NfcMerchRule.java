package com.dhb.nfc.entity;

import java.util.Comparator;
import java.util.Date;

public class NfcMerchRule{
	private String id;
	private String merchNo;
	private String nfcMerch;
	private String merchChannel;
	private Double merchRate;
	private String rulePrecedence;
	private Double minimumAmount;
	private Double maximumAmount;
	private Date minimumDate;
	private Date maximumDate;
	private String status;
	private String remark;
	private Date createdTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMerchNo() {
		return merchNo;
	}
	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}
	public String getNfcMerch() {
		return nfcMerch;
	}
	public void setNfcMerch(String nfcMerch) {
		this.nfcMerch = nfcMerch;
	}
	public String getMerchChannel() {
		return merchChannel;
	}
	public void setMerchChannel(String merchChannel) {
		this.merchChannel = merchChannel;
	}
	public Double getMerchRate() {
		return merchRate;
	}
	public void setMerchRate(Double merchRate) {
		this.merchRate = merchRate;
	}
	public String getRulePrecedence() {
		return rulePrecedence;
	}
	public void setRulePrecedence(String rulePrecedence) {
		this.rulePrecedence = rulePrecedence;
	}
	public Double getMinimumAmount() {
		return minimumAmount;
	}
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}
	public Double getMaximumAmount() {
		return maximumAmount;
	}
	public void setMaximumAmount(Double maximumAmount) {
		this.maximumAmount = maximumAmount;
	}
	public Date getMinimumDate() {
		return minimumDate;
	}
	public void setMinimumDate(Date minimumDate) {
		this.minimumDate = minimumDate;
	}
	public Date getMaximumDate() {
		return maximumDate;
	}
	public void setMaximumDate(Date maximumDate) {
		this.maximumDate = maximumDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
}
