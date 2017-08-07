package com.dhb.nfc.entity;

import java.util.Date;

public class NfcMerchToPayMerch {
	private String id;
	private String merchChannel;
	private String merchNo;
	private String payMerchNo;
	private String payMerchUnno;
	private String payMerchKey;
	private String status;
	private Date createdTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMerchChannel() {
		return merchChannel;
	}
	public void setMerchChannel(String merchChannel) {
		this.merchChannel = merchChannel;
	}
	public String getMerchNo() {
		return merchNo;
	}
	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}
	public String getPayMerchNo() {
		return payMerchNo;
	}
	public void setPayMerchNo(String payMerchNo) {
		this.payMerchNo = payMerchNo;
	}
	public String getPayMerchUnno() {
		return payMerchUnno;
	}
	public void setPayMerchUnno(String payMerchUnno) {
		this.payMerchUnno = payMerchUnno;
	}
	public String getPayMerchKey() {
		return payMerchKey;
	}
	public void setPayMerchKey(String payMerchKey) {
		this.payMerchKey = payMerchKey;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
}
