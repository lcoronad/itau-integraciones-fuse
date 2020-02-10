package com.itau;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;

import org.apache.camel.BeanInject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.itau.dto.Request;
import com.itau.util.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@MockEndpoints("log:*")
@UseAdviceWith
public class ServiceTest {
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private Logger logger = LoggerFactory.getLogger(ServiceTest.class);
	
	@BeanInject
	private CamelContext camelContext;
	

	@Value("${server.port}")
	private String serverPort;
	
	private final String URL = "http://localhost:";
	
	@Test
	public void responseOK() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		httpHeaders.add("requestUUID", "fdeb335c-b074-4be4-9aa2-7b7f7674df72");
		httpHeaders.add("dateTime", "2020-01-23T11:37:14");
		httpHeaders.add("originatorName", "App Unica");
		httpHeaders.add("originatorType", "141");
		httpHeaders.add("terminalId", "127.0.0.1");

		camelContext.getRouteDefinition("ROUTE_CONSULTA_DATOS").adviceWith(camelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// send the outgoing message to mock
				// Ok, funcionan los dos.
//	              weaveByToUri(Constant.ROUTE_CONSUMO_SOAP).replace().inOut("mock:routeB").removeHeaders("*").to("velocity:templates/response1.vm");
				interceptSendToEndpoint(Constants.ROUTE_CONSUMO_SOAP).skipSendToOriginalEndpoint()
						.setBody(constant("ok")).removeHeaders("*").to("velocity:templates/responseOK.vm");
			}
		});

		Request dto = new Request();
		dto.accounRecordRev.acctType = "AHO";
		dto.accounRecordRev.publiCurAmt.amt = new BigDecimal(100);
		dto.accounRecordRev.publiCurAmt.curCode = "CUP";
		HttpEntity<Request> httpEntity = new HttpEntity<Request>(dto, httpHeaders);
		ResponseEntity<String> r = restTemplate.exchange(URL + serverPort + "/accounts/v1/accounts/123456/credit_transactions/234234", HttpMethod.POST,httpEntity,String.class);
		logger.info("Respuesta:{}",r.getBody());
		assertThat(r.getStatusCodeValue()).isEqualTo(200);
	}
	

}
