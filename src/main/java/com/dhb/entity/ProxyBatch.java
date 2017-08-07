 package com.dhb.entity;

import java.util.Date;

//Dhb_Proxy_Batch_Record
public class ProxyBatch implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6427780522841786546L;

	private String batchId;
	private String outBatchId;
	private String bizType;//1.批量代收,2批量代付,3.
	private String merchId;
	private String filename;
	private String channelId;
	private Integer totalNum;
	private Double totalMoney;
	private Integer totalSuccNum;
	private Double totalSuccMoney;
	private Date createTime;
	private Date reviewTime;
	private String reviewStatus;
	private String reviewComments;
	private String status;
	private String remark;
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}
	public Integer getTotalSuccNum() {
		return totalSuccNum;
	}
	public void setTotalSuccNum(Integer totalSuccNum) {
		this.totalSuccNum = totalSuccNum;
	}
	public Double getTotalSuccMoney() {
		return totalSuccMoney;
	}
	public void setTotalSuccMoney(Double totalSuccMoney) {
		this.totalSuccMoney = totalSuccMoney;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getReviewTime() {
		return reviewTime;
	}
	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
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
	public String getReviewComments() {
		return reviewComments;
	}
	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public String getOutBatchId() {
		return outBatchId;
	}
	public void setOutBatchId(String outBatchId) {
		this.outBatchId = outBatchId;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	
}
