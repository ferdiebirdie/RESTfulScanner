package com.ferdie.rest.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Constants;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;

public class RabbitMqTest implements Constants {
	final static Logger log = Logger.getLogger(RabbitMqTest.class);
	
	public static void main(String[] args) throws IOException, TimeoutException {
//		QueueConsumer consumer = new QueueConsumer(RABBITMQ_QUEUE_NAME);
//		Thread consumerThread = new Thread(consumer);
//		consumerThread.start();
		
		ScannerServiceFacade fac = new ScannerServiceFacade();
		List<String> urls = new ArrayList<String>();
		urls.add("http://www.webscantest.com/rest/demo");
		urls.add("http://www.webscantest.com/react");
		urls.add("http://www.webscantest.com/business");
		urls.add("http://www.webscantest.com/static/");
		urls.add("http://www.webscantest.com/datastore/");
		urls.add("http://www.webscantest.com/crosstraining/aboutyou.php");
		urls.add("http://www.webscantest.com/crosstraining/blockedbyns.php");
		urls.add("http://www.webscantest.com/crosstraining/aboutyou2.php");
		urls.add("http://www.webscantest.com/crosstraining/linkout.php?name=Rake");
		urls.add("http://www.webscantest.com/crosstraining/products.php");
		urls.add("http://www.webscantest.com/crosstraining/sitereviews.php");
		urls.add("http://www.webscantest.com/crosstraining/reservation.php");
		urls.add("http://www.webscantest.com/crosstraining/search.php");
		urls.add("http://www.webscantest.com/crosstraining/checkitem.php");
		urls.add("http://www.webscantest.com/crosstraining/dom.php");
		
		for (String url : urls) { 
			fac.queueScan(1, url, fac.getNextScanId());
		}
	}
	
	public static void main_(String[] args) throws IOException, TimeoutException {
		QueueConsumer consumer = new QueueConsumer(RABBITMQ_QUEUE_NAME);
		GetResponse response = consumer.channel.basicGet(consumer.endPointName, false);
		consumer.channel.basicQos(999);
		if (response == null) {
		    // No message retrieved.
		} else {
		    AMQP.BasicProperties props = response.getProps();
		    byte[] body = response.getBody();
		    long deliveryTag = response.getEnvelope().getDeliveryTag();
		    System.out.println(new String(body));
		}
		consumer.close();
	}

}
