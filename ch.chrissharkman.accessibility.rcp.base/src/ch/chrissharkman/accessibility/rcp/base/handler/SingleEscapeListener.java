package ch.chrissharkman.accessibility.rcp.base.handler;

import org.eclipse.swt.widgets.Event;

import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

public class SingleEscapeListener extends AccessibleNavigationListener {
		
	@Override
	public void handleEvent(Event event) {
		if (this.isEnabled() && this.getLastEventTimestamp() < event.time) {
			this.setLastEventTimestamp(event.time);
			if (event.keyCode == AccessibleConstants.KEY_CODE_ESC && event.stateMask == 0) {
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_ESCAPE, null);
			}
		}		
	}

}
