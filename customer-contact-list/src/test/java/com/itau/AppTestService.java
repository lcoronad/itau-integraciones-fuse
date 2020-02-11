package com.itau;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.itau.util.Constant;

@RunWith(SpringRunner.class)
@Configuration
@MockEndpoints("log:*")
@UseAdviceWith
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppTestService {

	@BeanInject
	private CamelContext camelContext2;

	private Logger logger = LoggerFactory.getLogger(AppTestService.class);

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	private TestRestTemplate restTemplate;

	private final String URL = "http://localhost:";

	@Test
	public void aatestGet400() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		httpHeaders.add("requestUUID", "123456");
		httpHeaders.add("dateTime", "2019-12-30T18:33:12");
		httpHeaders.add("originatorName", "	");
		httpHeaders.add("originatorType", "47");
		httpHeaders.add("terminalId", "127.0.0.1");

		camelContext2.getRouteDefinition("ROUTE_GET_DATA").adviceWith(camelContext2, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// send the outgoing message to mock
				// Ok, funcionan los dos.
//	              weaveByToUri(Constant.ROUTE_CONSUMO_SOAP).replace().inOut("mock:routeB").removeHeaders("*").to("velocity:templates/response1.vm");
				interceptSendToEndpoint(Constant.ROUTE_CONSUMO_SOAP).skipSendToOriginalEndpoint()
						.setBody(constant("ok")).removeHeaders("*").to("velocity:templates/response1.vm");
			}
		});

		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(
				URL + serverPort+"/GET/1_123124/customers/v1/customers/getCustomerContactList?issuedIdentType=1&issuedIdentValue=1056",
				HttpMethod.GET, httpEntity, String.class);
		logger.info("Response:{}", response.getBody());

		assertThat(response.getStatusCodeValue()).isEqualByComparingTo(400);

	}

}
