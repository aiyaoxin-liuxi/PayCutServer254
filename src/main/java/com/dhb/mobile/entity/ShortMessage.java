package com.dhb.mobile.entity;

import java.util.Date;

public class ShortMessage {
	private String tranNo;
	private String mobiles;
	private String randnum;
	private Date createdTime;
	public String getTranNo() {
		return tranNo;
	}
	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}
	public String getMobiles() {
		return mobiles;
	}
	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	public String getRandnum() {
		return randnum;
	}
	public void setRandnum(String randnum) {
		this.randnum = randnum;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	

}
