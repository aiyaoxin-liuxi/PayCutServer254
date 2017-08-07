package com.dhb.entity;

public enum BizType {

	Cut("1","代收"),
	Pay("2","代付"),
	SendSalary("3","代付工资")
	,Cut_batch("5","批量代收")
	,Pay_batch("6","批量代付")
	,Bank_verify("7","认证")
	;
private String code;
private String description;
private BizType(String code, String description) {
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

public static BizType findByCode(String code){
	for(BizType type:BizType.values()){
		if(type.getCode().equals(code)){
			return type;
		}
	}
	return null;
}


}
