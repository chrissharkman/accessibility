package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.List;

public interface AccessibleElement {
	
	/**
	 * Method to check if object is a default element (landmark).
	 * @return true if object is default element, false if not.
	 */
	public boolean isDefaultElement();
	
	/**
	 * Method to get id of element.
	 * @return id as String of the element.
	 */
	public String getElementId();
	
	/**
	 * Method to return the AccessibleElement ArrayList of children elements.
	 * @return an ArrayList with the children (AccessibleElements)
	 * or null if the element cannot have any children. 
	 */
	public List<AccessibleElement> getAccessibleChildren();
	
	/**
	 * Method to return the parent, which contains the accessibleElement.
	 * @return the parent of this AccessibleElement object
	 */
	public AccessibleElement getParent();

	/**
	 * Method to get the GUI object which is linked
	 * to the model.
	 * @return the linked guiObject
	 */
	public Object getGuiObject();

	/**
	 * Method to set the GUI object which shall be linked
	 * to the model.
	 * @param guiObject the existing GUI object of the application
	 * which is represented by the AccessibleElement.
	 */
	public void setGuiObject(Object guiObject);
	
}
