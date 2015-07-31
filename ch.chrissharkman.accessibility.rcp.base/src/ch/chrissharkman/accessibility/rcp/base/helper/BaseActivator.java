package ch.chrissharkman.accessibility.rcp.base.helper;

import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class BaseActivator {
	
	private static boolean loggerIsInitialized = false;
	
	/**
	 * Method to initialize Logger with the command BasicConfigurator.configure() and to set its level.
	 */
	public static void initializeLogger(Level level) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("resources/log4j.xml");
		PropertyConfigurator.configure(url);
		Logger logger = Logger.getLogger(BaseActivator.class);
		logger.setLevel(level);
		logger.info("Starting Logger: Level = " + logger.getLevel().getClass());
		BaseActivator.setLoggerIsInitialized(true);
	}
	
	/**
	 * Method to get information if logger is initialized.
	 * @return true if logger is initialized, false when not
	 */
	public static boolean loggerIsInitialized() {
		return loggerIsInitialized;
	}

	private static void setLoggerIsInitialized(boolean loggerIsInitialized) {
		BaseActivator.loggerIsInitialized = loggerIsInitialized;
	}
	

}
