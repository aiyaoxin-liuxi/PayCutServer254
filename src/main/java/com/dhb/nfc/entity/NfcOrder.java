package com.dhb.nfc.entity;

import java.util.Date;

public class NfcOrder {
	private String orderNo;
	private String refundOrderNo;
	private String subMerchNo;
	private String merchNo;
	private String merchChannel;
	private String nfcType;
	private String nfcMerch;
	private String totalFee;
	private String refundFee;
	private String refundChanne;
	private String currency;
	private String notifyUrl;
	private String remark;
	private Date createdTime;
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRefundOrderNo() {
		return refundOrderNo;
	}
	public void setRefundOrderNo(String refundOrderNo) {
		this.refundOrderNo = refundOrderNo;
	}
	public String getSubMerchNo() {
		return subMerchNo;
	}
	public void setSubMerchNo(String subMerchNo) {
		this.subMerchNo = subMerchNo;
	}
	public String getMerchNo() {
		return merchNo;
	}
	public void setMerchNo(String merchNo) {
		this.merchNo = merchNo;
	}
	public String getMerchChannel() {
		return merchChannel;
	}
	public void setMerchChannel(String merchChannel) {
		this.merchChannel = merchChannel;
	}
	public String getNfcType() {
		return nfcType;
	}
	public void setNfcType(String nfcType) {
		this.nfcType = nfcType;
	}
	public String getNfcMerch() {
		return nfcMerch;
	}
	public void setNfcMerch(String nfcMerch) {
		this.nfcMerch = nfcMerch;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public String getRefundFee() {
		return refundFee;
	}
	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}
	public String getRefundChanne() {
		return refundChanne;
	}
	public void setRefundChanne(String refundChanne) {
		this.refundChanne = refundChanne;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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
