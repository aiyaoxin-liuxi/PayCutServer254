package com.dhb.entity;

public class SingleResp {

	private String tranNo;
	private String code;
	private String message;
	
	private String balance;//余额 余额	balance	N12	M	精确到分 杉德（4.4　商户余额查询）
	private String creditAmt;//可用额度	creditAmt	N12	C	请求到分杉德（4.4　商户余额查询）
	
	private String tranFee;//4.5　代付手续费查询
	
	private String content;//杉德的 4.8　对账单申请 文件下载链接
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTranFee() {
		return tranFee;
	}
	public void setTranFee(String tranFee) {
		this.tranFee = tranFee;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getCreditAmt() {
		return creditAmt;
	}
	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}
	public String getTranNo() {
		return tranNo;
	}
	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "SingleResp [tranNo=" + tranNo + ", code=" + code + ", message="
				+ message + "]";
	}
	
}
