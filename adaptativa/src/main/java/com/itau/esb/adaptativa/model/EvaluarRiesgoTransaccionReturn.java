package com.itau.esb.adaptativa.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "evaluarRiesgoTransaccionReturn", namespace = "http://ws.autenticacionadaptativa.intersoft.com.co")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class EvaluarRiesgoTransaccionReturn {

	private String OTP;
	private String deviceCookie;
	private String estadoCliente;
	private GenericResponse genericResponse;
	private Preguntas preguntas;
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

	@XmlElement
	public GenericResponse getGenericResponse() {
		return genericResponse;
	}

	public void setGenericResponse(GenericResponse genericResponse) {
		this.genericResponse = genericResponse;
	}

	@XmlElement
	public Preguntas getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(Preguntas preguntas) {
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
