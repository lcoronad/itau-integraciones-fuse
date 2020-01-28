package com.itau.esb.creditnote.model;

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
