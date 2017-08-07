package com.dhb.nfc.entity;

import java.math.BigDecimal;

public class NfcQjWater {
	private String id;
	private String cooperatorId;
	private String mId;
	private String qtMsgId;
	private BigDecimal amount;
	private String settleDate;
	private String respType;
	private BigDecimal shfee;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCooperatorId() {
		return cooperatorId;
	}
	public void setCooperatorId(String cooperatorId) {
		this.cooperatorId = cooperatorId;
	}
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public String getQtMsgId() {
		return qtMsgId;
	}
	public void setQtMsgId(String qtMsgId) {
		this.qtMsgId = qtMsgId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public String getRespType() {
		return respType;
	}
	public void setRespType(String respType) {
		this.respType = respType;
	}
	public BigDecimal getShfee() {
		return shfee;
	}
	public void setShfee(BigDecimal shfee) {
		this.shfee = shfee;
	}
}
