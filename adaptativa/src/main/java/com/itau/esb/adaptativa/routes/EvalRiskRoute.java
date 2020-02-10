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
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itau.esb.adaptativa.configurator.ConfigurationRoute;
import com.itau.esb.adaptativa.exceptions.CustomException;
import com.itau.esb.adaptativa.model.Response;
import com.itau.esb.adaptativa.properties.RestConsumer;
import com.itau.esb.adaptativa.transformations.FailureErrorProcessor;
import com.itau.esb.adaptativa.transformations.TransformationComponent;

@Component
public class EvalRiskRoute extends ConfigurationRoute {
	
	Namespaces ns = new Namespaces("test", "http://ws.autenticacionadaptativa.intersoft.com.co");
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	
	private static final String ERROR_LABEL = "Error capturado: ";
	private Logger logger = LoggerFactory.getLogger(EvalRiskRoute.class);

	@Autowired
	private RestConsumer restConfig;

	@Override
	public void configure() throws Exception {
		super.configure();
		response.setInclude("NON_NULL");
		response.disableFeature(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		
		onException(HttpHostConnectException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
//		
		onException(CustomException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
//		
//		onException(JsonParseException.class)
//			.handled(true)
//	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
//	        .process(new FailureErrorProcessor())
//	        .marshal(response)
//	        .removeHeaders("*")
//	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
//
//		 onException(JsonMappingException.class)
//	        .handled(true)
//	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
//	        .process(new FailureErrorProcessor())
//	        .marshal(response)
//	        .removeHeaders("*")
//	        .to("log:ERROR-CAPTURADO");
//		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_OK))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		from("direct:evalRiskRoute").routeId("ER_ROUTE_ADAPTATIVA").streamCaching()
			.setProperty("procesoId", simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de operacion")
			.to("direct:loadInfoER")
			.to("velocity:templates/evRiesgoRq.vm")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getOSBEvaluarRiesgoTransaccionMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getOSBEvaluarRiesgoTransaccion()))
			.setHeader("SOAPAction", constant(""))
			.setHeader(Exchange.CONTENT_TYPE, constant(restConfig.getOSBEvaluarRiesgoTransaccionContentType()))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Invoking ITAU SOAP ws")
//			.to("http4://SOAPService?throwExceptionOnFailure=false")
			.to("velocity:templates/response.vm")
			.setHeader("CamelHttpResponseCode", constant(200))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: WS Consumido, status code: ${headers.CamelHttpResponseCode} - body: ${body}")
			// Modificar segun respuesta
			.setProperty("ERTR").xpath("//*[local-name()='evaluarRiesgoTransaccionReturn']", ns)
			.setProperty("Preguntas").xpath("(//*[local-name()='preguntas'])[1]", ns)   // /*/*/*/*/*/*    //*[local-name()='preguntas']
			.to("direct:manageSuccessResponseER")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: End process")
		.end();
		
		from("direct:loadInfoER").routeId("ER_ROUTE_LOAD_INFO")
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
		
		from("direct:manageSuccessResponseER").routeId("ER_ROUTE_SUCCESS_RESPONSE")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Carga de datos a propiedades del exchange (Success Response)")
			.bean(TransformationComponent.class)
			.bean("transformationComponent", "mappingSuccessResponse")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Fin de mapeo de los datos... retornando respuesta")
			.removeHeaders("*")
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
		.end();
		
		
	}
}