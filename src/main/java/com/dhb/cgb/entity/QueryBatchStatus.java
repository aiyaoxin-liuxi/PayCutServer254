package com.dhb.cgb.entity;

import com.dhb.entity.DhbTranStatus;
import com.google.common.base.Strings;

public enum QueryBatchStatus {
	
/*	
 * 行内汇款：
 * 6：主机交易成功 
	7：主机交易失败 
	8：状态未知，没有收到后台系统返回的应答
	跨行汇款：
	A：支付系统正在处理
	B：处理成功
	C：处理失败
	D：状态未知
	E：大额查证
	9：查证取消交易
	b:汇款失败已冲账*/

	Status4("A,E","交易处理中(支付系统正在处理,大额查证)"),
	Succ("6,B","交易成功(主机交易成功,处理成功)"),
	Fail("7,C,9,b","交易失败(主机交易失败,处理失败,查证取消交易,汇款失败已冲账)"),
	UnKown("8,D","状态未知");
	private String code;
	private String description;
	private QueryBatchStatus(String code, String description) {
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
		StringBuilder sb = new StringBuilder();
		sb.append("currentStatus("+code+"),");
		boolean isAssign = true;
		for(QueryBatchStatus status:QueryBatchStatus.values()){
			if(status.getCode().contains(code)){
				if(isAssign){
					sb.append("code:("+status.getCode()+"),content:("+status.getDescription()+")");
					isAssign = false;
				}
				if(status.equals(QueryBatchStatus.Succ)){
					System.out.println(sb.toString());
					return DhbTranStatus.Succ;
				}
				else if(status.equals(QueryBatchStatus.Fail)){
					System.out.println(sb.toString());
					return DhbTranStatus.Fail;
				}else{
					System.out.println(sb.toString());
					return DhbTranStatus.Handling;
				}
			}
		}
		return DhbTranStatus.Unkown;
	}
}
