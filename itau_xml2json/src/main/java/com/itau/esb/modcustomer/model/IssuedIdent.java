package com.itau.esb.modcustomer.model;

public class IssuedIdent {
	private String issuedIdentType;
	private String issuedIdentValue;

	public String getIssuedIdentType() {
		return issuedIdentType;
	}

	public void setIssuedIdentType(String issuedIdentType) {
		this.issuedIdentType = issuedIdentType;
	}

	public String getIssuedIdentValue() {
		return issuedIdentValue;
	}

	public void setIssuedIdentValue(String issuedIdentValue) {
		this.issuedIdentValue = issuedIdentValue;
	}

	@Override
	public String toString() {
		return "IssuedIdent [issuedIdentType=" + issuedIdentType + ", issuedIdentValue=" + issuedIdentValue + "]";
	}

}
