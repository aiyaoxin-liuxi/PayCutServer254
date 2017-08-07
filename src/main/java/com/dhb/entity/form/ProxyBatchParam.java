package com.dhb.entity.form;

import com.dhb.entity.ProxyBatch;
import com.dhb.entity.page.PageModel;

public class ProxyBatchParam {
	private PageModel<ProxyBatch> pager = new PageModel<ProxyBatch>(ProxyBatch.class);
	private String startDate;
	private String endDate;
	private String review_status;
	private String merchId;
	private String secretKey;
	
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public PageModel<ProxyBatch> getPager() {
		return pager;
	}
	public void setPager(PageModel<ProxyBatch> pager) {
		this.pager = pager;
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
	public String getReview_status() {
		return review_status;
	}
	public void setReview_status(String review_status) {
		this.review_status = review_status;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
