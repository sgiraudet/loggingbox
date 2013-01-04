package com.log.core;

import java.util.LinkedList;
import java.util.Queue;

import com.log.model.Application;
import com.log.model.ApplicationObject;

public class ApplicationRegistration {

	private Application application;
	

	public ApplicationRegistration(Application application) {
		super();
		this.application = application;
		objects = new LinkedList<ApplicationObject>();
	}

	private Queue<ApplicationObject> objects;
	
	
	public void appendLog(ApplicationObject log) {
		synchronized (objects) {
			objects.add(log);
			objects.notifyAll();
		}
	}
	
	public ApplicationObject getNextObject() {
		synchronized (objects) {
			if(!objects.isEmpty()) {
				return objects.poll();
			}
		}
		try {
			synchronized (objects) {
				objects.wait();

				if(!objects.isEmpty()) {
					return objects.poll();
				} 
			}
		} catch (InterruptedException e) {
			return null;
		}
		return getNextObject();
		
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
}
