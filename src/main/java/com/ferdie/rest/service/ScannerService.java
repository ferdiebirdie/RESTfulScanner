package com.ferdie.rest.service;

import com.ferdie.rest.service.domain.ScanOrder;

public interface ScannerService {
	
	ScanOrder scan(String url);
	
	boolean save(ScanOrder result);
	
	String getScanStatus(Long id);
	
	String getActiveScans();
	
	Integer getScannerId();

	String getVulnerabilities(Long scanId);
	
}
