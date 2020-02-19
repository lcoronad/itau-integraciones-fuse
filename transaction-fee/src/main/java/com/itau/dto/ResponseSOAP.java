package com.itau.dto;

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

		@JsonProperty(value = "getTransactionFeeRs")
		public GetTransactionFeeRs getTransactionFeeRs = new GetTransactionFeeRs();
	}

	public static class GetTransactionFeeRs {

		@JsonProperty(value = "HeaderResponse")
		public JsonNode headerResponse = JsonNodeFactory.instance.objectNode();

		@JsonProperty(value = "TransactionFee")
		public JsonNode transactionFee = JsonNodeFactory.instance.objectNode();
	}

}
