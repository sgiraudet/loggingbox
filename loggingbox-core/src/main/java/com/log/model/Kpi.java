package com.log.model;

import java.util.Date;

public class Kpi implements ApplicationObject {
	
	private static final long serialVersionUID = 7579144301106221251L;

	private final static String objectType = Kpi.class.getName();
	
	private String id;
	private String name;
	private Date date;
	private double value;
	
	private String kpiDefinitionId;
	private String applicationId;
	private String host;
	
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Date getDate() {
		return date;
	}
	public double getValue() {
		return value;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public String getHost() {
		return host;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public void setHost(String host) {
		this.host = host;
	}
	@Override
	public String getObjectType() {
		return objectType;
	}
	public String getKpiDefinitionId() {
		return kpiDefinitionId;
	}
	public void setKpiDefinitionId(String kpiDefinitionId) {
		this.kpiDefinitionId = kpiDefinitionId;
	}
}
