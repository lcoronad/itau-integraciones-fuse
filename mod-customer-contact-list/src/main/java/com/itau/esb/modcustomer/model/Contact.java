package com.itau.esb.modcustomer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonRootName("contact")
public class Contact {
	
	private PhoneNum PhoneNum = new PhoneNum();

	private Email Email = new Email();
	
	@JsonProperty("contactType")
	@JacksonXmlProperty(localName="sch:contactType")
	private String contactType;
	
	@JsonProperty("contactIdent")
	@JacksonXmlProperty(localName="sch:contactIdent")
	private String contactIdent;

	@JsonProperty("Email")
	@JacksonXmlProperty(localName="sch:Email")
	public Email getEmail() {
		return Email;
	}

	public void setEmail(Email email) {
		Email = email;
	}

	@JsonProperty("PhoneNum")
	@JacksonXmlProperty(localName="sch:PhoneNum")
	public PhoneNum getPhoneNum() {
		return PhoneNum;
	}

	public void setPhoneNum(PhoneNum PhoneNum) {
		this.PhoneNum = PhoneNum;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getContactIdent() {
		return contactIdent;
	}

	public void setContactIdent(String contactIdent) {
		this.contactIdent = contactIdent;
	}

	@Override
	public String toString() {
		return "ClassPojo [PhoneNum = " + PhoneNum + ", contactType = " + contactType + ", contactIdent = "
				+ contactIdent + "]";
	}
}
