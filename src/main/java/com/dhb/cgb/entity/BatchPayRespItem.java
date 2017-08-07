package com.dhb.cgb.entity;

public class BatchPayRespItem {
	
	private String customerSalarySeq;
	private String retMes;
	private String flag;
	private double salary;
	private String bankstatus;
	public String getCustomerSalarySeq() {
		return customerSalarySeq;
	}
	public void setCustomerSalarySeq(String customerSalarySeq) {
		this.customerSalarySeq = customerSalarySeq;
	}
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public String getBankstatus() {
		return bankstatus;
	}
	public void setBankstatus(String bankstatus) {
		this.bankstatus = bankstatus;
	}
	public String getRetMes() {
		return retMes;
	}
	public void setRetMes(String retMes) {
		this.retMes = retMes;
	}

	

}
