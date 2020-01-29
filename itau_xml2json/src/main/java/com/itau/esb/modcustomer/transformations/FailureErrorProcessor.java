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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;

import com.itau.esb.modcustomer.interfaces.Headers;
import com.itau.esb.modcustomer.model.AdditionalStatus;
import com.itau.esb.modcustomer.model.Response;
import com.itau.esb.modcustomer.model.Status;
import com.itau.esb.modcustomer.model.TrnInfoList;

public class FailureErrorProcessor implements Processor {
	public void process(Exchange ex) throws Exception {
		Exception e = ex.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		Status status = new Status();
		status.setServerStatusCode("");
		status.setSeverity("");
		status.setStatusCode("" + HttpStatus.SC_INTERNAL_SERVER_ERROR);
		status.setStatusDesc(e.getMessage());

		TrnInfoList list = new TrnInfoList();
		list.setTrnCode("");
		list.setTrnSrc("");

		AdditionalStatus as = new AdditionalStatus();
		as.setServerStatusCode(ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class) != null
				? ex.getProperty(Headers.AD_SERVER_STATUS_CODE, String.class)
				: "");
		as.setSeverity(ex.getProperty(Headers.AD_SEVERITY, String.class) != null
				? ex.getProperty(Headers.AD_SEVERITY, String.class)
				: "");
		as.setStatusCode(ex.getProperty(Headers.AD_STATUS_CODE, String.class) != null
				? ex.getProperty(Headers.AD_STATUS_CODE, String.class)
				: "");
		as.setStatusDesc(ex.getProperty(Headers.AD_STATUS_DESC, String.class) != null
				? ex.getProperty(Headers.AD_STATUS_DESC, String.class)
				: "");

		Response res = new Response();
		res.setStatus(status);
		res.setTrnInfoList(list);
		res.setAdditionalStatus(as);
		ex.getIn().setBody(res);

	}
}