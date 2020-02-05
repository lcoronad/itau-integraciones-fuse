package com.itau.esb.custacctvalidaterelation.model;

public class AcctKey {
	private String acctType;

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	@Override
	public String toString() {
		return "AcctKey [acctType=" + acctType + "]";
	}

}
