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
package com.itau.esb.custacctvalidaterelation.routes;

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
import com.itau.esb.custacctvalidaterelation.configurator.ConfigurationRoute;
import com.itau.esb.custacctvalidaterelation.exceptions.CustomException;
import com.itau.esb.custacctvalidaterelation.interfaces.Headers;
import com.itau.esb.custacctvalidaterelation.model.Response;
import com.itau.esb.custacctvalidaterelation.properties.RestConsumer;
import com.itau.esb.custacctvalidaterelation.transformations.FailureErrorProcessor;

@Component
public class TransformationRoute extends ConfigurationRoute {
	Namespaces sch1 = new Namespaces("sch1", "http://itau.com.co/commoncannonical/v3/schemas");
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	private static final String ERROR_LABEL = "Error capturado: ";
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
//	        .marshal(response)
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
	        .removeHeaders("*")
	        .to("log:ERROR-CAPTURADO");
		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_INTERNAL_SERVER_ERROR))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log(LoggingLevel.ERROR, logger, ERROR_LABEL + exceptionMessage());
		
		from("direct:transformationRoute").routeId("custacctvalidaterelation_transformation")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de operacion")
			.process(e -> {
				String idData = e.getIn().getHeader("customer_id", String.class);
				if(idData.split("_").length == 2) {					
					e.getIn().setHeader("issuedIdentType", idData.split("_")[1]);
					e.getIn().setHeader("issuedIdentValue", idData.split("_")[0]);
				} else {
					throw new CustomException(env.getProperty("msgBasicDataError"));
				}
			})
			.setHeader("acctType").jsonpath("$.ValidatePartyAcctRel.AcctKey.acctType")
			.to("velocity:templates/request.vm")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getItauServiceMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getItauService()))
			.setHeader("Content-Type", constant(restConfig.getItauServiceContentType()))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Invoking ITAU SOAP ws")
			//.to("http4://SOAPService?throwExceptionOnFailure=false")
			.setBody(constant("<soapenv:Envelope 	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
					"	<Header 	xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"/>\r\n" + 
					"	<Body 	xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
					"		<sch:doValidateCustomerAcctRelRs 	xmlns:sch=\"http://itau.com.co/services/accounts/validatecustomeracctrel/v1/schemas\">\r\n" + 
					"			<sch1:HeaderResponse 	xmlns:sch1=\"http://itau.com.co/commoncannonical/v3/schemas\">\r\n" + 
					"				<sch1:MessageHeader>\r\n" + 
					"					<sch1:MessageKey>\r\n" + 
					"						<sch1:integrationId/>\r\n" + 
					"						<sch1:requestVersion/>\r\n" + 
					"						<sch1:requestUUID>b5473bcf-9c0d-ba1d-6aa9-5804c260ecf1</sch1:requestUUID>\r\n" + 
					"					</sch1:MessageKey>\r\n" + 
					"					<sch1:MessageInfo>\r\n" + 
					"						<sch1:dateTime>2020-01-31T14:29:09</sch1:dateTime>\r\n" + 
					"						<sch1:systemId/>\r\n" + 
					"						<sch1:originatorName>Portal</sch1:originatorName>\r\n" + 
					"						<sch1:originatorType>41</sch1:originatorType>\r\n" + 
					"						<sch1:terminalId>10.186.17.24</sch1:terminalId>\r\n" + 
					"						<sch1:trnType/>\r\n" + 
					"					</sch1:MessageInfo>\r\n" + 
					"					<sch1:TrnInfoList>\r\n" + 
					"						<sch1:TrnInfo>\r\n" + 
					"							<sch1:trnCode/>\r\n" + 
					"							<sch1:trnSrc>0020</sch1:trnSrc>\r\n" + 
					"						</sch1:TrnInfo>\r\n" + 
					"					</sch1:TrnInfoList>\r\n" + 
					"				</sch1:MessageHeader>\r\n" + 
					"				<sch1:Status>\r\n" + 
					"					<sch1:statusCode>000</sch1:statusCode>\r\n" + 
					"					<sch1:serverStatusCode>0</sch1:serverStatusCode>\r\n" + 
					"					<sch1:severity>Info</sch1:severity>\r\n" + 
					"					<sch1:statusDesc>Transacción exitosa</sch1:statusDesc>\r\n" + 
					"				</sch1:Status>\r\n" + 
					"			</sch1:HeaderResponse>\r\n" + 
					"			<sch1:AcctKey 	xmlns:sch1=\"http://itau.com.co/commoncannonical/v3/schemas\">\r\n" + 
					"				<sch1:acctType>CTE</sch1:acctType>\r\n" + 
					"			</sch1:AcctKey>\r\n" + 
					"		</sch:doValidateCustomerAcctRelRs>\r\n" + 
					"	</Body>\r\n" + 
					"</soapenv:Envelope>"))
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
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
		
		from("direct:manageSuccessResponse").routeId("ROUTE_SUCCESS_RESPONSE")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Carga de datos a propiedades del exchange (Success Response)")
			.setProperty(Headers.TRN_CODE).xpath("/*/*/*/*/*/*/*/sch1:trnCode/text()", String.class, sch1)
			.setProperty(Headers.TRN_SRC).xpath("/*/*/*/*/*/*/*/sch1:trnSrc/text()", String.class, sch1)
			.setProperty(Headers.STATUS_CODE).xpath("/*/*/*/*/*/sch1:statusCode/text()", String.class, sch1)
			.setProperty(Headers.SERVER_STATUS_CODE).xpath("/*/*/*/*/*/sch1:serverStatusCode/text()", String.class, sch1)
			.setProperty(Headers.SEVERITY).xpath("/*/*/*/*/*/sch1:severity/text()", String.class, sch1)
			.setProperty(Headers.STATUS_DESC).xpath("/*/*/*/*/*/sch1:statusDesc/text()", String.class, sch1)
			// Aditional Status
//			.setProperty(Headers.AD_STATUS_CODE).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:statusCode/text()", String.class, sch1)
//			.setProperty(Headers.AD_SERVER_STATUS_CODE).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:serverStatusCode/text()", String.class, sch1)
//			.setProperty(Headers.AD_SEVERITY).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:severity/text()", String.class, sch1)
//			.setProperty(Headers.AD_STATUS_DESC).xpath("/*/*/*/*/*/sch:AdditionalStatus/sch:statusDesc/text()", String.class, sch1)
			.removeHeaders("*", "acctType|CamelHttpResponseCode")
			.bean("transformationComponent", "mappingSuccessResponse")
			.marshal(response)
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Fin de mapeo de los datos... retornando respuesta")
		.end();
		
		
	}
}