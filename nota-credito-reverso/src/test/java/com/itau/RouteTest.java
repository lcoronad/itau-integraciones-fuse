package com.itau;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.apache.camel.BeanInject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.itau.dto.Request;
import com.itau.util.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@MockEndpoints("log:*")
@UseAdviceWith
public class RouteTest {

	@BeanInject
	private CamelContext camelContext;

	@Test
	public void testRoutePrincipal() throws Exception {
		camelContext.start();
		camelContext.getRouteDefinition("ROUTE_CONSULTA_DATOS").adviceWith(camelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// send the outgoing message to mock

//				weaveByToUri(Constants.ROUTE_CONSUMO_SOAP).replace().inOut("mock:routeB").removeHeaders("*").to("velocity:templates/responseErrorTecnico.vm");
              interceptSendToEndpoint(Constants.ROUTE_CONSUMO_SOAP).skipSendToOriginalEndpoint().setBody(constant("OK"));
              interceptSendToEndpoint(Constants.ROUTE_VALIDATOR_STATUS).skipSendToOriginalEndpoint().setBody(constant("OK"));
			}
		});
		ProducerTemplate template = camelContext.createProducerTemplate();
		Request dto = new Request();
		dto.accounRecordRev.acctType = "AHO";
		dto.accounRecordRev.publiCurAmt.amt = new BigDecimal(100);
		dto.accounRecordRev.publiCurAmt.curCode = "CUP";
		Object obj = template.requestBodyAndHeader(Constants.ROUTE_CONSULTA_DATOS, dto, "acctId", "651016053");
		assertNotNull(obj);
	}

}
