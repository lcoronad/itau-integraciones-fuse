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
package com.itau.esb.adaptativa.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class RestConsumer {

	private String evaluarRiesgoTransaccion;
	private String autenticarClienteTransaccion;
	private String apiPath;
	private String apiTitle;
	private String apiVersion;
	private String apiDescription;
	private String itauService;
	private String OSBEvaluarRiesgoTransaccion;
	private String OSBEvaluarRiesgoTransaccionMethod;
	private String OSBEvaluarRiesgoTransaccionContentType;
	private String OSBAutenticarClienteTransaccion;
	private String OSBAutenticarClienteTransaccionMethod;
	private String OSBAutenticarClienteTransaccionContentType;
	private String healthcheck;

	public String getEvaluarRiesgoTransaccion() {
		return evaluarRiesgoTransaccion;
	}

	public void setEvaluarRiesgoTransaccion(String evaluarRiesgoTransaccion) {
		this.evaluarRiesgoTransaccion = evaluarRiesgoTransaccion;
	}

	public String getAutenticarClienteTransaccion() {
		return autenticarClienteTransaccion;
	}

	public void setAutenticarClienteTransaccion(String autenticarClienteTransaccion) {
		this.autenticarClienteTransaccion = autenticarClienteTransaccion;
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

	public String getApiDescription() {
		return apiDescription;
	}

	public void setApiDescription(String apiDescription) {
		this.apiDescription = apiDescription;
	}

	public String getItauService() {
		return itauService;
	}

	public void setItauService(String itauService) {
		this.itauService = itauService;
	}

	public String getOSBEvaluarRiesgoTransaccion() {
		return OSBEvaluarRiesgoTransaccion;
	}

	public void setOSBEvaluarRiesgoTransaccion(String oSBEvaluarRiesgoTransaccion) {
		OSBEvaluarRiesgoTransaccion = oSBEvaluarRiesgoTransaccion;
	}

	public String getOSBEvaluarRiesgoTransaccionMethod() {
		return OSBEvaluarRiesgoTransaccionMethod;
	}

	public void setOSBEvaluarRiesgoTransaccionMethod(String oSBEvaluarRiesgoTransaccionMethod) {
		OSBEvaluarRiesgoTransaccionMethod = oSBEvaluarRiesgoTransaccionMethod;
	}

	public String getOSBAutenticarClienteTransaccion() {
		return OSBAutenticarClienteTransaccion;
	}

	public void setOSBAutenticarClienteTransaccion(String oSBAutenticarClienteTransaccion) {
		OSBAutenticarClienteTransaccion = oSBAutenticarClienteTransaccion;
	}

	public String getOSBAutenticarClienteTransaccionMethod() {
		return OSBAutenticarClienteTransaccionMethod;
	}

	public void setOSBAutenticarClienteTransaccionMethod(String oSBAutenticarClienteTransaccionMethod) {
		OSBAutenticarClienteTransaccionMethod = oSBAutenticarClienteTransaccionMethod;
	}

	public String getHealthcheck() {
		return healthcheck;
	}

	public void setHealthcheck(String healthcheck) {
		this.healthcheck = healthcheck;
	}

	public String getOSBEvaluarRiesgoTransaccionContentType() {
		return OSBEvaluarRiesgoTransaccionContentType;
	}

	public void setOSBEvaluarRiesgoTransaccionContentType(String oSBEvaluarRiesgoTransaccionContentType) {
		OSBEvaluarRiesgoTransaccionContentType = oSBEvaluarRiesgoTransaccionContentType;
	}

	public String getOSBAutenticarClienteTransaccionContentType() {
		return OSBAutenticarClienteTransaccionContentType;
	}

	public void setOSBAutenticarClienteTransaccionContentType(String oSBAutenticarClienteTransaccionContentType) {
		OSBAutenticarClienteTransaccionContentType = oSBAutenticarClienteTransaccionContentType;
	}

}
