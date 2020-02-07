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
package com.itau.esb.adaptativa.routes;

import org.apache.camel.Exchange;
import org.apache.camel.ExpressionEvaluationException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itau.esb.adaptativa.configurator.ConfigurationRoute;
import com.itau.esb.adaptativa.exceptions.CustomException;
import com.itau.esb.adaptativa.interfaces.Properties;
import com.itau.esb.adaptativa.model.Response;
import com.itau.esb.adaptativa.properties.RestConsumer;
import com.itau.esb.adaptativa.transformations.FailureErrorProcessor;

@Component
public class EvalRiskRoute extends ConfigurationRoute {
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	private static final String ERROR_LABEL = "Error capturado: ";
	private Logger logger = LoggerFactory.getLogger(EvalRiskRoute.class);

	@Autowired
	private RestConsumer restConfig;

	@Override
	public void configure() throws Exception {
		super.configure();
				
		onException(HttpHostConnectException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(CustomException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(JsonParseException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());

		 onException(JsonMappingException.class)
	        .handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .to("log:ERROR-CAPTURADO");
		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		from("direct:evalRiskRoute").routeId("ER_ROUTE_ADAPTATIVA")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de operacion")
			.to("direct:loadInfo")
			.to("velocity:templates/EvRiesgoRq.vm")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getOSBEvaluarRiesgoTransaccionMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getOSBEvaluarRiesgoTransaccion()))
			.setHeader("Content-Type", constant(restConfig.getOSBEvaluarRiesgoTransaccionContentType()))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Invoking ITAU SOAP ws")
			.to("http4://SOAPService?throwExceptionOnFailure=false")	
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: WS Consumido, status code: ${headers.CamelHttpResponseCode} - body: ${body}")
			.to("direct:manageSuccessResponse")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: End process")
		.end();
		
		from("direct:loadInfo").routeId("ER_ROUTE_LOAD_INFO")
			.setHeader("idClientType").jsonpath("$.idClientType")
			.setHeader("idClient").jsonpath("$.idClient")
			.setHeader("chanellId").jsonpath("$.chanellId")
			.setHeader("dateTx").jsonpath("$.dateTx")
			.setHeader("ip").jsonpath("$.ip")
			.setHeader("devicePrint").jsonpath("$.devicePrint")
			.setHeader("deviceCookie").jsonpath("$.deviceCookie")
			.setHeader("idTx").jsonpath("$.idTx")
			.setHeader("referer").jsonpath("$.referer")
			.setHeader("myAccountNumber").jsonpath("$.myAccountNumber")
			.setHeader("currency").jsonpath("$.currency")
			.setHeader("otherAccountType").jsonpath("$.otherAccountType")
			.setHeader("otherAccountBankType").jsonpath("$.otherAccountBankType")
			.setHeader("transferMediumType").jsonpath("$.transferMediumType")
			.setHeader("otherAccountOwnershipType").jsonpath("$.otherAccountOwnershipType")
			.setHeader("schedule").jsonpath("$.schedule")
			.setHeader("executionSpeed").jsonpath("$.executionSpeed")
			.setHeader("otherAccountRoutingCode").jsonpath("$.otherAccountRoutingCode")
			.setHeader("otherAccountNumber").jsonpath("$.otherAccountNumber")
			.setHeader("otherAccountCountry").jsonpath("$.otherAccountCountry")
			.setHeader("otherAccountReferenceCode").jsonpath("$.otherAccountReferenceCode")
			.setHeader("idioma").jsonpath("$.idioma")
		.end();
		
		from("direct:manageSuccessResponse").routeId("ER_ROUTE_SUCCESS_RESPONSE")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Carga de datos a propiedades del exchange (Success Response)")
			.setProperty(Properties.OTP).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/OTP/text()", String.class)
			.setProperty(Properties.DEVICE_COOKIE).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/deviceCookie/text()", String.class)
			.setProperty(Properties.ESTADO_CLIENTE).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/estadoCliente/text()", String.class)
			.setProperty(Properties.CODIGO_ERROR).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/genericResponse/codigoError/text()", String.class)
			.setProperty(Properties.DESCRIPCION).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/genericResponse/descripcion/text()", String.class)
			.setProperty(Properties.RECOMENDED_ACTION_AA).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/recomendedActionAA/text()", String.class)
			.setProperty(Properties.SESSION_ID).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/sessionId/text()", String.class)
			.setProperty(Properties.TRANSACTION_ID).xpath("/*/*/evaluarRiesgoTransaccionResponse/evaluarRiesgoTransaccionReturn/transactionId/text()", String.class)
			.removeHeaders("*")
			.bean("transformationComponent", "mappingSuccessResponse")
			.marshal(response)
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Fin de mapeo de los datos... retornando respuesta")
		.end();
		
		
	}
}