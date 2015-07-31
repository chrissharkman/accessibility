package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AccessiblePart implements AccessibleElement {

	private AccessibleElement parent;
	private String elementId;
	private String label;
	private boolean defaultTabAction;
	private boolean defaultElement;
	private boolean accessibleFeaturesSet;
	private Object guiObject;
	public List<AccessibleElement> subparts;
	
	public static Logger logger = Logger.getLogger(AccessiblePart.class);
	
	/**
	 * Constructor "minimal" to create an accessiblePart with a
	 * partId. All the other properties are set to null or false.
	 * @param elementId the id of the part.
	 */
	public AccessiblePart(String elementId, AccessibleElement parent) {
		this(elementId, parent, "", false, false, false);
	}
 	
	/**
	 * Constructor "complete" to set directly all the properties.
	 * @param elementId
	 * @param defaultElement
	 */
	public AccessiblePart(String elementId, AccessibleElement parent, String label, boolean defaultTabAction, boolean defaultElement, boolean accessibleFeaturesSet) {
		if (elementId == null || elementId.equalsIgnoreCase("")) {
			logger.info("Given partId is not valid. partId: " + elementId);
			return;
		}
		if (!(parent instanceof AccessiblePerspective)) {
			logger.info("Given Parent is not valid. parent: " + parent.getClass());
			return;
		}
		this.elementId = elementId;
		this.parent = parent;
		this.label = label;
		this.defaultElement = defaultElement;
		this.setAccessibleFeaturesSet(false);
		this.subparts = new ArrayList<>();
	}
	
	/**
	 * Method to add a subpart at the end of list.
	 * @param subpart the accessibleSubpart to insert
	 */
	public void addSubpart(AccessibleSubpart subpart) {
		if (subpart == null) {
			logger.info("Given widget is null");
			return;
		}
		this.subparts.add(subpart);
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
	 * Method to set if for this part accessible features are already set.
	 * @return accessibleFeaturesSet true if part 
	 */
	public boolean isAccessibleFeaturesSet() {
		return accessibleFeaturesSet;
	}
	
	public void setAccessibleFeaturesSet(boolean accessibleFeaturesSet) {
		this.accessibleFeaturesSet = accessibleFeaturesSet;
	}
	/**
	 * Method to return the child widgets of this AccessiblePart.
	 * @return ArrayList with AccessibleElements.
	 */
	@Override
	public List<AccessibleElement> getAccessibleChildren() {
		return this.subparts;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public AccessibleElement getParent() {
		return parent;
	}


}
