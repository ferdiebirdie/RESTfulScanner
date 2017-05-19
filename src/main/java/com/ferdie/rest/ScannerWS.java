package com.ferdie.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.ferdie.rest.service.ScannerServiceFacade;

@Path("/scanner")
public class ScannerWS {

	ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	
	@GET
	@Path("/scan/{scannerId}")
	public Response scan(@PathParam("scannerId") Long scannerId, @QueryParam("urls") final List<String> targetUrls) {
		String result = svcFacade.scan(scannerId, targetUrls);
		return Response.ok(result).build();
	}
	
	@GET
	@Path("/status/{scannerId}/{scanId}")
	public Response getScanStatus(@PathParam("scannerId") Long scannerId, @PathParam("scanId") Long scanId) {
		String result = svcFacade.getScanStatus(scannerId, scanId);
		return Response.ok(result).build();
	}
	
	/*@GET
	@Path("/logs/{scannerId}/{scanId}")
	public Response getLogs(@PathParam("scannerId") Long scannerId, @PathParam("scanId") Long scanId) {
		String result = svcFacade.getScanLogs(scannerId, scanId);
		return Response.ok(result).build();
	}*/
	
	/*@GET
	@Path("/active/{scannerId}")
	public Response getActiveScans(@PathParam("scannerId") Long scannerId) {
		String result = svcFacade.getActiveScans(scannerId);
		return Response.ok(result).build();
	}*/

}
