package com.itau.esb.jpattransferlogs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Response {
	
	@JsonProperty("TrnInfoList")
	private List<TrnInfoList> trnInfoList;
	
	@JsonProperty("Status")
	private Status status;

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

	@Override
	public String toString() {
		return "Response [trnInfoList=" + trnInfoList + ", status=" + status + "]";
	}

}
