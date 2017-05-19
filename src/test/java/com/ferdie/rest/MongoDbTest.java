package com.ferdie.rest;

import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDbTest {
	
	
	@Test
	public void testMongo() {
		String dbURI = "mongodb://admin:admin@localhost:27017";
		try {
			MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
			DB db = mongoClient.getDB("testDB");
			System.out.println(db.getCollectionNames());
			
			// insert
			DBCollection table = db.getCollection("user");
			BasicDBObject document = new BasicDBObject();
			document.put("name", "ferdie");
			document.put("age", 35);
			document.put("createdDate", new Date());
			table.insert(document);
			
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
	

}
