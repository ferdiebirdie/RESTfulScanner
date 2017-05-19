package com.ferdie.rest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Scanner;

public class ScannerServiceFacadeTest {
	
	ScannerServiceFacade svc = new ScannerServiceFacade();
	
	@Test
	public void testScan() {
		System.out.println("Testing: startScan()....");
		List<String> targetUrls = new ArrayList<String>();
		targetUrls.add("http://www.webscantest.com/shutterform/");
		svc.scan(Scanner.W3AF.getId(), targetUrls);
	}
	

}
