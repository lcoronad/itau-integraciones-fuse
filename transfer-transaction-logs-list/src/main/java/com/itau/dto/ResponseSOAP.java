package com.itau.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ResponseSOAP {

	@JsonProperty(value = "Header")
	public JsonNode header = JsonNodeFactory.instance.objectNode();
	@JsonProperty(value = "Body")
	public Body bodyObject;

	// Getter Methods

	public static class Body {

		@JsonProperty(value = "getTransferTransactionLogListRs")
		public GetTransferTransactionLogListRs getTransactionFeeRs = new GetTransferTransactionLogListRs();
	}

	public static class GetTransferTransactionLogListRs {

		@JsonProperty(value = "HeaderResponse")
		public JsonNode headerResponse = JsonNodeFactory.instance.objectNode();
		
		@JsonProperty(value = "TransferTransctionInfoList")
		public TransferTransctionInfoList listTransferTransctionInfoList = new TransferTransctionInfoList();
		
	}
	
	public static class TransferTransctionInfoList{
		
		
		@JsonProperty(value = "TransferTransactionInfo")
		public List<JsonNode> transactionFee = new ArrayList<>();
	}

}
