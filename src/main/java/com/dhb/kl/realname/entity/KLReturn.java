package com.dhb.kl.realname.entity;

public class KLReturn {

	private String retCode ;//错误代码
	private String retMsg; /// 错误信息
	private String retData; // 对应的业务数据
	private String sign;
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
	public String getRetData() {
		return retData;
	}
	public void setRetData(String retData) {
		this.retData = retData;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
