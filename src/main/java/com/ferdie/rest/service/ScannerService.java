package com.ferdie.rest.service;

import java.util.List;

import com.ferdie.rest.service.domain.ScanOrder;

public interface ScannerService {
	
	ScanOrder scan(List<String> targetUrls);
	
	void save(ScanOrder result);
	
	String getScanStatus(Long id);
	
	String getActiveScans();
	
	Long getScannerId();

	String getVulnerabilities(Long scanId);
	
}
