package com.itau.esb.adaptativa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ResponseACTR {
	AutenticarClienteTransaccionReturn autenticarClienteTransaccionReturn;

	public AutenticarClienteTransaccionReturn getAutenticarClienteTransaccionReturn() {
		return autenticarClienteTransaccionReturn;
	}

	public void setAutenticarClienteTransaccionReturn(
			AutenticarClienteTransaccionReturn autenticarClienteTransaccionReturn) {
		this.autenticarClienteTransaccionReturn = autenticarClienteTransaccionReturn;
	}
	
	
}
