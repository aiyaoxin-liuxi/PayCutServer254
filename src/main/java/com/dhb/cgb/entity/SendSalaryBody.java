//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.15 at 05:55:24 ���� CST 
//


package com.dhb.cgb.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "customerBatchNo",
    "accountNo",
    "allCount",
    "allSalary",
    "records"
})
@XmlRootElement(name = "Body")
public class SendSalaryBody {

    protected String customerBatchNo;
    protected String accountNo;
    protected int allCount;
    protected Double allSalary;
/*    private List<Record> records;*/
    @XmlElement(required = true)
    protected SendSalaryRecords records;
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
	public Double getAllSalary() {
		return allSalary;
	}
	public void setAllSalary(Double allSalary) {
		this.allSalary = allSalary;
	}
	public SendSalaryRecords getRecords() {
		return records;
	}
	public void setRecords(SendSalaryRecords records) {
		this.records = records;
	}
/*	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}*/

   
}
