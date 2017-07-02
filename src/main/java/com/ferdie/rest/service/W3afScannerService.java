package com.ferdie.rest.service;

import static com.ferdie.rest.util.JsonUtil.JsonUtil;
import static com.ferdie.rest.util.MongoDbUtil.MongoDbUtil;
import static com.ferdie.rest.util.PropertiesUtil.PropertiesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.service.domain.ScanAction;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;
import com.mongodb.BasicDBObject;

public class W3afScannerService implements ScannerService, Constants {
	final static Logger log = Logger.getLogger(W3afScannerService.class);

	public ScanOrder scan(String url) {
		ScanOrder scan = null;
		Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
		WebTarget webTarget = client.target(PropertiesUtil.getProperty(KEY_WS_URL_W3AF) + "/scans/");

		List<String> urls = new ArrayList<String>(1);
		urls.add(url);
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("target_urls", urls);
		json.put("scan_profile", getProfile());
		
		String input = JSONValue.toJSONString(json);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(input, MediaType.APPLICATION_JSON));
		String result = response.readEntity(String.class);
		if (response.getStatus() == 201) { // success
//			log.debug("Successfully created scan order.");
			scan = new ScanOrder(getScannerId(), url, result);
		} else {
			log.error("Failed creating scan order: " + result);
			scan = new ScanOrder(result);
		}
		return scan;
	}

	public String deleteActiveScan() {
		Long id = getActiveOrderId();
		if (null != id) {
			return manageScanOrder(ScanAction.DELETE, id);
		} else {
			return JsonUtil.prettyPrint(MSG_NOTHING_DELETED);
		}
	}

	public String getScanStatus(Long scanId) {
		try {
			BasicDBObject scan = (BasicDBObject) MongoDbUtil.findById(scanId);
			if (null != scan) {
				return JsonUtil.prettyPrint("{\"status\" : \"" + Objects.toString(scan.get("status")) + "\"}");
			} else {
				return JsonUtil.prettyPrint(MSG_NOT_FOUND);
			}
			
		} catch (Exception e) {
			log.error(e);
			return "Error on search! Check logs.";
		}
	}

	public String getVulnerabilities(Long scanId) {
		try {
			Object o = MongoDbUtil.findById(scanId, "vulnerabilities");
			if (null == o) {
				return JsonUtil.prettyPrint(MSG_NOT_FOUND);
			} else if (StringUtils.isEmpty(o.toString())) {
				return JsonUtil.prettyPrint(MSG_NO_VULNERS);
			} else {
				o = "pending";
				if (PENDING.equals(o)) {
					return JsonUtil.prettyPrint("{\"vulnerabilities\" : \"" + o + "\"}");
				} else {
					return JsonUtil.prettyPrint(o.toString());
				}
			}
		} catch (Exception e) {
			log.error(e);
			return "Error getting vulnerabilities. Check logs.";
		}
	}

	public String getActiveScans() {
		return manageScanOrder(ScanAction.GET_ACTIVE_SCANS, null);
	}

	private String manageScanOrder(ScanAction action, Long orderId) {
		return manageScanOrder(action, orderId, null);
	}

	private String manageScanOrder(ScanAction action, Long orderId, Long detailId) {
		String reqUrl = "/scans/";
		switch (action) {
		case GET_STATUS:
			reqUrl = reqUrl + orderId + "/status";
			break;
		case GET_LOGS:
			reqUrl = reqUrl + orderId + "/log";
			break;
		case GET_VULN_ALL:
			reqUrl = reqUrl + orderId + "/kb/";
			break;
		case GET_VULN_BY_ID:
			reqUrl = reqUrl + orderId + "/kb/" + detailId;
			break;

		case DELETE:
			reqUrl = reqUrl + orderId;

			Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
			WebTarget webTarget = client.target(PropertiesUtil.getProperty(KEY_WS_URL_W3AF) + reqUrl);

			Invocation.Builder invocationBuilder = webTarget.request();
			Response response = invocationBuilder.delete();

			return response.readEntity(String.class);
		case GET_ACTIVE_SCANS:
			break;
		default:
			break;
		}
		Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
		WebTarget webTarget = client.target(PropertiesUtil.getProperty(KEY_WS_URL_W3AF) + reqUrl);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		return response.readEntity(String.class);

	}

	public String getProfile() {
		InputStream in = null;
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {
			in = getClass().getResourceAsStream("/profiles/w3af/fast_scan_corrected.pw3af");
			fr = new InputStreamReader(in);
			br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String line = "";
			while (null != (line = br.readLine())) {
				if (sb.length() > 0) {
					sb.append(System.lineSeparator());
				}
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			log.error(e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if (null != fr) {
				try {
					fr.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		return "";
	}
	
	private Long getActiveOrderId() {
		JSONObject json = getActiveScan();
		if (null != json) {
			return (Long) json.get("id");
		}
		return null;
	}

	private JSONObject getActiveScan() {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(getActiveScans());
			JSONArray items = (JSONArray) json.get("items");
			if (null != items && items.size() > 0) {
				for (Object obj : items) {
					// current w3af version only allow 1 scan at a time
					return (JSONObject) obj;
				}
			}
		} catch (ParseException e) {
			log.error("Parsing error: ", e);
		}
		return null;
	}
	
	public boolean isScanStopped() {
		JSONObject scan = getActiveScan();
		return null == scan || null == scan.get("status") || "Stopped".equals(scan.get("status").toString());
	}

	public boolean save(ScanOrder order) {
		try {
			// give enough time for w3af to process first
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log.error("Error: ", e);
			}
			
			JSONObject scan = getActiveScan();
			if (null != scan) {
				log.debug("Scan Order: " + scan);
				while (!isScanStopped()) {
					// do nothing...
				}
				saveVulners(order.getScanId());
			} else {
				log.warn("No active scan detected!");
			}
			return true;
		} catch (Exception e1) {
			log.error(e1);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void saveVulners(Long scanId) throws ParseException {
		try {
			Long orderId = getActiveOrderId();
			String kbALl = manageScanOrder(ScanAction.GET_VULN_ALL, orderId);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(kbALl);
			JSONArray items = (JSONArray) json.get("items");
			if (null != items && items.size() > 0) {
				JSONArray details = new JSONArray();
				for (Object o : items) {
					JSONObject item = (JSONObject) o;
					Long id = (Long) item.get("id");
					String kb = manageScanOrder(ScanAction.GET_VULN_BY_ID, orderId, id);
					details.add(parser.parse(kb));
				}
				MongoDbUtil.updateVulners(scanId, details);
				log.debug("Vulnerabilities: " + items.size());
			} else {
				log.debug("Vulnerabilities: " + 0);
			}
		} catch (ParseException e) {
			log.error("Error saving vulnerabilities.", e);
			throw e;
		}
	}

	public Integer getScannerId() {
		return Scanner.W3AF.getId();
	}

}
