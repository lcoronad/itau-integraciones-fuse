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

		@JsonProperty(value = "doDebitAccountRs")
		public DebitAccountsRs debitAccountsRs = new DebitAccountsRs();
	}

	public static class DebitAccountsRs {

		@JsonProperty(value = "HeaderResponse")
		public JsonNode headerResponse = JsonNodeFactory.instance.objectNode();

	}

}
