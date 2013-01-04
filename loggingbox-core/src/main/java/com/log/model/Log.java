package com.log.model;

import java.util.Date;

public class Log implements ApplicationObject {

	private static final long serialVersionUID = 347106230852291670L;
	
	private final static String objectType = Log.class.getName();
	private String id;

	private Level level;
	private Date date;
	private String host;
	private String type;

	private String data;

	private String applicationId;

	/***********************
	 ******* GETTERS *******
	 **********************/

	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public Level getLevel() {
		return level;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getHost() {
		return host;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}
	/***********************
	 ******* SETTERS *******
	 **********************/

	public void setId(String id) {
		this.id = id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}


	public void setHost(String host) {
		this.host = host;
	}


}
