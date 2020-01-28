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
package com.itau.esb.itausoap2json.routes;

import org.apache.camel.Exchange;
import org.apache.camel.ExpressionEvaluationException;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itau.esb.itausoap2json.configurator.ConfigurationRoute;
import com.itau.esb.itausoap2json.exceptions.CustomException;
import com.itau.esb.itausoap2json.interfaces.Headers;
import com.itau.esb.itausoap2json.model.Contact;
import com.itau.esb.itausoap2json.model.Response;
import com.itau.esb.itausoap2json.properties.RestConsumer;
import com.itau.esb.itausoap2json.transformations.FailureErrorProcessor;

@Component
public class TransformationRoute extends ConfigurationRoute {

	Namespaces sch = new Namespaces("sch", "http://itau.com.co/commoncannonical/v3/schemas");
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	JacksonDataFormat contacts = new JacksonDataFormat(Contact.class);

	@Autowired
	private RestConsumer restConfig;

	@Autowired
	private Environment env;

	@Override
	public void configure() throws Exception {
		super.configure();
				
		onException(HttpHostConnectException.class)
			.handled(true)
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .process(new FailureErrorProcessor())
	        .log("${body}")
	        .marshal(response)
	        .removeHeaders("*")
	        .log("Error capturado: " + exceptionMessage());
		
		onException(CustomException.class)
			.handled(true)
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log("Error capturado: " + exceptionMessage());
		
		onException(JsonParseException.class)
			.handled(true)
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .log("Error capturado: " + exceptionMessage());

		 onException(JsonMappingException.class)
	        .handled(true)
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .process(new FailureErrorProcessor())
	        .marshal(response)
	        .removeHeaders("*")
	        .to("log:ERROR-CAPTURADO");
		 
		 onException(ExpressionEvaluationException.class)
			.handled(true)
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .process(new FailureErrorProcessor())
	        .removeHeaders("*")
	        .log("Error capturado: " + exceptionMessage());
		
		from("direct:transformationRoute").routeId("itausoap2json_transformation")
			.log("Inicio de operacion")
			.setHeader("issuedIdentType").jsonpath("$.IssuedIdent.issuedIdentType", String.class)
			.setHeader("issuedIdentValue").jsonpath("$.IssuedIdent.issuedIdentValue", String.class)
			.setHeader("contacts").jsonpath("$.ContactList")
			.to("direct:loadContactList")
			.setHeader("userName", constant(env.getProperty("vm.userName")))
			.setHeader("employeeIdentlNum", constant(env.getProperty("vm.employeeIdentlNum")))
			.to("velocity:templates/request.vm")
			.bean("transformationComponent", "deleteEmptyNodes")
			.log("body: ${body}")
			.setHeader(Exchange.HTTP_METHOD, constant(restConfig.getItauServiceMethod()))
			.setHeader(Exchange.HTTP_URI, constant(restConfig.getItauService()))
			.setHeader("Content-Type", constant(restConfig.getItauServiceContentType()))
			.log("Invoking ITAU SOAP ws")
			.to("http4://SOAPService?throwExceptionOnFailure=false")	
			.log("WS Consumido, status code: ${headers.CamelHttpResponseCode} - body: ${body}")
			.to("direct:manageSuccessResponse")
			.log("headers: ${headers}")
			.log("End process")
		.end();
		
		from("direct:loadContactList").routeId("ROUTE_LOAD_CONTACT_LIST")
			.log("Inicio de carga de lista de contactos")
			.setBody(simple("${headers.contacts}"))
			.marshal(contacts)
			.bean("transformationComponent", "loadContactList")
		.end();
		
		from("direct:manageSuccessResponse").routeId("ROUTE_SUCCESS_RESPONSE")
			.log("Carga de datos a propiedades del exchange (Success Response)")
			.setProperty(Headers.TRN_CODE).xpath("/*/*/*/*/*/*/*/sch:trnCode/text()", String.class, sch)
			.setProperty(Headers.TRN_SRC).xpath("/*/*/*/*/*/*/*/sch:trnSrc/text()", String.class, sch)
			.setProperty(Headers.STATUS_CODE).xpath("/*/*/*/*/*/sch:statusCode/text()", String.class, sch)
			.setProperty(Headers.SERVER_STATUS_CODE).xpath("/*/*/*/*/*/sch:serverStatusCode/text()", String.class, sch)
			.setProperty(Headers.SEVERITY).xpath("/*/*/*/*/*/sch:severity/text()", String.class, sch)
			.setProperty(Headers.STATUS_DESC).xpath("/*/*/*/*/*/sch:statusDesc/text()", String.class, sch)
			.bean("transformationComponent", "mappingSuccessResponse")
			.log("Fin de mapeo de los datos... retornando respuesta")
			.removeHeaders("*")
		.end();
		
		
	}
}