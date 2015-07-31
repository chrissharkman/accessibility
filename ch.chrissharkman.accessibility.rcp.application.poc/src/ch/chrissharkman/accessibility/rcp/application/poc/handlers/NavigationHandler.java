package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Class to handle binded keys that call the navigationCommand.
 * For a binding key in Application.e4xmi: don't forget to set a keyparameter (which will be delivered
 * as argument) and the keybinding needs absolutely the tag "type:user".
 * @author ChristianHeimann
 *
 */
public class NavigationHandler {
	
	private static Logger logger = Logger.getLogger(NavigationHandler.class);
	
	/**
	 * Function to control actions that will be initiated by shortcuts and access keys.
	 * execute() called when navigation handler is called.
	 * 
	 * @param app representation of the Application model
	 * @param modelService service to create and handle model elements
	 * @param partService service to create and handle part
	 * @param key the keyparameter String given by the keybinding parameter
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Execute
	public void execute(MApplication app, EModelService modelService, EPartService partService,
			@Named("keyparameter") String key) throws InvocationTargetException, InterruptedException {
		logger.info("execute");
		switch(key) {
		
		case "M1+TAB":
			logger.info("M1+TAB pressed");			
			break;
		case "M2+TAB":
			logger.info("M2+TAB pressed");
			break;
		case "SHIFT+TAB":
			logger.info("Shift+TAB pressed");
			break;
		case "TAB":
			logger.info("TAB pressed");
			break;
		case "Z":
			logger.info("Z pressed");
			break;
		case "M1+L":
			logger.info("M1 + L pressed");
			break;
		}
	}
	
}
