package com.dhb.entity;

public enum ReviewType {
	Commit("0","提交"),
	Refuse("2","拒绝"),
	Pass("1","审核通过");
private String code;
private String description;
private ReviewType(String code, String description) {
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


}
