package com.dhb.entity.test;



import com.dhb.entity.page.PageModel;


public class ProxyAcpParam {
	private PageModel<ProxyAcpPay> pager = new PageModel<ProxyAcpPay>(ProxyAcpPay.class);
	private String startDate;
	private String endDate;
	private String acctname;
	private String acctno;
	private String handle_status;
	private String review_status;
	private String bath_no;
	public PageModel<ProxyAcpPay> getPager() {
		return pager;
	}
	public void setPager(PageModel<ProxyAcpPay> pager) {
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
	public String getAcctname() {
		return acctname;
	}
	public void setAcctname(String acctname) {
		this.acctname = acctname;
	}
	public String getAcctno() {
		return acctno;
	}
	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}
	public String getHandle_status() {
		return handle_status;
	}
	public void setHandle_status(String handle_status) {
		this.handle_status = handle_status;
	}
	public String getReview_status() {
		return review_status;
	}
	public void setReview_status(String review_status) {
		this.review_status = review_status;
	}
	public String getBath_no() {
		return bath_no;
	}
	public void setBath_no(String bath_no) {
		this.bath_no = bath_no;
	}
}
