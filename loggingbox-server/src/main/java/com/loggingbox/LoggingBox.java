package com.loggingbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.GzipHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.log.harvester.SystemHarvester;
import com.loggingbox.service.ServerProperties;
import com.loggingbox.servlet.LogGetServlet;
import com.loggingbox.servlet.LogInsertServlet;
import com.loggingbox.servlet.LogsInsertServlet;

public class LoggingBox {

	private final static String DEFAULT_PROPERTIES_FILE = "loggingbox.properties";

	private static final Logger LOGGER = Logger.getLogger(LoggingBox.class);

	public static void main(String[] args) throws Exception {

		ContextLoaderListener contextLoaderListener = new ContextLoaderListener();
		Server server = new Server(8080);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("war");

		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.setContextPath("/api");
		servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM,
				"classpath*:applicationContext.xml");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, servletContext,
				new DefaultHandler() });

		GzipHandler gzipHandler = new GzipHandler();
		gzipHandler.setHandler(handlers);

		// GZipRequestHandler gZipRequestHandler = new GZipRequestHandler();
		// gZipRequestHandler.setHandler(gzipHandler);
		server.setHandler(gzipHandler);

		server.start();

		contextLoaderListener.contextInitialized(new ServletContextEvent(
				servletContext.getServletContext()));

		WebApplicationContext context = ContextLoaderListener
				.getCurrentWebApplicationContext();

		ServerProperties serverProperties = context
				.getBean(ServerProperties.class);
		URL url = LoggingBox.class.getClassLoader().getResource(
				DEFAULT_PROPERTIES_FILE);
		if (url != null) {
			LOGGER.info(String.format("Found default file conf {%s}",
					url.toString()));
			try {
				serverProperties.load(new FileInputStream(new File(url
						.getFile())));
				LOGGER.info(url.getFile() + " has been loaded. ");
			} catch (FileNotFoundException e) {
				LOGGER.error(String.format(
						"Failed to load default configuration file {%s}",
						DEFAULT_PROPERTIES_FILE), e);
			} catch (IOException e) {
				LOGGER.error(String.format(
						"Failed to load default configuration file {%s}",
						DEFAULT_PROPERTIES_FILE), e);
			}
		} else {
			LOGGER.error(DEFAULT_PROPERTIES_FILE
					+ " file has not been found in your classpath. ");
		}

		servletContext.addServlet(
				new ServletHolder(context.getBean(LogGetServlet.class)),
				"/log/get");
		servletContext.addServlet(
				new ServletHolder(context.getBean(LogInsertServlet.class)),
				"/log/insert");
		servletContext.addServlet(
				new ServletHolder(context.getBean(LogsInsertServlet.class)),
				"/log/insert_batch");

		SystemHarvester systemHarvester = new SystemHarvester(InetAddress
				.getLocalHost().getHostName(), "8080", "test");
		systemHarvester.setDelayInMs(10000);
		systemHarvester.start();

		server.join();
	}
}
