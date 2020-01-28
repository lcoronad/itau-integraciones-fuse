package com.itau.esb.itausoap2json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonRootName("PhoneNum")
public class PhoneNum {

	@JsonProperty("phone")
	@JacksonXmlProperty(localName = "sch:phone")
	private String phone;

	@JsonProperty("phoneType")
	@JacksonXmlProperty(localName = "sch:phoneType")
	private String phoneType;

	@JsonProperty("phoneDesc")
	@JacksonXmlProperty(localName = "sch:phoneDesc")
	private String phoneDesc;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getPhoneDesc() {
		return phoneDesc;
	}

	public void setPhoneDesc(String phoneDesc) {
		this.phoneDesc = phoneDesc;
	}

}
