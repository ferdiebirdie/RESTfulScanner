package com.ferdie.rest;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.util.PropertiesUtil;

import junit.framework.Assert;

public class PropertiesUtilTest implements Constants {
	final static Logger log = Logger.getLogger(PropertiesUtilTest.class);
	
	@Test
	public void testGetProperty() throws Exception {
		Assert.assertEquals("http://192.168.99.100:5000", PropertiesUtil.instance.getProperty(KEY_WS_URL_W3AF));
	}

}
