//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.15 at 05:55:24 ���� CST 
//


package com.dhb.cgb.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "customerBatchNo",
    "customerSalarySeq"
})
@XmlRootElement(name = "Body")
public class OutBatchQueryBodyResp {

	private String customerBatchNo;
	private String accountNo;
	private int allCount;
	private double allSalary;
	private double allHandlefee;
	private int count;
	private int errCount;
	private int unknowCount;
	private OutBatchQueryRecords record;
	public String getCustomerBatchNo() {
		return customerBatchNo;
	}
	public void setCustomerBatchNo(String customerBatchNo) {
		this.customerBatchNo = customerBatchNo;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public int getAllCount() {
		return allCount;
	}
	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}
	public double getAllSalary() {
		return allSalary;
	}
	public void setAllSalary(double allSalary) {
		this.allSalary = allSalary;
	}
	public double getAllHandlefee() {
		return allHandlefee;
	}
	public void setAllHandlefee(double allHandlefee) {
		this.allHandlefee = allHandlefee;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getErrCount() {
		return errCount;
	}
	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}
	public int getUnknowCount() {
		return unknowCount;
	}
	public void setUnknowCount(int unknowCount) {
		this.unknowCount = unknowCount;
	}
	public OutBatchQueryRecords getRecord() {
		return record;
	}
	public void setRecord(OutBatchQueryRecords record) {
		this.record = record;
	}
	

}
