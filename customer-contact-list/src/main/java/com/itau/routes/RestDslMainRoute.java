package com.itau.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.itau.util.Constant;

import io.swagger.annotations.Api;

/**
 * 
 * @author Red Hat
 *
 */
@Component
@Api(value = "Initial Proyect Camel-REST", description = "Estrucutura Basica Proyecto Rest Y Camel")
public class RestDslMainRoute extends RouteBuilder {

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Autowired
    private Environment env;
    

    @Override
    public void configure() throws Exception {
    // @formatter:off
        restConfiguration()
            .component("servlet")
            
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .port(env.getProperty("server.port", "8080"))
            .contextPath(contextPath.substring(0, contextPath.length() - 2))
            // turn on swagger api-doc
            .apiContextPath("/api-doc")
            .apiProperty("api.title",  env.getProperty("api.title"))
            .apiProperty("api.version", env.getProperty("api.version"));
        
        rest().description(env.getProperty("api.description"))
            .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
        
        .get(env.getProperty("endpoint.healthcheck")).description("Test de OCP").outType(String.class)
            .responseMessage().code(200).message("All users successfully returned").endResponseMessage()
            .route().from("direct:health").setBody(constant("OK")).endRest()
        .get(env.getProperty("endpoint.get.customer.list")).description(env.getProperty("api.description.get.customer.list"))
        	.outType(String.class)
        	.to(Constant.ROUTE_CONSULTA_DATOS);
       
        
        // @formatter:on
    }

}
