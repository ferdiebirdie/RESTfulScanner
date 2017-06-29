package com.ferdie.rest.service.domain;

import org.apache.log4j.Logger;

public enum Scanner {
	W3AF(1)
	// other scanners here
	;

	final static Logger log = Logger.getLogger(Scanner.class);
	private Integer scannerId;
	
	private Scanner(Integer scannerId) {
		this.scannerId = scannerId;
	}
	
	public Integer getId() {
		return this.scannerId;
	}
	
	public static Scanner toScanner(Integer id) {
		if (null != id) {
			Scanner[] scanners = values();
			for (Scanner scanner : scanners) {
				if (scanner.getId().intValue() == id.intValue()) {
					return scanner;
				}
			}			
		}
		return Scanner.W3AF; // default
	}
	
	public static boolean isValid(Integer id) {
		if (null != id) {
			Scanner[] scanners = values();
			for (Scanner scanner : scanners) {
				if (scanner.getId().intValue() == id.intValue()) {
					return true;
				}
			}			
		}
		return false; // default
	}
	
}
