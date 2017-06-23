package com.ferdie.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.ScanOrder;

/**
 * @author ferdie
 *
 */
@Path("/scanner")
public class ScannerWS {
	final static Logger log = Logger.getLogger(ScannerWS.class);
	ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	
	// /scan?scannerId=1
	@GET
	@Path("/scan")
	@Produces(MediaType.APPLICATION_JSON)
	public String scan(@QueryParam("scannerId") Long scannerId, @QueryParam("urls") final List<String> targetUrls) {
		ScanOrder result = svcFacade.scan(scannerId, targetUrls);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			log.error(e);
			return "Error";
		}
	}
	
	// /status?scanId=123
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public String getScanStatus(@QueryParam("scanId") Long scanId) {
		String result = svcFacade.getScanStatus(scanId);
		ObjectMapper mapper = new ObjectMapper();
		JSONObject json;
		try {
			json = mapper.readValue(result, JSONObject.class);
			json.remove("vulnerabilities");
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			log.error(e);
			return "Error";
		}
	}
	
	// /vulner?scanId=123
	@GET
	@Path("/vulner")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVulnerabilities(@QueryParam("scanId") Long scanId) {
		String result = svcFacade.getVulnerabilities(scanId);
		ObjectMapper mapper = new ObjectMapper();
		Object json;
		try {
			json = mapper.readValue(result, Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			log.error(e);
			return "Error";
		}
	}
	
	// /delete?id=123
	@GET
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteScan() {
		log.debug("Deleting active scan order...");
		String result = svcFacade.deleteScan();
		return Response.ok(result).build(); 
	}
}
