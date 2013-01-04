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
	public  synchronized List<Log> getLogs(String applicationId, String beginLogId,
			int maxNumber) {

		List<Log> result = new ArrayList<Log>();

		TreeMap<String, Log> logs = logsByApplicationId.get(applicationId);
		if (logs != null && !logs.isEmpty()) {
			String startKey = null;
			if (beginLogId != null && logs.containsKey(beginLogId)) {
				startKey = logs.higherKey(beginLogId);

			}
			if (startKey == null) {
				startKey = logs.firstKey();
			}
			
			String currentKey = startKey;
			while(currentKey != null && result.size() <= maxNumber) {
				result.add(logs.get(currentKey));
				currentKey = logs.higherKey(currentKey);
			}
		}
		return result;
	}

	@Override
	public List<Log> getLogs(String applicationId, Date fromDate, Date toDate,
			String beginLogId, Integer maxNumber) {
		LOGGER.error("getLogs is not implemented");
		return null;
	}

}
