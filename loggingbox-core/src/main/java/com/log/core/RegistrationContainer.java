package com.log.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.log.model.ApplicationObject;

@Component
public class RegistrationContainer {

	private static final Logger LOGGER = Logger
			.getLogger(RegistrationContainer.class);
	
	private Map<String, List<ApplicationRegistration>> registrations;

	public RegistrationContainer() {
		registrations = new HashMap<String, List<ApplicationRegistration>>();
	}

	public void appendLog(ApplicationObject object) {
		String applicationId = object.getApplicationId();

		List<ApplicationRegistration> applicationRegistrations = null;

		synchronized (registrations) {
			applicationRegistrations = registrations.get(applicationId);
		}
		if (applicationRegistrations != null) {
			synchronized (applicationRegistrations) {
				for (ApplicationRegistration applicationRegistration : applicationRegistrations) {
					applicationRegistration.appendLog(object);
				}
			}
		}
	}
	
	public void appendObjects(List<? extends ApplicationObject> objects) {
		if(objects.isEmpty()) {
			return;
		}
		String applicationId = objects.get(0).getApplicationId();
		List<ApplicationRegistration> applicationRegistrations = null;

		synchronized (registrations) {
			applicationRegistrations = registrations.get(applicationId);
		}
		if (applicationRegistrations != null) {
			synchronized (applicationRegistrations) {
				for (ApplicationRegistration applicationRegistration : applicationRegistrations) {
					for(ApplicationObject object : objects) {
						assert object.getApplicationId().equals(applicationId);
						applicationRegistration.appendLog(object);
					}
				}
			}
		}
	}

	public void register(ApplicationRegistration applicationRegistration) {
		String applicationId = applicationRegistration.getApplication().getId();
		
		LOGGER.info(String.format("Register application %s", applicationId));
		synchronized (registrations) {
			if(!registrations.containsKey(applicationId)) {
				registrations.put(applicationId, new ArrayList<ApplicationRegistration>());
			}

			synchronized (registrations.get(applicationId)) {
			registrations.get(applicationId).add(applicationRegistration);
			}
		}
	}

	public void unregister(ApplicationRegistration applicationRegistration) {
		String applicationId = applicationRegistration.getApplication().getId();
		
		LOGGER.info(String.format("Unregister application %s", applicationId));
		synchronized (registrations) {
			if(registrations.containsKey(applicationId)) {
				synchronized (registrations.get(applicationId)) {

					registrations.get(applicationId).remove(applicationRegistration);
				}
			}
		}
	}
}
