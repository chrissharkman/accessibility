package ch.chrissharkman.accessibility.rcp.base;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface for all views injected into parts, that shall be handled
 * by the AccessibleManager and its ViewMediator.
 * @author ChristianHeimann
 *
 */
public interface AccessibleView {
	
	/**
	 * Method returns parent composite which is usually given
	 * in the @PostConstructe createComposite() method.
	 * @return the parent composite of the view
	 */
	public Composite getViewComposite();
}
