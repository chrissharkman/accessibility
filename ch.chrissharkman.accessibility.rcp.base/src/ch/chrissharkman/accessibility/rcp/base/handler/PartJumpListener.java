package ch.chrissharkman.accessibility.rcp.base.handler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

public class PartJumpListener extends AccessibleNavigationListener {
	
	@Override
	public void handleEvent(Event event) {
		if (this.isEnabled() && this.getLastEventTimestamp() < event.time) {
			this.setLastEventTimestamp(event.time);
			if (event.keyCode == SWT.F6 && event.stateMask == 0) {
				AccessibleManager.getBroker().send(AccessibleConstants.FOCUS_PART_JUMP, null);
			}
		}	
		
	}

}
