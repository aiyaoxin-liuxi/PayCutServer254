package com.dhb.jyt.entity;

public class JYTResp {

	private String tranCode;
	private String respCode;
	private String respDesc;
	private String tranState;
	private String tranRespCode;
	private String tranRespDesc;
	private String message;
	private String isBcnAndidnConform;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getTranState() {
		return tranState;
	}
	public void setTranState(String tranState) {
		this.tranState = tranState;
	}
	public String getTranRespCode() {
		return tranRespCode;
	}
	public void setTranRespCode(String tranRespCode) {
		this.tranRespCode = tranRespCode;
	}
	public String getTranRespDesc() {
		return tranRespDesc;
	}
	public void setTranRespDesc(String tranRespDesc) {
		this.tranRespDesc = tranRespDesc;
	}
	public String getIsBcnAndidnConform() {
		return isBcnAndidnConform;
	}
	public void setIsBcnAndidnConform(String isBcnAndidnConform) {
		this.isBcnAndidnConform = isBcnAndidnConform;
	}

}
