package com.itau.beans;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

	public ObjectNode responseError120150(Exchange e) throws Exception {
		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode obj = null;
		dto.status = mapper.readTree(e.getProperty(Constants.RESPONSE_STATUS, String.class));
		dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		obj = mapper.valueToTree(dto);
		obj.remove("TrnInfoList");
		return obj;
	}

}
