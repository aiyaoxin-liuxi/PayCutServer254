package com.dhb.entity;

public enum DhbTranStatus {

	Handling("0","交易处理中"),
	Succ("1","交易完成"),
	Fail("2","交易失败"),
	Exception("3","交易异常"),
	Unkown("4","交易未知");
private String code;
private String description;
private DhbTranStatus(String code, String description) {
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

public static DhbTranStatus findByCode(String code){
	for(DhbTranStatus type:DhbTranStatus.values()){
		if(type.getCode().equals(code)){
			return type;
		}
	}
	return null;
}


}
