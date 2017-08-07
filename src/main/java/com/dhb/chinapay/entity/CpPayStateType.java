package com.dhb.chinapay.entity;

public enum CpPayStateType {
	Success("s","S","交易成功"),
	Handing2("2","U","交易已接受"),
	Handing3("3","U","财务已确认"),
	Handing4("4","U","交易成功"),
	Handing5("5","U","已发往银行"),
	Handing6("6","F","银行已退单"),
	Handing7("7","U","重汇已提交"),
	Handing8("8","U","重汇已发送");


	private String code;
	private String status;
	private String discription;
	private CpPayStateType(String code, String status, String discription) {
		this.code = code;
		this.status = status;
		this.discription = discription;
	}
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
	public String getDiscription() {
		return discription;
	}
	public void setDiscription(String discription) {
		this.discription = discription;
	}
	
	public static CpPayStateType getStateByCode(String code){
		for(CpPayStateType state:CpPayStateType.values()){
			if(state.code.equals(code)){
				return state;
			}
		}
		return null;
	}

}
