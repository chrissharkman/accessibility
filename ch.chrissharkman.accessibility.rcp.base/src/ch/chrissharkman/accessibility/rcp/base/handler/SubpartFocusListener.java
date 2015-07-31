package ch.chrissharkman.accessibility.rcp.base.handler;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

/**
 * Focus Listener to reset focus on source (Control) when focusLost.
 * @author ChristianHeimann
 *
 */
public class SubpartFocusListener implements FocusListener {
	
	private static Logger logger = Logger.getLogger(SubpartFocusListener.class);

	/**
	 * Method mentions that focus on source is gained.
	 */
	@Override
	public void focusGained(FocusEvent e) {
		logger.info("focus gained on control. Source: " + e.getSource());
		
	}

	/**
	 * Method called when focus lost, then subpart lost focus event is send.
	 */
	@Override
	public void focusLost(FocusEvent e) {
		logger.info("focus lost on control. Source: " + e.getSource());
		AccessibleManager.getBroker().send(AccessibleConstants.SUBPART_LOST_FOCUS, (Control) e.getSource());			
	}

}
