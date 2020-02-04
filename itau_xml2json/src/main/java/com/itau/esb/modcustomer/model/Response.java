package com.itau.esb.modcustomer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
	private TrnInfoList trnInfoList;
	private Status status;
	private AdditionalStatus additionalStatus;

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

	public AdditionalStatus getAdditionalStatus() {
		return additionalStatus;
	}

	public void setAdditionalStatus(AdditionalStatus additionalStatus) {
		this.additionalStatus = additionalStatus;
	}

	@Override
	public String toString() {
		return "Response [trnInfoList=" + trnInfoList + ", status=" + status + ", additionalStatus=" + additionalStatus
				+ "]";
	}

}
