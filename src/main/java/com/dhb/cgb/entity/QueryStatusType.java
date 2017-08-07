package com.dhb.cgb.entity;

import java.util.Map;

import com.google.common.collect.Maps;

public class QueryStatusType {

	private static Map<String,String> map = Maps.newHashMap();
/*	2：待授权 
	3：部分授权（一录两审时预审通过状态）
	4：授权拒绝
	5：授权通过（一录一审或一录再审最终通过的状态）
	6：主机交易成功 
	7：主机交易失败 
	8：状态未知，没有收到后台系统返回的应答
	E：大额查证*/

	static {
		map.put("2", "待授权 ");
		map.put("3", "部分授权 ");
		map.put("4", "授权拒绝");
		map.put("5", "授权通过");
		map.put("6", "主机交易成功");
		map.put("7", "主机交易失败");
		map.put("8", "状态未知，没有收到后台系统返回的应答");
		map.put("E", "大额查证");
	}
public static String getMessageByCode(String code){
	return map.get(code);
}

}
