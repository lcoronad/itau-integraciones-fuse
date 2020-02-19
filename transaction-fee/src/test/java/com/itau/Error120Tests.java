package com.itau;

import com.itau.dto.Request;
import com.itau.util.Constants;
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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@MockEndpoints("log:*")
@UseAdviceWith
public class Error120Tests {

    @Autowired
    private TestRestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    @BeanInject
    private CamelContext camelContext;


    @Value("${server.port}")
    private String serverPort;

    private final String URL = "http://localhost:";

    @Test
    public void responseWarning() throws Exception{

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.add("requestUUID", "fdeb335c-b074-4be4-9aa2-7b7f7674df72");
        httpHeaders.add("dateTime", "2020-01-23T11:37:14");
        httpHeaders.add("originatorName", "App Unica");
        httpHeaders.add("originatorType", "141");
        httpHeaders.add("terminalId", "127.0.0.1");

        camelContext.getRouteDefinition("ROUTE_REQUEST_TRANSACTION_FEE").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(Constants.ROUTE_CONSUMO_SOAP).skipSendToOriginalEndpoint()
                        .setBody(constant("ok")).removeHeaders("*").to("velocity:templates/responseError120.vm");
            }
        });

        Request dto = new Request();
        dto.ownerInd = true;
        dto.trnCategory = "0";
        dto.trnCode = "850";
        dto.referenceInfo.reference = "TRANSFER-850";
        dto.fromAcct.accKeys.acctId = "731029642";
        dto.fromAcct.accKeys.acctType = "AHO";
        dto.fromAcct.accKeys.acctSubType = "211";
        dto.fromAcct.custID.custPermId = "79805128";
        dto.fromAcct.custID.custType = "1";
        dto.fromAcct.addInfo = "";
        dto.toAcct.accKeys.acctId = "731029642";
        dto.toAcct.accKeys.acctType = "AHO";
        dto.toAcct.accKeys.acctSubType = "211";
        dto.toAcct.custID.custPermId = "79805128";
        dto.toAcct.custID.custType = "1";
        dto.toAcct.addInfo = "";
        dto.bankId = "014";
        dto.curAmt.amt = 1000L;
        dto.curAmt.curCode = "COP";
        dto.effDt = "2020-02-07T20:36:19.970";
        dto.terminalType = "Cajero Propio";
        HttpEntity<Request> httpEntity = new HttpEntity<Request>(dto, httpHeaders);
        ResponseEntity<String> r = restTemplate.exchange(URL + serverPort + "/support/v1/transaction_fee", HttpMethod.POST,httpEntity,String.class);
        logger.info("Respuesta:{}",r.getBody());
        assertThat(r.getStatusCodeValue()).isEqualTo(400);

    }
}
