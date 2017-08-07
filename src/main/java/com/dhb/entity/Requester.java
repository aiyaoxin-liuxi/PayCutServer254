package com.dhb.entity;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Requester {

@SerializedName("faultTypeCode")
private String FaultTypeCode;
@SerializedName("subFaultTypeCode")
private String SubFaultTypeCode;
@Expose
private String firstName;
@Expose
private String lastName;
@Expose
private String address;
@Expose
private String city;
@Expose
private String zipCode;
@Expose
private String country;
@Expose
private String phoneNumber;
@Expose
private String email;

/**
* 
* @return
* The FaultTypeCode
*/
public String getFaultTypeCode() {
return FaultTypeCode;
}

/**
* 
* @param FaultTypeCode
* The FaultTypeCode
*/
public void setFaultTypeCode(String FaultTypeCode) {
this.FaultTypeCode = FaultTypeCode;
}

/**
* 
* @return
* The SubFaultTypeCode
*/
public String getSubFaultTypeCode() {
return SubFaultTypeCode;
}

/**
* 
* @param SubFaultTypeCode
* The SubFaultTypeCode
*/
public void setSubFaultTypeCode(String SubFaultTypeCode) {
this.SubFaultTypeCode = SubFaultTypeCode;
}

/**
* 
* @return
* The firstName
*/
public String getFirstName() {
return firstName;
}

/**
* 
* @param firstName
* The firstName
*/
public void setFirstName(String firstName) {
this.firstName = firstName;
}

/**
* 
* @return
* The lastName
*/
public String getLastName() {
return lastName;
}

/**
* 
* @param lastName
* The lastName
*/
public void setLastName(String lastName) {
this.lastName = lastName;
}

/**
* 
* @return
* The address
*/
public String getAddress() {
return address;
}

/**
* 
* @param address
* The address
*/
public void setAddress(String address) {
this.address = address;
}

/**
* 
* @return
* The city
*/
public String getCity() {
return city;
}

/**
* 
* @param city
* The city
*/
public void setCity(String city) {
this.city = city;
}

/**
* 
* @return
* The zipCode
*/
public String getZipCode() {
return zipCode;
}

/**
* 
* @param zipCode
* The zipCode
*/
public void setZipCode(String zipCode) {
this.zipCode = zipCode;
}

/**
* 
* @return
* The country
*/
public String getCountry() {
return country;
}

/**
* 
* @param country
* The country
*/
public void setCountry(String country) {
this.country = country;
}

/**
* 
* @return
* The phoneNumber
*/
public String getPhoneNumber() {
return phoneNumber;
}

/**
* 
* @param phoneNumber
* The phoneNumber
*/
public void setPhoneNumber(String phoneNumber) {
this.phoneNumber = phoneNumber;
}

/**
* 
* @return
* The email
*/
public String getEmail() {
return email;
}

/**
* 
* @param email
* The email
*/
public void setEmail(String email) {
this.email = email;
}

@Override
public String toString() {
	return "Requester [FaultTypeCode=" + FaultTypeCode + ", SubFaultTypeCode="
			+ SubFaultTypeCode + ", firstName=" + firstName + ", lastName="
			+ lastName + ", address=" + address + ", city=" + city
			+ ", zipCode=" + zipCode + ", country=" + country
			+ ", phoneNumber=" + phoneNumber + ", email=" + email + "]";
}

}

