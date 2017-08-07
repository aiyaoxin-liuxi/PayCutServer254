package com.dhb.entity;
/**
 * <大网关外放的商户表>
 * @author wxw
 *
 */
public class DhbOutMerchant {
	private String merchantId;
	private String merName;
	private Double merFee;
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerName() {
		return merName;
	}
	public void setMerName(String merName) {
		this.merName = merName;
	}
	public Double getMerFee() {
		return merFee;
	}
	public void setMerFee(Double merFee) {
		this.merFee = merFee;
	}
}
