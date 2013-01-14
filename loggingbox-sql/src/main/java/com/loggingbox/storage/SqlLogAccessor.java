package com.loggingbox.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.log.model.Log;
import com.log.model.command.GetLogs;
import com.log.model.result.GetLogsResult;
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
	public void insertLogs(List<Log> logs) {
		for(Log log : logs) {
			String newId = log.getApplicationId() + "_"
					+ (Long.MAX_VALUE - log.getDate().getTime()) + "_"
					+ Math.abs(new Random().nextInt());
			log.setId(newId);
			logDao.insertLog(SqlLog.fromLog(log));
		}
	}
	

	@Override
	@Transactional
	public  GetLogsResult getLogs(GetLogs getLogs) {

		GetLogsResult result = new GetLogsResult();
		List<Log> logsList = new ArrayList<Log>();
		List<SqlLog> logs;
		if(getLogs.getOffset() > 0) {
			logs = new ArrayList<SqlLog>();
			
			List<SqlLog> offsetList = logDao.getLogsPage(getLogs.getApplicationId(), getLogs.getStartLogId(), getLogs.getOffset(), !getLogs.isAscendingOrder());
			for(int i= offsetList.size()-1; i > 0; i--) {
				logs.add(offsetList.get(i));
			}
			logs.addAll(logDao.getLogsPage(getLogs.getApplicationId(), getLogs.getStartLogId(), getLogs.getMaxItemNumber()-logs.size()+1, getLogs.isAscendingOrder()));
		}else {

			logs = logDao.getLogsPage(getLogs.getApplicationId(), getLogs.getStartLogId(), getLogs.getMaxItemNumber(), getLogs.isAscendingOrder());
		}

		for(SqlLog sqlLog : logs) {
			logsList.add(SqlLog.toLog(sqlLog));
		}

		result.setApplicationId(getLogs.getApplicationId());
		result.setLogs(logsList);
		result.setAscendingOrder(getLogs.isAscendingOrder());
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
