package com.ferdie.rest.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;

public class ScannerServiceFacade {
	final static Logger log = Logger.getLogger(ScannerServiceFacade.class);
	private W3afScannerService svc = new W3afScannerService();
	
	public ScanOrder scan(Long scannerId, List<String> targetUrls) {
		ScanOrder scan;
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				scan = svc.scan(targetUrls);
				if (scan.isCreated()) {
					svc.save(scan);
				}
				log.debug(scan);
				return scan;
			// handle other scanners
			default:
				return new ScanOrder("Unhandled!");
		}
		
	}
	
	public String getScanStatus(Long scanId) {
		return svc.getScanStatus(scanId);
	}

	public String getVulnerabilities(Long scanId) {
		return svc.getVulnerabilities(scanId);
	}
	
	public String deleteScan() {
		return svc.deleteActiveScan();
	}

}
