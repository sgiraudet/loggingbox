package com.loggingbox.storage.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.log.model.Application;

@Entity
@Table(name="application")
public class SqlApplication {

	private String id;
	private String name;

	@Id
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

	
	public static SqlApplication fromLog(Application application) {
		SqlApplication sqlApplication = new SqlApplication();
		sqlApplication.setId(application.getId());
		sqlApplication.setName(application.getName());
		return sqlApplication;
	}
	
	public static Application toApp(SqlApplication sqlApplication) {
		Application application = new Application();
		application.setId(sqlApplication.getId());
		application.setName(sqlApplication.getName());
		return application;
	}



	
}
