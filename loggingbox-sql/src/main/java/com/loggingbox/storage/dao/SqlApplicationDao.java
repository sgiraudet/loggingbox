package com.loggingbox.storage.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.loggingbox.storage.model.SqlApplication;
import com.loggingbox.storage.model.SqlKpiDefinition;

@Component
public class SqlApplicationDao {

	@Autowired
	SessionFactory sessionFactory;


	@SuppressWarnings("unchecked")
	public List<SqlApplication> getApplications() {
		return (List<SqlApplication>) sessionFactory.getCurrentSession()
				.createCriteria(SqlApplication.class).list();
	}
	
	public SqlApplication getApplication(String id) {
		return (SqlApplication) sessionFactory.getCurrentSession().get(SqlApplication.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<SqlKpiDefinition> getKpiDefinitions(String applicationId) {
		return (List<SqlKpiDefinition>) sessionFactory.getCurrentSession()
				.createCriteria(SqlKpiDefinition.class)
				.add(Restrictions.eq("applicationId", applicationId)).list();
	}
}
