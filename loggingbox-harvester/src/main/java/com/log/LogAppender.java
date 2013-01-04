package com.log;

import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.log.harvester.LogSender;

public class LogAppender extends AppenderSkeleton {

	private String host;
	private String port;
	private String applicationId;
	private String type;

	private LogSender logSender;

	/**
	 * Constructs an unconfigured appender.
	 */
	public LogAppender() {
	}

	/**
	 * Creates a configured appender.
	 * 
	 * @param layout
	 *            layout, may not be null.
	 * 
	 */
	public LogAppender(Layout layout, String host, String port,
			String applicationId) {

		setLayout(layout);
		setHost(host);
		setPort(port);
		setApplicationId(applicationId);
		activateOptions();
	}

	@Override
	protected void append(LoggingEvent event) {
		String message = this.layout.format(event);
		if (layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for (int i = 0; i < len; i++) {
					message += s[i];
					message += Layout.LINE_SEP;
				}
			}
		}
		if(type == null) {
			type = "";
		}
		getLogSender().sendLog(new Date(), type, message, event.getLevel()
				.toString(), null);

	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	protected LogSender getLogSender() {
		if(logSender == null ){
			logSender = new LogSender();
			logSender.setApplicationId(applicationId);
			logSender.setLoggingBoxPort(port);
			logSender.setLoggingBoxHost(host);
			logSender.start();
		}
		return logSender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}