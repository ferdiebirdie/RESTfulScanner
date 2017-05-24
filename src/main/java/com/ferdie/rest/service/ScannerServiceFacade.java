package com.ferdie.rest.service;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ferdie.rest.mongo.MongoUtil;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;
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
			BasicDBObject scan = (BasicDBObject) MongoUtil.getInstance().findById(scanId);
//			log.debug("Scan: " + scan);
			String dbStatus = Objects.toString(scan.get("status"));
//			log.debug("DB status: " + dbStatus);
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(svc.getScanStatus(scan.getLong("orderId")));
			String apiStatus = Objects.toString(json.get("status"));
//			log.debug("API status: " + apiStatus);
			
			if (dbStatus.equals("Running") && apiStatus.equals("Stopped")) {
				// update DB
				log.debug("Syncing API to DB status...");
				return MongoUtil.getInstance().updateStatus(scanId, "Completed");
			}
			return dbStatus;
		} catch (Exception e) {
			log.error(e);
			return "Error on search! Check logs.";
		}
	}

	public String getVulnerabilities(Long scanId) {
		return svc.getVulnerabilities(scanId);
	}

}
