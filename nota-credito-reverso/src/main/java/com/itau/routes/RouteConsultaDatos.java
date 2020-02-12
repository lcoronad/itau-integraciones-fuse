package com.itau.routes;

import java.nio.charset.StandardCharsets;

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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.beans.ResponseHandler;
import com.itau.dto.Request;
import com.itau.dto.ResponseSOAP;
import com.itau.exception.BusException;
import com.itau.exception.DataException;
import com.itau.exception.JsonMapperException;
import com.itau.util.Constants;

@Component
public class RouteConsultaDatos extends RouteBuilder{

	private Logger logger = LoggerFactory.getLogger(RouteConsultaDatos.class);

	@Override
	public void configure() throws Exception {
		
		onException(Exception.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception generica fuera de ruta= ${exception.message}")
			.setHeader(Constants.MESSAGE, simple("${exception.message}"))
			.to("velocity:templates/response.json")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
		
		onException(DataException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception generica fuera de ruta= ${exception.message}")
			.setHeader(Constants.MESSAGE, simple("${exception.message}"))
			.to("velocity:templates/response.json")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
	
		onException(JsonMapperException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception en mapeo json= ${exception.message}")
			.setHeader(Constants.MESSAGE, simple("${exception.message}"))
			.to("velocity:templates/response.json")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
		
		from(Constants.ROUTE_CONSULTA_DATOS).routeId("ROUTE_CONSULTA_DATOS").streamCaching()
			.onException(HttpOperationFailedException.class , HttpHostConnectException.class, ConnectTimeoutException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception HttpException: ${exception.message}")
				.setHeader(Constants.MESSAGE, simple("${exception.message}"))
				.to("velocity:templates/response.json")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
			.onException(NullPointerException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una my exception general: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo proceso \n ${body}")
		 		.removeHeaders("*")
		 		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		    .onException(BusException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception general: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo busexception \n ${body}")
		 		.removeHeaders("*")
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		 	.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception generica: ${exception.message}")
				.setHeader(Constants.MESSAGE, simple("${exception.message}"))
				.to("velocity:templates/response.json")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
			.setProperty(Constants.PROCESO_ID, simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta principal")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Datos del cliente ${headers.id_cedula}")
			.setHeader("systemId", simple("{{param.header.systemId}}"))
			.setHeader("trnCode", simple("{{param.header.trnCode}}"))
			.setHeader("trnSrc", simple("{{param.header.trnSrc}}"))
			.process(x->{
				
				Request dto = x.getIn().getBody(Request.class);
				dto.accounRecordRev.acctId = x.getIn().getHeader(Constants.ACCID,String.class);
				XmlMapper mapper = new XmlMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				String xml = mapper.writeValueAsString(dto.accounRecordRev);
				logger.info("Resultado:{}", xml);
				x.getIn().setBody(xml);
				
			})
			.to("velocity:templates/request.vm?propertiesFile=templates/velocity.properties")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Cargo la platilla \n ${body}")
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a consumir el servicio  SOAP")
		 	.to(Constants.ROUTE_CONSUMO_SOAP)
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
					logger.info("Proceso:{} | Mensaje: Resultado:{}" ,x.getProperty(Constants.PROCESO_ID), jsondto);
					
					
		 	 })
		 	.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicia a invocar ruta de validar datos")
		 	.to(Constants.ROUTE_VALIDATOR_STATUS)
			.end();
		
		from(Constants.ROUTE_CONSUMO_SOAP).routeId("CONSULTA_SOAP").streamCaching("true")
			.errorHandler(noErrorHandler())
			.removeHeader("CamelHttpQuery")
			.setHeader(Exchange.HTTP_URI, simple("{{servicio.url}}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.to("http4:dummy?httpClient.connectTimeout={{servicio.connection.timeout}}&httpClient.socketTimeout={{servicio.connection.timeout}}&throwExceptionOnFailure=true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Status respuesta de servicio  SOAP  ${headers.CamelHttpResponseCode}")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo consumo de servicio  SOAP  ${body}")
			.convertBodyTo(String.class)
			.end();
		
		from(Constants.ROUTE_VALIDATOR_STATUS).routeId("VALIDATOR_DATA").streamCaching()
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Validando Data del servicio")
			 
			.setProperty("status").jsonpath("$.Body.doCreditAccountRevRs.*.Status.statusCode")
			.setProperty("severity").jsonpath("$.Body.doCreditAccountRevRs.*.Status.severity")
			
			
			.choice()				
				.when(PredicateBuilder.and(exchangeProperty("status").isEqualTo("000"), exchangeProperty("severity").isEqualTo("Info")))
					.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: No se encontro código de error")
					.setProperty(Constants.RESPONSE_TRNINFOLIST).jsonpath("$.Body.doCreditAccountRevRs.*.*.TrnInfoList.TrnInfo")
					.setProperty(Constants.RESPONSE_STATUS).jsonpath("$.Body.doCreditAccountRevRs.*.Status")
					.bean(ResponseHandler.class)
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
					.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo el proceso")					
				.endChoice()	
				.when(PredicateBuilder.and(exchangeProperty("status").isEqualTo("000"), exchangeProperty("severity").isEqualTo("Warning")))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
					.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} |Mensaje: Error bus warning")
					.inOnly(Constants.ROUTE_EXCEPTION_STATUS)
				.endChoice()	
				.when(PredicateBuilder.or(exchangeProperty("status").convertToString().isEqualTo("120")))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
					.inOnly(Constants.ROUTE_EXCEPTION_STATUS_ERROR_BUS)
				.endChoice()	
				.when(PredicateBuilder.or(exchangeProperty("status").convertToString().isEqualTo("150")))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
					.inOnly(Constants.ROUTE_EXCEPTION_STATUS_ERROR_BUS)
				.endChoice()	
			.end();
			
			from(Constants.ROUTE_EXCEPTION_STATUS).routeId("EXCEPTION-STATUS").streamCaching()
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio ")
				.setProperty(Constants.RESPONSE_STATUS).jsonpath("$.Body.doCreditAccountRevRs.*.Status")
				.setProperty(Constants.RESPONSE_TRNINFOLIST).jsonpath("$.Body.doCreditAccountRevRs.*.*.TrnInfoList.TrnInfo")
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Busqueda ${exchangeProperty.responseStatus}")
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
				.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
			.end();
			
			from(Constants.ROUTE_EXCEPTION_STATUS_ERROR_BUS).routeId("EXCEPTION-STATUS-ERROR-BUS").streamCaching()
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio ")
				.setProperty(Constants.RESPONSE_STATUS).jsonpath("$.Body.doCreditAccountRevRs.*.Status")
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Busqueda ${exchangeProperty.responseStatus}")
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
				.bean(ResponseHandler.class,"error")
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
	 		.end();
		
	}
	
	
}
