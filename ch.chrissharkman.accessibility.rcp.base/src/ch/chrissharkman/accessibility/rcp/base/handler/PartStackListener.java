package ch.chrissharkman.accessibility.rcp.base.handler;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;

public class PartStackListener implements IPartListener {
	
	private static Logger logger = Logger.getLogger(PartStackListener.class);

	@Override
	public void partActivated(MPart part) {
		logger.info("IPartListener: part activated: " + part.getElementId());
		
	}

	@Override
	public void partBroughtToTop(MPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partDeactivated(MPart part) {
		logger.info("IPartListener: part desactivated: " + part.getElementId());
		
	}

	@Override
	public void partHidden(MPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partVisible(MPart part) {
		// TODO Auto-generated method stub
		
	}

}
