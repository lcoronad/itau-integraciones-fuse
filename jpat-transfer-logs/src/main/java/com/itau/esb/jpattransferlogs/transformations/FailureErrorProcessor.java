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
package com.itau.esb.jpattransferlogs.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;

import com.itau.esb.jpattransferlogs.interfaces.Headers;
import com.itau.esb.jpattransferlogs.model.AdditionalStatus;
import com.itau.esb.jpattransferlogs.model.Response;
import com.itau.esb.jpattransferlogs.model.Status;

public class FailureErrorProcessor implements Processor {
	public void process(Exchange ex) throws Exception {
Exception e = ex.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		
		Status status = new Status();
		status.setStatusCode(""+HttpStatus.SC_INTERNAL_SERVER_ERROR);
		status.setServerStatusCode("");
		status.setSeverity("Error");
		status.setStatusDesc("Error Tecnico");

		List<AdditionalStatus> asList = new ArrayList<>();
		AdditionalStatus as = new AdditionalStatus();
		as.setServerStatusCode(ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class) != null
				? ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class)
				: "");
		as.setSeverity(ex.getProperty(Headers.AD_SEVERITY, String.class) != null
				? ex.getProperty(Headers.AD_SEVERITY, String.class)
				: "Error");
		as.setStatusCode(ex.getProperty(Headers.AD_STATUS_CODE, String.class) != null
				? ex.getProperty(Headers.AD_STATUS_CODE, String.class)
				: ""+HttpStatus.SC_INTERNAL_SERVER_ERROR);
		as.setStatusDesc(ex.getProperty(Headers.AD_STATUS_DESC, String.class) != null
				? ex.getProperty(Headers.AD_STATUS_DESC, String.class)
				: e.getMessage());

		status.setAdditionalStatus(asList);
		asList.add(as);
		Response res = new Response();
		res.setStatus(status);
		ex.getIn().setBody(res);

	}
}