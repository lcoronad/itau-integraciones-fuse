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
package com.itau.esb.itausoap2json.transformations;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.esb.itausoap2json.interfaces.Headers;
import com.itau.esb.itausoap2json.model.ContactList;
import com.itau.esb.itausoap2json.model.Response;
import com.itau.esb.itausoap2json.model.Status;
import com.itau.esb.itausoap2json.model.TrnInfoList;

@Component("transformationComponent")
public class TransformationComponent {

	ObjectMapper mapper = new ObjectMapper();

	public static String transformation(String body) {
		return body;
	}

	public void mappingSuccessResponse(Exchange ex) throws JsonProcessingException {
		Status status = new Status();
		status.setServerStatusCode(ex.getProperty(Headers.SERVER_STATUS_CODE, String.class));
		status.setSeverity(ex.getProperty(Headers.SEVERITY, String.class));
		status.setStatusCode(ex.getProperty(Headers.STATUS_CODE, String.class));
		status.setStatusDesc(ex.getProperty(Headers.STATUS_DESC, String.class));

		TrnInfoList list = new TrnInfoList();
		list.setTrnCode(ex.getProperty(Headers.TRN_CODE, String.class));
		list.setTrnSrc(ex.getProperty(Headers.TRN_SRC, String.class));

		Response res = new Response();
		res.setStatus(status);
		res.setTrnInfoList(list);
		ex.getIn().setBody(res);
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
