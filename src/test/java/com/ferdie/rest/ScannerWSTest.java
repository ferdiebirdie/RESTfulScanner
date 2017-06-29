package com.ferdie.rest;

import static com.ferdie.rest.util.ValidatorUtil.ValidatorUtil;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.util.JsonUtilTest;

public class ScannerWSTest {
	final static Logger log = Logger.getLogger(JsonUtilTest.class);
	
	@Test
	public void test() {
		String scannerId = "1";
		String url = "http://asda.ac/asd.php";
		
		String message = "success!";
		if (!ValidatorUtil.isValidScannerId(scannerId)) {
			message = "Invalid scannerId parameter: " + scannerId;
		} else if (!ValidatorUtil.isValidUrl(url)) {
			message = "Invalid url parameter: " + url;
		}
		log.debug("{\"message\" : \"" + message + "\"}");
	}
	

}
