package com.ferdie.rest.service;

import com.ferdie.rest.service.domain.ScanOrder;

public interface ScannerService {
	
	ScanOrder scan(String url);
	
	void save(ScanOrder result);
	
	String getScanStatus(Long id);
	
	String getActiveScans();
	
	Long getScannerId();

	String getVulnerabilities(Long scanId);
	
}
