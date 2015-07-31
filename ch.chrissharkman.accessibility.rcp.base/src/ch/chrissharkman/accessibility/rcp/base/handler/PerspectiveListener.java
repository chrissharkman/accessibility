package ch.chrissharkman.accessibility.rcp.base.handler;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;

public class PerspectiveListener implements IPerspectiveListener3, Listener {
	
	private static Logger logger = Logger.getLogger(PerspectiveListener.class);

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {
		logger.info("PERSPLISTENER");
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective,
			IPerspectiveDescriptor newPerspective) {
		logger.info("PERSPLISTENER");
		
	}

	@Override
	public void handleEvent(Event event) {
		logger.info("PERSPLISTENER HANDLE EVENT *****");
		
	}

}
