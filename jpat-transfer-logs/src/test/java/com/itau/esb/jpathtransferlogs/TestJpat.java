package com.itau.esb.jpathtransferlogs;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = TestJpat.class)
@UseAdviceWith
@SpringBootApplication
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("direct:finalStep")
public class TestJpat {

	@Autowired
	private ProducerTemplate template;

	@Autowired
	private CamelContext ctx;

	@EndpointInject(uri = "mock:direct:finalStep")
	MockEndpoint mockFinalStep;

	private final static String RQ_SUCCESS_MSG = "src/test/resources/in/rq_ok.txt";
	private final static String RS_SUCCESS_MSG = "src/test/resources/in/rs_ok.txt";
	private final static String RS_FAIL_MSG = "src/test/resources/in/rs_fail.txt";

	private final static String RS_ITAU_SUCCESS_MSG = "src/test/resources/itauService/responseOk.txt";
	private final static String RS_ITAU_FAIL_MSG = "src/test/resources/itauService/responseFail.txt";

	@Test
	public void Ok() throws Exception {
		final RouteDefinition routeTx = ctx.getRouteDefinition("jpattransferlogs_transformation");
		routeTx.adviceWith(ctx, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				weaveById("firstStep").after().process(e -> {
					// load headers
					e.getIn().setHeader("requestUUID" , "d69f0dc3-209d-4de9-bf5e-1758c2664010");
					e.getIn().setHeader("datetime" , "2019-12-10T10:29:25");
					e.getIn().setHeader("originatorName" , "Portal");
					e.getIn().setHeader("originatorType" , "10");
					e.getIn().setHeader("terminalID" , "172.22.91.111");
					e.getIn().setHeader("Content-Type", "application/json");
				});

//				weaveById("consumeItauService").replace().process(e -> {
//					e.getIn().setBody(new File(RS_ITAU_SUCCESS_MSG));
//					e.getIn().setHeader("CamelHttpResponseCode", 200);
//				});
			}
		});

		ctx.start();
		assertTrue(ctx.getStatus().isStarted());

		template.requestBody("direct:transformationRoute", new File(RQ_SUCCESS_MSG));
		mockFinalStep.expectedMessageCount(1);
		mockFinalStep.assertIsSatisfied();

		assertTrue(mockFinalStep.getExchanges().get(0).getIn().getBody(String.class)
				.equals(Files.toString(new File(RS_SUCCESS_MSG), Charsets.UTF_8)));
	}

//	@Test
//	public void TestErrorAddr() throws Exception {
//		final RouteDefinition routeTx = context.getRouteDefinition("jpattransferlogs_transformation");
//		routeTx.adviceWith(context, new AdviceWithRouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				weaveById("firstStep").after().process(e -> {
//					// load headers
//					e.getIn().setHeader("requestUUID", "d69f0dc3-209d-4de9-bf5e-1758c2664010");
//					e.getIn().setHeader("datetime", "2019-12-10T10:29:25");
//					e.getIn().setHeader("originatorType", "141");
//					e.getIn().setHeader("terminalID", "172.22.91.111");
//					e.getIn().setHeader("Content-Type", "application/json");
//					e.getIn().setHeader("id_data", "1022995281");
//				});
//			}
//		});
//
//		context.start();
//		assertTrue(context.getStatus().isStarted());
//
//		template.requestBody("direct:transformationRoute", new File(RQ_SUCCESS_MSG));
//		mockFinalStep.expectedMessageCount(1);
//		mockFinalStep.assertIsSatisfied();
//		assertTrue(mockFinalStep.getExchanges().get(0).getIn().getBody(String.class)
//				.contains("Debe proporcionar los datos de ident"));
//	}
//
//	@Test
//	public void testNotRequestUUID() throws Exception {
//		final RouteDefinition routeTx = context.getRouteDefinition("jpattransferlogs_transformation");
//		routeTx.adviceWith(context, new AdviceWithRouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				weaveById("firstStep").after().process(e -> {
//					// load headers
////					e.getIn().setHeader("requestUUID", "d69f0dc3-209d-4de9-bf5e-1758c2664010");
//					e.getIn().setHeader("datetime", "2019-12-10T10:29:25");
//					e.getIn().setHeader("originatorType", "141");
//					e.getIn().setHeader("terminalID", "172.22.91.111");
//					e.getIn().setHeader("Content-Type", "application/json");
//					e.getIn().setHeader("id_data", "1022995281_1");
//				});
//
//				weaveById("consumeItauService").replace().process(e -> {
//					e.getIn().setBody(new File(RS_ITAU_FAIL_MSG));
//					e.getIn().setHeader("CamelHttpResponseCode", 200);
//				});
//			}
//		});
//
//		context.start();
//		assertTrue(context.getStatus().isStarted());
//
//		template.requestBody("direct:transformationRoute", new File(RQ_SUCCESS_MSG));
//		mockFinalStep.expectedMessageCount(1);
//		mockFinalStep.assertIsSatisfied();
//		assertTrue(mockFinalStep.getExchanges().get(0).getIn().getBody(String.class)
//				.equals(Files.toString(new File(RS_FAIL_MSG), Charsets.UTF_8)));
//	}
}
