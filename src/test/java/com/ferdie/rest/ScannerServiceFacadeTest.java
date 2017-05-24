package com.ferdie.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Scanner;

public class ScannerServiceFacadeTest {
	final static Logger log = Logger.getLogger(ScannerServiceFacadeTest.class);
	private ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	private Long scanId = 14L;
	
	@Test
	public void testScan() {
		log.debug("Testing: startScan()....");
		List<String> targetUrls = new ArrayList<String>();
		targetUrls.add("http://www.webscantest.com/shutterform/");
//		targetUrls.add("http://www.webscantest.com/datastore/search_by_id.php");
		log.debug(svcFacade.scan(Scanner.W3AF.getId(), targetUrls));
	}
	
	@Test
	public void testGetScanStatus() {
		log.debug("Status: " + svcFacade.getScanStatus(scanId));
	}
	

}
