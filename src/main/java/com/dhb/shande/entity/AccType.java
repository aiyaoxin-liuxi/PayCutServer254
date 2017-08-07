package com.dhb.shande.entity;

import java.util.HashMap;
import java.util.Map;

public enum AccType {

	
	ACCTYPE_PRIVATE("0","对私"),
	ACCTYPE_PUBLIC("1","对公");
	
	private String code;
	private String name;
	
	private AccType(String code,String name){
		this.code=code;
		this.name=name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private static Map<String, AccType> valueMap = new HashMap<String, AccType>();

	static {
		for (AccType _enum : AccType.values()) {
			valueMap.put(_enum.code, _enum);
		}
	}
	
	public static AccType parseOf(String code) {
		for (AccType item : values())
			if (item.getCode().equals(code))
				return item;

		throw new IllegalArgumentException("AccType异常错误代码[" + code + "]不匹配!");
	}
	
	
}
