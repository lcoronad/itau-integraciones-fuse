package com.itau.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itau.dto.Response;
import com.itau.dto.ResponseList;
import com.itau.dto.Status;
import com.itau.exception.JsonMapperException;
import com.itau.exception.MyException;
import com.itau.util.Constant;

import io.swagger.annotations.ApiModelProperty;

@Component
public class ResponseHandler {

	@Handler
	@ApiModelProperty(notes = "Parametro De Salida")
	public Response handler(Exchange e) throws MyException, JsonMapperException {

		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dto.status = new Status();
			dto.status.AdditionalStatus = null;
			dto.status.serverStatusCode = Objects.nonNull(e.getProperty(Constant.SERVER_STATUS_CODE, String.class))
					? e.getProperty(Constant.SERVER_STATUS_CODE, String.class)
					: "";
			dto.status.severity = Objects.nonNull(e.getProperty(Constant.SEVERITY, String.class))
					? e.getProperty(Constant.SEVERITY, String.class)
					: "";
			dto.status.statusCode = Objects.nonNull(e.getProperty(Constant.STATUS_CODE, String.class))
					? e.getProperty(Constant.STATUS_CODE, String.class)
					: "";
			dto.status.statusDesc = Objects.nonNull(e.getProperty(Constant.STATUS_DESC, String.class))
					? e.getProperty(Constant.STATUS_DESC, String.class)
					: "";
			dto.trnInfoList = Objects.nonNull(e.getProperty(Constant.RESPONSE_TRNINFOLIST, String.class))
					? mapper.readTree(e.getProperty(Constant.RESPONSE_TRNINFOLIST, String.class))
					: JsonNodeFactory.instance.objectNode();
			/*
			 * obj = mapper.valueToTree(dto.status); obj.remove("AdditionalStatus");
			 * dto.status = mapper.readValue(obj.asText(), Status.class);
			 */
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}
		return dto;
	}

	public ObjectNode responseError120150(Exchange e) throws MyException, JsonProcessingException, IOException {

		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode obj = null;
		dto.status = new Status();
		if (e.getProperty(Constant.ADDITIONAL_STATUS) != null) {
			JsonNode node = mapper.convertValue(e.getProperty(Constant.ADDITIONAL_STATUS), JsonNode.class);
			List<Object> lista = new ArrayList<Object>();
			lista.add(node);
			dto.status.AdditionalStatus = Objects.nonNull(lista) ? mapper.convertValue(lista, JsonNode.class)
					: JsonNodeFactory.instance.objectNode();
			dto.status.serverStatusCode = null;

			dto.status.severity = "Error";
			dto.status.statusCode = "" + HttpStatus.SC_INTERNAL_SERVER_ERROR;
			dto.status.statusDesc = "Error Tecnico";
			obj = mapper.valueToTree(dto);
			obj.remove("TrnInfoList");
		} else {
			JsonNode node = mapper.convertValue(e.getProperty(Constant.RESPONSE_TRNINFOLIST), JsonNode.class);
			List<Object> lista = new ArrayList<Object>();
			lista.add(node);
			dto.trnInfoList = mapper.convertValue(lista, JsonNode.class);
			dto.status.serverStatusCode = e.getProperty(Constant.SERVER_STATUS_CODE) + "";
			dto.status.severity = e.getProperty(Constant.SEVERITY) + "";
			dto.status.statusCode = e.getProperty(Constant.STATUS_CODE) + "";
			dto.status.statusDesc = e.getProperty(Constant.STATUS_DESC) + "";
			dto.status.AdditionalStatus = null;
			obj = mapper.valueToTree(dto);
		}

		return obj;
	}

	public ResponseList responseOK(Exchange e) throws MyException {
		ResponseList dto = new ResponseList();
		ObjectMapper mapper = new ObjectMapper();

		try {
			Object str = e.getProperty(Constant.RESPONSE_STATUS, List.class).get(0);
			dto.status = mapper.convertValue(str, Status.class);
			dto.trnInfoList = mapper.readTree(e.getProperty(Constant.RESPONSE_TRNINFOLIST, String.class));
			dto.listContacts = mapper.readTree(e.getProperty(Constant.RESPONSE_LIST_CONTACTS, String.class));
//			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new MyException(e2);
		}

		return dto;
	}
}
