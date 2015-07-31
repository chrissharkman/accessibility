package ch.chrissharkman.accessibility.rcp.base.handler;

import org.eclipse.swt.widgets.Listener;

public abstract class AccessibleNavigationListener implements Listener {

	private long lastEventTimestamp;
	private boolean isEnabled;
	
	/**
	 * Method to check if event handling is enabled.
	 * If handling is disabled, then no Event is posted.
	 * @return true if it is enabled, false if not. 
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Method to set if Event is handled or not.
	 * @param isEnabled true if events shall be handled, false if not.
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * Method to get the last event timestamp. needed to check when last event happened.
	 * @return long timestamp
	 */
	public long getLastEventTimestamp() {
		return lastEventTimestamp;
	}

	/**
	 * Method to set last event timestamp. needed to set when event happened.
	 * @param eventTimestamp timestamp of the current event
	 */
	public void setLastEventTimestamp(long eventTimestamp) {
		this.lastEventTimestamp = eventTimestamp;
	}
	
}
