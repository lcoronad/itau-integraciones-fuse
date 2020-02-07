/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.itau.esb.adaptativa.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.esb.adaptativa.interfaces.Properties;
import com.itau.esb.adaptativa.model.EvaluarRiesgoTransaccionReturn;
import com.itau.esb.adaptativa.model.GenericResponse;
import com.itau.esb.adaptativa.model.Pregunta;
import com.itau.esb.adaptativa.model.Response;

@Component("transformationComponent")
public class TransformationComponent {

	ObjectMapper mapper = new ObjectMapper();

	public static String transformation(String body) {
		return body;
	}

	public void mappingSuccessResponse(Exchange ex) {
		List<Pregunta> preguntas = new ArrayList<Pregunta>();
		
		GenericResponse GR = new GenericResponse();
		GR.setCodigoError(ex.getProperty(Properties.CODIGO_ERROR, String.class));
		GR.setDescripcion(ex.getProperty(Properties.DESCRIPCION, String.class));

		EvaluarRiesgoTransaccionReturn ERTR = new EvaluarRiesgoTransaccionReturn();
		ERTR.setDeviceCookie(ex.getProperty(Properties.DEVICE_COOKIE, String.class));
		ERTR.setEstadoCliente(ex.getProperty(Properties.ESTADO_CLIENTE, String.class));
		ERTR.setGenericResponse(GR);
		ERTR.setOTP(ex.getProperty(Properties.OTP, String.class));
		ERTR.setPreguntas(preguntas);
		ERTR.setRecomendedActionAA(ex.getProperty(Properties.RECOMENDED_ACTION_AA, String.class));
		ERTR.setSessionId(ex.getProperty(Properties.SESSION_ID, String.class));
		ERTR.setTransactionId(ex.getProperty(Properties.TRANSACTION_ID, String.class));
		Response res = new Response();
		res.setEvaluarRiesgoTransaccionReturn(ERTR);
		
		ex.getIn().setBody(res);
	}
}
