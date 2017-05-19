package com.ferdie.rest.service.domain;

public enum Scanner {

	W3AF(1L),
	DEFAULT(-1L)
	;
	
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
			if (scanner.getId() == id) {
				return scanner;
			}
		}
		return Scanner.DEFAULT;
	}
	
}
