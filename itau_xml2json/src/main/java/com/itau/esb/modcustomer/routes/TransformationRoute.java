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
package com.itau.esb.modcustomer.routes;

import java.nio.charset.StandardCharsets;

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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itau.esb.modcustomer.configurator.ConfigurationRoute;
import com.itau.esb.modcustomer.exceptions.CustomException;
import com.itau.esb.modcustomer.interfaces.Headers;
import com.itau.esb.modcustomer.model.Contact;
import com.itau.esb.modcustomer.model.Response;
import com.itau.esb.modcustomer.properties.RestConsumer;
import com.itau.esb.modcustomer.transformations.FailureErrorProcessor;

@Component
public class TransformationRoute extends ConfigurationRoute {

	Namespaces sch = new Namespaces("sch", "http://itau.com.co/commoncannonical/v3/schemas");
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	JacksonDataFormat contacts = new JacksonDataFormat(Contact.class);
	private static final String ERROR_LABEL = "Error capturado: ";
	private static final String TRANSFORMARTION = "transformationComponent";
	private Logger logger = LoggerFactory.getLogger(TransformationRoute.class);

	@Autowired
	private RestConsumer restConfig;

	@Autowired
	private Environment env;

	@Override
	public void configure() throws Exception {
		super.configure();
				
		onException(HttpHostConnectException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(CustomException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		onException(JsonParseException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());

		 onException(JsonMappingException.class)
	        .handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*");
		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		from("direct:transformationRoute").routeId("modcustomer_transformation").streamCaching()
			.setProperty("procesoId", simple("${exchangeId}"))
			.process(e -> {
				String idData = e.getIn().getHeader("id_data", String.class);
				if(idData.split("_").length == 2) {					
					e.getIn().setHeader("issuedIdentValue", idData.split("_")[0]);
					e.getIn().setHeader("issuedIdentType", idData.split("_")[1]);
				} else {
					throw new CustomException(new String(env.getProperty("msgBasicDataError").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8));
				}
			})
			.setHeader("contacts").jsonpath("$.ContactList")
			.to("direct:loadContactList")
			.setHeader("userName", constant(env.getProperty("vm.userName")))
			.setHeader("employeeIdentlNum", constant(env.getProperty("vm.employeeIdentlNum")))
			.to("velocity:templates/request.vm")
			.bean(TRANSFORMARTION, "deleteEmptyNodes")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getItauServiceMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getItauService()))
			.setHeader("Content-Type", constant(restConfig.getItauServiceContentType()))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Invoking ITAU SOAP ws")
			.to("http4://SOAPService?throwExceptionOnFailure=false")	
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: WS Consumido, status code: ${headers.CamelHttpResponseCode} - body: ${body}")
			.to("direct:manageSuccessResponse")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: End process")
			.choice()
				.when(simple("${headers.CamelHttpResponseCode} == 200"))
					.unmarshal(response)
				.otherwise()
					.log("Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio")
			.endChoice()
		.end();
		
		from("direct:loadContactList").routeId("ROUTE_LOAD_CONTACT_LIST")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de carga de lista de contactos")
			.setBody(simple("${headers.contacts}"))
			.marshal(contacts)
			.bean(TRANSFORMARTION, "loadContactList")
		.end();
		
		from("direct:manageSuccessResponse").routeId("ROUTE_SUCCESS_RESPONSE")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Carga de datos a propiedades del exchange (Success Response)")
			.setProperty(Headers.TRN_CODE).xpath("/*/*/*/*/*/*/*/sch:trnCode/text()", String.class, sch)
			.setProperty(Headers.TRN_SRC).xpath("/*/*/*/*/*/*/*/sch:trnSrc/text()", String.class, sch)
			.setProperty(Headers.STATUS_CODE).xpath("/*/*/*/*/*/sch:statusCode/text()", String.class, sch)
			.setProperty(Headers.SERVER_STATUS_CODE).xpath("/*/*/*/*/*/sch:serverStatusCode/text()", String.class, sch)
			.setProperty(Headers.SEVERITY).xpath("/*/*/*/*/*/sch:severity/text()", String.class, sch)
			.setProperty(Headers.STATUS_DESC).xpath("/*/*/*/*/*/sch:statusDesc/text()", String.class, sch)
			// Aditional Status
			.setProperty(Headers.AD_STATUS_CODE).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:statusCode/text()", String.class, sch)
			.setProperty(Headers.AD_SERVER_STATUS_CODE).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:serverStatusCode/text()", String.class, sch)
			.setProperty(Headers.AD_SEVERITY).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:severity/text()", String.class, sch)
			.setProperty(Headers.AD_STATUS_DESC).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:statusDesc/text()", String.class, sch)
			.removeHeaders("*")
			.bean(TRANSFORMARTION, "mappingSuccessResponse")
			.marshal(response)
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Fin de mapeo de los datos... retornando respuesta")
		.end();
		
		
	}
}