package com.ferdie.rest.mongo;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ferdie.rest.service.domain.Constants;
import com.ferdie.rest.util.MongoDbUtil;
import com.ferdie.rest.util.PropertiesUtil;
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
		DB db = MongoDbUtil.instance.getDB();
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
		Long id = MongoDbUtil.instance.createScan(1L, 1L);
		log.debug("ID: " + id);
		findById(id);
	}
	
	@Test
	public void testUpdateScan() throws Exception {
		Long id = 8L;
		MongoDbUtil.instance.updateVulners(id, null);
		findById(id);
	}

	@Test
	public void testGetNextSequence() throws Exception {
		log.debug(MongoDbUtil.instance.getNextSequence());
	}
	
	@Test
	public void test() throws Exception {
		MongoClient mongoClient = new MongoClient(new MongoClientURI(PropertiesUtil.instance.getProperty(KEY_DB_URI)));
		System.out.println(mongoClient);
		mongoClient.close();
	}
}
