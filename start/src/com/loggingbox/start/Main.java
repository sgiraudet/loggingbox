package com.loggingbox.start;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

	private static String CLASSPATH_DIR = "/lib";
	private static String CONF_DIR = "/etc";
	private static String LIB_EXT = ".jar";
	private static String CONF_EXT = ".properties";

	private static String LAUNCHER_CLASS = "com.loggingbox.LoggingBox";

	private static ClassLoader classLoader;

	
	private static void initClassLoader(String homePath) {
		try {
			
			File libPath = new File(homePath+CLASSPATH_DIR);
			// get jar files from jarPath
			File[] jarFiles = libPath.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().endsWith(Main.LIB_EXT);
				}
			});
			
			
			File confPath = new File(homePath+CONF_DIR);
			// get jar files from jarPath
			File[] confFiles = confPath.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().endsWith(Main.CONF_EXT);
				}
			});
			URL[] classpath = new URL[jarFiles.length+confFiles.length];
			for (int j = 0; j < jarFiles.length; j++) {
				classpath[j] = jarFiles[j].toURI().toURL();
			}
			for (int j = 0; j < confFiles.length; j++) {
				classpath[j+jarFiles.length] = confFiles[j].toURI().toURL();
			}
			classLoader = new URLClassLoader(classpath, Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(classLoader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		
		initClassLoader(args[0]);
		Thread.currentThread().setContextClassLoader(classLoader);
		
		Class<?> cls = Class.forName(LAUNCHER_CLASS, true, classLoader);
		Method meth = cls.getMethod("main", String[].class);
		meth.invoke(null, (Object) args); 
	}

}
