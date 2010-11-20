package core;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class UyooLogger {

	private static Logger theLogger = Logger.getLogger("UyooLogger");
	
	static {
		PropertyConfigurator.configureAndWatch("resources/log4j.properties");
	}
	
	public static Logger getLogger() {
		return theLogger;
	}
}
