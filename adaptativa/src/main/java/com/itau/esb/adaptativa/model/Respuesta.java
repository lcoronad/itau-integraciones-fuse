package com.itau.esb.adaptativa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonRootName("respuesta")
@JacksonXmlRootElement(localName="ws:respuestasWS")
public class Respuesta {

	@JsonProperty("codigo")
	@JacksonXmlProperty(localName="vo:codigo")
	private String codigo;
	
	@JsonProperty("respuesta")
	@JacksonXmlProperty(localName="vo:respuesta")
	private String respuesta;
	
	@JsonProperty("texto")
	@JacksonXmlProperty(localName="vo:texto")
	private String texto;
	
	@JsonProperty("tipo")
	@JacksonXmlProperty(localName="vo:tipo")
	private String tipo;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "Respuesta [codigo=" + codigo + ", respuesta=" + respuesta + ", texto=" + texto + ", tipo=" + tipo + "]";
	}

}
