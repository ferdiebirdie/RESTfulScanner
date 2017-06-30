package com.ferdie.rest;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import com.ferdie.rest.service.W3afScannerService;
import com.ferdie.rest.service.domain.ScanOrder;

public class W3afScannerServiceTest {
	final static Logger log = Logger.getLogger(W3afScannerServiceTest.class);
	W3afScannerService svc = new W3afScannerService();
	Long scanId = 17L;
	Long orderId = 8L;
	
	@Test
	public void startScan() {
		log.debug("Testing: startScan()....");
		String url = "http://www.webscantest.com/shutterform/";
		ScanOrder result = svc.scan(url);
		log.debug(result);
	}
	
	@Test
	public void getScanStatus() {
		log.debug("Testing: getScanStatus()....");
		String result = svc.getScanStatus(orderId);
		log.debug(result);
	}
	
	@Test
	public void getActiveScans() {
		log.debug("Testing: getActiveScans()....");
		String result = svc.getActiveScans();
		log.debug(result);
	}
	
	@Test
	public void isScanStopped() {
		log.debug("Testing: hasRunningScan()....");
		boolean result = svc.isScanStopped();
		log.debug(result);
	}
	
	@Test
	public void testSave() {
		log.debug("Testing: testSave()....");
		final String result = "{\"href\": \"/scans/"+orderId+"\", \"id\": "+orderId+", \"message\": \"Success\" }";
		ScanOrder scan = new ScanOrder(result);
		scan.setScanId(scanId);
		svc.save(scan);
	}
	
	@Test
	public void testSaveVulners() {
		log.debug("Testing: testSaveInDB()....");
		try {
			svc.saveVulners(scanId);
		} catch (ParseException e) {
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testGetVulnerabilities() throws ParseException {
		log.debug(svc.getVulnerabilities(scanId));
	}

	@Test
	public void testRandom() {
		ScanOrder order = new ScanOrder();
		log.debug(order);
		testFinal(order);
		log.debug(order);
	}
	
	private void testFinal(ScanOrder order) {
		order.setScanId(999L);
	}
	
	@Test
	public void testGetProfile() {
		String p = svc.getProfile();
		log.debug(p);
		Assert.assertFalse("".equals(p));
	}
	
}
