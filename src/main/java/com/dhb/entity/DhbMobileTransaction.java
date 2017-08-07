package com.dhb.entity;

import java.util.Date;

public class DhbMobileTransaction {
	private String id;
	private String moborderId;
	private Date platTime;
	private Long amt;
	private Integer state;
	private String mobile;
	private String spOrderId;
	private String ofCardId;
	private String ofCardNum;
	private String ofOrderCache;
	private String ofCardName;
	private String ofTraceNum;
	private String retCode;
	private String retMsg;
	private String gameState;
	private String ext1;
	private Date retTime;
	private String returl;
	public String getReturl() {
		return returl;
	}
	public void setReturl(String returl) {
		this.returl = returl;
	}
	public Date getRetTime() {
		return retTime;
	}
	public void setRetTime(Date retTime) {
		this.retTime = retTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMoborderId() {
		return moborderId;
	}
	public void setMoborderId(String moborderId) {
		this.moborderId = moborderId;
	}
	public Date getPlatTime() {
		return platTime;
	}
	public void setPlatTime(Date platTime) {
		this.platTime = platTime;
	}
	public Long getAmt() {
		return amt;
	}
	public void setAmt(Long amt) {
		this.amt = amt;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getSpOrderId() {
		return spOrderId;
	}
	public void setSpOrderId(String spOrderId) {
		this.spOrderId = spOrderId;
	}
	public String getOfCardId() {
		return ofCardId;
	}
	public void setOfCardId(String ofCardId) {
		this.ofCardId = ofCardId;
	}
	public String getOfCardNum() {
		return ofCardNum;
	}
	public void setOfCardNum(String ofCardNum) {
		this.ofCardNum = ofCardNum;
	}
	
	public String getOfOrderCache() {
		return ofOrderCache;
	}
	public void setOfOrderCache(String ofOrderCache) {
		this.ofOrderCache = ofOrderCache;
	}
	public String getOfCardName() {
		return ofCardName;
	}
	public void setOfCardName(String ofCardName) {
		this.ofCardName = ofCardName;
	}
	public String getOfTraceNum() {
		return ofTraceNum;
	}
	public void setOfTraceNum(String ofTraceNum) {
		this.ofTraceNum = ofTraceNum;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public String getGameState() {
		return gameState;
	}
	public void setGameState(String gameState) {
		this.gameState = gameState;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
}
