package com.ferdie.rest.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Constants;

public class RabbitMqTest implements Constants {
	final static Logger log = Logger.getLogger(RabbitMqTest.class);
	
	public static void main(String[] args) throws IOException, TimeoutException {
//		QueueConsumer consumer = new QueueConsumer(RABBITMQ_QUEUE_NAME);
//		Thread consumerThread = new Thread(consumer);
//		consumerThread.start();
		
		ScannerServiceFacade fac = new ScannerServiceFacade();
		List<String> urls = new ArrayList<String>(2);
		urls.add("http://www.webscantest.com/shutterform");
		urls.add("http://www.webscantest.com/crosstraining");
		for (String url : urls) {
			System.out.println(fac.queueScan(1L, url, fac.getNextScanId()));
		}
	}

}
