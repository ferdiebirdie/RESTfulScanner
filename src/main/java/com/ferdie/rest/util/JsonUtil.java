package com.ferdie.rest.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum JsonUtil {
	JsonUtil;
	
	final static Logger log = Logger.getLogger(JsonUtil.class);
	
	private JSONObject defaultJson = new JSONObject();
	private ObjectMapper mapper = new ObjectMapper();
	
	private JsonUtil() {
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	public ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject stringToJson(String result) {
		if (StringUtils.isNotEmpty(result)) {
			JSONParser parser = new JSONParser();
			try {
				return (JSONObject) parser.parse(result);
			} catch (ParseException e) {
				JSONObject json = new JSONObject();
				json.put("message", result);
				return json;
			}
		}
		return defaultJson;
	}
	
	public String prettyPrint(String str) {
		try {
			Object json = mapper.readValue(str, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (IOException e) {
			log.warn(e);
			return str;
		}
	}

}
