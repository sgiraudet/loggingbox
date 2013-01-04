package com.loggingbox.storage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.log.model.Application;
import com.log.model.KpiDefinition;
import com.log.storage.ApplicationAccessor;
import com.loggingbox.storage.dao.SqlApplicationDao;
import com.loggingbox.storage.model.SqlApplication;
import com.loggingbox.storage.model.SqlKpiDefinition;

@Component
public class SqlApplicationAccessor implements ApplicationAccessor {


	@Autowired
	SqlApplicationDao applicationDao;

	@Override
	@Transactional
	public List<Application> getApplications() {
		List<Application> result = new ArrayList<Application>();

		List<SqlApplication> applications = applicationDao.getApplications();
		for (SqlApplication application : applications) {
			result.add(SqlApplication.toApp(application));
		}
		return result;
	}

	@Override
	@Transactional
	public Application getApplication(String id) {
		return SqlApplication.toApp(applicationDao.getApplication(id));
	}

	@Override
	@Transactional
	public List<KpiDefinition> getKpiDefinitions(String applicationId) {
		List<KpiDefinition> result = new ArrayList<KpiDefinition>();

		List<SqlKpiDefinition> definitions = applicationDao
				.getKpiDefinitions(applicationId);
		for (SqlKpiDefinition kpiDefinition : definitions) {
			result.add(SqlKpiDefinition.to(kpiDefinition));
		}
		return result;
	}

}
