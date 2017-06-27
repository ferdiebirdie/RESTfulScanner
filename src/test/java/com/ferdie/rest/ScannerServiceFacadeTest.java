package com.ferdie.rest;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Scanner;
import com.ferdie.rest.util.MongoDbUtil;

public class ScannerServiceFacadeTest {
	final static Logger log = Logger.getLogger(ScannerServiceFacadeTest.class);
	private ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	private Long scanId = 14L;
	
	@Test
	public void testScan() {
		log.debug("Testing: startScan()....");
		String url = "http://www.webscantest.com/shutterform/";
//		targetUrls.add("http://www.webscantest.com/datastore/search_by_id.php");
		svcFacade.queueScan(Scanner.W3AF.getId(), url, MongoDbUtil.instance.getNextSequence());
	}
	
	@Test
	public void testGetScanStatus() {
		log.debug("Status: " + svcFacade.getScanStatus(scanId));
	}
	

}
