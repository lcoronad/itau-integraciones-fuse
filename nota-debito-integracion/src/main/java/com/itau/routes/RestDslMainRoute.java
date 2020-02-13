package com.itau.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.itau.dto.Request;
import com.itau.dto.Response;
import com.itau.util.Constants;

import io.swagger.annotations.Api;

/**
 * 
 * @author 
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
        
        .get(env.getProperty("endpoint.healtcheck")).description(env.getProperty("endpoint.description.service")).outType(String.class)
            .responseMessage().code(200).message("All users successfully returned").endResponseMessage()
            .route().setBody(constant("OK")).endRest()
         .post(env.getProperty("endpoint.nota.debito")).description(env.getProperty("api.description.service")).type(Request.class).description(
                 env.getProperty("endpoint.nota.debito.description.service")).outType(Response.class) 
             .responseMessage().code(200).message("All users successfully created").endResponseMessage()
             .to(Constants.ROUTE_CONSULTA_DATOS);
        
        onException(Exception.class)
			.handled(true)
			.log(LoggingLevel.ERROR, "RestDslMain", "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception generica fuera de ruta= ${exception.message}")
//			.setBody(simple("{\"Status\":{\"statusCode\": \"500\",\"serverStatusCode\": null,\"severity\": \"Error\",\"statusDesc\": \"${exception.message}\"} }"))
			.setHeader("error", simple("${exception.message}", String.class))
			.process(x->{
				String e = x.getIn().getHeader("error", String.class);
				x.getIn().setHeader("error", e.replaceAll("\"", "'"));
			})
			.to("velocity:templates/response.json")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
     
        // @formatter:on
    }

}
