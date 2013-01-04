package com.loggingbox.storage.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.log.model.Level;
import com.log.model.Log;

@Entity
@Table(name="log")
public class SqlLog {

	private String id;

	private Level level;
	private Date date;
	private String host;
	private String type;

	private String data;

	private String applicationId;

	@Id
	public String getId() {
		return id;
	}

	@Enumerated(EnumType.STRING)
	public Level getLevel() {
		return level;
	}

	public Date getDate() {
		return date;
	}

	public String getHost() {
		return host;
	}

	public String getType() {
		return type;
	}

	@Lob
	public String getData() {
		return data;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	
	public static SqlLog fromLog(Log log) {
		SqlLog sqlLog = new SqlLog();
		sqlLog.setId(log.getId());
		sqlLog.setApplicationId(log.getApplicationId());
		sqlLog.setData(log.getData());
		sqlLog.setDate(log.getDate());
		sqlLog.setHost(log.getHost());
		sqlLog.setLevel(log.getLevel());
		sqlLog.setType(log.getType());
		return sqlLog;
	}
	
	public static Log toLog(SqlLog sqlLog) {
		Log log = new Log();
		log.setId(sqlLog.getId());
		log.setApplicationId(sqlLog.getApplicationId());
		log.setData(sqlLog.getData());
		log.setDate(sqlLog.getDate());
		log.setHost(sqlLog.getHost());
		log.setLevel(sqlLog.getLevel());
		log.setType(sqlLog.getType());
		return log;
	}
}
