package com.itau.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;

@JacksonXmlRootElement
@ApiModel(description = "Request DTO Object")
public class Request {

	@JsonProperty("PhoneNum")
	@JacksonXmlProperty(localName = "sch:PhoneNum")
	public PhoneNum phoneNum = new PhoneNum();

	@JsonProperty("trnSubType")
	@JacksonXmlProperty(localName = "sch:trnSubType")
	public Boolean trnSubType;

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:maxRec")
	public String maxRec = "";

	public class PhoneNum {
		public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

		@JsonProperty(value = "phoneType")
		@JacksonXmlProperty(localName = "sch1:phoneType")
		public String phoneType;

		@JsonProperty(value = "phone")
		@JacksonXmlProperty(localName = "sch1:phone")
		public String phone;
	}

}
