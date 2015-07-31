package ch.chrissharkman.accessibility.rcp.base.handler;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

/**
 * Class for traverse key action handling of a defined set of
 * combinations: Tab, Shift + Tab, Ctrl + Tab, Ctrl + Shift + Tab and ESC
 * @author ChristianHeimann
 *
 */
public class TraverseKeyListener extends AccessibleNavigationListener {
	
	/**
	 * Instance of Logger
	 */
	public static Logger logger = Logger.getLogger(TraverseKeyListener.class);
	
	/**
	 * Method selects which event will be post, tanking the event.keyCode and the
	 * event.stateMask into account. To avoid multiple calls, a check is made, if lastTab event
	 * happened before this event.time.
	 * Also a check is made, if handle-event is active or not.
	 * The definitions behind the constants of key codes and state masks are the following:
	 * TAB: 9 && 0
	 * SHIFT + TAB: 9 && 131072
	 * CTRL + TAB: 9 && 262144
	 * CTRL + SHIFT + TAB: 9 && 393216
	 * ESC: 27
	 */
	@Override
	public void handleEvent(Event event) {
		
		if (this.isEnabled() && this.getLastEventTimestamp() < event.time) {
			this.setLastEventTimestamp(event.time);
			if (event.keyCode == AccessibleConstants.KEY_CODE_TAB && event.stateMask == AccessibleConstants.NO_STATE_MASK) {
//				logger.info("tab pressed");
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_NEXT_WIDGET, null);
			} else if (event.keyCode == AccessibleConstants.KEY_CODE_TAB && event.stateMask == AccessibleConstants.STATE_MASK_SHIFT) {
//				logger.info("tab + shift pressed");
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_PREVIOUS_WIDGET, null);
			} else if (event.keyCode == AccessibleConstants.KEY_CODE_TAB && event.stateMask == AccessibleConstants.STATE_MASK_CTRL) {
//				logger.info("ctrl + tab pressed");
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_NEXT_SUBPART, null);
			} else if (event.keyCode == AccessibleConstants.KEY_CODE_TAB && event.stateMask == AccessibleConstants.STATE_MASK_CTRL_SHIFT) {
//				logger.info("ctrl + shift + tab pressed");
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_PREVIOUS_SUBPART, null);
			}
			
		}

	}

}
