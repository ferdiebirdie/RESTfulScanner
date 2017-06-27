package com.ferdie.rest.service.domain;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.ferdie.rest.util.JsonUtil;

public class ScanOrder {
	final static Logger log = Logger.getLogger(ScanOrder.class);
	private Long scanId;
	private Long orderId;
	private Long scannerId;
	private String message;
	private String status;
	private String result;
	private String url;
	private Boolean isCreated = Boolean.FALSE;
	
	public ScanOrder(Long scannerId, String url, String result) {
		this(result);
		this.scannerId = scannerId;
		this.url = url;
		this.status = "Running";
		this.isCreated = true;
	}
	
	public Boolean isCreated() {
		return isCreated;
	}

	public void setCreated(Boolean isCreated) {
		this.isCreated = isCreated;
	}
	
	public ScanOrder(String result) {
		parseJson(result);
	}

	public ScanOrder() {
	}

	@Override
	public String toString() {
		return "ScanOrder[scanId="+ scanId + ", scannerId="+ scannerId + ", url=" + url +"]";
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
	
	private void parseJson(String result) {
		JSONObject json = JsonUtil.instance.stringToJson(result);
		orderId = (Long) json.get("id");
		status = (String) json.get("status");
		message = (String) json.get("message");
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
