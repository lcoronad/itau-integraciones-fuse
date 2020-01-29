package com.itau.esb.modcustomer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonRootName("PostAddr")
public class PostAddr {

	@JsonProperty("addr1")
	@JacksonXmlProperty(localName = "sch:addr1")
	private String addr1;

	@JsonProperty("stateProv")
	@JacksonXmlProperty(localName = "sch:stateProv")
	private String stateProv;

	@JsonProperty("city")
	@JacksonXmlProperty(localName = "sch:city")
	private String city;

	@JsonProperty("cityCode")
	@JacksonXmlProperty(localName = "sch:cityCode")
	private String cityCode;

	@JsonProperty("countyDistrict")
	@JacksonXmlProperty(localName = "sch:countyDistrict")
	private String countyDistrict;

	@JsonProperty("countyDistrictCode")
	@JacksonXmlProperty(localName = "sch:countyDistrictCode")
	private String countyDistrictCode;

	@JsonProperty("postalCode")
	@JacksonXmlProperty(localName = "sch:postalCode")
	private String postalCode;

	@JsonProperty("addrDesc")
	@JacksonXmlProperty(localName = "sch:addrDesc")
	private String addrDesc;

	@JsonProperty("Country")
	@JacksonXmlProperty(localName = "sch:Country")
	private Country Country;

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getStateProv() {
		return stateProv;
	}

	public void setStateProv(String stateProv) {
		this.stateProv = stateProv;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCountyDistrict() {
		return countyDistrict;
	}

	public void setCountyDistrict(String countyDistrict) {
		this.countyDistrict = countyDistrict;
	}

	public String getCountyDistrictCode() {
		return countyDistrictCode;
	}

	public void setCountyDistrictCode(String countyDistrictCode) {
		this.countyDistrictCode = countyDistrictCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddrDesc() {
		return addrDesc;
	}

	public void setAddrDesc(String addrDesc) {
		this.addrDesc = addrDesc;
	}

	public Country getCountry() {
		return Country;
	}

	public void setCountry(Country country) {
		Country = country;
	}

}
