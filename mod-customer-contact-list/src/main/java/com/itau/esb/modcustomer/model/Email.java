package com.itau.esb.modcustomer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonRootName("Email")
public class Email {
	
	@JsonProperty
	@JacksonXmlProperty(localName="sch:emailAddr")
	private String emailAddr;

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	@Override
	public String toString() {
		return "Email [emailAddr=" + emailAddr + "]";
	}

}
