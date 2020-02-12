package com.itau.esb.adaptativa;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestXML {

	@Test
	public void Test1() throws JsonParseException, JsonMappingException, IOException {
		assertTrue(true);
	}

	@Test
	public void Test2() throws JsonParseException, JsonMappingException, IOException {
		assertTrue(true);
	}

	@Test
	public void test() throws Exception {
		String input = "<soapenv:Envelope 	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
				+ "	<soap:Header 	xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
				+ "	</soap:Header>\r\n" + "	<soapenv:Body>\r\n"
				+ "		<sch2:modCustomerContactListRq 	xmlns:log=\"http://itau.com.co/services/common/auditory/v2/schemas\" xmlns:sch2=\"http://itau.com.co/services/customer/customer/v1/schemas\" xmlns:sch=\"http://itau.com.co/commoncannonical/v3/schemas\" xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:pc=\"http://xmlns.oracle.com/pcbpel/\" xmlns:ns0=\"http://itau.com.co/services/common/auditory/v2/schemas\" xmlns:tns=\"http://xmlns.oracle.com/pcbpel/adapter/jms/Auditory/RegisterAuditory/JmsAuditRequestTRX\">\r\n"
				+ "			<sch:HeaderRequest>\r\n" + "				<sch:MessageHeader>\r\n"
				+ "					<sch:MessageKey>\r\n"
				+ "						<sch:requestUUID>d69f0dc3-209d-4de9-bf5e-1758c2664010</sch:requestUUID>\r\n"
				+ "					</sch:MessageKey>\r\n" + "					<sch:MessageInfo>\r\n"
				+ "						<sch:dateTime>2019-12-1T10:29:25</sch:dateTime>\r\n"
				+ "						<sch:systemId>141</sch:systemId>\r\n"
				+ "						<sch:originatorName>APP Unica</sch:originatorName>\r\n"
				+ "						<sch:originatorType>141</sch:originatorType>\r\n"
				+ "						<sch:terminalId>172.22.91.111</sch:terminalId>\r\n"
				+ "					</sch:MessageInfo>\r\n" + "				</sch:MessageHeader>\r\n"
				+ "				<sch:User>\r\n" + "					<sch:userName>OSBPORTA</sch:userName>\r\n"
				+ "					<sch:employeeIdentlNum>2871</sch:employeeIdentlNum>\r\n"
				+ "				</sch:User>\r\n" + "			</sch:HeaderRequest>\r\n"
				+ "			<sch:IssuedIdent>\r\n" + "				<sch:issuedIdentType>1</sch:issuedIdentType>\r\n"
				+ "				<sch:issuedIdentValue>1022995281</sch:issuedIdentValue>\r\n"
				+ "			</sch:IssuedIdent>\r\n"
				+ "			<sch:ContactList><sch:Contact><sch:contactType>1</sch:contactType><sch:contactIdent>1</sch:contactIdent><sch:Email><sch:emailAddr/></sch:Email><sch:PhoneNum><sch:phone>3145612101</sch:phone><sch:phoneType>Mobile</sch:phoneType><sch:phoneDesc/></sch:PhoneNum></sch:Contact><sch:Contact><sch:contactType>2</sch:contactType><sch:contactIdent>2</sch:contactIdent><sch:Email><sch:emailAddr>a@a.xom</sch:emailAddr></sch:Email><sch:PhoneNum><sch:phone/><sch:phoneType/><sch:phoneDesc/></sch:PhoneNum></sch:Contact></sch:ContactList>\r\n"
				+ "		</sch2:modCustomerContactListRq>\r\n" + "	</soapenv:Body>\r\n" + "</soapenv:Envelope>";
		System.out.println(input + "\n\n\n");
		input = input.replaceAll(" *<\\w*:\\w* *\\/>", "");
		System.out.println(input + "\n\n\n");
		input = input.replaceAll(" *<\\w*:\\w*>\\n* *<\\/\\w*:\\w*>", "");
		System.out.println(input);
		assertTrue(true);
	}
}
