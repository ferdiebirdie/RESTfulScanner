package com.ferdie.rest.service.domain;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ScanOrder {

	private Long id;
	private String message;
	private String status;
	private String result;
	
	public ScanOrder(String result) {
		parseJsonString(result);
	}
	
	public ScanOrder() {
	}

	@Override
	public String toString() {
		return "ScanOrder[id="+ id + ", message=" + message + ", status="+ status +", success="+isSuccess()+"]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public boolean isSuccess() {
		return null != id;
	}

	public void parseJsonString(String result) {
		this.result = result;
		if (StringUtils.isNotEmpty(result)) {
			JSONParser parser = new JSONParser();
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(result);
				id = (Long) json.get("id");
				message = (String) json.get("message");
			} catch (ParseException e) {
				System.err.println(e.getMessage());
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

}
