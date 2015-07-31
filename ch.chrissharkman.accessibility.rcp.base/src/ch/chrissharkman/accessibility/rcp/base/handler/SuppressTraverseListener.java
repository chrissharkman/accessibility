package ch.chrissharkman.accessibility.rcp.base.handler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class SuppressTraverseListener implements TraverseListener {

	@Override
	public void keyTraversed (TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
			e.doit = false;
		}	
	}
	
}
