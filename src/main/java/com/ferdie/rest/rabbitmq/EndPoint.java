package com.ferdie.rest.rabbitmq;

import static com.ferdie.rest.util.PropertiesUtil.PropertiesUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.ferdie.rest.service.domain.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class EndPoint implements Constants {
	final static Logger log = Logger.getLogger(EndPoint.class);
	protected Channel channel;
	protected Connection connection;
	protected String endPointName;

	public EndPoint(String endpointName) throws IOException, TimeoutException {
		this.endPointName = endpointName;

		// Create a connection factory
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(PropertiesUtil.getProperty(KEY_RABBITMQ_HOST));
		factory.setVirtualHost(RABBITMQ_VHOST);

		// getting a connection
		connection = factory.newConnection();

		// creating a channel
		channel = connection.createChannel();

		// declaring a queue for this channel. If queue does not exist,
		// it will be created on the server.
		channel.queueDeclare(endpointName, false, false, false, null);
		channel.basicQos(1);
	}

	/**
	 * Close channel and connection. Not necessary as it happens implicitly any
	 * way.
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void close() throws IOException, TimeoutException {
		this.channel.close();
		this.connection.close();
	}
}
