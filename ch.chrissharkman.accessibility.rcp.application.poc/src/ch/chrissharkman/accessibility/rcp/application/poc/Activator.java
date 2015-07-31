package ch.chrissharkman.accessibility.rcp.application.poc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Class to configure the proof of concept application: logger and other classes
 * that must be activated and values that must be initialized.
 * 
 * @author ChristianHeimann
 *
 */
public class Activator implements BundleActivator {

	private static final String CONTENT_BUNDLE = "ch.chrissharkman.accessibility.rcp.application.poc";
	public static final String ACCESSIBLE_PROPERTY_FILEPATH = "accessible/poc-accessible-properties.xml";
	private static final String WORKSPACE_DIR = "WORKSPACE_DIR";
	private static boolean startIsRunning = true;

	/**
	 * Method to initialize logger, set content bundle of Content Manager and
	 * set flag false when start method has finished.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.initializeLogger(Level.DEBUG);
		ContentManager.instance().setContentBundle(Activator.CONTENT_BUNDLE);
		Activator.setStartIsRunning(false);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * Function to get information if start method is still running.
	 * 
	 * @return true if start method is still running, false when finished
	 */
	public static boolean getStartIsRunning() {
		return startIsRunning;
	}

	private static void setStartIsRunning(boolean startIsRunning) {
		Activator.startIsRunning = startIsRunning;
	}

	/**
	 * Method to initialize Logger with the command
	 * BasicConfigurator.configure() and to set its level.
	 */
	public static void initializeLogger(Level level) {
		// to set append logger file repository: current workspace
		String workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		System.setProperty(WORKSPACE_DIR, workspaceDir);
		URL url = null;
		try {
			url = new URL("platform:/plugin/" + CONTENT_BUNDLE + "/resources/log4j.xml");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// configure log4j
		DOMConfigurator.configure(url);
		Logger logger = Logger.getLogger(Activator.class);
		logger.setLevel(level);
		logger.info("***********************************************************************************************");
		logger.info("************************ Starting Logger: Level = " + logger.getLevel() + " ***************************************");
	}

}
