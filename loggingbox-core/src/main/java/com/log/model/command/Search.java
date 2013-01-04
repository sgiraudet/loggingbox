package com.log.model.command;

import com.log.model.ApplicationObject;

public class Search implements ApplicationObject{

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = Search.class.getName();
	
	private String applicationId;
	private String token;
	
	public Search() {
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

}
