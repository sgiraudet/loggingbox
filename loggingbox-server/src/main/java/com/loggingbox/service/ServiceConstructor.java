package com.loggingbox.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.log.harvester.SystemHarvester;
import com.loggingbox.servlet.LogGetServlet;
import com.loggingbox.servlet.LogInsertServlet;
import com.loggingbox.servlet.LogsInsertServlet;

/**
 * Manages the construction and initialisation of server side components.
 * 
 * This class implements ServletContextListener, and is notified about changes
 * to the servlet context of the web application. It listens for context
 * initialized events and initialises the server components when this occurs.
 * 
 * @author Stanislas Giraudet
 * 
 */
public final class ServiceConstructor implements ServletContextListener {

	private final static String DEFAULT_PROPERTIES_FILE = "loggingbox.properties";

	private static final Logger LOGGER = Logger
			.getLogger(ServiceConstructor.class);

	public ServiceConstructor() {
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// setup netsas configuration

		WebApplicationContext context = ContextLoaderListener
				.getCurrentWebApplicationContext();

		ServletContext servletContext = event.getServletContext();
		servletContext.setInitParameter(
				"org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		servletContext.setInitParameter("dirAllowed", "false");
		servletContext.setInitParameter("redirectWelcome", "true");

		ServerProperties serverProperties = context
				.getBean(ServerProperties.class);

		URL url = getClass().getClassLoader().getResource(
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

		
		
		servletContext.addServlet(LogGetServlet.class.getName(),
				context.getBean(LogGetServlet.class)).addMapping(
				"/api/log/get");

		servletContext.addServlet(LogInsertServlet.class.getName(),
				context.getBean(LogInsertServlet.class)).addMapping(
				"/api/log/insert");

		servletContext.addServlet(LogsInsertServlet.class.getName(),
				context.getBean(LogsInsertServlet.class)).addMapping(
				"/api/log/insert_batch");
		
		SystemHarvester systemHarvester = new SystemHarvester("localhost", "8080", "test");
		systemHarvester.setDelayInMs(10000);
		systemHarvester.start();
		
	}

}
