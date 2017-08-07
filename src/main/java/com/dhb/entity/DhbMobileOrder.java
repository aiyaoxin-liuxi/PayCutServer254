package com.dhb.entity;

import java.util.Date;

public class DhbMobileOrder {
	private String id;
	private String merchid;
	private String mobile;
	private Long amt;
	private String channelId;
	private Date platTime;
	private Integer state;
	private String accId;
	private String accType;
	private String accAuthMode;
	private String acqBin;
	private String pDate;
	private String pTraceNum;
	private String currency;
	private String orderInfo;
	private String orderNum;
	private String extInfo;
	private String pubkeyIndex;
	private String ticket;
	private String ext1;
	private String memo;
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getExtInfo() {
		return extInfo;
	}
	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMerchid() {
		return merchid;
	}
	public void setMerchid(String merchid) {
		this.merchid = merchid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Long getAmt() {
		return amt;
	}
	public void setAmt(Long amt) {
		this.amt = amt;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public Date getPlatTime() {
		return platTime;
	}
	public void setPlatTime(Date platTime) {
		this.platTime = platTime;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getAccId() {
		return accId;
	}
	public void setAccId(String accId) {
		this.accId = accId;
	}
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public String getAccAuthMode() {
		return accAuthMode;
	}
	public void setAccAuthMode(String accAuthMode) {
		this.accAuthMode = accAuthMode;
	}
	public String getAcqBin() {
		return acqBin;
	}
	public void setAcqBin(String acqBin) {
		this.acqBin = acqBin;
	}
	public String getpDate() {
		return pDate;
	}
	public void setpDate(String pDate) {
		this.pDate = pDate;
	}
	public String getpTraceNum() {
		return pTraceNum;
	}
	public void setpTraceNum(String pTraceNum) {
		this.pTraceNum = pTraceNum;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getPubkeyIndex() {
		return pubkeyIndex;
	}
	public void setPubkeyIndex(String pubkeyIndex) {
		this.pubkeyIndex = pubkeyIndex;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
}
