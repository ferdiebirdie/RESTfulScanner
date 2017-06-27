package com.ferdie.rest.service.domain;

import org.apache.log4j.Logger;

public enum Scanner {
	W3AF(1L),
	DEFAULT(-1L)
	;

	final static Logger log = Logger.getLogger(Scanner.class);
	private Long scannerId;
	
	private Scanner(Long scannerId) {
		this.scannerId = scannerId;
	}
	
	public Long getId() {
		return this.scannerId;
	}
	
	public static Scanner toScanner(Long id) {
		Scanner[] scanners = values();
		for (Scanner scanner : scanners) {
			if (scanner.getId().longValue() == id.longValue()) {
				return scanner;
			}
		}
		return Scanner.DEFAULT;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[ ");
		Scanner[] vals = values();
		for (Scanner scanner : vals) {
			if (sb.length() > 2) {
				sb.append(", ");
			}
			sb.append(scanner.getId());
			if (scanner == DEFAULT) {
				sb.append(" (default)");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}
	
}
