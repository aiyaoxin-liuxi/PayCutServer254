package com.dhb.entity;

import java.util.Date;



/* 业务类型：1.代收；2.代付 */

public class ProxyAcpPay {
	private static final long serialVersionUID = -2325009218833091560L;

	private String id;
	private String merchId;
	private String bizType;
    private String channelId;
	private String contractNo;
	private String fromBankCode;
	private String fromBankName;
	private String fromBankCardNo;
	private String fromUserName;
	private String fromIdentityNo;
	private String toBankCode;
	private String toBankName;
	private String toBankCardNo;
	private String toUserName;
	private String toIdentityNo;
	private Double money;
	private String chargemode;
	private Double fee_rate;
	private Double fee; 
	private String currency;
	private String memo;
	private Date   createTime;
	private String creatorId;
	private Date   reviewTime;
	private String reviewerId;
	private String reviewStatus;
	private String reviewComments;
	private String handleStatus;
	private String handleRemark;
	private String batchId;
	
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getFromBankCode() {
		return fromBankCode;
	}
	public void setFromBankCode(String fromBankCode) {
		this.fromBankCode = fromBankCode;
	}
	public String getFromBankName() {
		return fromBankName;
	}
	public void setFromBankName(String fromBankName) {
		this.fromBankName = fromBankName;
	}
	public String getFromBankCardNo() {
		return fromBankCardNo;
	}
	public void setFromBankCardNo(String fromBankCardNo) {
		this.fromBankCardNo = fromBankCardNo;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getFromIdentityNo() {
		return fromIdentityNo;
	}
	public void setFromIdentityNo(String fromIdentityNo) {
		this.fromIdentityNo = fromIdentityNo;
	}
	public String getToBankCode() {
		return toBankCode;
	}
	public void setToBankCode(String toBankCode) {
		this.toBankCode = toBankCode;
	}
	public String getToBankName() {
		return toBankName;
	}
	public void setToBankName(String toBankName) {
		this.toBankName = toBankName;
	}
	public String getToBankCardNo() {
		return toBankCardNo;
	}
	public void setToBankCardNo(String toBankCardNo) {
		this.toBankCardNo = toBankCardNo;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getToIdentityNo() {
		return toIdentityNo;
	}
	public void setToIdentityNo(String toIdentityNo) {
		this.toIdentityNo = toIdentityNo;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getChargemode() {
		return chargemode;
	}
	public void setChargemode(String chargemode) {
		this.chargemode = chargemode;
	}
	public Double getFee_rate() {
		return fee_rate;
	}
	public void setFee_rate(Double fee_rate) {
		this.fee_rate = fee_rate;
	}
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public Date getReviewTime() {
		return reviewTime;
	}
	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}
	public String getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	public String getReviewComments() {
		return reviewComments;
	}
	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
	public String getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
	public String getHandleRemark() {
		return handleRemark;
	}
	public void setHandleRemark(String handleRemark) {
		this.handleRemark = handleRemark;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
}
