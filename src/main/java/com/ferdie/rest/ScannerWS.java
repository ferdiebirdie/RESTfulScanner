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
	public Response scan(@QueryParam("scannerId") Long scannerId, @QueryParam("urls") final List<String> targetUrls) {
		ScanOrder result = svcFacade.scan(scannerId, targetUrls);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = mapper.writeValueAsString(result);
			return Response.ok(jsonInString).build();
		} catch (JsonProcessingException e) {
			return Response.ok(result.toString()).build();
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
		}
		return "";

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
		}
		return "";
	}
	
	// /delete?id=123
	@GET
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteScan(@QueryParam("id") Long orderId) {
		log.debug("Deleting scan order id=" + orderId + "...");
		String result = svcFacade.deleteScan(orderId);
		return Response.ok(result).build(); 
	}
}
