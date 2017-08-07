package com.dhb.shande.entity;

import java.io.Serializable;

/**
 * 
 *  示例数据
 *  {
	"orderCode": "000000000003",
	"reqReserved": "请求方保留测试abc123_S",
	"respCode": "0000",
	"respDesc": "成功",
	"resultFlag": "0",
	"sandSerial": "2017060269130000000001961604",
	"tranDate": "20170602",
	"tranFee": "150",
	"tranTime": "20170602152341"
}
 *
 * <strong>retDataSD</strong>. <br> 
 * <strong>Description : TODO...</strong> <br>
 * <strong>Create on : 2017年6月2日 下午3:29:00</strong>. <br>
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
public class RetDataSD implements Serializable{
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */
	
	private static final long serialVersionUID = -2519343200623340074L;
	
	//响应码	respCode	N4	M
	private String respCode;//1
	private String respDesc;//2 //响应描述	respDesc	ANS1..50	M
	private String tranTime;//3 // 交易时间	tranTime	N14	R
	private String orderCode;// 4 订单号 R
	private String resultFlag;//0-成功 1-失败 2-处理中(已发往银行)  M 5
	private String sandSerial;//杉德系统流水号  N20 C
	private String tranDate;//C
	private String tranFee;//手续费	tranFee	N12	C	精确到分 8
	private String reqReserved;//1.		请求方保留域	reqReserved	ANS1..256	R  9
	private String extend;//扩展域	extend	ANS1..256	C  10
	
	private String balance;//余额 余额	balance	N12	M	精确到分
	private String creditAmt;//可用额度	creditAmt	N12	C	请求到分
	
	private String content;//文件下载链接 4.8　对账单申请
	private String validateStatus;//0-通过 1-认证失败 
	private String causeCode;//原因码
	private String certPicture;//证件图片
	
	
	public String getCauseCode() {
		return causeCode;
	}
	public void setCauseCode(String causeCode) {
		this.causeCode = causeCode;
	}
	public String getCertPicture() {
		return certPicture;
	}
	public void setCertPicture(String certPicture) {
		this.certPicture = certPicture;
	}
	public String getValidateStatus() {
		return validateStatus;
	}
	public void setValidateStatus(String validateStatus) {
		this.validateStatus = validateStatus;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getTranTime() {
		return tranTime;
	}
	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getResultFlag() {
		return resultFlag;
	}
	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}
	public String getSandSerial() {
		return sandSerial;
	}
	public void setSandSerial(String sandSerial) {
		this.sandSerial = sandSerial;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getTranFee() {
		return tranFee;
	}
	public void setTranFee(String tranFee) {
		this.tranFee = tranFee;
	}
	public String getReqReserved() {
		return reqReserved;
	}
	public void setReqReserved(String reqReserved) {
		this.reqReserved = reqReserved;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getCreditAmt() {
		return creditAmt;
	}
	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}
	
	
}
