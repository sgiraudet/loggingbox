package com.log.storage;

import java.util.Date;
import java.util.List;

import com.log.model.Log;
import com.log.model.command.GetLogs;
import com.log.model.result.GetLogsResult;

/**
 * Interface to access or store Log objects.
 * 
 * @author Stanislas Giraudet
 * 
 * 
 */
public interface LogAccessor {

	/**
	 * Insert a log. The log must have an applicationId, and a date.
	 * 
	 * @param log
	 */
	void insertLog(Log log);
	

	/**
	 * Insert a list of logs.
	 * @param logs
	 */
	void insertLogs(List<Log> logs);

	/**
	 * Get a page of logs.
	
	 * @param getLogs
	 * @return
	 */
	GetLogsResult getLogs(GetLogs getLogs);

	/**
	 * 
	 * Get logs between the from Date and the toDate.
	 * We can specify a max number of logs to retrieve. If so, we can get the next items with the beginLogId parameter.
	 * 
	 * @param applicationId
	 *            : Id of the application
	 * @param fromDate 
	 * @param toDate
	 * @param beginLogId : the id of the last logs retrieved. If null, we start from the first log form the fromDate.
	 * @param maxNumber 
	 *            : maxNumber of logs to retrieve
	 * @return
	 */
	List<Log> getLogs(String applicationId, Date fromDate, Date toDate,
			String beginLogId, Integer maxNumber);

}
