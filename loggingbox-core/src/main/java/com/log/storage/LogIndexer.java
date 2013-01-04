package com.log.storage;

import com.log.model.Log;
import com.log.model.command.Search;
import com.log.model.result.SearchResult;

/**
 * Interface to index and search logs
 * 
 * @author Stanislas Giraudet
 *
 *
 */
public interface LogIndexer {

	/**
	 * Index a log.
	 * This method may return before the log is indexed.
	 * 
	 * @param log
	 */
	void indexLog(Log log);
	
	SearchResult searchLogs(Search search);
	
}
