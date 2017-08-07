package com.dhb.entity;
//商户账户表
public class ProxyMerchAmt {
	private String merchId;
	private Double balance;
	private Double validBalance;
	public String getMerchId() {
		return merchId;
	}
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getValidBalance() {
		return validBalance;
	}
	public void setValidBalance(Double validBalance) {
		this.validBalance = validBalance;
	}
}
