package com.ferdie.rest.mongo;

import static com.ferdie.rest.util.PropertiesUtil.PropertiesUtil;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.service.domain.ScanOrder;
import com.ferdie.rest.util.MongoDbUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDbTest implements Constants {
	final static Logger log = Logger.getLogger(MongoDbTest.class);
	
	@Test
	public void findById() throws Exception {
		findById(4L);
	}
	
	private void findById(Long id) throws Exception {
		DB db = MongoDbUtil.MongoDbUtil.getDB();
		DBCollection table = db.getCollection("scan");
			
		// search
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", id);
		DBCursor cursor = table.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject o = cursor.next();
			log.debug(o);
		}
	}
	
	@Test
	public void testCreateScan() throws Exception {
		ScanOrder order = new ScanOrder();
		order.setScanId(1L);
		order.setScannerId(1);
		order.setOrderId(1L);
		MongoDbUtil.MongoDbUtil.createScan(order);
		log.debug("ID: " + order.getScanId());
		findById(order.getScanId());
	}
	
	@Test
	public void testUpdateScan() throws Exception {
		Long id = 8L;
		MongoDbUtil.MongoDbUtil.updateVulners(id, null);
		findById(id);
	}

	@Test
	public void testGetNextSequence() throws Exception {
		log.debug(MongoDbUtil.MongoDbUtil.getNextSequence());
	}
	
	@Test
	public void test() throws Exception {
		MongoClient mongoClient = new MongoClient(new MongoClientURI(PropertiesUtil.getProperty(KEY_DB_URI)));
		log.debug(mongoClient);
		mongoClient.close();
	}
}
