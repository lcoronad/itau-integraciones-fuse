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
		
		@JsonProperty(value = "getCustomerContactListRs")
		public GetCustomerContactListRs getCustomerContactListRs = new GetCustomerContactListRs();
	}

	public static class GetCustomerContactListRs{
		
		@JsonProperty(value = "HeaderResponse")
		public JsonNode headerResponse = JsonNodeFactory.instance.objectNode();
		
		@JsonProperty(value = "ContactList")
		public ContactList contactList = new ContactList();
		
	}
	
	

	public static class ContactList{
		
		@JsonProperty(value = "Contact")
		public List<Contact> contacts = new ArrayList<>();
	}

	public static class Contact{
		
		private Contact(){
			
		}
		
		@JsonProperty(value = "contactType")
		public String contactType;
		
		@JsonProperty(value = "contactIdent")
		public String contactIdent;
		
		@JsonProperty(value = "PhoneNum")
		public JsonNode phoneNum = JsonNodeFactory.instance.objectNode();
		@JsonProperty(value = "PostAddr")
		public JsonNode postAddr = JsonNodeFactory.instance.objectNode();
		@JsonProperty(value = "Email")
		public JsonNode email = JsonNodeFactory.instance.objectNode();
		
	}

}




