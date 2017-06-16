package com.ferdie.rest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Constants;

public enum PropertiesUtil implements Constants {
	instance;
	
	final static Logger log = Logger.getLogger(ScannerServiceFacade.class);
	private Properties props;
	
	public String getProperty(String key) {
		if (null == props) {
			props = new Properties();
			InputStream is = null;
			
			try {
				is = PropertiesUtil.class.getResourceAsStream("/config.properties");
//				is = new FileInputStream(CONFIG_FILE);
				props.load(is);
			} catch (IOException e) {
				log.error("", e);
			} finally {
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}
		return props.getProperty(key);
	}
	
}
