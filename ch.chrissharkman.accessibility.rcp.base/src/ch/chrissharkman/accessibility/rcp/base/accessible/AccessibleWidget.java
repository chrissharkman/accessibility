package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.List;

import org.apache.log4j.Logger;

public class AccessibleWidget implements AccessibleElement {

	private AccessibleElement parent;
	private String elementId;
	private String label;
	private String accessibleName;
	private boolean defaultTabAction;
	private boolean defaultElement;
	private Object guiObject;
	
	public static Logger logger = Logger.getLogger(AccessiblePart.class);
	
	/**
	 * Constructor "minimal" with only a widget property.
	 * All the other properties are set to null or false.
	 * @param elementId the id of the widget as String.
	 */
	public AccessibleWidget(String elementId, AccessibleElement parent) {
		this(elementId, parent, null, null, false, false);
	}
	
	/**
	 * Constructor "complete" with all properties to set.
	 * @param elementId
	 * @param label
	 * @param accessibleName
	 * @param defaultElement
	 */
	public AccessibleWidget(String elementId, AccessibleElement parent, String label, String accessibleName, boolean defaultTabAction, boolean defaultElement) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given widgetId is not valid. widgetId: " + elementId);
			return;
		}
		if (!(parent instanceof AccessibleSubpart)) {
			logger.info("Given Parent is not valid. parent: " + parent.getClass());
			return;
		}
		this.elementId = elementId;
		this.parent = parent;
		this.label = label;
		this.accessibleName = accessibleName;
		this.defaultElement = defaultElement;
	}

	/**
	 * Method to get id of widget.
	 * @return id of the widget as a String.
	 */
	@Override
	public String getElementId() {
		return this.elementId;
	}
	
	/**
	 * Method to get label of widget.
	 * @return label of the widget as as String.
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Method to set label of widget.
	 * @param label the label to set.
	 */
	public void setLabel(String label) {
		if (label == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given label is not valid. label: " + label);
			return;
		}
		this.label = label;
	}
	
	/**
	 * Method to get accessible name of widget.
	 * @return accessible name of the widget as a String,
	 * null if this property was not set.
	 */
	public String getAccessibleName() {
		return this.accessibleName;
	}
	
	public void setAccessibleName(String accessibleName) {
		if (accessibleName == null || accessibleName.equalsIgnoreCase("")) {
			logger.info("Given accessibleName is not valid. accessibleName: " + accessibleName);
			return;
		}
		this.accessibleName = accessibleName;
	}

	/**
	 * Method to check if element needs default tab action
	 * @return true if widget uses default tab action.
	 */
	public boolean needDefaultTabAction() {
		return this.defaultTabAction;
	}
	
	/**
	 * Method to set if widget is needs default tab action or not.
	 * @param defaultTabAction true if widget needs default tab action, false if not.
	 */
	public void setDefaultTabAction(boolean defaultTabAction) {
		this.defaultTabAction = defaultTabAction;
	}	
	
	/**
	 * Method to check if element is considered as
	 * default element (landmark).
	 * @return true if widget is a default element.
	 */
	public boolean isDefaultElement() {
		return this.defaultElement;
	}
	
	/**
	 * Method to set if widget is default element or not.
	 * @param defaultElement true if widget is landmark, false if not.
	 */
	public void setAsDefaultElement(boolean defaultElement) {
		this.defaultElement = defaultElement;
	}

	/**
	 * Method to get children: returns null because
	 * AccessibleWidget cannot contain children.
	 */
	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return null;
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

	@Override
	public AccessibleElement getParent() {
		return this.parent;
	}

}
