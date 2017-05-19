package com.ferdie.rest.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ferdie.rest.service.domain.ScanAction;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;

public class W3afScannerService implements ScannerService {
	
	private String baseUrl = "http://127.0.0.1:5000";
	
	public ScanOrder scan(List<String> targetUrls) {
		if (hasRunningScan()) {
			return new ScanOrder("Previous scan still running. Try again later.");
		}
		List<Long> ids = getCompletedScanIds();
		if (null != ids && ids.size() > 0) {
			System.out.println("Deleting completed scans...");
			for (Long id : ids) {
				deleteScan(id);
			}
			System.out.println("Deleted Ids: " + ids);
		}
		System.out.println("Starting scan...");
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFeature.class ) );
		WebTarget webTarget = client.target(baseUrl + "/scans/");
		 
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("scan_profile", getProfile());
		json.put("target_urls", targetUrls);
		
		String input = JSONValue.toJSONString(json);
		 
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(input, MediaType.APPLICATION_JSON));
		 
		String result = response.readEntity(String.class);
		ScanOrder scan = new ScanOrder(result);
		System.out.println("Scan result: " + scan);
		return scan;
		
	}
	
	public String deleteScan(Long id) {
		return manageScans(ScanAction.DELETE, id);
	}

	public String getScanStatus(Long id) {
		return manageScans(ScanAction.GET_STATUS, id);
	}

	/*public String getScanLogs(Long id) {
		return manageScans(ScanAction.GET_LOGS, id);
	}*/

	public String getActiveScans() {
		return manageScans(ScanAction.GET_ACTIVE_SCANS, null);
	}

	private String manageScans(ScanAction action, Long scanId) {
		return manageScans(action, scanId, null);
	}
	
	private String manageScans(ScanAction action, Long scanId, Long detailId) {
		String reqUrl = "/scans/";
		switch (action) {
			case GET_STATUS:
				reqUrl = reqUrl + scanId + "/status";
				break;
			case GET_LOGS:
				reqUrl = reqUrl + scanId + "/log";
				break;
			case GET_VULN_ALL:
				reqUrl = reqUrl + scanId + "/kb/";
				break;
			case GET_VULN_BY_ID:
				reqUrl = reqUrl + scanId + "/kb/" + detailId;
				break;
				
			case DELETE:
				reqUrl = reqUrl + scanId;
				
				Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFeature.class ) );
				WebTarget webTarget = client.target(baseUrl + reqUrl);
				 
				Invocation.Builder invocationBuilder =  webTarget.request();
				Response response = invocationBuilder.delete();
				 
				return response.readEntity(String.class);
			case GET_ACTIVE_SCANS:
				break;
			default:
				break;
		}
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFeature.class ) );
		WebTarget webTarget = client.target(baseUrl + reqUrl);
		 
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		 
		return response.readEntity(String.class);
			     
	}

	public static String getProfile() {
		try {
			return new String(Files.readAllBytes(Paths.get("/home/ferdie/w3af/profiles/fast_scan.pw3af")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public List<Long> getCompletedScanIds() {
		List<Long> ids = new ArrayList<Long>(1);
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(getActiveScans());
			JSONArray items = (JSONArray) json.get("items");
			if (null != items && items.size() > 0) {
				for (Object obj : items) {
					JSONObject item = (JSONObject) obj;
					Long id = (Long) item.get("id");
					String status = (String) item.get("status");
					if ("Stopped".equalsIgnoreCase(status)) {
						ids.add(id);
					} else {
						System.out.println("Skipped: [Id=" + id + ", Status=" + status + "]");
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public boolean hasRunningScan() {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(getActiveScans());
			JSONArray items = (JSONArray) json.get("items");
			if (null != items && items.size() > 0) {
				for (Object obj : items) {
					JSONObject item = (JSONObject) obj;
					if ("Running".equals(item.get("status"))) {
						return true;
					}
				}
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return false;
	}

	public void save(final ScanOrder scan) {
		new Thread(new Runnable() {
			public void run() {
				final int waitingTime = 60;
				while(true) {
					if (hasRunningScan()) {
						System.err.println("********************** Waiting for another "+ waitingTime +"sec **********************");
						try {
							Thread.sleep(waitingTime*1000);
						} catch (InterruptedException e) {
							System.err.println("Error in saveResult()... " + e.getMessage());
							break;
						}
					} else {
						saveInDB(scan);
						break;
					}
				}
			}
		}).run();
	}
	
	public void saveInDB(ScanOrder scan) {
		System.out.println("Saving results in DB");
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			Long scanId = scan.getId();
			
			String kbALl = manageScans(ScanAction.GET_VULN_ALL, scanId);
			// master table: (PK, FK=scanner_id, raw_kb)
			System.out.println("Saving KB List Results: PK=<Autogenerated>, FK=" + getScannerId() + ", DATA=" + kbALl);
			// child table: (PK, FK=master_pk, detailed_kb)
			Long idMaster = 999L; // TODO: get real value from DB
			
			json = (JSONObject) parser.parse(kbALl);
			JSONArray items = (JSONArray) json.get("items");
			int size = 0;
			if (null != items && items.size() > 0) {
				size = items.size();
				for (Object o: items) {
					JSONObject item = (JSONObject)o;
					Long id = (Long) item.get("id");
					String kb = manageScans(ScanAction.GET_VULN_BY_ID, scanId, id);
					System.out.println("Saving KB Detail: PK=<Autogenerated>, FK=" + idMaster + ", KB="+ id +", DATA=" + kb);
				}
			}
			System.out.println(size + " KBs saved in DB...");
		} catch (ParseException e) {
			System.err.println("Error saving KB results..." + e.getMessage());
		}
	}

	public Long getScannerId() {
		return Scanner.W3AF.getId();
	}
	
	
}
