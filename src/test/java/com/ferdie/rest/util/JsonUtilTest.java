package com.ferdie.rest.util;

import static com.ferdie.rest.util.JsonUtil.JsonUtil;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilTest {
	final static Logger log = Logger.getLogger(JsonUtilTest.class);
	
	@Test
	public void test() throws IOException {
		String s = "{\"message\": \"Request now in queue.\", \"scanId\": " + 1 + "}";
		System.out.println(s);
		ObjectMapper mapper = JsonUtil.getObjectMapper();
		Object json = mapper.readValue(s, Object.class);
		String response =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		log.debug(response);
	}
	
}
