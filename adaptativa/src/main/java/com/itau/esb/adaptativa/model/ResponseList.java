package com.itau.esb.adaptativa.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseList {

	@JsonProperty("Response")
	@JacksonXmlProperty(localName="ws:respuestasWS")
	List<Respuesta> responses = new ArrayList<>();

	public List<Respuesta> getResponses() {
		return responses;
	}

	public void setResponses(List<Respuesta> responses) {
		this.responses = responses;
	}

}
