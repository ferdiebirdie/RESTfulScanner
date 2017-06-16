package com.ferdie.rest.mongo;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.util.PropertiesUtil;

import junit.framework.Assert;

public class PropertiesUtilTest implements Constants {
	final static Logger log = Logger.getLogger(PropertiesUtilTest.class);
	
	@Test
	public void testGetProperty() throws Exception {
		Assert.assertEquals("http://128.199.201.92:5000", PropertiesUtil.instance.getProperty("ws.url.w3af"));
	}

}
