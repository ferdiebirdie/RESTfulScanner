package com.ferdie.rest.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.service.domain.ScanAction;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.service.domain.Scanner;
import com.ferdie.rest.util.MongoDbUtil;
import com.ferdie.rest.util.PropertiesUtil;

public class W3afScannerService implements ScannerService, Constants {
	final static Logger log = Logger.getLogger(W3afScannerService.class);

	public ScanOrder scan(List<String> targetUrls) {
		ScanOrder scan = null;
		JSONObject active = getActiveScan();
		if (null != active) {
			scan = new ScanOrder(active.toJSONString());
			scan.setMessage("Previous scan still running. Try again later.");
			scan.setOrderId((Long) active.get("id"));
			return scan;
		}
		log.debug("------------------ Scan Started ------------------");
		Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
		WebTarget webTarget = client.target(PropertiesUtil.instance.getProperty(KEY_WS_URL_W3AF) + "/scans/");

		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("scan_profile", getProfile());
		json.put("target_urls", targetUrls);

		String input = JSONValue.toJSONString(json);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(input, MediaType.APPLICATION_JSON));

		String result = response.readEntity(String.class);
		scan = new ScanOrder(getScannerId(), result);
		return scan;

	}

	public String deleteActiveScan() {
		JSONObject json = getActiveScan();
		Long id = (Long) json.get("id");
		return manageScanOrder(ScanAction.DELETE, id);
	}

	public String getScanStatus(Long orderId) {
		return manageScanOrder(ScanAction.GET_STATUS, orderId);
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
			WebTarget webTarget = client.target(PropertiesUtil.instance.getProperty(KEY_WS_URL_W3AF) + reqUrl);

			Invocation.Builder invocationBuilder = webTarget.request();
			Response response = invocationBuilder.delete();

			return response.readEntity(String.class);
		case GET_ACTIVE_SCANS:
			break;
		default:
			break;
		}
		Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFeature.class));
		WebTarget webTarget = client.target(PropertiesUtil.instance.getProperty(KEY_WS_URL_W3AF) + reqUrl);

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

	public boolean isScanActive() {
		return null != getActiveScan();
	}
	
	public boolean isScanRunning() {
		JSONObject scan = getActiveScan();
		return null != scan && "Running".equals(scan.get("status"));
	}

	public void save(final ScanOrder scan) {
		try {
			Long scanId = MongoDbUtil.instance.createScan(scan.getScannerId(), scan.getOrderId());
			scan.setScanId(scanId);
			
			log.info("Acquired scanId=" + scan.getScanId());
			// give enough time for w3af to process first
			try {
				log.debug("Giving w3af enough time to process (5sec)....");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error("Error: ", e);
			}
			if (isScanActive()) {
				log.debug("Started saving vulnerabilities...");
				Runnable r = new Runnable() {
					public void run() {
						final int waitingTime = 60;
						while (true) {
							boolean running = isScanRunning();
							if (running) {
								// add max time, if reached, exit with error message
								log.debug("W3AF scan still in progress, checking after " + waitingTime + "sec...");
								try {
									Thread.sleep(waitingTime * 1000);
								} catch (InterruptedException e) {
									log.error("Error while sleeping", e);
									break;
								}
							} else {
								try {
									saveVulners(scan.getScanId());
									// temporary
									deleteActiveScan();
								} catch (ParseException e) {
									log.error("Skipping scan delete", e);
								}
								break;
							}
						}
					}
				};
				new Thread(r).start();
			}

		} catch (Exception e1) {
			log.error(e1);
		}
	}

	@SuppressWarnings("unchecked")
	public void saveVulners(Long scanId) throws ParseException {
		Long orderId = (Long) MongoDbUtil.instance.findById(scanId, "orderId");
		if (null == orderId) {
			log.warn("Vulnerabilities not saved! ScanId=" + scanId + " not found in database." );
		} else {
			log.debug("Saving vulnerabilities...");
			JSONParser parser = new JSONParser();
			JSONObject json;
			try {
				String kbALl = manageScanOrder(ScanAction.GET_VULN_ALL, orderId);
				json = (JSONObject) parser.parse(kbALl);
				JSONArray items = (JSONArray) json.get("items");
				if (null != items && items.size() > 0) {
					JSONArray details = new JSONArray();
					for (Object o : items) {
						JSONObject item = (JSONObject) o;
						Long id = (Long) item.get("id");
						String kb = manageScanOrder(ScanAction.GET_VULN_BY_ID, orderId, id);
						details.add(parser.parse(kb));
					}
					MongoDbUtil.instance.updateVulners(scanId, details);
					log.debug("Saved " + items.size() + " vulnerabilities.");
				} else {
					log.debug("No vulnerabilities found.");
				}
			} catch (ParseException e) {
				log.error("Error saving vulnerabilities. Check logs.", e);
				throw e;
			}
		}
	}

	public Long getScannerId() {
		return Scanner.W3AF.getId();
	}

	public String getVulnerabilities(Long scanId) {
		try {
			Object o = MongoDbUtil.instance.findById(scanId, "vulnerabilities");
			if (null != o) {
				return o.toString();
			}
			return "None";
		} catch (Exception e) {
			log.error(e);
			return "Error getting vulnerabilities. Check logs.";
		}
	}

}
