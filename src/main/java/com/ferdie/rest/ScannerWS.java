package com.ferdie.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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

	ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	
	// /scan?scannerId=1
	@GET
	@Path("/scan")
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
	public Response getScanStatus(@QueryParam("scanId") Long scanId) {
		String result = svcFacade.getScanStatus(scanId);
		return Response.ok(result).build();
	}
	
	// /vulner?scanId=123
	@GET
	@Path("/vulner")
	public Response getVulnerabilities(@QueryParam("scanId") Long scanId) {
		String result = svcFacade.getVulnerabilities(scanId);
		return Response.ok(result).build();
	}
}
