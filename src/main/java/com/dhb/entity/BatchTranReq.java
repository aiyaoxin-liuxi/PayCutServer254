package com.dhb.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class BatchTranReq {

	private String batchId;
	private String merchId;
	private Integer totalNum;
	private String channelId;
	private String bizType;
	private Double totalBalance;
	private String bigChannelId;
	private List<OutRequestInfo> info = Lists.newArrayList();
	
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Double getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(Double totalBalance) {
		this.totalBalance = totalBalance;
	}
	public List<OutRequestInfo> getInfo() {
		return info;
	}
	public void setInfo(List<OutRequestInfo> info) {
		this.info = info;
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
	public String getBigChannelId() {
		return bigChannelId;
	}
	public void setBigChannelId(String bigChannelId) {
		this.bigChannelId = bigChannelId;
	}

}
