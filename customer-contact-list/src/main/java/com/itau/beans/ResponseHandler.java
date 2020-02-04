package com.itau.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.itau.dto.Response;
import com.itau.dto.ResponseList;
import com.itau.exception.MyException;
import com.itau.util.Constant;

import io.swagger.annotations.ApiModelProperty;

@Component
public class ResponseHandler {

	@Handler
	@ApiModelProperty(notes = "Parametro De Salida")
	public Response handler(Exchange e) throws MyException {
		
		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dto.status = mapper.readTree(e.getProperty(Constant.RESPONSE_STATUS, String.class));
			dto.trnInfoList = mapper.readTree(e.getProperty(Constant.RESPONSE_TRNINFOLIST, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new MyException(e2);
		}
		
		
		return dto;
	}
	
	public ResponseList responseOK(Exchange e)  throws MyException {
		ResponseList dto = new ResponseList();
		ObjectMapper mapper = new ObjectMapper();
		try {
			dto.status = mapper.readTree(e.getProperty(Constant.RESPONSE_STATUS, String.class));
			dto.trnInfoList = mapper.readTree(e.getProperty(Constant.RESPONSE_TRNINFOLIST, String.class));
			dto.listContacts = mapper.readTree(e.getProperty(Constant.RESPONSE_LIST_CONTACTS, String.class));
			dto.status = dto.status.get(0) == null ? JsonNodeFactory.instance.objectNode() : dto.status.get(0);
		} catch (Exception e2) {
			throw new MyException(e2);
		}
	
		return dto;
	}
}
