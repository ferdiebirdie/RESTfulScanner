package com.ferdie.rest.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public enum CommonUtil {
	CommonUtil;
	
	final static Logger log = Logger.getLogger(CommonUtil.class);
	
	public String encodeParam(String param) {
		try {
			return URLEncoder.encode(StringUtils.trimToEmpty(param), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return param;
		}
	}
}
