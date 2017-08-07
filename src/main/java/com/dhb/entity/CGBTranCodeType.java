package com.dhb.entity;

public enum CGBTranCodeType {
	BankInTran("0011","行内付款交易"),
	SendSalaryTran("0019","代发工资"),
	BankOutTran("0021","行外付款不落地交易"),
	QueryStatus("0004","查询交易状态"),
	QueryBalance("0001","账户余额查询"),
	OutBatchPay("0033","批量付款不落地处理交易"),
	SinglePayQuery("0022","查询汇款交易状态"),
	OutBatchQuery("0034","代付交易查询");
private String code;
private String description;
private CGBTranCodeType(String code, String description) {
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
