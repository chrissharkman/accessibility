package ch.chrissharkman.accessibility.rcp.base.handler;

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;

import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleWidget;

/**
 * Class extending AccessibleAdapter that has an AccessibleWidget object from
 * which properties are read to set the event results.
 * @author ChristianHeimann
 *
 */
public class AccessibleAdapterWidget extends AccessibleAdapter {
	private AccessibleWidget widget;

	/**
	 * Constructor of AccessibleAdapterWidget which takes an AccessibleWidget as argument.
	 * This given widget will be set as property and then accessed to deliver result
	 * values.
	 * @param widget an AccessibleWidget object which is containing the control
	 * that will receive this AccessibleAdapterWidget Listener.
	 */
	public AccessibleAdapterWidget(AccessibleWidget widget) {
		this.widget = widget;
	}
	
	/**
	 * Method to set as result the accessible name of the
	 * widget set as property of this instance. The property 
	 * "accessibleName" is validated with the accessibleExtension of actual
	 * accessibleManager.
	 */
	@Override
	public void getName(AccessibleEvent e) {
		String fullAccessibleName = AccessibleManager.instance().getAccessibleExtension().getAccessibleNameString(this.widget.getAccessibleName());
		e.result = fullAccessibleName;
		
	}
}
