package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class PerspectiveHandler {

	private static Logger logger = Logger.getLogger(PerspectiveHandler.class);

	@Execute
	public void execute(MApplication app, EModelService modelService, EPartService partService,
			@Named("contentparameter") String content) throws InvocationTargetException, InterruptedException {
		MPerspective myPerspective;
		switch (content) {
		case "home":
			myPerspective = (MPerspective) modelService.find("poc.perspective.home", app);
			partService.switchPerspective(myPerspective);
		break;
		case "main":
			myPerspective = (MPerspective) modelService.find("poc.perspective.main", app);
			partService.switchPerspective(myPerspective);
			break;
		case "imprint":
			myPerspective = (MPerspective) modelService.find("poc.perspective.imprint", app);
			partService.switchPerspective(myPerspective);
			break;
		}	
	}
}