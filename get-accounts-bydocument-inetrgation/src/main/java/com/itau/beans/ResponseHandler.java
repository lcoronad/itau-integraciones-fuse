package com.itau.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itau.dto.Response;
import com.itau.dto.ResponseList;
import com.itau.exception.JsonMapperException;
import com.itau.util.Constants;
import com.jayway.jsonpath.internal.filter.ValueNode.JsonNode;

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

	public ObjectNode error(Exchange e) throws JsonMapperException {

		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode obj = null;
		try {
			dto.status = mapper.readTree(e.getProperty(Constants.RESPONSE_STATUS, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
			obj = mapper.valueToTree(dto);
			obj.remove("TrnInfoList");
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}

		return obj;
	}

	public ResponseList responseOK(Exchange e) throws JsonMapperException {
		ResponseList dto = new ResponseList();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dto.status = mapper.readTree(e.getProperty(Constants.RESPONSE_STATUS, String.class));
			dto.trnInfoList = mapper.readTree(e.getProperty(Constants.RESPONSE_TRNINFOLIST, String.class));
			dto.accountsDetailList = mapper.readTree(e.getProperty(Constants.RESPONSE_ACCOUNTS_CONTAC, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}

		return dto;
	}

	public ResponseList responseErrorValidate(Exchange e) throws JsonMapperException {
		ResponseList dto = new ResponseList();
		ObjectMapper mapper = new ObjectMapper();
		try {

			String jsontrnInfoList = "{ \"trnCode\" : \"\", \"trnSrc\" : \"\"  } ";
			String jsonstatus = "{ \"statusCode\" : \"" + e.getProperty(Constants.RESPONSE_STATUS, String.class)
					+ "\", \"serverStatusCode\" : \"\", \"severity\" : \"\", \"statusDesc\" : "
					+ e.getProperty(Constants.RESPONSE_TRNINFOLIST, String.class) + " } ";
			ObjectMapper objectMapper = new ObjectMapper();

			dto.trnInfoList = objectMapper.readTree(jsontrnInfoList);
			dto.status = mapper.readTree(jsonstatus);
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}

		return dto;
	}
}
