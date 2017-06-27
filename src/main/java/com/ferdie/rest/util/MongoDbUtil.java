package com.ferdie.rest.util;

import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.service.domain.ScanOrder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public enum MongoDbUtil implements Constants {
	instance;
	
	private MongoClient mongoClient;
	final static Logger log = Logger.getLogger(MongoDbUtil.class);
	
	public Long getNextSequence() {
		DB db = getDB();
        DBCollection scanSeq = db.getCollection(SCAN_SEQUENCE);
        
	    BasicDBObject searchQuery = new BasicDBObject("_id", "scanid");
	    BasicDBObject increase = new BasicDBObject("seq", 1);
	    BasicDBObject updateQuery = new BasicDBObject("$inc", increase);
	    DBObject result = scanSeq.findAndModify(searchQuery, updateQuery);
	    return ((Double)result.get("seq")).longValue();
	}

	public void createScan(ScanOrder order) {
		DB db = getDB();
		DBCollection scan = db.getCollection(SCAN_TABLE);
	    BasicDBObject document = new BasicDBObject();
	    document.append("_id", order.getScanId());
	    document.append("scannerId", order.getScannerId());
	    document.append("orderId", order.getOrderId());
	    document.append("status", "Running");
	    document.append("createTs", new Date());
	    scan.insert(document);
	}

	public void updateVulners(Long scanId, JSONArray vulners) {
		DB db;
		try {
			db = getDB();
			DBCollection scan = db.getCollection(SCAN_TABLE);
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject updates = new BasicDBObject();
			updates.append("status", "Completed");
			updates.append("vulnerabilities", vulners);
			newDocument.append("$set", updates);
			
			BasicDBObject searchQuery = new BasicDBObject().append("_id", scanId);
			scan.update(searchQuery, newDocument);
		    
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public String updateStatus(Long scanId, String status) throws Exception {
		DB db;
		try {
			db = getDB();
			DBCollection scan = db.getCollection(SCAN_TABLE);
			
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject updates = new BasicDBObject();
			updates.append("status", status);
			newDocument.append("$set", updates);
			
			BasicDBObject searchQuery = new BasicDBObject().append("_id", scanId);
			scan.update(searchQuery, newDocument);
		    return status;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}
	
	public Object findById(Long scanId, String field) {
		// search
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", scanId);
		Object o;
		try {
			o = findById(scanId);
			if (null != o) {
				return ((DBObject)o).get(field);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	public Object findById(Long scanId) throws Exception {
		DB db = getDB();
		DBCollection table = db.getCollection(SCAN_TABLE);
			
		// search
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", scanId);
		return table.findOne(searchQuery);
	}
	
	@SuppressWarnings("deprecation")
	public DB getDB() {
		if (null == mongoClient) {
			mongoClient = new MongoClient(new MongoClientURI(PropertiesUtil.instance.getProperty(KEY_DB_URI)));	
		}
		return mongoClient.getDB(SCANNER_DB);
	}
	
}
