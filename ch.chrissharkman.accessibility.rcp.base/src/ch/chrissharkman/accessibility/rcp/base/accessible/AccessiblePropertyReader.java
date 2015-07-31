package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.List;

/**
 * Interface for Accessibility properties (any kind of input)
 * which are the details for key bindings, navigation hierarchy and
 * and to get single elements from an input for delayed instatiation. 
 * @author ChristianHeimann
 *
 */
public interface AccessiblePropertyReader {
	
	/**
	 * Abstract method to get all key bindings set in a property input
	 */
	public void getKeybindings();
	
	/**
	 * Abstract method to get all perspective, its child parts, its grand child widgets with details
	 * @return 
	 */
	public AccessibleViewTree getTemplateViewTree();
	
	/**
	 * Abstract method to get the accessible property of an individual perspective, part or widget. 
	 * @param partId The id of the part from which accessible properties should be returned.
	 */
	public void getAccessiblePropertyOf(String elementId);
	
}
