package com.log.model;

public class KpiDefinition implements ApplicationObject{

	private static final long serialVersionUID = 5797034829725546938L;

	private final static String objectType = KpiDefinition.class.getName();

	private String id;
	private String name;
	private String applicationId;

	private String type;
	private String regex;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getRegex() {
		return regex;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setType(String type) {
		this.type = type;
	}

}
