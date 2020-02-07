package com.itau.esb.adaptativa.model;

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
