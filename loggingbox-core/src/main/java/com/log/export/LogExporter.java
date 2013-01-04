package com.log.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.log.model.Log;
import com.log.model.command.Export;
import com.log.model.result.ExportResult;
import com.log.storage.LogAccessor;

@Component
public class LogExporter {

	private static final Logger LOGGER = Logger.getLogger(LogExporter.class);

	private final static int PAGE_SIZE = 1000;

	@Autowired
	LogAccessor logAccessor;

	@Autowired
	Properties properties;

	public ExportResult exportLogs(Export export) {
		try {
			// Create file

			int fileId = Math.abs(new Random().nextInt());
			File tempFile = new File(
					properties.getProperty("com.loggingbox.export.filepath")
							+ "/" + fileId + ".txt");
			tempFile.deleteOnExit();
			FileWriter fstream = new FileWriter(tempFile);
			BufferedWriter out = new BufferedWriter(fstream);

			List<Log> logs = logAccessor.getLogs(export.getApplicationId(),
					export.getFromDate(), export.getToDate(), null, PAGE_SIZE);
			String lastLogId = null;
			while (logs != null && !logs.isEmpty()) {
				for (Log log : logs) {
					out.write(log.getData());
					out.newLine();
					lastLogId = log.getId();
				}
				logs = logAccessor.getLogs(export.getApplicationId(),
						export.getFromDate(), export.getToDate(), lastLogId,
						PAGE_SIZE);
			}

			out.close();

			ExportResult result = new ExportResult();
			result.setApplicationId(export.getApplicationId());
			result.setFileId(fileId);
			return result;

		} catch (Exception ex) {
			LOGGER.error("Failed to export logs", ex);
		}
		return null;
	}
}
