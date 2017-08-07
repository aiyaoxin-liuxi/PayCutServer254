package com.dhb.kl.realname.entity;

import java.util.Map;

import com.google.common.collect.Maps;

public class KLReq {
	private String reqData;
	private Map<String,String> map =Maps.newHashMap();
	private String customerId;
	private String prdGrpId; 
	private String prdId;
	private String sign;


	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPrdGrpId() {
		return prdGrpId;
	}
	public void setPrdGrpId(String prdGrpId) {
		this.prdGrpId = prdGrpId;
	}
	public String getPrdId() {
		return prdId;
	}
	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getReqData() {
		return reqData;
	}
	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	public Map<String,String> getMap() {
		return map;
	}
	public void setMap(Map<String,String> map) {
		this.map = map;
	}
	
	
}
