package com.ferdie.rest.service;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;
import com.ferdie.rest.util.MongoDbUtil;
import com.mongodb.BasicDBObject;

public class ScannerServiceFacade {
	final static Logger log = Logger.getLogger(ScannerServiceFacade.class);
	private W3afScannerService svc = new W3afScannerService();
	
	public ScanOrder scan(Long scannerId, List<String> targetUrls) {
		ScanOrder scan;
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				scan = svc.scan(targetUrls);
				svc.save(scan);
				log.debug(scan);
				return scan;
			// handle other scanners
			default:
				return new ScanOrder("Unhandled!");
		}
		
	}

	public String getScanStatus(Long scanId) {
		try {
			BasicDBObject scan = (BasicDBObject) MongoDbUtil.instance.findById(scanId);
			if (null != scan) {
				String dbStatus = Objects.toString(scan.get("status"));
				/*
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(svc.getScanStatus(scan.getLong("orderId")));
				String apiStatus = Objects.toString(json.get("status"));
				
				if (dbStatus.equals("Running") && apiStatus.equals("Stopped")) {
					// update DB
					log.debug("Syncing API to DB status...");
					return MongoDbUtil.instance.updateStatus(scanId, "Completed");
				}*/ 
				return dbStatus;	
			} else {
				return "Not available";
			}
			
		} catch (Exception e) {
			log.error(e);
			return "Error on search! Check logs.";
		}
	}

	public String getVulnerabilities(Long scanId) {
		return svc.getVulnerabilities(scanId);
	}

}
