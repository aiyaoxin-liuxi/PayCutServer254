package com.dhb.shande.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 杉德的产品ID
 *
 * <strong>ProductIdSD</strong>. <br> 
 * <strong>Description : TODO...</strong> <br>
 * <strong>Create on : 2017年6月2日 下午2:55:42</strong>. <br>
 * <p>
 * <strong>Copyright (C) zhl Co.,Ltd.</strong> <br>
 * </p>
 * @author zts zhaotisheng@qq.com <br>
 * @version <strong>zhl-0.1.0</strong> <br>
 * <br>
 * <strong>修改历史: .</strong> <br>
 * 修改人 修改日期 修改描述<br>
 * Copyright ©  zhl by zts Inc. All Rights Reserved
 * -------------------------------------------<br>
 * <br>
 * <br>
 */
public enum ProductIdSD {

	CUT2PUBLIC("00000001","代收对公","1","3")
	,CUT2PRIVATE("00000002","代收对私","0","4")
	,PAY2PUBLIC("00000003","代付对公","1","3")
	,PAY2PRIVATE("00000004","代付对私","0","4")
	;
	private String code;
	private String name;
	private String accAttr;
	private String accType;
	//0 si   1 公
	
	
//	public static void main(String[] args) {
//	 System.out.println(ProductIdSD.parseOf("1").getName());	
//	}
	
	public String getCode() {
		return code;
	}
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public String getAccAttr() {
		return accAttr;
	}
	public void setAccAttr(String accAttr) {
		this.accAttr = accAttr;
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
	private ProductIdSD(String code, String name,String accAttr,String accType) {
		this.code = code;
		this.name = name;
		this.accAttr = accAttr;
		this.accType=accType;
	}
	
	private static Map<String, ProductIdSD> valueMap = new HashMap<String, ProductIdSD>();

	static {
		for (ProductIdSD _enum : ProductIdSD.values()) {
			valueMap.put(_enum.code, _enum);
		}
	}
	
	public static ProductIdSD parseOf(String code) {
		for (ProductIdSD item : values())
			if (item.getCode().equals(code))
				return item;

		throw new IllegalArgumentException("ProductIdSD异常错误代码[" + code + "]不匹配!");
	}
}
