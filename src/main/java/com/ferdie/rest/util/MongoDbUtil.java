package com.ferdie.rest.util;

import java.util.Date;

import org.json.simple.JSONArray;

import com.ferdie.rest.service.domain.Constants;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public enum MongoDbUtil implements Constants {
	instance;
	
	private MongoClient mongoClient;
	
	public Long getNextSequence() throws Exception {
		DB db = getDB();
        DBCollection scanSeq = db.getCollection(SCAN_SEQUENCE);
        
	    BasicDBObject searchQuery = new BasicDBObject("_id", "scanid");
	    BasicDBObject increase = new BasicDBObject("seq", 1);
	    BasicDBObject updateQuery = new BasicDBObject("$inc", increase);
	    DBObject result = scanSeq.findAndModify(searchQuery, updateQuery);
	    return ((Double)result.get("seq")).longValue();
	}

	public Long createScan(Long scannerId, Long orderId) throws Exception {
		DB db;
		try {
			db = getDB();
			DBCollection scan = db.getCollection(SCAN_TABLE);
		    BasicDBObject document = new BasicDBObject();
		    Long scanId = getNextSequence();
		    document.append("_id", scanId);
		    document.append("scannerId", scannerId);
		    document.append("orderId", orderId);
		    document.append("status", "Running");
		    document.append("createTs", new Date());
		    scan.insert(document);
		    return scanId;
		} catch (Exception e) {
			throw e;
		}
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
			e.printStackTrace();
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
			throw e;
		}
	}
	
	public Object findById(Long scanId, String field) throws Exception {
		// search
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", scanId);
		Object o = findById(scanId);
		if (null != o) {
			return ((DBObject)o).get(field);
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
	
	public DB getDB() throws Exception {
		if (null == mongoClient) {
			mongoClient = new MongoClient(new MongoClientURI(PropertiesUtil.instance.getProperty(KEY_DB_URI)));	
		}
		return mongoClient.getDB(SCANNER_DB);
	}
	
}
