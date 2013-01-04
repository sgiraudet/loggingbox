package com.log.model;

public class Application  implements ApplicationObject{
	
	private static final long serialVersionUID = -480781809729396778L;

	private final static String objectType = Application.class.getName();

	private String id;
	
	private String name;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getApplicationId() {
		return id;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}
	
}
