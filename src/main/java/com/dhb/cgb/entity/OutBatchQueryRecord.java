//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.20 at 12:31:40 ���� CST 
//


package com.dhb.cgb.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}customerSalarySeq"/>
 *         &lt;element ref="{}transType"/>
 *         &lt;element ref="{}inaccname"/>
 *         &lt;element ref="{}inacc"/>
 *         &lt;element ref="{}inaccbank"/>
 *         &lt;element ref="{}inaccadd"/>
 *         &lt;element ref="{}bankCode"/>
 *         &lt;element ref="{}salary"/>
 *         &lt;element ref="{}remark"/>
 *         &lt;element ref="{}comment"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "customerSalarySeq",
    "fee",
    "bankstatus",
    "ewpSequence",
    "errorreason"
})
@XmlRootElement(name = "record")
public class OutBatchQueryRecord {

    protected String customerSalarySeq;
    protected double fee;
    protected String bankstatus;
    protected String ewpSequence;
    protected String errorreason;
	public String getCustomerSalarySeq() {
		return customerSalarySeq;
	}
	public void setCustomerSalarySeq(String customerSalarySeq) {
		this.customerSalarySeq = customerSalarySeq;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public String getBankstatus() {
		return bankstatus;
	}
	public void setBankstatus(String bankstatus) {
		this.bankstatus = bankstatus;
	}
	public String getEwpSequence() {
		return ewpSequence;
	}
	public void setEwpSequence(String ewpSequence) {
		this.ewpSequence = ewpSequence;
	}
	public String getErrorreason() {
		return errorreason;
	}
	public void setErrorreason(String errorreason) {
		this.errorreason = errorreason;
	}
}
