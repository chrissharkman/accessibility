package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class EditHandler {
	
	private static Logger logger = Logger.getLogger(EditHandler.class);
	
	@Inject
	IEventBroker broker;
	

	@Execute
	public void execute(MApplication app, EModelService modelService, EPartService partService) throws InvocationTargetException, InterruptedException {
		broker.post("edit", null);
		logger.debug("broker posted edit");		
	}
	
}
