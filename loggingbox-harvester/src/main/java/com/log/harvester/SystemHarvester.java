package com.log.harvester;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

public class SystemHarvester extends Thread {

	private static final Logger LOGGER = Logger
			.getLogger(SystemHarvester.class);

	private boolean stop;

	private int delayInMs = 1000;
	
	private LogSender logSender;
	public SystemHarvester(String host, String port,
			String applicationId) {

		logSender = new LogSender();
		logSender.setApplicationId(applicationId);
		logSender.setLoggingBoxPort(port);
		logSender.setLoggingBoxHost(host);
		logSender.start();
	}

	@Override
	public void run() {
		stop = false;
		try {
			while (true) {
				if (!stop) {
					harvestSystemLogs();

					synchronized (this) {
						wait(delayInMs);
					}
				} else {
					break;
				}

			}
		} catch (InterruptedException e) {
		}
	}

	private void harvestSystemLogs() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss.SSS", Locale.US);
		String dateString = String.format(" %s  - ", dateFormat.format(date));

		long freeMemory = Runtime.getRuntime().freeMemory();

		logSender.sendLog(
				date,
				"Free Memory",
				dateString
						+ String.format("Free Memory : %s o, %s ko, %s Mo",
								freeMemory, freeMemory / 1000,
								freeMemory / 1000000), "DEBUG", null);

		long totalMemory = Runtime.getRuntime().totalMemory();
		logSender.sendLog(
				date,
				"Total Memory",
				dateString
						+ String.format("Total Memory : %s o, %s ko, %s Mo",
								totalMemory, totalMemory / 1000,
								totalMemory / 1000000), "DEBUG", null);

		/* Get a list of all filesystem roots on this system */
		File[] roots = File.listRoots();

		/* For each filesystem root, print some info */
		for (File root : roots) {

			logSender
					.sendLog(
							date,
							"Disk space",
							dateString
									+ String.format(
											"Disk %s : Total space (Mo): %s, Free space (Mo): %s, Usable space (Mo): %s ",
											root.getAbsolutePath(),
											root.getTotalSpace() / 1000000,
											root.getFreeSpace() / 1000000,
											root.getUsableSpace() / 1000000),
							"DEBUG", null);

		}
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public int getDelayInMs() {
		return delayInMs;
	}

	public void setDelayInMs(int delayInMs) {
		this.delayInMs = delayInMs;
	}

}
