package com.itau.routes;

import java.nio.charset.StandardCharsets;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.beans.ResponseHandler;
import com.itau.dto.ResponseSOAP;
import com.itau.exception.MyException;
import com.itau.util.Constant;

@Component
public class RouteConsultaDatos extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(RouteConsultaDatos.class);
			

	@SuppressWarnings("unchecked")
	@Override
	public void configure() throws Exception {
		
		onException(MyException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Ah ocurrido una excepcion = ${exception.message}")
			.setBody(simple("{\"error\":\"Servicio no disponible\",\"message\": \"${exception.message}\"}"))
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
		
		onException(CamelException.class)
 			.handled(true)
 			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception general: ${exception.message}")
 			.bean(ResponseHandler.class)
 			.marshal().json(JsonLibrary.Jackson)
 			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
 			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
 			.end();
		
		
		from(Constant.ROUTE_CONSULTA_DATOS).routeId("ROUTE_GET_DATA").streamCaching("true")
			.onException(HttpOperationFailedException.class , HttpHostConnectException.class, ConnectTimeoutException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception HttpException: ${exception.message}")
				.setBody(simple("{\"error\":\"Servicio no disponible\",\"message\": \"${exception.message}\"}"))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
			.onException(MyException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una my exception general: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
		 		.removeHeaders("*")
		 		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		    .onException(CamelException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception general: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		 	.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception HttpException: ${exception.message}")
				.setBody(simple("{\"error\":\"Error interno\",\"message\": \"${exception.message}\"}"))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
		 	.setProperty(Constant.PROCESO_ID, simple("${exchangeId}"))
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta de Consulta de datos")
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Datos recividos ${headers.id_cedula}")
		 	.process(e->{
		 		String[] dataClient = e.getIn().getHeader("id_cedula", String.class).split("_");
		 		e.getIn().setHeader("issuedIdentType", dataClient[1]);
		 		e.getIn().setHeader("issuedIdentValue", dataClient[0]);
		 	})
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Headers recividos  ${headers}")
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a cargar el template")
		 	.to("velocity:templates/request.vm?propertiesFile=velocity.properties")
		 	.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Plantilla cargada de request \n ${body}")
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a consumir el servicio  SOAP")
		 	.to(Constant.ROUTE_CONSUMO_SOAP)
		 	.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo consumo de servicio  SOAP \n ${body}")
		 	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a mapear el dto")
		 	.process(x->{
		 			String body = x.getIn().getBody(String.class);
					XmlMapper xmlMapper = new XmlMapper();
					xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					xmlMapper.setDefaultUseWrapper(false);
					ResponseSOAP dto = xmlMapper.readValue(body.getBytes(StandardCharsets.UTF_8), ResponseSOAP.class);
				    			
					ObjectMapper objectMapper = new ObjectMapper();
					String jsondto = objectMapper.writeValueAsString(dto);
					x.getIn().setBody(jsondto);
					logger.info("Proceso:{} | Mensaje: Resultado:{}" ,x.getProperty(Constant.PROCESO_ID), jsondto);
					
					
		 	})
		 	.to(Constant.ROUTE_VALIDATOR_STATUS)
		 	.end();
		
		from(Constant.ROUTE_CONSUMO_SOAP).routeId("CONSULTA_SOAP").streamCaching("true")
			.errorHandler(noErrorHandler())
			.removeHeader("CamelHttpQuery")
			.setHeader(Exchange.HTTP_URI, simple("{{servicio.url}}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.to("http4:dummy?httpClient.connectTimeout={{servicio.connection.timeout}}&httpClient.socketTimeout={{servicio.connection.timeout}}&throwExceptionOnFailure=true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Status respuesta de servicio  SOAP  ${headers.CamelHttpResponseCode}")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo consumo de servicio  SOAP  ${body}")
			.convertBodyTo(String.class)
			.end();
		
		from(Constant.ROUTE_VALIDATOR_STATUS).routeId("VALIDATOR_DATA").streamCaching()
		.errorHandler(noErrorHandler())
		.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Validando Data del servicio")
		
		.setProperty("status").jsonpath("$.Body.getCustomerContactListRs.*.Status.statusCode")
		.setProperty("severity").jsonpath("$.Body.getCustomerContactListRs.*.Status.severity")
		
		.log(LoggingLevel.DEBUG, logger, "Status: ${exchangeProperty.status} | Severity: ${exchangeProperty.severity}")
				
		.choice()				
				.when(PredicateBuilder.and(exchangeProperty("status").isEqualTo("000"), exchangeProperty("severity").isEqualTo("Info")))
					.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: No se encontro cÃ³digo de error")
					.setProperty(Constant.RESPONSE_TRNINFOLIST).jsonpath("$.Body.getCustomerContactListRs.*.*.TrnInfoList.TrnInfo")
					.setProperty(Constant.RESPONSE_STATUS).jsonpath("$.Body.getCustomerContactListRs.*.Status")
					.setProperty(Constant.RESPONSE_LIST_CONTACTS).jsonpath("$.Body.getCustomerContactListRs.*.Contact[*]")
					.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: ${exchangeProperty.responseStatus[0]}")
					.bean(ResponseHandler.class, "responseOK")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))				
				.endChoice()	
				.when(PredicateBuilder.and(exchangeProperty("status").isEqualTo("000"), exchangeProperty("severity").isEqualTo("Warning")))
					.log(LoggingLevel.DEBUG, logger, "Response Code: 422")
					.removeHeaders("*")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
					.inOnly(Constant.ROUTE_EXCEPTION_STATUS)
				.endChoice()	
				.when(PredicateBuilder.or(exchangeProperty("status").convertToString().isEqualTo("120")))
					.log(LoggingLevel.DEBUG, logger, "Response Code: 400")
					.removeHeaders("*")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
					.inOnly(Constant.ROUTE_EXCEPTION_STATUS)
				.endChoice()	
				.when(PredicateBuilder.or(exchangeProperty("status").convertToString().isEqualTo("150")))
					.log(LoggingLevel.DEBUG, logger, "Response Code: 500")
					.removeHeaders("*")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
					.inOnly(Constant.ROUTE_EXCEPTION_STATUS)
				.endChoice()
			.end()
		 .end();
	
		from(Constant.ROUTE_EXCEPTION_STATUS).routeId("EXCEPTION-STATUS").streamCaching()
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio ")
			.setProperty(Constant.RESPONSE_STATUS).jsonpath("$.Body.getCustomerContactListRs.*.Status")
			.setProperty(Constant.RESPONSE_TRNINFOLIST).jsonpath("$.Body.getCustomerContactListRs.*.*.TrnInfoList.TrnInfo")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Busqueda ${exchangeProperty.responseStatus}")
			.throwException(CamelException.class, "Error en info")
		.end()
			.end();
		
	}

}
