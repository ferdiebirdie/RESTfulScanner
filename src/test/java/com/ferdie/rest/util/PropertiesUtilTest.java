package com.ferdie.rest.util;

import static com.ferdie.rest.util.PropertiesUtil.PropertiesUtil;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.domain.Constants;

import junit.framework.Assert;

public class PropertiesUtilTest implements Constants {
	final static Logger log = Logger.getLogger(PropertiesUtilTest.class);
	
	@Test
	public void testGetProperty() throws Exception {
		Assert.assertEquals("http://192.168.99.100:5000", PropertiesUtil.getProperty(KEY_WS_URL_W3AF));
	}

}
