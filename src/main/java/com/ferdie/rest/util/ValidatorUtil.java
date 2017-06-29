package com.ferdie.rest.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.ferdie.rest.service.domain.Scanner;

public enum ValidatorUtil {
	ValidatorUtil;

	UrlValidator urlValidator;
	
	private ValidatorUtil() {
		String[] schemes = { "http", "https" };
		urlValidator = new UrlValidator(schemes);
	}
	
	public boolean isValidScanId(String scanId) {
		return isPositiveLong(scanId);
	}

	public boolean isValidScannerId(String scannerId) {
		if (StringUtils.isBlank(scannerId)) {
			return true;
		} else {
			try {
				int id = Integer.parseInt(scannerId);
				if (Math.signum(id) == 1) {
					return Scanner.isValid(id);
				}
			} catch (NumberFormatException e) { ; }
		}
		return false;
		
	}

	public boolean isValidUrl(String url) {
		return urlValidator.isValid(url);
	}

	private boolean isPositiveLong(String theLong) {
		if (StringUtils.isNotBlank(theLong)) {
			try {
				long l = Long.parseLong(theLong);
				return Math.signum(l) == 1; // must be positive non-zero
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

}
