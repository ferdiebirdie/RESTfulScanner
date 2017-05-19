package com.ferdie.rest.service;

import java.util.List;

import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;

public class ScannerServiceFacade {

	W3afScannerService svc = new W3afScannerService();
	
	public String scan(Long scannerId, List<String> targetUrls) {
		ScanOrder result;
		// scan
		// save id, results in db
		// return standard output
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				result = svc.scan(targetUrls);
				// check if success
				if (result.isSuccess()) {
					// insert record in DB
					
					// sleep 10sec
					try {
						System.out.println("Sleeping for 10sec..");
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					}
					svc.save(result);
				}
				return result.getResult();
				
			// handle other scanners
			default:
				return "Unhandled!";
		}
		
	}

	public String getScanStatus(Long scannerId, Long scanId) {
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				return svc.getScanStatus(scanId);
			// handle other scanners
			default:
				return "Unhandled!";
		}
	}

	/*public String getScanLogs(Long scannerId, Long scanId) {
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				return svc.getScanLogs(scanId);
			// handle other scanners
			default:
				return "Unhandled!";
		}
	}*/

	/*public String getActiveScans(Long scannerId) {
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				return svc.getActiveScans();
			// handle other scanners
			default:
				return "Unhandled!";
		}
	}*/

}
