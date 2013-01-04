package com.loggingbox.storage.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.log.model.KpiDefinition;


@Entity
@Table(name="kpi_definition")
public class SqlKpiDefinition {

	private String id;
	private String name;
	private String applicationId;
	
	private String type;
	private String regex;

	@Id
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
	
	
	
	public static SqlKpiDefinition fromLog(KpiDefinition kpiDefinition) {
		SqlKpiDefinition sqlKpiDefinition = new SqlKpiDefinition();
		sqlKpiDefinition.setId(kpiDefinition.getId());
		sqlKpiDefinition.setName(kpiDefinition.getName());
		sqlKpiDefinition.setRegex(kpiDefinition.getRegex());
		sqlKpiDefinition.setType(kpiDefinition.getType());
		sqlKpiDefinition.setApplicationId(kpiDefinition.getApplicationId());
		return sqlKpiDefinition;
	}
	
	public static KpiDefinition to(SqlKpiDefinition sqlKpiDefinition) {
		KpiDefinition kpiDefinition = new KpiDefinition();
		kpiDefinition.setId(sqlKpiDefinition.getId());
		kpiDefinition.setName(sqlKpiDefinition.getName());
		kpiDefinition.setRegex(sqlKpiDefinition.getRegex());
		kpiDefinition.setType(sqlKpiDefinition.getType());
		kpiDefinition.setApplicationId(sqlKpiDefinition.getApplicationId());
		return kpiDefinition;
	}
	
}
