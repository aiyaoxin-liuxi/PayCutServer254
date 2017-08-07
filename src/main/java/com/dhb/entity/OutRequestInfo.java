package com.dhb.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class OutRequestInfo {
	private String recordId;
	private String tranNo;
	private String channelId;
	private String bizType;
	private String merchId;
	private Double banlance;
	private String sign;
	private String timestamp;//yyyy-mm-dd hh:mi:ss
	private String accNo;
	private String accName;
	private String certType;
	private String certNo;
	private String bankName;
	private String bankCode;
	private String comments;
	private String accType;//01对私,02对公
	private String bankProvince;
	private String batchId;
	private String bankCity;
	private String mobile;
	private String tel;
	private String bigChannelId;
	private String businessType;//四要素认证交易类型
	private String createdTime;//四要素认证创建时间
	private String remark;//摘要信息
	private String notifyUrl;//通知地址
	private String subContractId;//子协议号
	private String cvn2;//信用卡 
	private String validityTerm;//信用卡有效期
	
	private String cardName;//对公账户名称
	private String cardType;//卡类型 0对私 借记卡  1对私信用卡
	
	private String proxyBizJournalId;//用于代付扣款

	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getBankProvince() {
		return bankProvince;
	}
	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}
	public String getBankCity() {
		return bankCity;
	}
	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}
	public void setBanlance(Double banlance) {
		this.banlance = banlance;
	}

	public String getTranNo() {
		return tranNo;
	}
	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public double getBanlance() {
		return banlance;
	}

	public static void main(String[] args) {
		Gson g = new GsonBuilder().serializeNulls().create();
		OutRequestInfo info = new OutRequestInfo();
		System.out.println(g.toJson(info));
	
	}

	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBigChannelId() {
		return bigChannelId;
	}
	public void setBigChannelId(String bigChannelId) {
		this.bigChannelId = bigChannelId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getSubContractId() {
		return subContractId;
	}
	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}
	public String getCvn2() {
		return cvn2;
	}
	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}
	public String getValidityTerm() {
		return validityTerm;
	}
	public void setValidityTerm(String validityTerm) {
		this.validityTerm = validityTerm;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getProxyBizJournalId() {
		return proxyBizJournalId;
	}
	public void setProxyBizJournalId(String proxyBizJournalId) {
		this.proxyBizJournalId = proxyBizJournalId;
	}
}
