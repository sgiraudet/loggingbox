package com.loggingbox.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.log.model.Log;
import com.log.storage.LogAccessor;
import com.loggingbox.storage.dao.SqlLogDao;
import com.loggingbox.storage.model.SqlLog;

public class SqlLogAccessor implements LogAccessor {


	private final static SqlLogAccessor INSTANCE = new SqlLogAccessor();

	public static SqlLogAccessor getInstance() {
		return INSTANCE;
	}
	
	@Autowired SqlLogDao logDao;

	@Override
	@Transactional
	public void insertLog(Log log) {
		

		String newId = log.getApplicationId() + "_"
				+ (Long.MAX_VALUE - log.getDate().getTime()) + "_"
				+ Math.abs(new Random().nextInt());
		log.setId(newId);
		logDao.insertLog(SqlLog.fromLog(log));
	}

	@Override
	@Transactional
	public List<Log> getLogs(String applicationId, String beginLogId,
			int maxNumber) {

		List<Log> result = new ArrayList<Log>();

		List<SqlLog> logs = logDao.getLastLogs(applicationId, beginLogId, maxNumber);
		for(SqlLog sqlLog : logs) {
			result.add(SqlLog.toLog(sqlLog));
		}
		return result;
	}

	@Override
	@Transactional
	public List<Log> getLogs(String applicationId, Date fromDate, Date toDate,
			String beginLogId, Integer maxNumber) {
		List<Log> result = new ArrayList<Log>();

		List<SqlLog> logs = logDao.getLogs(applicationId, fromDate, toDate, beginLogId, maxNumber);
		for(SqlLog sqlLog : logs) {
			result.add(SqlLog.toLog(sqlLog));
		}
		return result;
	}

}
