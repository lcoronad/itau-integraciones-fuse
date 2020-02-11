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
package com.itau.esb.custacctvalidaterelation.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.esb.custacctvalidaterelation.interfaces.Headers;
import com.itau.esb.custacctvalidaterelation.model.AcctKey;
import com.itau.esb.custacctvalidaterelation.model.Response;
import com.itau.esb.custacctvalidaterelation.model.Status;
import com.itau.esb.custacctvalidaterelation.model.TrnInfoList;

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
		TrnInfoList list = new TrnInfoList();
		list.setTrnCode(ex.getProperty(Headers.TRN_CODE, String.class));
		list.setTrnSrc(ex.getProperty(Headers.TRN_SRC, String.class));

		AcctKey key = new AcctKey();
		key.setAcctType(ex.getIn().getHeader("acctType", String.class));

		lista.add(list);
		Response res = new Response();
		res.setStatus(status);
		res.setTrnInfoList(lista);
		ex.getIn().setBody(res);
		setResponseStatusCode(ex, status);
		if(ex.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).equals(200)) {
			res.setAcctKey(key);
		}
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
}
