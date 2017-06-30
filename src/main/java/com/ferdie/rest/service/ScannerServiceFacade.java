package com.ferdie.rest.service;

import static com.ferdie.rest.util.JsonUtil.JsonUtil;
import static com.ferdie.rest.util.MongoDbUtil.MongoDbUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdie.rest.rabbitmq.QueueProducer;
import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;

public class ScannerServiceFacade implements Constants {
	final static Logger log = Logger.getLogger(ScannerServiceFacade.class);
	private W3afScannerService svc = new W3afScannerService();
	
	public String queueScan(Integer scannerId, String url) {
		return queueScan(scannerId, url, null);
	}
	
	public String queueScan(Integer scannerId, String url, Long scanId) {
		QueueProducer producer;
		try {
			producer = new QueueProducer(RABBITMQ_QUEUE_NAME);
			HashMap<String, Object> msg = new HashMap<String, Object>(2);
			if (null == scanId) {
				scanId = getNextScanId();
				MongoDbUtil.createScan(new ScanOrder(scanId, scannerId, url, SHEDULED));
			}
			msg.put("scanId", scanId);
			msg.put("scannerId", scannerId);
			msg.put("url", url);
			producer.sendMessage(msg);
			log.debug("Message sent: " + msg);
			
			ObjectMapper mapper = JsonUtil.getObjectMapper();
			String s = "{\"scanId\": " + scanId + ", \"status\": \"scheduled\"}";
			Object json = mapper.readValue(s, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (IOException | TimeoutException e1) {
			log.error(e1);
			return MSG_ERR_LOGS;
		}
	}
	
	public void scan(Map<String, Object> map) {
		Integer scannerId = (Integer) map.get(SCANNER_ID);
		Long scanId = (Long) map.get(SCAN_ID);
		String url = (String) map.get(URL);
		
		ScanOrder scan;
		switch (Scanner.toScanner(scannerId)) {
			case W3AF:
				// insert to DB
				svc.deleteActiveScan();
				scan = svc.scan(url);
				if (scan.isCreated()) {
					MongoDbUtil.updateStatus(scanId, RUNNING);
					scan.setScanId(scanId);
					boolean isSuccess = svc.save(scan);
					MongoDbUtil.updateStatus(scanId, isSuccess ? COMPLETED : FAILED);
				} else {
					MongoDbUtil.updateStatus(scanId, FAILED);
				}
				break;
			// TODO: handle other scanners
			default:
		}
	}
	
	public String getScanStatus(Long scanId) {
		String result = svc.getScanStatus(scanId);
		ObjectMapper mapper = JsonUtil.getObjectMapper();
		JSONObject json;
		try {
			json = mapper.readValue(result, JSONObject.class);
			return JsonUtil.prettyPrint("{\"status\" : \"" + Objects.toString(json.get("status"), "Not found") + "\"}");
		} catch (Exception e) {
			log.error(e);
			return MSG_ERR_LOGS;
		}
	}

	public String getVulnerabilities(Long scanId) {
		String result = svc.getVulnerabilities(scanId);
		ObjectMapper mapper = JsonUtil.getObjectMapper();
		Object json;
		try {
			json = mapper.readValue(result, Object.class);
			return  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			log.error(e);
			return MSG_ERR_LOGS;
		}
	}
	
	public String deleteActiveScan() {
		return svc.deleteActiveScan();
	}
	
	public Long getNextScanId() {
		return MongoDbUtil.getNextSequence();
	}

}
