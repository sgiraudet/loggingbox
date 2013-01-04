package com.log.storage;

import java.util.List;

import com.log.model.Application;
import com.log.model.KpiDefinition;

public interface ApplicationAccessor {


	List<Application> getApplications();
	Application getApplication(String id);
	
	List<KpiDefinition> getKpiDefinitions(String applicationId);
	
}
