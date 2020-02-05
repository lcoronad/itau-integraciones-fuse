package com.itau.esb.custacctvalidaterelation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {
	private TrnInfoList trnInfoList;
	private Status status;
	private AcctKey acctKey;
//	private AdditionalStatus additionalStatus;

	public TrnInfoList getTrnInfoList() {
		return trnInfoList;
	}

	public void setTrnInfoList(TrnInfoList trnInfoList) {
		this.trnInfoList = trnInfoList;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public AcctKey getAcctKey() {
		return acctKey;
	}

	public void setAcctKey(AcctKey acctKey) {
		this.acctKey = acctKey;
	}

	@Override
	public String toString() {
		return "Response [trnInfoList=" + trnInfoList + ", status=" + status + ", acctKey=" + acctKey + "]";
	}

//	public AdditionalStatus getAdditionalStatus() {
//		return additionalStatus;
//	}
//
//	public void setAdditionalStatus(AdditionalStatus additionalStatus) {
//		this.additionalStatus = additionalStatus;
//	}

//	@Override
//	public String toString() {
//		return "Response [trnInfoList=" + trnInfoList + ", status=" + status + ", additionalStatus=" + additionalStatus
//				+ "]";
//	}
}
