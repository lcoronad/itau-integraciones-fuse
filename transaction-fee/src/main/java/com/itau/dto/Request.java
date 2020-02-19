package com.itau.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Request DTO Object")
public class Request {
	
	@JsonProperty("ownerInd")
	@JacksonXmlProperty(localName = "sch:acctType")
	public Boolean ownerInd;

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:trnCategory")
	public String trnCategory = "";

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:trnCode")
	public String trnCode = "";

	@JsonProperty("ReferenceInfo")
	@JacksonXmlProperty(localName = "sch:ReferenceInfo")
	public ReferenceInfo referenceInfo = new ReferenceInfo();

	@JsonProperty("FromAcct")
	@JacksonXmlProperty(localName = "sch:FromAcct")
	public Acct fromAcct = new Acct();

	@JsonProperty("ToAcct")
	@JacksonXmlProperty(localName = "sch:ToAcct")
	public Acct toAcct = new Acct();

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:bankId")
	public String bankId;

	@JsonProperty("CurAmt")
	@JacksonXmlProperty(localName = "sch:CurAmt")
	public CurAmt curAmt = new CurAmt();

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:effDt")
	public String effDt;

	@JsonProperty
	@JacksonXmlProperty(localName = "sch:terminalType")
	public String terminalType;
	
	public class ReferenceInfo {
		public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

		@JsonProperty(value = "referenceID")
		@JacksonXmlProperty(localName = "sch:referenceID")
		public String referenceID;

		@JsonProperty(value = "reference")
		@JacksonXmlProperty(localName = "sch:reference")
		public String reference;
	}

	public class Acct{
		public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

		@JsonProperty("AcctKeys")
		@JacksonXmlProperty(localName = "sch:AcctKeys")
		public AcctKeys accKeys = new AcctKeys();

		@JsonProperty("CustID")
		@JacksonXmlProperty(localName = "sch:CustID")
		public CustID custID = new CustID();

		@JsonProperty("addInfo")
		@JacksonXmlProperty(localName = "sch:addInfo")
		public String addInfo = "";

		public class AcctKeys {
			public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

			@JsonProperty("acctId")
			@JacksonXmlProperty(localName = "sch:acctId")
			public String acctId  = "";

			@JsonProperty
			@JacksonXmlProperty(localName = "sch:acctType")
			public String acctType = "";

			@JsonProperty
			@JacksonXmlProperty(localName = "sch:acctSubType")
			public String acctSubType = "";
		}

		public class CustID {
			public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

			@JsonProperty
			@JacksonXmlProperty(localName = "sch:custPermId")
			public String custPermId;

			@JsonProperty
			@JacksonXmlProperty(localName = "sch:custType")
			public String custType;

		}

	}

	public class CurAmt {
		public static final String DEF_NMS = "http://itau.com.co/commoncannonical/v2/schemas";

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:amt")
		public Long amt;

		@JsonProperty
		@JacksonXmlProperty(localName = "sch:curCode")
		public String curCode = "";
	}


}
