/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.itau.esb.modcustomer.transformations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.esb.modcustomer.interfaces.Headers;
import com.itau.esb.modcustomer.model.AdditionalStatus;
import com.itau.esb.modcustomer.model.ContactList;
import com.itau.esb.modcustomer.model.Response;
import com.itau.esb.modcustomer.model.Status;
import com.itau.esb.modcustomer.model.TrnInfoList;

@Component("transformationComponent")
public class TransformationComponent {

	ObjectMapper mapper = new ObjectMapper();

	public static String transformation(String body) {
		return body;
	}

	public void mappingSuccessResponse(Exchange ex) {
		Status status = new Status();
		status.setServerStatusCode(ex.getProperty(Headers.SERVER_STATUS_CODE, String.class));
		status.setSeverity(ex.getProperty(Headers.SEVERITY, String.class));
		status.setStatusCode(ex.getProperty(Headers.STATUS_CODE, String.class));
		status.setStatusDesc(ex.getProperty(Headers.STATUS_DESC, String.class));

		List<TrnInfoList> lista = new ArrayList<>();
//		List<AdditionalStatus> asList = new ArrayList<>();
		TrnInfoList list = new TrnInfoList();
		list.setTrnCode(ex.getProperty(Headers.TRN_CODE, String.class));
		list.setTrnSrc(ex.getProperty(Headers.TRN_SRC, String.class));
		lista.add(list);
		
//		AdditionalStatus as = new AdditionalStatus();
//		as.setServerStatusCode(ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class) != null
//				? ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class)
//				: "");
//		as.setSeverity(ex.getProperty(Headers.AD_SEVERITY, String.class) != null
//				? ex.getProperty(Headers.AD_SEVERITY, String.class)
//				: "");
//		as.setStatusCode(ex.getProperty(Headers.AD_STATUS_CODE, String.class) != null
//				? ex.getProperty(Headers.AD_STATUS_CODE, String.class)
//				: "");
//		as.setStatusDesc(ex.getProperty(Headers.AD_STATUS_DESC, String.class) != null
//				? ex.getProperty(Headers.AD_STATUS_DESC, String.class)
//				: "");
//
//		asList.add(as);
//		status.setAdditionalStatus(asList);
		Response res = new Response();
		res.setStatus(status);
		res.setTrnInfoList(lista);
		ex.getIn().setBody(res);
		setResponseStatusCode(ex, status);
	}

	private void setResponseStatusCode(Exchange ex, Status status) {
		// Set the response code according to response data
		if (status.getStatusCode().equals("000")) {
			if (status.getSeverity().equalsIgnoreCase("Info")) {
				// 200 ok
				ex.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_OK);
			} else if (status.getSeverity().equalsIgnoreCase("Warning")) {
				// 422
				ex.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_UNPROCESSABLE_ENTITY);
			}
		} else if (status.getStatusCode().equals("120")) {
			// 400
			ex.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_BAD_REQUEST);
		} else if (status.getStatusCode().equals("150")) {
			// 500
			ex.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void loadContactList(Exchange ex) throws IOException {
		Object cont = ex.getIn().getHeader("contacts", Object.class);
		String str = "{ \"Contact\":" + mapper.writeValueAsString(cont) + " }";
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		ContactList cl = mapper.readValue(str.getBytes(), ContactList.class);

		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		ObjectMapper objectMapper = new XmlMapper(module);

		ex.getIn().setBody(objectMapper.writeValueAsString(cl));
	}

	public String deleteEmptyNodes(String body) {
		body = body.replaceAll(" *<\\w*:\\w* *\\/>", "");
		body = body.replaceAll(" *<\\w*:\\w*>\\n* *<\\/\\w*:\\w*>", "");
		return body;
	}
}
