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
		List<String> urls = new ArrayList<String>(3);
		urls.add("http://www.webscantest.com/shutterform");
		urls.add("https://ci-test-beta.promotexter.com");
		urls.add("http://www.webscantest.com/crosstraining");
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
