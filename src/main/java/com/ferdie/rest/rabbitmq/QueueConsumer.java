package com.ferdie.rest.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.ferdie.rest.service.ScannerServiceFacade;
import com.ferdie.rest.service.domain.Constants;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueConsumer extends EndPoint implements Runnable, Consumer, Constants {
	final static Logger log = Logger.getLogger(QueueConsumer.class);

	ScannerServiceFacade svcFacade = new ScannerServiceFacade();
	
	public QueueConsumer(String endPointName) throws IOException, TimeoutException {
		super(endPointName);
	}
	
	public static void main(String[] args) {
		QueueConsumer consumer;
		try {
			consumer = new QueueConsumer(RABBITMQ_QUEUE_NAME);
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();
		} catch (IOException | TimeoutException e) {
			log.error(e);
		}
	}

	public void run() {
		try {
			// start consuming messages. Auto acknowledge messages.
			channel.basicConsume(endPointName, true, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when consumer is registered.
	 */
	public void handleConsumeOk(String consumerTag) {
		log.info("Consumer " + consumerTag + " registered");
	}

	/**
	 * Called when new message is available.
	 */
	public void handleDelivery(String consumerTag, Envelope env, BasicProperties props, byte[] body) throws IOException {
		Map map = (HashMap) SerializationUtils.deserialize(body);
		//log.debug("Message Received: " + map);
		svcFacade.scan(map);
	}

	public void handleCancel(String consumerTag) {
	}

	public void handleCancelOk(String consumerTag) {
	}

	public void handleRecoverOk(String consumerTag) {
	}

	public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
	}

}
