package com.itau.esb.adaptativa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class AutenticarClienteTransaccionReturn {
	private String deviceTokenCookie;
	private String recomendedActionAA;
	private GenericResponse genericResponse;

	public String getDeviceTokenCookie() {
		return deviceTokenCookie;
	}

	public void setDeviceTokenCookie(String deviceTokenCookie) {
		this.deviceTokenCookie = deviceTokenCookie;
	}

	public String getRecomendedActionAA() {
		return recomendedActionAA;
	}

	public void setRecomendedActionAA(String recomendedActionAA) {
		this.recomendedActionAA = recomendedActionAA;
	}

	public GenericResponse getGenericResponse() {
		return genericResponse;
	}

	public void setGenericResponse(GenericResponse genericResponse) {
		this.genericResponse = genericResponse;
	}

	@Override
	public String toString() {
		return "AutenticarClienteTransaccionReturn [deviceTokenCookie=" + deviceTokenCookie + ", recomendedActionAA="
				+ recomendedActionAA + ", genericResponse=" + genericResponse + "]";
	}

}
