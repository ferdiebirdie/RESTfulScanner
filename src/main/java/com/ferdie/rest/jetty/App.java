package com.ferdie.rest.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class App {

	public static void main(String[] args) {
		ResourceConfig config = new ResourceConfig();
		config.packages("com.ferdie.rest", "com.jersey.jaxb", "com.fasterxml.jackson.jaxrs.json");
		ServletHolder servlet = new ServletHolder(new ServletContainer(config));

		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(server, "/*");
		context.addServlet(servlet, "/*");

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}
	}

}
