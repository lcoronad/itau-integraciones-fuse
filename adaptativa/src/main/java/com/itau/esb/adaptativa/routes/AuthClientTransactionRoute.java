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
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itau.esb.adaptativa.configurator.ConfigurationRoute;
import com.itau.esb.adaptativa.exceptions.CustomException;
import com.itau.esb.adaptativa.model.Response;
import com.itau.esb.adaptativa.model.Respuesta;
import com.itau.esb.adaptativa.properties.RestConsumer;
import com.itau.esb.adaptativa.transformations.FailureErrorProcessor;

@Component
public class AuthClientTransactionRoute extends ConfigurationRoute {
	Namespaces sch = new Namespaces("sch", "http://itau.com.co/commoncannonical/v2/schemas");
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	JacksonDataFormat respuestas = new JacksonDataFormat(Respuesta.class);
	private static final String ERROR_LABEL = "Error capturado: ";
	private Logger logger = LoggerFactory.getLogger(AuthClientTransactionRoute.class);

	@Autowired
	private RestConsumer restConfig;

	@Override
	public void configure() throws Exception {
		super.configure();
				
		onException(HttpHostConnectException.class, HttpOperationFailedException.class , HttpHostConnectException.class, ConnectTimeoutException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(CustomException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(JsonParseException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());

		 onException(JsonMappingException.class)
	        .handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .to("log:ERROR-CAPTURADO");
		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		from("direct:authClientTransactionRoute").routeId("ACT_act_adaptativa_transformation")
			.setProperty("procesoId", simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de operacion")
			.to("direct:loadInfo")
			.setHeader("respuestasWS").jsonpath("$.respuestasWS")
			.to("direct:loadResponsesList")
			.to("velocity:templates/AuthClientTrxRq.vm")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getOSBAutenticarClienteTransaccionMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getOSBAutenticarClienteTransaccion()))
			.setHeader("Content-Type", constant(restConfig.getOSBAutenticarClienteTransaccionContentType()))
			.setHeader("SOAPAction", constant(""))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Invoking ITAU SOAP ws")
			.to("http4://SOAPService?httpClient.connectTimeout={{servicio.connection.timeout}}&httpClient.socketTimeout={{servicio.connection.timeout}}&throwExceptionOnFailure=true")	
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: WS Consumido, status code: ${headers.CamelHttpResponseCode} - body: ${body}")
			.to("direct:manageSuccessResponse")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: End process")
		.end();
		
		from("direct:loadResponsesList").routeId("ROUTE_LOAD_RESPONSES_LIST")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de carga de lista de respuestas")
			.setBody(simple("${headers.respuestasWS}"))
			.marshal(respuestas)
			.bean("transformationComponent", "loadResponsesList")
		.end();
		
		from("direct:loadInfo").routeId("ACT_ACT_ROUTE_LOAD_INFO")
			.setHeader("tipoAutenticacion").jsonpath("$.tipoAutenticacion")
			.setHeader("tipoIdCliente").jsonpath("$.tipoIdCliente")
			.setHeader("idCliente").jsonpath("$.idCliente")
			.setHeader("canalId").jsonpath("$.canalId")
			.setHeader("fechaTx").jsonpath("$.fechaTx")
			.setHeader("ip").jsonpath("$.ip")
			.setHeader("deviceCookie").jsonpath("$.deviceCookie")
			.setHeader("OTP").jsonpath("$.OTP")
			.setHeader("telefono").jsonpath("$.telefono")
			.setHeader("movil").jsonpath("$.movil")
			.setHeader("sessionId").jsonpath("$.sessionId")
			.setHeader("transactionId").jsonpath("$.transactionId")
		.end();		
		
		from("direct:manageSuccessResponse").routeId("ACT_ROUTE_SUCCESS_RESPONSE")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Carga de datos a propiedades del exchange (Success Response)")
			.removeHeaders("*")
			.setHeader("deviceTokenCookie").xpath("//*[local-name()='deviceTokenCookie']/text()")
			.setHeader("codigoError").xpath("//*[local-name()='codigoError']/text()")
			.setHeader("descripcion").xpath("//*[local-name()='descripcion']/text()")
			.setHeader("recomendedActionAA").xpath("//*[local-name()='recomendedActionAA']/text()")
			.bean("transformationComponent", "mappingSuccessResponseACT")
			.removeHeaders("*")
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Fin de mapeo de los datos... retornando respuesta")
		.end();
		
		
	}
}