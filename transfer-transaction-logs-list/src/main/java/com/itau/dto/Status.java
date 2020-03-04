package com.itau.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(Include.NON_NULL)
public class Status {
	
	public String statusCode;
	
	public String serverStatusCode;
	
	public String severity;
	
	public String statusDesc;
	
	@JsonProperty(value = "AdditionalStatus")
    @ApiModelProperty(dataType = "Object")
    public transient JsonNode AdditionalStatus = JsonNodeFactory.instance.objectNode();
	
	
	
	

}
