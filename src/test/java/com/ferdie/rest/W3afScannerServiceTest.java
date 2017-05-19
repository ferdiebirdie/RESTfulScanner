package com.ferdie.rest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ferdie.rest.service.W3afScannerService;
import com.ferdie.rest.service.domain.ScanOrder;

public class W3afScannerServiceTest {
	
	W3afScannerService svc = new W3afScannerService();
	Long scanId = 3L;
	
	@Test
	public void startScan() {
		System.out.println("Testing: startScan()....");
		List<String> targetUrls = new ArrayList<String>();
		targetUrls.add("http://www.webscantest.com/shutterform/");
		//targetUrls.add("http://www.webscantest.com/datastore/search_by_id.php");
		ScanOrder result = svc.scan(targetUrls);
		System.out.println(result);
	}
	
	@Test
	public void deleteScan() {
		System.out.println("Testing: deleteScan()....");
		String result = svc.deleteScan(scanId);
		System.out.println(result);
	}
	
	@Test
	public void getScanStatus() {
		System.out.println("Testing: getScanStatus()....");
		String result = svc.getScanStatus(scanId);
		System.out.println(result);
	}
	
	/*@Test
	public void getScanLogs() {
		System.out.println("Testing: getScanLogs()....");
		String result = svc.getScanLogs(scanId);
		System.out.println(result);
	}*/
	
	@Test
	public void getActiveScans() {
		System.out.println("Testing: getActiveScans()....");
		String result = svc.getActiveScans();
		System.out.println(result);
	}
	
	@Test
	public void getCompletedScanIds() {
		System.out.println("Testing: getCompletedScanIds()....");
		List<Long> result = svc.getCompletedScanIds();
		System.out.println(result);
	}
	
	@Test
	public void hasRunningScan() {
		System.out.println("Testing: hasRunningScan()....");
		boolean result = svc.hasRunningScan();
		System.out.println(result);
	}
	
	String id = "2";
	
	@Test
	public void testSave() {
		System.out.println("Testing: testSave()....");
		final String result = "{\"href\": \"/scans/"+id+"\", \"id\": "+id+", \"message\": \"Success\" }";
		ScanOrder scan = new ScanOrder(result);
		svc.save(scan);
	}
	
	@Test
	public void testSaveInDB() {
		System.out.println("Testing: testSaveInDB()....");
		final String result = "{\"href\": \"/scans/"+id+"\", \"id\": "+id+", \"message\": \"Success\" }";
		ScanOrder scan = new ScanOrder(result);
		svc.saveInDB(scan);
	}

}
