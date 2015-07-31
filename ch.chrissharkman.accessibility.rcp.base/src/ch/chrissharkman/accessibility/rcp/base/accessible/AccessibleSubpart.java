package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AccessibleSubpart implements AccessibleElement {

	private String elementId;
	private AccessibleElement parent;
	private boolean defaultTabAction;
	private boolean defaultElement;
	private Object guiObject;
	public List<AccessibleElement> widgets;
	
	public static Logger logger = Logger.getLogger(AccessiblePart.class);
	
	/**
	 * Constructor "minimal" to create an accessiblePart with a
	 * partId. All the other properties are set to null or false.
	 * @param elementId the id of the part.
	 */
	public AccessibleSubpart(String elementId, AccessibleElement parent) {
		this(elementId, parent, false, false);
	}
 	
	/**
	 * Constructor "complete" to set directly all the properties.
	 * @param elementId
	 * @param defaultElement
	 */
	public AccessibleSubpart(String elementId, AccessibleElement parent, boolean defaultTabAction, boolean defaultElement) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given partId is not valid. elementId: " + elementId);
			return;
		}
		if (!(parent instanceof AccessiblePart)) {
			logger.info("Given Parent is not valid. parent: " + parent.getClass());
			return;
		}
		this.elementId = elementId;
		this.parent = parent;
		this.defaultElement = defaultElement;
		this.widgets = new ArrayList<>();
	}
	
	/**
	 * Method to add a widget at the end of list.
	 * @param widget the accessibleWidget to insert
	 */
	public void addWidget(AccessibleWidget widget) {
		if (widget == null) {
			logger.info("Given widget is null");
			return;
		}
		this.widgets.add(widget);
	}
	
	/**
	 * Method to get the id of the part.
	 * @return the id of the part as String.
	 */
	@Override
	public String getElementId() {
		return this.elementId;
	}
	
	/**
	 * Method to check if part needs default tab action.
	 * @return true if part needs default tab action, false if not.
	 */
	public boolean isDefaultTabAction() {
		return defaultTabAction;
	}
	
	/**
	 * Method to set default tab action for part. If true, part
	 * will use default behavior when tab is pressed. 
	 * @param defaultTabAction true if part should have default behaviour, false if not.
	 */
	public void setDefaultTabAction(boolean defaultTabAction) {
		this.defaultTabAction = defaultTabAction;
	}
	
	/**
	 * Method to check if part is a default element (landmark).
	 * @return true if part is default element, false if not.
	 */
	public boolean isDefaultElement() {
		return defaultElement;
	}
	
	/**
	 * Method to set if part is default element (landmark).
	 * @param defaultElement true if part is default element, false if not.
	 */
	public void setAsDefaultElement(boolean defaultElement) {
		this.defaultElement = defaultElement;
	}

	/**
	 * Method to return the child widgets of this AccessiblePart.
	 * @return ArrayList with AccessibleElements.
	 */
	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return this.widgets;
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
