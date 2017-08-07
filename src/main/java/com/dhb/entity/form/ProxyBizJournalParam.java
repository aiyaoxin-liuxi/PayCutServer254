package com.dhb.entity.form;

import com.dhb.entity.DhbBizJournal;
import com.dhb.entity.page.PageModel;

public class ProxyBizJournalParam {
	private PageModel<DhbBizJournal> pager = new PageModel<DhbBizJournal>(DhbBizJournal.class);
	private String startDate;
	private String endDate;
	private String bizType;

	private String fromAccNo;
	private String fromAccName;
	private String toAccNo;
	private String toAccName;
	private String merchId;
	private String secretKey;
	
	public String getFromAccNo() {
		return fromAccNo;
	}
	public void setFromAccNo(String fromAccNo) {
		this.fromAccNo = fromAccNo;
	}
	public String getFromAccName() {
		return fromAccName;
	}
	public void setFromAccName(String fromAccName) {
		this.fromAccName = fromAccName;
	}
	public String getToAccNo() {
		return toAccNo;
	}
	public void setToAccNo(String toAccNo) {
		this.toAccNo = toAccNo;
	}
	public String getToAccName() {
		return toAccName;
	}
	public void setToAccName(String toAccName) {
		this.toAccName = toAccName;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public PageModel<DhbBizJournal> getPager() {
		return pager;
	}
	public void setPager(PageModel<DhbBizJournal> pager) {
		this.pager = pager;
	}
}
