package com.ferdie.rest.util;

import static com.ferdie.rest.util.PropertiesUtil.PropertiesUtil;

import java.util.Date;

import org.apache.log4j.Level;
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
	MongoDbUtil;
	
	private MongoClient mongoClient;
	final static Logger log = Logger.getLogger(MongoDbUtil.class);
	
	private MongoDbUtil() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.FATAL);
	}
	
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
		try {
			DB db = getDB();
			DBCollection scan = db.getCollection(SCAN_TABLE);
		    BasicDBObject document = new BasicDBObject();
		    document.append("_id", order.getScanId());
		    document.append("scannerId", order.getScannerId());
		    document.append("orderId", order.getOrderId());
		    document.append("status", order.getStatus());
		    document.append("createTs", new Date());
		    scan.insert(document);
		    log.debug("Created record in DB");
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void updateVulners(Long scanId, JSONArray vulners) {
		DB db;
		try {
			db = getDB();
			DBCollection scan = db.getCollection(SCAN_TABLE);
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject updates = new BasicDBObject();
			updates.append("vulnerabilities", vulners);
			newDocument.append("$set", updates);
			
			BasicDBObject searchQuery = new BasicDBObject().append("_id", scanId);
			scan.update(searchQuery, newDocument);
		    
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void updateStatus(Long scanId, String status) {
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
		} catch (Exception e) {
			log.error("Failed updating status to " + status + "[scanId=" + scanId + "]:", e);
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
				Object v = ((DBObject)o).get(field);
				if (null == v) {
					return "";
				}
				return v;
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
			mongoClient = new MongoClient(new MongoClientURI(PropertiesUtil.getProperty(KEY_DB_URI)));	
		}
		return mongoClient.getDB(SCANNER_DB);
	}
	
}
