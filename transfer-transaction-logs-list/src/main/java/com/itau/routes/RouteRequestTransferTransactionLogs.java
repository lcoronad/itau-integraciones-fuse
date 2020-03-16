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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.beans.ResponseHandler;
import com.itau.dto.ResponseSOAP;
import com.itau.exception.DataException;
import com.itau.exception.JsonMapperException;
import com.itau.util.Constants;

@Component
public class RouteRequestTransferTransactionLogs extends RouteBuilder{

	private Logger logger = LoggerFactory.getLogger(RouteRequestTransferTransactionLogs.class);
	
	private static final String STATUS = "$.Body.getTransferTransactionLogListRs.*.Status";
	private static final String TRNINFO = "$.Body.getTransferTransactionLogListRs.*.*.TrnInfoList.TrnInfo";
	private static final String STATUS_CODE = "$.Body.getTransferTransactionLogListRs.HeaderResponse.Status.statusCode";
	private static final String SERVER_STATUS_CODE = "$.Body.getTransferTransactionLogListRs.HeaderResponse.Status.serverStatusCode";
	private static final String SEVERITY = "$.Body.getTransferTransactionLogListRs.HeaderResponse.Status.severity";
	private static final String STATUSDESC = "$.Body.getTransferTransactionLogListRs.HeaderResponse.Status.statusDesc";
	private static final String EXCEPTION_MESSAGE = "${exception.message}";
	private static final String LOAD_TEMPLATE = "velocity:templates/response.json";
	private static final String STATUS_VALIDATOR = "status";
	private static final String SEVERITY_VALIDATOR = "severity";

	@SuppressWarnings("unchecked")
	@Override
	public void configure() throws Exception {
		
		onException(Exception.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception generica fuera de ruta= ${exception.message}")
			.setHeader(Constants.ERROR, simple(EXCEPTION_MESSAGE))
			.to(LOAD_TEMPLATE)
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
		
		onException(DataException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception general 1: ${exception.message}")
			.bean(ResponseHandler.class)
			.marshal().json(JsonLibrary.Jackson)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo.. \n ${body}")
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
	
		onException(JsonMapperException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Se presento una exception en mapeo json= ${exception.message}")
			.setHeader(Constants.ERROR, simple(EXCEPTION_MESSAGE))
			.to(LOAD_TEMPLATE)
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
			.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end();
		
		from(Constants.ROUTE_REQUEST_TRANSACTION_FEE).routeId("ROUTE_REQUEST_TRANSACTION_LOGS").streamCaching()
			.onException(HttpOperationFailedException.class , HttpHostConnectException.class, ConnectTimeoutException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception HttpException: ${exception.message}")
				.setHeader(Constants.ERROR, simple(EXCEPTION_MESSAGE))
				.to(LOAD_TEMPLATE)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
			.onException(NullPointerException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una my exception general: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo..... \n ${body}")
		 		.removeHeaders("*")
		 		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		    .onException(DataException.class)
		 		.handled(true)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception general 2: ${exception.message}")
		 		.bean(ResponseHandler.class)
		 		.marshal().json(JsonLibrary.Jackson)
		 		.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")		 		
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
		 	.end()
		 	.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Encontro una exception General: ${exception.message}")
				.setHeader(Constants.ERROR, simple(EXCEPTION_MESSAGE))
				.to(LOAD_TEMPLATE)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
			.end()
			.setProperty(Constants.PROCESO_ID, simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta principal")
			.setHeader("servicioUsername", simple("{{servicio.username}}"))
			.setHeader("servicioPassword", simple("{{servicio.password}}"))
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
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("http4:dummy?httpClient.connectTimeout={{servicio.connection.timeout}}&httpClient.socketTimeout={{servicio.connection.timeout}}&throwExceptionOnFailure=true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Status respuesta de servicio  SOAP  ${headers.CamelHttpResponseCode}")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo consumo de servicio  SOAP  ${body}")
			.convertBodyTo(String.class)
			.end();
		
		from(Constants.ROUTE_VALIDATOR_STATUS).routeId("VALIDATOR_DATA").streamCaching()
		.errorHandler(noErrorHandler())
		.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Validando Data del servicio")
		 
		.setProperty(STATUS_VALIDATOR).jsonpath("$.Body.getTransferTransactionLogListRs.*.Status.statusCode")
		.setProperty(SEVERITY_VALIDATOR).jsonpath("$.Body.getTransferTransactionLogListRs.*.Status.severity")
		.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Capturo status y severity")
		.choice()				
			.when(PredicateBuilder.and(exchangeProperty(STATUS_VALIDATOR).isEqualTo("000"), exchangeProperty(SEVERITY_VALIDATOR).isEqualTo("Info")))
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: No se encontro cÃ³digo de error")
				.setProperty(Constants.RESPONSE_TRNINFOLIST).jsonpath(TRNINFO)
				.setProperty(Constants.RESPONSE_STATUS).jsonpath(STATUS)
				.setProperty(Constants.TRANSFERTRANSACTIONINFO).jsonpath("$.Body.getTransferTransactionLogListRs.TransferTransctionInfoList.TransferTransactionInfo")
				.setProperty(Constants.BENEFITNAME).jsonpath("$.Body.getTransferTransactionLogListRs.*.*.BenefitName")
				.setProperty(Constants.FROMPHONENUM).jsonpath("$.Body.getTransferTransactionLogListRs.*.*.FromPhoneNum")
				.setProperty(Constants.TOPHONENUM).jsonpath("$.Body.getTransferTransactionLogListRs.*.*.ToPhoneNum")
				.setProperty(Constants.STATUS_CODE).jsonpath(STATUS_CODE)
	            .setProperty(Constants.SERVER_STATUS_CODE).jsonpath(SERVER_STATUS_CODE)
	            .setProperty(Constants.SEVERITY).jsonpath(SEVERITY)
	            .setProperty(Constants.STATUS_DESC).jsonpath(STATUSDESC)
				.bean(ResponseHandler.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo el proceso")					
			.endChoice()	
			.when(PredicateBuilder.and(exchangeProperty(STATUS_VALIDATOR).isEqualTo("000"), exchangeProperty(SEVERITY_VALIDATOR).isEqualTo("Warning")))
				.log(LoggingLevel.DEBUG, logger, "Response Code: 422")
				.removeHeaders("*")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
				.inOnly(Constants.ROUTE_EXCEPTION_STATUS)
			.endChoice()	
			.when(PredicateBuilder.or(exchangeProperty(STATUS_VALIDATOR).convertToString().isEqualTo("120")))
				.log(LoggingLevel.DEBUG, logger, "Response Code: 400")
				.removeHeaders("*")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.inOnly(Constants.ROUTE_ERROR_120_150)
			.endChoice()	
			.when(PredicateBuilder.or(exchangeProperty(STATUS_VALIDATOR).convertToString().isEqualTo("150")))
				.log(LoggingLevel.DEBUG, logger, "Response Code: 500")
				.removeHeaders("*")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.inOnly(Constants.ROUTE_ERROR_120_150)
			.endChoice()
			.otherwise()
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: No ingreso por ningun choice")
				.throwException(Exception.class, "No se encontro data")
		.end();
		
		from(Constants.ROUTE_EXCEPTION_STATUS).routeId("EXCEPTION-STATUS").streamCaching()
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio ")
			.setProperty(Constants.RESPONSE_STATUS).jsonpath(STATUS)
			.setProperty(Constants.RESPONSE_TRNINFOLIST).jsonpath(TRNINFO)
			.setProperty(Constants.STATUS_CODE).jsonpath(STATUS_CODE)
            .setProperty(Constants.SERVER_STATUS_CODE).jsonpath(SERVER_STATUS_CODE)
            .setProperty(Constants.SEVERITY).jsonpath(SEVERITY)
            .setProperty(Constants.STATUS_DESC).jsonpath(STATUSDESC)
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Busqueda ${exchangeProperty.responseStatus}")				
			.throwException(DataException.class, "Error en info")
		.end()
			.end();

        from(Constants.ROUTE_ERROR_120_150).routeId("ROUTE_ERROR_120_150").streamCaching()
            .log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en el servicio ")
            .setProperty(Constants.RESPONSE_STATUS).jsonpath(STATUS)
            .setProperty(Constants.STATUS_CODE).jsonpath(STATUS_CODE)
            .setProperty(Constants.SERVER_STATUS_CODE).jsonpath(SERVER_STATUS_CODE)
            .setProperty(Constants.SEVERITY).jsonpath(SEVERITY)
            .setProperty(Constants.STATUS_DESC).jsonpath(STATUSDESC)
            .setProperty(Constants.ADDITIONAL_STATUS).jsonpath("$.Body.getTransferTransactionLogListRs.*.*.AdditionalStatus")
            .log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Busqueda ${exchangeProperty.responseStatus}")
            .bean(ResponseHandler.class, "responseError120150")
            .marshal().json(JsonLibrary.Jackson)
            .log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Filanizo \n ${body}")
            .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
        .end()
            .end();
		
	}
	
	
}
