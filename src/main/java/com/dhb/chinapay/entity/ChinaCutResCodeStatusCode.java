package com.dhb.chinapay.entity;

public enum ChinaCutResCodeStatusCode {
    SUCCESS("00","1001","交易成功","处理完成或接收成功","S"),
    Waiting1("45","2000","初始","系统正在对数据处理","U"),
    Waiting2("","2000","初始","系统正在对数据处理","U"),
    Waiting3("46","2045","未知","交易待查询","U"),
    Waiting4("09","2009","超时未知","提交银行处理","U"),
    FAIL1("E2","20E2","交易失败","数字签名或证书错","F"),
    FAIL2("01","2001","账户/卡错误","查开户方原因","F"),
    FAIL3("03","2003","交易失败","无效商户","F"),
    FAIL4("05","2005","交易失败","未开通业务","F"),
    FAIL5("06","2006","系统处理失败","系统处理失败","F"),
    FAIL6("13","2013","交易失败","货币错误","F"),
    FAIL7("14","2014","账户/卡错误","无效卡号","F"),
    FAIL8("22","2022","交易失败","交易失败","F"),
    FAIL9("30","2030","交易失败","报文内容检查错或者处理错","F"),
    FAIL10("31","2031","交易失败","无路由或路由参数有误","F"),
    FAIL11("41","2041","账户/卡错误","已挂失折","F"),
    FAIL12("51","2051","金额错误","余额不足","F"),
    FAIL13("61","2061","金额错误","超出提款限额","F"),
    FAIL14("94","2094","交易失败","重复业务","F"),
    FAIL15("EC","20EC","交易失败","商户状态不合法","F"),
    FAIL16("F3","20F3","交易失败","累计退货金额大于原交易金额","F"),
    FAIL17("FF","20FF","交易失败","非白名单卡号","F"),
    FAIL18("P9","20P9","交易失败","账户已冻结","F"),
    FAIL19("PD","20PD","交易失败","账户未加办代收付标志","F"),
    FAIL20("PS","20PS","交易失败","户名不符","F"),
    FAIL21("PU","20PU","交易失败","订单号错误","F"),
    FAIL22("PZ","20PZ","交易失败","原交易信息不存在","F"),
    FAIL23("Q3","20Q3","交易失败","日期错误","F"),
    FAIL24("QB","20QB","交易失败","商户审核不通过","F"),
    FAIL25("QS","20QS","交易失败","系统忙，请稍后再提交","F"),
    FAIL26("ST","20ST","交易失败","已撤销","F"),
    FAIL27("T4","20T4","交易失败","未签约账户","F"),
    FAIL28("TY","20TY","请求失败","IP不通过","F"),
    FAIL29("EL","20EL","交易失败","交易失败","F"),
    FAIL30("001","0001","查询失败","系统处理失败","F"),
    NONE("9999","未知","未知","未知","F");

	private String code;
	private String statusCode;
	private String handleStatus;
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



	private ChinaCutResCodeStatusCode(String code, String statusCode,String status, String description,String handleStatus) {
		this.code = code;
		this.statusCode = statusCode;
		this.status = status;
		this.description = description;
		this.handleStatus= handleStatus;
	}

	

	public static ChinaCutResCodeStatusCode fromCode(String code) {
		for (ChinaCutResCodeStatusCode c : ChinaCutResCodeStatusCode.values()) {
			if (c.getCode().equals(code)) {
				return c;
			}
		}
		return NONE;
	}
	public static ChinaCutResCodeStatusCode fromStatusCode(String statusCode) {
		for (ChinaCutResCodeStatusCode c : ChinaCutResCodeStatusCode.values()) {
			if (c.getStatusCode().equals(statusCode)) {
				return c;
			}
		}
		return NONE;
	}
	public static void main(String[] args) {
		
	}



	public String getStatusCode() {
		return statusCode;
	}



	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}



	public String getHandleStatus() {
		return handleStatus;
	}



	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
}
