package com.dhb.entity;

public enum OutBatchQueryType {
	Status("0011","行内付款交易"),
	SendSalaryTran("0019","代发工资"),
	BankOutTran("0021","行外付款不落地交易"),
	QueryStatus("0004","查询交易状态"),
	QueryBalance("0001","账户余额查询"),
	OutBatchPay("0033","批量付款不落地处理交易"),
	OutBatchQuery(",","");
private String code;
private String description;
private OutBatchQueryType(String code, String description) {
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
