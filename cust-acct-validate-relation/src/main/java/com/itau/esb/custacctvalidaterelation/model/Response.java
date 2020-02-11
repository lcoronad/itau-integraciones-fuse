package com.itau.esb.custacctvalidaterelation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {
	
	@JsonProperty("TrnInfoList")
	private List<TrnInfoList> trnInfoList;
	
	@JsonProperty("Status")
	private Status status;
	
	@JsonProperty("AcctKey")
	private AcctKey acctKey;

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

}
