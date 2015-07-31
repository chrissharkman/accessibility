package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class HomeHandler {
		
	private static Logger logger = Logger.getLogger(HomeHandler.class);

	@Execute
	public void execute(MApplication app, EModelService modelService, EPartService partService, @Named("contentparameter") String param) throws InvocationTargetException, InterruptedException {
		MPerspective myPerspective = (MPerspective) modelService.find("ch.chrissharkman.accessibility.rcp.application.poc.perspective.homebrowser", app);
		partService.switchPerspective(myPerspective);
		logger.info("switched to homebrowser");
		logger.info("This value is passed: " + param);
	}
}
