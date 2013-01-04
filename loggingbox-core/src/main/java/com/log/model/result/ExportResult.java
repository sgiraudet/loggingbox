package com.log.model.result;

import com.log.model.ApplicationObject;

public class ExportResult implements ApplicationObject{

	private static final long serialVersionUID = 7365593855490773273L;

	private final String objectType = ExportResult.class.getName();
	
	private String applicationId;
	private int fileId;
	
	
	public ExportResult() {
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

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

}
