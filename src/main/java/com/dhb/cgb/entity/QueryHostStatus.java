package com.dhb.cgb.entity;

import com.dhb.entity.DhbTranStatus;
import com.google.common.base.Strings;

public enum QueryHostStatus {
	Status2("2","待授权"),
	Status3("3","部分授权(一录两审时预审通过状态)"),
	Status4("4","授权通过(一录一审或一录再审最终通过的状态)"),
	Status5("5","授权通过"),
	Succ("6","主机交易成功"),
	Fail("7","主机交易失败 "),
	UnKown("8","状态未知，没有收到后台系统返回的应答"),
	StatusE("E","大额查证");
	private String code;
	private String description;
	private QueryHostStatus(String code, String description) {
	this.code = code;
	this.description = description;
}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public static DhbTranStatus getTranStatusByCode(String code){
		if(Strings.isNullOrEmpty(code)){
			return DhbTranStatus.Unkown;
		}
		for(QueryHostStatus status:QueryHostStatus.values()){
			if(status.getCode().equals(code)){
				if(status.equals(QueryHostStatus.Succ)){
					return DhbTranStatus.Succ;
				}
				else if(status.equals(QueryHostStatus.Fail)){
					return DhbTranStatus.Fail;
				}else{
					return DhbTranStatus.Handling;
				}
			}
		}
		return DhbTranStatus.Unkown;
	}
}
