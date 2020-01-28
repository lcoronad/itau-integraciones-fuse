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

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itau.esb.itausoap2json.configurator.ConfigurationRoute;
import com.itau.esb.itausoap2json.model.Response;
import com.itau.esb.itausoap2json.properties.RestConsumer;
import com.itau.esb.itausoap2json.transformations.FailureErrorProcessor;


@Component
public class RestConsumerRoute extends ConfigurationRoute {
    
	@Autowired
	private RestConsumer restConfig;
	
	@Autowired
    private Environment env;
	
	JacksonDataFormat response = new JacksonDataFormat(Response.class);
	
   @Override
    public void configure() throws Exception {
	   super.configure();
	   
	   onException(JsonParseException.class)
			.handled(true)
			.setHeader("CamelHttpResponseCode", simple("200"))
			.process(new FailureErrorProcessor())
			.to("log:ERROR-CAPTURADO");
   
	   onException(JsonMappingException.class)
			.handled(true)
	       .setHeader("CamelHttpResponseCode", simple("200"))
	       .process(new FailureErrorProcessor())
	       .marshal(response)
	       .log("Error capturado: " + exceptionMessage());
	   
        restConfiguration()
        	.component("servlet")
        	.bindingMode(RestBindingMode.json)
        	.dataFormatProperty("prettyPrint", "true")
            .apiContextPath(restConfig.getApiPath())
                .apiProperty("api.title", restConfig.getApiPath())
                .apiProperty("api.version", restConfig.getApiVersion())
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api");
        
        rest()
        	.description(restConfig.getApiDescription())
        	.consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
        	.produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
        	
        	.get(restConfig.getHealthcheck()).description("Test de OCP")
        		.outType(String.class)
        		.responseMessage().code(200).message("All users successfully returned")
        		.endResponseMessage()
        		.route().from("direct:health").setBody(constant("OK")).endRest()
            
        	.put(restConfig.getServiceName())
            	.to("direct:transformationRoute");
    }
}
