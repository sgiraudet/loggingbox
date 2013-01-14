package com.log.model.result;

import java.util.List;

import com.log.model.ApplicationObject;
import com.log.model.Log;

public class GetLogsResult implements ApplicationObject {

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = GetLogsResult.class.getName();

	private String applicationId;
	private boolean ascendingOrder;

	private List<Log> logs;

	public GetLogsResult() {
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

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public boolean isAscendingOrder() {
		return ascendingOrder;
	}

	public void setAscendingOrder(boolean ascendingOrder) {
		this.ascendingOrder = ascendingOrder;
	}

}
