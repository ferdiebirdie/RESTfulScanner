package com.ferdie.rest.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

public enum ValidatorUtil {
	ValidatorUtil;

	UrlValidator urlValidator;
	
	private ValidatorUtil() {
		String[] schemes = { "http", "https" };
		urlValidator = new UrlValidator(schemes);
	}
	
	public boolean isValidScanId(String scanId) {
		return isNonNullLong(scanId);
	}

	public boolean isValidScannerId(String scannerId) {
		return isNonNullInt(scannerId);
	}

	public boolean isValidUrl(String url) {
		return urlValidator.isValid(url);
	}

	private boolean isNonNullLong(String theLong) {
		if (StringUtils.isNotBlank(theLong)) {
			try {
				long l = Long.parseLong(theLong);
				return Math.signum(l) == 1; // must be positive non-zero
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	private boolean isNonNullInt(String theInt) {
		if (StringUtils.isNotBlank(theInt)) {
			try {
				int i = Integer.parseInt(theInt);
				return Math.signum(i) == 1; // must be positive non-zero
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}
}
