package com.itau.esb.jpattransferlogs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TrnInfoList {
	private String trnCode;
	private String trnSrc;

	public String getTrnCode() {
		return trnCode;
	}

	public void setTrnCode(String trnCode) {
		this.trnCode = trnCode;
	}

	public String getTrnSrc() {
		return trnSrc;
	}

	public void setTrnSrc(String trnSrc) {
		this.trnSrc = trnSrc;
	}

	@Override
	public String toString() {
		return "TrnInfoList [trnCode=" + trnCode + ", trnSrc=" + trnSrc + "]";
	}

}
