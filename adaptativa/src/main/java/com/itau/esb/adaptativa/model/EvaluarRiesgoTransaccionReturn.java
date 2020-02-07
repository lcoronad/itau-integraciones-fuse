package com.itau.esb.adaptativa.model;

import java.util.List;

public class EvaluarRiesgoTransaccionReturn {
	private String OTP;
	private String deviceCookie;
	private String estadoCliente;
	private GenericResponse genericResponse;
	private List<Pregunta> preguntas;
	private String recomendedActionAA;
	private String sessionId;
	private String transactionId;

	public String getOTP() {
		return OTP;
	}

	public void setOTP(String oTP) {
		OTP = oTP;
	}

	public String getDeviceCookie() {
		return deviceCookie;
	}

	public void setDeviceCookie(String deviceCookie) {
		this.deviceCookie = deviceCookie;
	}

	public String getEstadoCliente() {
		return estadoCliente;
	}

	public void setEstadoCliente(String estadoCliente) {
		this.estadoCliente = estadoCliente;
	}

	public GenericResponse getGenericResponse() {
		return genericResponse;
	}

	public void setGenericResponse(GenericResponse genericResponse) {
		this.genericResponse = genericResponse;
	}

	public List<Pregunta> getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(List<Pregunta> preguntas) {
		this.preguntas = preguntas;
	}

	public String getRecomendedActionAA() {
		return recomendedActionAA;
	}

	public void setRecomendedActionAA(String recomendedActionAA) {
		this.recomendedActionAA = recomendedActionAA;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "EvaluarRiesgoTransaccionReturn [OTP=" + OTP + ", deviceCookie=" + deviceCookie + ", estadoCliente="
				+ estadoCliente + ", genericResponse=" + genericResponse + ", preguntas=" + preguntas
				+ ", recomendedActionAA=" + recomendedActionAA + ", sessionId=" + sessionId + ", transactionId="
				+ transactionId + "]";
	}

}
