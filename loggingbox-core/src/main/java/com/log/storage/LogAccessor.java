package com.log.storage;

import java.util.Date;
import java.util.List;

import com.log.model.Log;

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
	 * Get a page of logs.
	 * 
	 * @param applicationId
	 *            : Id of the application
	 * @param beginLogId
	 *            : if null, start from the last log inserted (most recet date).
	 *            Else, start from the log just before the log id specified.
	 * @param maxNumber
	 *            : maxNumber of logs to retrieve
	 * 
	 * @return
	 */
	List<Log> getLogs(String applicationId, String beginLogId, int maxNumber);

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
