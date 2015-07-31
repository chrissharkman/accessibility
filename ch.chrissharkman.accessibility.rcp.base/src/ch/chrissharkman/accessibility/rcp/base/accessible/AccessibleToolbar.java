package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AccessibleToolbar implements AccessibleElement {
	
	private String elementId;
	private boolean defaultTabAction;
	private boolean defaultElement;
	private Object guiObject;
	public List<AccessibleElement> handledToolItems;
	
	private static Logger logger = Logger.getLogger(AccessibleToolbar.class);

	/**
	 * Constructor "minimal" to create an accessibleToolbar with a
	 * partId. All the other properties are set to null or false.
	 * @param elementId the id of the toolbar.
	 */
	public AccessibleToolbar(String elementId) {
		this(elementId, false, false);
	}
 	
	/**
	 * Constructor "complete" to set directly all the properties.
	 * @param elementId
	 * @param defaultElement
	 */
	public AccessibleToolbar(String elementId, boolean defaultTabAction, boolean defaultElement) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given partId is not valid. partId: " + elementId);
			return;
		}
		this.elementId = elementId;
		this.defaultTabAction = defaultTabAction;
		this.defaultElement = defaultElement;
		this.handledToolItems = new ArrayList<>();
	}
	
	
	/**
	 * Method to add a handletToolItem at the end of list.
	 * @param handledToolItem the accessibleHandledToolItem to insert
	 */
	public void addHandledToolItem(AccessibleElement handledToolItem) {
		if (handledToolItem != null && handledToolItem instanceof AccessibleHandledToolItem) {
			this.handledToolItems.add(handledToolItem);		
		}
	}
	
	
	/**
	 * Method to check if toolbar needs default tab action.
	 * @return true if toolbar needs default tab action, false if not.
	 */
	public boolean isDefaultTabAction() {
		return defaultTabAction;
	}
	
	@Override
	public boolean isDefaultElement() {
		return this.defaultElement;
	}

	@Override
	public String getElementId() {
		return this.elementId;
	}

	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return this.handledToolItems;
	}

	@Override
	public Object getGuiObject() {
		return this.guiObject;
	}

	@Override
	public void setGuiObject(Object guiObject) {
		this.guiObject = guiObject;
	}

	public void setAsDefaultElement(boolean defaultElement) {
		this.defaultElement = defaultElement;
		
	}
	
	/**
	 * Method to get parent: returns null, because parent
	 * is not an accessibleElement but AccessibleViewTree object.
	 */
	@Override
	public AccessibleElement getParent() {
		return null;
	}

}
