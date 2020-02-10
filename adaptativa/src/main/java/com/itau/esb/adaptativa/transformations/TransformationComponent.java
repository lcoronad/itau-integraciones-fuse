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

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.StringSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itau.esb.adaptativa.model.AutenticarClienteTransaccionReturn;
import com.itau.esb.adaptativa.model.EvaluarRiesgoTransaccionReturn;
import com.itau.esb.adaptativa.model.GenericResponse;
import com.itau.esb.adaptativa.model.Response;
import com.itau.esb.adaptativa.model.ResponseACTR;
import com.itau.esb.adaptativa.model.ResponseList;

@Component("transformationComponent")
public class TransformationComponent {
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Handler
	public static void transformation(Exchange ex) throws JAXBException {
		String ERTR = ex.getProperty("ERTR", String.class);
		JAXBContext jc = JAXBContext.newInstance(EvaluarRiesgoTransaccionReturn.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		EvaluarRiesgoTransaccionReturn obj = (EvaluarRiesgoTransaccionReturn)unmarshaller.unmarshal(new StringSource(ERTR));
		ex.setProperty("ERTR", obj);
	}

	public static void mappingSuccessResponse(Exchange ex) throws JsonProcessingException {
		EvaluarRiesgoTransaccionReturn ob = ex.getProperty("ERTR", EvaluarRiesgoTransaccionReturn.class);
		Response res = new Response();
		res.setEvaluarRiesgoTransaccionReturn(ob);
		ex.getIn().setBody(res);
	}
	
	public static void mappingSuccessResponseACT(Exchange ex) throws JsonProcessingException {
		AutenticarClienteTransaccionReturn ACTR = new AutenticarClienteTransaccionReturn();
		GenericResponse GR = new GenericResponse();
		
		GR.setCodigoError(ex.getIn().getHeader("codigoError", String.class));
		GR.setDescripcion(ex.getIn().getHeader("descripcion", String.class));
		
		ACTR.setDeviceTokenCookie(ex.getIn().getHeader("deviceTokenCookie", String.class));
		ACTR.setRecomendedActionAA(ex.getIn().getHeader("recomendedActionAA", String.class));
		ACTR.setGenericResponse(GR);
		
		ResponseACTR res = new ResponseACTR();
		res.setAutenticarClienteTransaccionReturn(ACTR);
		ex.getIn().setBody(res);
	}
	
	public void loadResponsesList(Exchange ex) throws JsonParseException, JsonMappingException, IOException {
		Object cont = ex.getIn().getHeader("respuestasWS", Object.class);
		String str = "{ \"Response\":" + mapper.writeValueAsString(cont) + " }";
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		ResponseList cl = mapper.readValue(str.getBytes(), ResponseList.class);

		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		ObjectMapper objectMapper = new XmlMapper(module);

		ex.getIn().setBody(objectMapper.writeValueAsString(cl).replaceAll("<ResponseList>", "").replaceAll("</ResponseList>", ""));
	}
}
