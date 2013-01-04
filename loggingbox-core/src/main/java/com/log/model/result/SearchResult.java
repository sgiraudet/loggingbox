package com.log.model.result;

import java.util.List;

import com.log.model.ApplicationObject;
import com.log.model.Log;

public class SearchResult implements ApplicationObject{

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = SearchResult.class.getName();
	
	private String applicationId;
	private String token;
	
	private List<Log> logs;
	
	public SearchResult() {
	}

	@Override
	public String getApplicationId() {
		return applicationId;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
	}

	public String getToken() {
		return token;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

}
