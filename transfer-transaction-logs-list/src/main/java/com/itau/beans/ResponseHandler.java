package com.itau.beans;

import java.util.Objects;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itau.dto.Response;
import com.itau.dto.Status;
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
			dto.status = new Status();
			dto.status.additionalStatus = null;
			dto.status.serverStatusCode = Objects.nonNull(e.getProperty(Constants.SERVER_STATUS_CODE, String.class))
					? e.getProperty(Constants.SERVER_STATUS_CODE, String.class)
					: "";
			dto.status.severity = Objects.nonNull(e.getProperty(Constants.SEVERITY, String.class))
					? e.getProperty(Constants.SEVERITY, String.class)
					: "";
			dto.status.statusCode = Objects.nonNull(e.getProperty(Constants.STATUS_CODE, String.class))
					? e.getProperty(Constants.STATUS_CODE, String.class)
					: "";
			dto.status.statusDesc = Objects.nonNull(e.getProperty(Constants.STATUS_DESC, String.class))
					? e.getProperty(Constants.STATUS_DESC, String.class)
					: "";
			dto.trnInfoList = mapper.readTree(e.getProperty(Constants.RESPONSE_TRNINFOLIST, String.class));
			if (Objects.nonNull(e.getProperty(Constants.TRANSFERTRANSACTIONINFO, String.class))) {
				dto.trnList = mapper.readTree(e.getProperty(Constants.TRANSFERTRANSACTIONINFO, String.class));
				dto.trnList = dto.trnList == null ? null : dto.trnList.get(0);

			} else {
				dto.trnList = null;
			}

		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}
		return dto;
	}

	public ObjectNode responseError120150(Exchange e) throws JsonMapperException {

		Response dto = new Response();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode obj = null;
		try {
			dto.status = new Status();
			dto.status.additionalStatus = mapper.readTree(e.getProperty(Constants.ADDITIONAL_STATUS, String.class));
			dto.status.serverStatusCode = Objects.nonNull(e.getProperty(Constants.SERVER_STATUS_CODE, String.class))
					? e.getProperty(Constants.SERVER_STATUS_CODE, String.class)
					: "";
			dto.status.severity = Objects.nonNull(e.getProperty(Constants.SEVERITY, String.class))
					? e.getProperty(Constants.SEVERITY, String.class)
					: "";
			dto.status.statusCode = Objects.nonNull(e.getProperty(Constants.STATUS_CODE, String.class))
					? e.getProperty(Constants.STATUS_CODE, String.class)
					: "";
			dto.status.statusDesc = Objects.nonNull(e.getProperty(Constants.STATUS_DESC, String.class))
					? e.getProperty(Constants.STATUS_DESC, String.class)
					: "";
			
			obj = mapper.valueToTree(dto);
			obj.remove("TrnInfoList");
		} catch (Exception e2) {
			throw new JsonMapperException(e2);
		}
		return obj;
	}

}
