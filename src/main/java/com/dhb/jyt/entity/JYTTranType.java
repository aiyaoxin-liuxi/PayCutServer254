package com.dhb.jyt.entity;

public enum JYTTranType {

	SingleCut("TC1001","单笔代收"),
	QuerySingleCut("TC2001","查询单笔代收"),
	QuerySinglePay("TC2002","查询单笔代收"),
	SinglePay("TC1002","单笔代付"),
	BankCarRealName("TR4001","银行卡实名认证"),
	CerdNoRealName("TR4002","身份证实名认证"),
	FourRealName("TR4003","银行卡四要素");
private String code;
private String description;
private JYTTranType(String code, String description) {
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

public static JYTTranType findByCode(String code){
	for(JYTTranType type:JYTTranType.values()){
		if(type.getCode().equals(code)){
			return type;
		}
	}
	return null;
}


}
