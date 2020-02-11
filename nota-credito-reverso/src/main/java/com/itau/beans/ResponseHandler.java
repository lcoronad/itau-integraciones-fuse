package com.itau.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.itau.dto.Response;

import com.itau.exception.JsonMapperException;
import com.itau.util.Constants;

import io.swagger.annotations.ApiModelProperty;

@Component
public class ResponseHandler {

	@Handler
	@ApiModelProperty(notes = "Parametro De Salida")
	public Response handler(Exchange e) throws JsonMapperException {

		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dto.status = mapper.readTree(e.getProperty(Constants.RESPONSE_STATUS, String.class));
			dto.trnInfoList = mapper.readTree(e.getProperty(Constants.RESPONSE_TRNINFOLIST, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}

		return dto;
	}

	public Response error(Exchange e) throws JsonMapperException {		
		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		
		String status = "\"Status\": {\r\n" + 
				"    \"statusCode\": \"000\",\r\n" + 
				"    \"serverStatusCode\": \"0\",\r\n" + 
				"    \"severity\": \"Info\",\r\n" + 
				"    \"statusDesc\": \"0\"    \r\n" + 
				"}";
		
		try {
			dto.status = mapper.readTree(e.getProperty(Constants.RESPONSE_STATUS, String.class));
			dto.trnInfoList = mapper.readTree(e.getProperty(Constants.RESPONSE_TRNINFOLIST, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}

		return dto;
	}

}
