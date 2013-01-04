package com.log.model.command;

import java.util.Date;

import com.log.model.ApplicationObject;

public class Export implements ApplicationObject{

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = Export.class.getName();
	
	private String applicationId;
	private Date fromDate;
	private Date toDate;
	
	public Export() {
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

	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	

}
