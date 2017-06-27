package com.ferdie.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Constants;

/**
 * @author ferdie
 *
 */
@Path("/scanner")
public class ScannerWS implements Constants {
	final static Logger log = Logger.getLogger(ScannerWS.class);
	ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	
	// /scan?scannerId=1
	@GET
	@Path("/scan")
	@Produces(MediaType.APPLICATION_JSON)
	public String scan(@QueryParam("scannerId") Long scannerId, @QueryParam("url") final String url) {
		return svcFacade.queueScan(scannerId, url);
	}
	
	// /status?scanId=123
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public String getScanStatus(@QueryParam("scanId") Long scanId) {
		return svcFacade.getScanStatus(scanId);
	}
	
	// /vulner?scanId=123
	@GET
	@Path("/vulner")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVulnerabilities(@QueryParam("scanId") Long scanId) {
		return svcFacade.getVulnerabilities(scanId);
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
