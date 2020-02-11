package com.itau.esb.adaptativa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {
	private String statusCode;
	private String serverStatusCode;
	private String severity;
	private String statusDesc;

	@JsonProperty("AdditionalStatus")
	private List<AdditionalStatus> additionalStatus;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getServerStatusCode() {
		return serverStatusCode;
	}

	public void setServerStatusCode(String serverStatusCode) {
		this.serverStatusCode = serverStatusCode;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public List<AdditionalStatus> getAdditionalStatus() {
		return additionalStatus;
	}

	public void setAdditionalStatus(List<AdditionalStatus> additionalStatus) {
		this.additionalStatus = additionalStatus;
	}

	@Override
	public String toString() {
		return "Status [statusCode=" + statusCode + ", serverStatusCode=" + serverStatusCode + ", severity=" + severity
				+ ", statusDesc=" + statusDesc + ", additionalStatus=" + additionalStatus + "]";
	}

}
