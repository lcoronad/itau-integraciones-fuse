package com.itau.esb.adaptativa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {
	
	EvaluarRiesgoTransaccionReturn evaluarRiesgoTransaccionReturn;

	public EvaluarRiesgoTransaccionReturn getEvaluarRiesgoTransaccionReturn() {
		return evaluarRiesgoTransaccionReturn;
	}

	public void setEvaluarRiesgoTransaccionReturn(EvaluarRiesgoTransaccionReturn evaluarRiesgoTransaccionReturn) {
		this.evaluarRiesgoTransaccionReturn = evaluarRiesgoTransaccionReturn;
	}

	@Override
	public String toString() {
		return "Response [evaluarRiesgoTransaccionReturn=" + evaluarRiesgoTransaccionReturn + "]";
	}

}
