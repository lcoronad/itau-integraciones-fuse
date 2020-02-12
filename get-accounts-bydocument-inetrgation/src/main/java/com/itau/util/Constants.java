package com.itau.util;

public class Constants {

	private Constants() {

	}

	public static final String ROUTE_CONSULTA_DATOS = "direct:consulta-soap";
	public static final String PROCESO_ID = "procesoId";
	public static final String[] HEADERS = { "custPermId", "custType", "restrictedDbInd", "restrictedCrInd",
			"lockStatusInd", "requestUUID", "dateTime", "originatorName", "originatorType", "terminalId","acctStatusCode" ,"additionalStatus" };
	
	public static final String ROUTE_CONSUMO_SOAP = "direct:consumo-soap";
	public static final String ROUTE_VALIDATOR_STATUS = "direct:validator-data";
	public static final String RESPONSE_STATUS = "responseStatus";
	public static final String ROUTE_EXCEPTION_STATUS = "direct:route-exception-status";
	public static final String ROUTE_EXCEPTION_STATUS_ERROR_BUS = "direct:route-exception-status-error-bus";
	public static final String RESPONSE_TRNINFOLIST = "responseTrnInfoList";
	public static final String RESPONSE_ACCOUNTS_CONTAC = "responseAccountsContact";
	public static final String MESSAGE = "message";
	
	
	
}
