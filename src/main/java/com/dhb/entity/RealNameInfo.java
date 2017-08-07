package com.dhb.entity;

import java.util.Date;

public class RealNameInfo {
	private String certNo;
	private String accNo;
	private String userName;
	private String tel;
	private String businessType;
	private Date createdTime;
	private String errText;
	private String cvn2;//信用卡
	private String validityTerm;//信用卡有效期
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getErrText() {
		return errText;
	}
	public void setErrText(String errText) {
		this.errText = errText;
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
	
}
