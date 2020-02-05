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
package com.itau.esb.custacctvalidaterelation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class RestConsumer {

	private String serviceName;
	private String apiPath;
	private String apiTitle;
	private String apiVersion;
	private String apiDescription;
	private String itauService;
	private String itauServiceMethod;
	private String itauServiceContentType;
	private String healthcheck;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getApiTitle() {
		return apiTitle;
	}

	public void setApiTitle(String apiTitle) {
		this.apiTitle = apiTitle;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getItauService() {
		return itauService;
	}

	public void setItauService(String itauService) {
		this.itauService = itauService;
	}

	public String getItauServiceMethod() {
		return itauServiceMethod;
	}

	public void setItauServiceMethod(String itauServiceMethod) {
		this.itauServiceMethod = itauServiceMethod;
	}

	public String getItauServiceContentType() {
		return itauServiceContentType;
	}

	public void setItauServiceContentType(String itauServiceContentType) {
		this.itauServiceContentType = itauServiceContentType;
	}

	public String getApiDescription() {
		return apiDescription;
	}

	public void setApiDescription(String apiDescription) {
		this.apiDescription = apiDescription;
	}

	public String getHealthcheck() {
		return healthcheck;
	}

	public void setHealthcheck(String healthcheck) {
		this.healthcheck = healthcheck;
	}

}
