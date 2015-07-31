package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.List;

import org.apache.log4j.Logger;

public class AccessibleHandledToolItem implements AccessibleElement {

	private AccessibleElement parent;
	private String elementId;
	private String tooltip;
	private String shortcut;
	private boolean defaultElement;
	private boolean defaultTabAction;
	private Object guiObject;
	
	private static Logger logger = Logger.getLogger(AccessibleHandledToolItem.class);
	
	
	/**
	 * Constructor "minimal" to create an accessibleToolbar with a
	 * partId. All the other properties are set to null or false.
	 * @param elementId the id of the toolbar.
	 */
	public AccessibleHandledToolItem(String elementId, AccessibleElement parent) {
		this(elementId, parent, "", "", false, false);
	}
 	
	/**
	 * Constructor "complete" to set directly all the properties.
	 * @param elementId
	 * @param defaultElement
	 */
	public AccessibleHandledToolItem(String elementId, AccessibleElement parent, String tooltip, String shortcut, boolean defaultTabAction, boolean defaultElement) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given elementId is not valid. elementId: " + elementId);
			return;
		}
		if (!(parent instanceof AccessibleToolbar)) {
			logger.info("Given Parent is not valid. parent: " + parent.getClass());
			return;
		}
		this.parent = parent;
		this.elementId = elementId;
		this.defaultElement = defaultElement;
		this.tooltip = tooltip;
		this.shortcut = shortcut;
	}
	
	/**
	 * Method to check if handledToolItem needs default tab action.
	 * @return true if handledToolItem needs default tab action, false if not.
	 */
	public boolean isDefaultTabAction() {
		return this.defaultTabAction;
	}
	
	@Override
	public boolean isDefaultElement() {
		return this.defaultElement;
	}

	@Override
	public String getElementId() {
		return this.elementId;
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getShortcut() {
		return shortcut;
	}
	
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}
	
	@Override
	public Object getGuiObject() {
		return this.guiObject;
	}

	@Override
	public void setGuiObject(Object guiObject) {
		this.guiObject = guiObject;
	}

	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return null;
	}

	public void setAsDefaultElement(boolean defaultElement) {
		this.defaultElement = defaultElement;
		
	}

	@Override
	public AccessibleElement getParent() {
		return this.parent;
	}

}
