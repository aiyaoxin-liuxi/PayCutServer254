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
 *         &lt;element ref="{}traceNo"/>
 *         &lt;element ref="{}outAccName"/>
 *         &lt;element ref="{}outAcc"/>
 *         &lt;element ref="{}outAccBank"/>
 *         &lt;element ref="{}inAccName"/>
 *         &lt;element ref="{}inAcc"/>
 *         &lt;element ref="{}inAccBank"/>
 *         &lt;element ref="{}inAccAdd"/>
 *         &lt;element ref="{}amount"/>
 *         &lt;element ref="{}remark"/>
 *         &lt;element ref="{}date"/>
 *         &lt;element ref="{}comment"/>
 *         &lt;element ref="{}creNo"/>
 *         &lt;element ref="{}frBalance"/>
 *         &lt;element ref="{}toBalance"/>
 *         &lt;element ref="{}handleFee"/>
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

    "outAccName",
    "outAcc",
    "inAccName",
    "inAcc",
    "inAccBank",
    "inAccAdd",
    "amount",
    "remark",
    "comment",
    "paymentBankid"
})
@XmlRootElement(name = "Body")
public class OutBody {

    protected String outAccName="";
    @XmlElement(required = true)
    protected String outAcc="";
    @XmlElement(required = true)
    protected String inAccName="";
    @XmlElement(required = true)
    protected String inAcc="";
    protected String inAccBank="";
    protected String inAccAdd="";
    @XmlElement(required = true)
    protected double amount;
    protected String remark="";
    protected String comment="";
    private String paymentBankid;

    /**
     * Gets the value of the outAccName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutAccName() {
        return outAccName;
    }

    /**
     * Sets the value of the outAccName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutAccName(String value) {
        this.outAccName = value;
    }


    public String getOutAcc() {
		return outAcc;
	}

	public void setOutAcc(String outAcc) {
		this.outAcc = outAcc;
	}


	/**
     * Gets the value of the inAccName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInAccName() {
        return inAccName;
    }

    /**
     * Sets the value of the inAccName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInAccName(String value) {
        this.inAccName = value;
    }

	public String getInAcc() {
		return inAcc;
	}

	public void setInAcc(String inAcc) {
		this.inAcc = inAcc;
	}

	public String getInAccBank() {
		return inAccBank;
	}

	public void setInAccBank(String inAccBank) {
		this.inAccBank = inAccBank;
	}

	public String getInAccAdd() {
		return inAccAdd;
	}

	public void setInAccAdd(String inAccAdd) {
		this.inAccAdd = inAccAdd;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPaymentBankid() {
		return paymentBankid;
	}

	public void setPaymentBankid(String paymentBankid) {
		this.paymentBankid = paymentBankid;
	}

}