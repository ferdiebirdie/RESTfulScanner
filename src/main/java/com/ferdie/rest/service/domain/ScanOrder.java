package com.ferdie.rest.service.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ScanOrder {
	final static Logger log = Logger.getLogger(ScanOrder.class);
	private Long orderId;
	private Long scannerId;
	private Long scanId;
	private String message;
	private String status;
	private String result;
	private boolean isCreated;
	
	public ScanOrder(Long scannerId, String result) {
		this(result);
		this.scannerId = scannerId;
		this.status = "Running";
		this.isCreated = true;
	}
	
	public boolean isCreated() {
		return isCreated;
	}

	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}
	
	public ScanOrder(String result) {
		parseJsonString(result);
	}

	public ScanOrder() {
	}

	@Override
	public String toString() {
		return "ScanOrder[orderId="+ orderId + ", scanId="+ scanId + ", scannerId="+ scannerId + ", message=" + message + ", status="+ status +"]";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void parseJsonString(String result) {
		this.result = result;
		if (StringUtils.isNotEmpty(result)) {
			JSONParser parser = new JSONParser();
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(result);
				orderId = (Long) json.get("id");
				message = (String) json.get("message");
			} catch (ParseException e) {
				//log.warn("", e.getMessage());
				message = result;
			}
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Long getScanId() {
		return scanId;
	}

	public void setScanId(Long scanId) {
		this.scanId = scanId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getScannerId() {
		return scannerId;
	}

	public void setScannerId(Long scannerId) {
		this.scannerId = scannerId;
	}

}
