package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class to have a generic container for perspectives.
 * @author ChristianHeimann
 *
 */
public class AccessiblePerspective implements AccessibleElement {
	
	private String elementId;
	private boolean defaultElement;
	private Object	guiObject;
	public List<AccessibleElement> parts;
	
	public static Logger logger = Logger.getLogger(AccessiblePerspective.class);
	
	/**
	 * Constructor for AccessiblePerspective needs a perspectiveId for its attribute.
	 * @param elementId the id of the object.
	 */
	public AccessiblePerspective(String elementId) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given perspectiveId is not valid. perspectiveId: " + elementId);
			return;
		}
		this.elementId = elementId;
		this.parts = new ArrayList<>();
	}
	
	/**
	 * Method to add a part into the List,
	 * added as last element.
	 * @param part the part to add to the AccessiblePerspective
	 */
	public void addPart(AccessiblePart part) {
		if (part == null) {
			logger.info("Given part is null.");
			return;
		}
		parts.add(part);
	}
	
	/**
	 * Method to get the perspective id
	 * @return the perspective id as a String
	 */
	@Override
	public String getElementId() {
		return this.elementId;
	}
	
	/**
	 * Method to set if element is default (landmark).
	 * @param defaultElement true if element is default, false if not
	 */
	public void setAsDefaultElement(boolean defaultElement) {
		this.defaultElement = defaultElement;
	}
	
	/**
	 * Method to check if object is a default element
	 * @return true if element is default (landmark), false if not
	 */
	public boolean isDefaultElement() {
		return this.defaultElement;
	}

	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return this.parts;
	}
	
	/**
	 * Method to get the GUI object which is linked
	 * to this model.
	 * @return the linked guiObject
	 */
	public Object getGuiObject() {
		return guiObject;
	}

	/**
	 * Method to set the GUI object which shall be linked
	 * to this model.
	 * @param guiObject
	 */
	public void setGuiObject(Object guiObject) {
		this.guiObject = guiObject;
	}

	/**
	 * Method to get parent: returns null because accessiblePerspective
	 * does not have a parent.
	 */
	@Override
	public AccessibleElement getParent() {
		return null;
	}

}
