package com.itau.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Request DTO Object")

public class Request {
	
	@JsonProperty("AccounRecord")
	public AccounRecord accounRecord = new AccounRecord();
	
	@JacksonXmlRootElement(localName = "sch:AccounRecord")
	public class AccounRecord {
		public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";
		
		@JsonProperty
		@JacksonXmlProperty(localName = "sch:acctType")
		public String acctType = "";
		
		@JacksonXmlProperty(localName = "sch:acctId")
		public String accId = "";

		@JsonProperty(value = "PaidCurAmt")
		@JacksonXmlProperty(localName = "sch:PaidCurAmt")
		public PaidCurAmt publiCurAmt = new PaidCurAmt();

		

	

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:chargeCode")
		public String chargeCode = "";

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:trnCategory")
		public String trnCategory = "";

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:desc")
		public String desc = "";

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:branchId")
		public String branchId = "";

		public class PaidCurAmt {

			@JsonProperty
			@JacksonXmlProperty(localName = "sch1:amt")
			public BigDecimal amt;
			@JsonProperty
			@JacksonXmlProperty(localName = "sch1:curCode")
			public String curCode = "";

		}
	}
}
