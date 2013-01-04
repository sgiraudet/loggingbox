package com.log.harvester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class LogSender extends Thread {

	private static final Logger LOGGER = Logger.getLogger(LogSender.class);

	private boolean stop;

	private int errorNumber = 0;
	private int retryDelayInMs = 30000;

	private String applicationId;

	private String loggingBoxHost;
	private String loggingBoxPort;

	private List<Log> writingQueue;
	private List<Log> readingQueue;

	private Object writeSyncObject = new Object();

	private final ObjectMapper objectMapper = new ObjectMapper();
	private String host;

	public LogSender() {
		readingQueue = new ArrayList<LogSender.Log>();
		writingQueue = new ArrayList<LogSender.Log>();

		setPriority(MIN_PRIORITY);
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			host = localMachine.getHostName();
		} catch (java.net.UnknownHostException uhe) {
			LOGGER.warn("Could not resolve hostname.", uhe);
			host = "";
		}
	}

	public final void sendLog(Date date, String type, String logMessage,
			String level, String host) {
		if (stop) {
			return;
		}
		Log log = new Log();
		log.date = date;
		log.level = level;
		log.data = logMessage;
		log.type = type;
		log.applicationId = applicationId;
		if (host == null) {
			log.host = this.host;
		} else {
			log.host = host;
		}

		synchronized (writeSyncObject) {
			writingQueue.add(log);
			writeSyncObject.notifyAll();
		}
	}

	@Override
	public void run() {

		while (true) {
			while (readingQueue.isEmpty()) {
				synchronized (writeSyncObject) {
					if (!writingQueue.isEmpty()) {
						// swap the 2 lists
						List<Log> temp = readingQueue;
						readingQueue = writingQueue;
						writingQueue = temp;
					} else {
						try {
							writeSyncObject.wait();
						} catch (InterruptedException e) {
							LOGGER.error(e);
						}
					}
				}
			}

			sendLogs();
			if (errorNumber > 3) {
				// if we have more the 3 errors here, we empty the list : we
				// don't want out
				// of memory error because server is not responding.
				LOGGER.warn("Some logs won't be sent, because server is not responding");
				break;
			}

			readingQueue.clear();
		}

	}

	private void sendLogs() {
		if (errorNumber > 3) {
			synchronized (this) {
				try {
					this.wait(retryDelayInMs);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}
		try {

			String parameters = objectMapper.writeValueAsString(readingQueue);

			URL url = new URL(String.format(
					"http://%s:%s/api/log/insert_batch", loggingBoxHost,
					loggingBoxPort));
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Encoding", "gzip");
			connection.setUseCaches(false);

			byte b[] = parameters.getBytes("UTF-8");
			GZIPOutputStream gz = new GZIPOutputStream(
					connection.getOutputStream(), b.length);
			gz.write(b, 0, b.length);
			gz.flush();
			gz.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			reader.close();
			errorNumber = 0;
		} catch (MalformedURLException e) {
			LOGGER.error(e);
			errorNumber++;
		} catch (IOException e) {
			LOGGER.error(e);
			errorNumber++;
		}
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getLoggingBoxHost() {
		return loggingBoxHost;
	}

	public String getLoggingBoxPort() {
		return loggingBoxPort;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setLoggingBoxHost(String loggingBoxHost) {
		this.loggingBoxHost = loggingBoxHost;
	}

	public void setLoggingBoxPort(String loggingBoxPort) {
		this.loggingBoxPort = loggingBoxPort;
	}

	public final class Log {

		private String applicationId;
		private String level;
		private Date date;
		private String host;
		private String type;
		private String data;

		/***********************
		 ******* GETTERS *******
		 **********************/

		public String getApplicationId() {
			return applicationId;
		}

		public Date getDate() {
			return date;
		}

		public String getType() {
			return type;
		}

		public String getData() {
			return data;
		}

		public String getLevel() {
			return level;
		}

		public String getHost() {
			return host;
		}

		/***********************
		 ******* SETTERS *******
		 **********************/

		public void setDate(Date date) {
			this.date = date;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setData(String data) {
			this.data = data;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public void setApplicationId(String applicationId) {
			this.applicationId = applicationId;
		}

	}

}
