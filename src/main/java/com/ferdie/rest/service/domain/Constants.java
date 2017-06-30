package com.ferdie.rest.service.domain;

public interface Constants {
	
	String SCANNER_ID = "scannerId";
	String SCAN_ID = "scanId";
	String URL = "url";
	String SCAN_TABLE = "scan";
	String SCAN_SEQUENCE = "scanSeq";
	
	// Scan Statuses
	String SHEDULED = "scheduled";
	String RUNNING = "running";
	String COMPLETED = "completed";
	String FAILED = "failed";
	
	// WEBSERVICES
	String KEY_WS_URL_W3AF = "ws.url.w3af";
	
	// MONGO DB
	String SCANNER_DB = "ScannerDB";
	String KEY_DB_URI = "db.uri";
	
	// messages
	String MSG_NOTHING_DELETED = "{\"message\": \"Nothing deleted.\"}";
	String MSG_NOT_FOUND = "{\"message\": \"Not found.\"}";
	String MSG_NO_VULNERS = "{\"message\": \"No vulnerabilities found.\"}";
	String MSG_SCAN_INPROGRESS = "{\"message\": \"Scan in progress.\"}";
	String MSG_ERR_LOGS = "{\"message\": \"Error was encountered. Check logs for details.\"}";
	
	// RabbitMQ
	String RABBITMQ_QUEUE_NAME = "vsqueue";
	String RABBITMQ_VHOST = "vsrabbit_vhost";
	String KEY_RABBITMQ_HOST = "rabbitmq.host";
	
	
}
