package com.dhb.entity;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Policy {

@Expose
private String number;
@Expose
private String startDate;
@Expose
private String endDate;
@Expose
private String status;

/**
* 
* @return
* The number
*/
public String getNumber() {
return number;
}

/**
* 
* @param number
* The number
*/
public void setNumber(String number) {
this.number = number;
}

/**
* 
* @return
* The startDate
*/
public String getStartDate() {
return startDate;
}

/**
* 
* @param startDate
* The startDate
*/
public void setStartDate(String startDate) {
this.startDate = startDate;
}

/**
* 
* @return
* The endDate
*/
public String getEndDate() {
return endDate;
}

/**
* 
* @param endDate
* The endDate
*/
public void setEndDate(String endDate) {
this.endDate = endDate;
}

/**
* 
* @return
* The status
*/
public String getStatus() {
return status;
}

/**
* 
* @param status
* The status
*/
public void setStatus(String status) {
this.status = status;
}

@Override
public String toString() {
	return "Policy [number=" + number + ", startDate=" + startDate
			+ ", endDate=" + endDate + ", status=" + status + "]";
}

}

