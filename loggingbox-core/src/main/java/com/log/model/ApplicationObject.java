package com.log.model;

import java.io.Serializable;

public interface ApplicationObject extends Serializable{
	String getApplicationId();
	String getObjectType();
}
