package com.log.component;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.log.core.RegistrationContainer;
import com.log.model.Log;
import com.log.storage.ApplicationAccessor;
import com.log.storage.LogAccessor;
import com.log.storage.LogIndexer;

@Component
public class LogComponent {

	private static final Logger LOGGER = Logger.getLogger(LogComponent.class);

	@Autowired
	RegistrationContainer registrationContainer;

	@Autowired
	LogAccessor logAccessor;
	@Autowired
	LogIndexer logIndexer;
	@Autowired
	ApplicationAccessor applicationAccessor;
	
	public void insertLog(Log log) {

		logAccessor.insertLog(log);
		logIndexer.indexLog(log);
		registrationContainer.appendLog(log);

		LOGGER.trace(String.format("Log inserted. Log timestamp %s", log.getDate().getTime()));
		
//		List<KpiDefinition> definitions = applicationAccessor
//				.getKpiDefinitions(log.getApplicationId());
//		for (KpiDefinition kpiDefinition : definitions) {
//			checkKpiExtration(kpiDefinition, log);
//		}
	}
	
	public void insertLog(List<Log> logs) {
		if(logs.isEmpty()) {
			return;
		}
		logAccessor.insertLogs(logs);
		logIndexer.indexLogs(logs);
		registrationContainer.appendObjects(logs);

		LOGGER.trace(String.format("%s logs inserted.", logs.size()));
		
//		List<KpiDefinition> definitions = applicationAccessor
//				.getKpiDefinitions(log.getApplicationId());
//		for (KpiDefinition kpiDefinition : definitions) {
//			checkKpiExtration(kpiDefinition, log);
//		}
	}
	
	
//	private void checkKpiExtration(KpiDefinition kpiDefinition, Log log) {
//
//		if (kpiDefinition.getType() != null
//				&& !kpiDefinition.getType().equals(log.getType())) {
//			return;
//		}
//		try {
//			Pattern p = Pattern.compile(kpiDefinition.getRegex());
//			Matcher m = p.matcher(log.getData());
//			if (m.find()) {
//				Kpi kpi = new Kpi();
//				kpi.setDate(log.getDate());
//				kpi.setHost(log.getHost());
//				kpi.setValue(Double.parseDouble(m.group(1)));
//				kpi.setName(kpiDefinition.getName());
//				kpi.setApplicationId(kpiDefinition.getApplicationId());
//				kpi.setKpiDefinitionId(kpiDefinition.getId());
//
//				registrationContainer.appendLog(kpi);
//			}
//		} catch (Exception ex) {
//
//		}
//
//	}
}
