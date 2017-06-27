package com.ferdie.rest.util;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilTest {
	
	@Test
	public void test() throws IOException {
		String s = "{\"message\": \"Request now in queue.\", \"scanId\": " + 1 + "}";
		System.out.println(s);
		ObjectMapper mapper = JsonUtil.instance.getObjectMapper();
		Object json = mapper.readValue(s, Object.class);
		String response =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println(response);
	}

}
