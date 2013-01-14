package com.log.model.command;

import com.log.model.ApplicationObject;

public class GetLogs implements ApplicationObject {

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = GetLogs.class.getName();

	private String applicationId;
	
	
	/*maxNumber of logs to retrieve*/
	private int maxItemNumber;

	/*
	 * if null, start from the last log inserted (most recent date). Else, start
	 * from the log just before the log id specified.*/
	private String startLogId;
	private boolean ascendingOrder;
	private int offset;

	

	public GetLogs() {
	}
	
	public GetLogs(String applicationId, int maxItemNumber, String startLogId,
			boolean ascendingOrder) {
		super();
		this.applicationId = applicationId;
		this.maxItemNumber = maxItemNumber;
		this.startLogId = startLogId;
		this.ascendingOrder = ascendingOrder;
		offset = 0;
	}
	
	
	@Override
	public String getApplicationId() {
		return applicationId;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}

	public boolean isAscendingOrder() {
		return ascendingOrder;
	}
	public void setObjectType(String objectType) {
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public int getMaxItemNumber() {
		return maxItemNumber;
	}

	public String getStartLogId() {
		return startLogId;
	}

	public void setMaxItemNumber(int maxItemNumber) {
		this.maxItemNumber = maxItemNumber;
	}

	public void setStartLogId(String startLogId) {
		this.startLogId = startLogId;
	}


	public void setAscendingOrder(boolean ascendingOrder) {
		this.ascendingOrder = ascendingOrder;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
