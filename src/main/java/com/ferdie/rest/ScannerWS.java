package com.ferdie.rest;

import static com.ferdie.rest.util.CommonUtil.CommonUtil;
import static com.ferdie.rest.util.JsonUtil.JsonUtil;
import static com.ferdie.rest.util.ValidatorUtil.ValidatorUtil;

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
	
	// /scan?scannerId=1&url=
	@GET
	@Path("/scan")
	@Produces(MediaType.APPLICATION_JSON)
	public String scan(@QueryParam("scannerId") String scannerId, @QueryParam("url") String url) {
		if (!ValidatorUtil.isValidScannerId(scannerId)) {
			return JsonUtil.prettyPrint("{\"message\" : \"Invalid parameter: scannerId - " + CommonUtil.encodeParam(scannerId) + "\"}");
		} else if (!ValidatorUtil.isValidUrl(url)) {
			return JsonUtil.prettyPrint("{\"message\" : \"Invalid parameter: url - " + CommonUtil.encodeParam(url) + "\"}");
		}
		return svcFacade.queueScan(Long.parseLong(scannerId), url);
	}
	
	// /status?scanId=123
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public String getScanStatus(@QueryParam("scanId") String scanId) {
		if (!ValidatorUtil.isValidScanId(scanId)) {
			return JsonUtil.prettyPrint("{\"message\" : \"Invalid parameter: scannerId - " + CommonUtil.encodeParam(scanId) + "\"}");
		}
		return svcFacade.getScanStatus(Long.parseLong(scanId));
	}
	
	// /vulner?scanId=123
	@GET
	@Path("/vulner")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVulnerabilities(@QueryParam("scanId") String scanId) {
		if (!ValidatorUtil.isValidScanId(scanId)) {
			return JsonUtil.prettyPrint("{\"message\" : \"Invalid parameter: scannerId - " + CommonUtil.encodeParam(scanId) + "\"}");
		}
		return svcFacade.getVulnerabilities(Long.parseLong(scanId));
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
