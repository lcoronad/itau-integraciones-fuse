package com.itau.esb.creditnote.model;

import java.util.List;

public class Response {
	private List<TrnInfoList> trnInfoList;
	private Status status;
	private AdditionalStatus additionalStatus;

	public List<TrnInfoList> getTrnInfoList() {
		return trnInfoList;
	}

	public void setTrnInfoList(List<TrnInfoList> trnInfoList) {
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
