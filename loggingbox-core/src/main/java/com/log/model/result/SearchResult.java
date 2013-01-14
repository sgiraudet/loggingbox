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
	private long itemFounds;
	private int from;
	private int size;
	
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

	public long getItemFounds() {
		return itemFounds;
	}

	public void setItemFounds(long itemFounds) {
		this.itemFounds = itemFounds;
	}

	public int getFrom() {
		return from;
	}

	public int getSize() {
		return size;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
