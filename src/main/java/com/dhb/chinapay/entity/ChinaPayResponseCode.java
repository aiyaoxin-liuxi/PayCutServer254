package com.dhb.chinapay.entity;

public enum ChinaPayResponseCode {
	
/*	a）若responseCode的接收状态为“接收成功”，表示ChinaPay系统已接收商户代付交易请求；进入步骤4
	b）若responseCode的接收状态为“接收失败”，表示ChinaPay系统接收商户代付请求失败；
	c）若responseCode的接收状态为“待查询”，需调用单笔查询接口查询订单状态和处理结果；
	d）其余应答，需调用单笔查询接口查询订单状态和处理结果；
	4、商户判定交易状态stat
		a）若stat为明确的成功/失败结果，商户记录订单结果，并进行后续处理。
		b）stat没有返回明确的成功/失败结果，表示代付处理中，后续需要通过查询订单状态；部分银行无法返回明确的回盘结果，可通过批量退单查询/控台查询等方式获取结果*/

	SUCCESS("0000","接收成功","接收成功"),
	FAIL0("0100","接收失败","商户提交的字段长度、格式错误"),
	FAIL1("0101","接收失败","商户验签错误"),
	FAIL2("0102","接收失败","手续费计算出错"),
	FAIL3("0103","接收失败","商户备付金帐户金额不足"),
	FAIL4("0104","接收失败","操作拒绝"),
	FAIL5("0105","接收失败","重复交易"),
	NONE("9999","未知","未知");
	private String code;
	private String status;
	private String description;
	




	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	private ChinaPayResponseCode(String code, String status, String description) {
		this.code = code;
		this.status = status;
		this.description = description;
	}

	

	public static ChinaPayResponseCode fromCode(String code) {
		for (ChinaPayResponseCode c : ChinaPayResponseCode.values()) {
			if (c.getCode().equals(code)) {
				return c;
			}
		}
		return NONE;
	}

	public static void main(String[] args) {
		
	}
}
