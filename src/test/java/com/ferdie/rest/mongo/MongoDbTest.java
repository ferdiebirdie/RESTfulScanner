package com.ferdie.rest.mongo;

import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDbTest {
	
	@Test
	public void testMongo() {
		String dbURI = "mongodb://admin:admin@localhost:27017";
		try {
			MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
			DB db = mongoClient.getDB("testDB");
			DBCollection table = db.getCollection("user");
			
			// insert
			BasicDBObject doc = new BasicDBObject();
			doc.put("name", "ferdie");
			doc.put("age", 35);
			doc.put("createdDate", new Date());
			table.insert(doc);
			
			System.out.println("ID: " + doc.get("_id"));
			
			// search
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", "ferdie");
			DBCursor cursor = table.find(searchQuery);
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	public void findById() throws Exception {
		findById(4L);
	}
	
	private void findById(Long id) throws Exception {
		DB db = MongoUtil.getInstance().getDB();
		DBCollection table = db.getCollection("scan");
			
		// search
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", id);
		DBCursor cursor = table.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject o = cursor.next();
			System.out.println(o);
		}
	}
	
	@Test
	public void testCreateScan() throws Exception {
		Long id = MongoUtil.getInstance().createScan(1L, 1L);
		System.out.println("ID: " + id);
		findById(id);
	}
	
	@Test
	public void testUpdateScan() throws Exception {
		Long id = 8L;
		MongoUtil.getInstance().updateVulners(id, null);
		findById(id);
	}

	@Test
	public void testGetNextSequence() throws Exception {
		System.out.println(MongoUtil.getInstance().getNextSequence());
	}
}
