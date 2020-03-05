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

import com.itau.dto.Request;
import com.itau.util.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Configuration
@MockEndpoints("log:*")
@UseAdviceWith
public class ErrorWarningTests {

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

        camelContext.getRouteDefinition("ROUTE_REQUEST_TRANSACTION_LOGS").adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(Constants.ROUTE_CONSUMO_SOAP).skipSendToOriginalEndpoint()
                        .setBody(constant("ok")).removeHeaders("*").to("velocity:templates/responseErrorWarning.vm");
            }
        });


        HttpEntity<Request> httpEntity = new HttpEntity<Request>(httpHeaders);
        ResponseEntity<String> r = restTemplate.exchange(URL + serverPort + "/support/v1/transfer_transaction_log?phone=3105552211&trnSubType=Enviar&maxRec=3", HttpMethod.GET,httpEntity,String.class);
        logger.info("Respuesta:{}",r.getBody());
        assertThat(r.getStatusCodeValue()).isEqualTo(422);

    }
}
