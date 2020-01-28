package com.itau.esb.itausoap2json.model;

public class Response {
	private TrnInfoList trnInfoList;
	private Status status;

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

	@Override
	public String toString() {
		return "[trnInfoList=" + trnInfoList + ", status=" + status + "]";
	}
}
