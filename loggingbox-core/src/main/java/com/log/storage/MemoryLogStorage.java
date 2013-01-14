package com.log.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.log.model.Log;
import com.log.model.command.GetLogs;
import com.log.model.result.GetLogsResult;



public class MemoryLogStorage implements LogAccessor {

	private static final Logger LOGGER = Logger
			.getLogger(MemoryLogStorage.class);
	
	private Map<String, TreeMap<String, Log>> logsByApplicationId;

	public MemoryLogStorage() {
		logsByApplicationId = new HashMap<String, TreeMap<String, Log>>();
	}

	@Override
	public synchronized void insertLog(Log log) {
		if (!logsByApplicationId.containsKey(log.getApplicationId())) {
			logsByApplicationId.put(log.getApplicationId(),
					new TreeMap<String, Log>());
		}
		TreeMap<String, Log> logs = logsByApplicationId.get(log
				.getApplicationId());


		String newId = log.getApplicationId() + "_"
				+ (Long.MAX_VALUE - log.getDate().getTime()) + "_"
				+ Math.abs(new Random().nextInt());
		
		log.setId(newId);
		logs.put(log.getId(), log);

	}
	@Override
	public void insertLogs(List<Log> logs) {
		for(Log log : logs) {
			insertLog(log);
		}
		
	}

	@Override
	public  synchronized GetLogsResult getLogs(GetLogs getLogs) {
		GetLogsResult result = new GetLogsResult();
		List<Log> logsList = new ArrayList<Log>();

		TreeMap<String, Log> logs = logsByApplicationId.get(getLogs.getApplicationId());
		if (logs != null && !logs.isEmpty()) {
			String startKey = null;
			if (getLogs.getStartLogId() != null && logs.containsKey(getLogs.getStartLogId())) {
				if(getLogs.isAscendingOrder()) {
					startKey = logs.higherKey(getLogs.getStartLogId());
				}else {
					startKey = logs.lowerKey(getLogs.getStartLogId());
				}

			}
			if (startKey == null) {
				if(getLogs.isAscendingOrder()) {
					startKey = logs.lastKey();
				}else {
					startKey = logs.firstKey();
				}
			}
			
			String currentKey = startKey;
			while(currentKey != null && logsList.size() <= getLogs.getMaxItemNumber()) {
				logsList.add(logs.get(currentKey));
				currentKey = logs.higherKey(currentKey);
			}
		}
		result.setApplicationId(getLogs.getApplicationId());
		result.setLogs(logsList);
		return result;
	}

	@Override
	public List<Log> getLogs(String applicationId, Date fromDate, Date toDate,
			String beginLogId, Integer maxNumber) {
		LOGGER.error("getLogs is not implemented");
		return null;
	}

	

}
