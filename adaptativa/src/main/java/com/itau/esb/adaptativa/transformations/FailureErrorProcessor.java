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
package com.itau.esb.adaptativa.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;

import com.itau.esb.adaptativa.model.AdditionalStatus;
import com.itau.esb.adaptativa.model.ResponseError;
import com.itau.esb.adaptativa.model.Status;

public class FailureErrorProcessor implements Processor {
	public void process(Exchange ex) throws Exception {
		Exception e = ex.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		ResponseError res = new ResponseError();
		Status status = new Status();
		AdditionalStatus as = new AdditionalStatus();
		List<AdditionalStatus> asList = new ArrayList<>();
		
		status.setServerStatusCode("");
		status.setSeverity("Error");
		status.setStatusCode(""+HttpStatus.SC_INTERNAL_SERVER_ERROR);
		status.setStatusDesc("Error Técnico");
		status.setAdditionalStatus(asList);
		
		as.setStatusCode(""+HttpStatus.SC_INTERNAL_SERVER_ERROR);
		as.setSeverity("Error");
		as.setServerStatusCode("");
		as.setStatusDesc(e.getMessage());
		
		asList.add(as);
		res.setStatus(status);		
		ex.getIn().setBody(res);
	}
}