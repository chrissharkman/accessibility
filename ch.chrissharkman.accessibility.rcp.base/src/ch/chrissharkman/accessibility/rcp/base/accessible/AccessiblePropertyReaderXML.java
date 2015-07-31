package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.osgi.framework.Bundle;



public class AccessiblePropertyReaderXML implements AccessiblePropertyReader {

	private String contentBundle;
	private String accessiblePropertyFilePath;
	private static Logger logger = Logger.getLogger(AccessiblePropertyReaderXML.class);
	
	/**
	 * Constructor to create Accessible property reader for xml files.
	 * A content bundle path and a file path are needed.
	 * @param contentBundle the content bundle path e.g. ch.chrissharkman.accessible.base
	 * @param filePath the file path of the xml property file to read.
	 */
	public AccessiblePropertyReaderXML(String contentBundle, String filePath) {
		if (contentBundle == null || contentBundle.equalsIgnoreCase("")) {
			logger.warn("Constructor: contentBundle is null or an empty string.");
		}
		if (filePath == null || filePath.equalsIgnoreCase("")) {
			logger.warn("Constructor: filePath is null or an empty string.");
		}
		this.contentBundle = contentBundle;
		this.accessiblePropertyFilePath = filePath;
	}

	/**
	 * Method to get the complete view tree (hierarchical structure
	 * of the perspectives, parts and widgets for accessible manager
	 * and view mediator.
	 * @return an ArrayList with AccessiblePerspective objects which contain
	 * AccessibleParts and AccessibleWidgets.
	 */
	@Override
	public AccessibleViewTree getTemplateViewTree() {
		Document document = null;
		try {
			document = this.getDocumentFromFile();
		} catch (Exception e) {
			logger.warn("Document from File " + this.accessiblePropertyFilePath + " was not found: " + e.getMessage());
			return null;
		}
		List<AccessibleElement> perspectives = this.parsePerspectivestack(document);
		List<AccessibleElement> toolbars = this.parseTrimbars(document);
		AccessibleViewTree viewTree = new AccessibleViewTree(perspectives, toolbars);
		return viewTree;
	}

	/**
	 * Method to parse the given document and search for complete perspectives
	 * and for complete toolbars.
	 * @param document the JDOM document with all elements
	 * @return an ArrayList with AccessiblePerspective objects that are also completed with
	 * AccessibleParts, AccessibleSubparts and AccessibleWidgets, in accordance with
	 * the given JDOM document.
	 */
	private ArrayList<AccessibleElement> parsePerspectivestack(Document document) {
		ArrayList<AccessibleElement> perspectivestackList = new ArrayList<>();
		Element root = document.getRootElement();
		Element perspectivesRoot = root.getChild(AccessibleConstants.XML_ELEMENT_PERSPECTIVESTACK);
		if (perspectivesRoot != null) {
			try {
				List<Element> perspectives = perspectivesRoot.getChildren(AccessibleConstants.XML_ELEMENT_PERSPECTIVE);
				for (Element perspective : perspectives) {
					AccessiblePerspective accessiblePerspective = this.getCompletePerspective(perspective);
					perspectivestackList.add(accessiblePerspective);
				}
			} catch (Exception e) {
				logger.warn("Tree parsing from given document's perspectivestack failed: " + e);
			}
		}		
		return perspectivestackList;
	}
	
	private ArrayList<AccessibleElement> parseTrimbars(Document document) {
		ArrayList<AccessibleElement> trimbarsList = new ArrayList<>();
		Element root = document.getRootElement();
		Element trimbars = root.getChild(AccessibleConstants.XML_ELEMENT_TRIMBARS);
		if (trimbars != null) {
			try {
				List<Element> toolbars = trimbars.getChildren(AccessibleConstants.XML_ELEMENT_TOOLBAR);
				for (Element toolbar : toolbars) {
					AccessibleToolbar accessibleToolbar = this.getCompleteToolbar(toolbar);
					if (accessibleToolbar != null) {
						trimbarsList.add(accessibleToolbar);
					}	
				}
			} catch (Exception e) {
				logger.warn("Tree parsing from given document's trimbars failed: " + e);
			}
		}
		return trimbarsList;
	}
	
	private AccessibleToolbar getCompleteToolbar(Element toolbar) {
		if (toolbar.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessibleToolbar accessibleToolbar = new AccessibleToolbar(toolbar.getAttributeValue(AccessibleConstants.XML_ELEMENTID));
		// set its properties
		if (toolbar.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessibleToolbar.setAsDefaultElement(toolbar.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + toolbar.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		
		List<Element> handledToolItems = toolbar.getChildren(AccessibleConstants.XML_ELEMENT_HANDLED_TOOL_ITEM);
		for (Element handledToolItem : handledToolItems) {
			AccessibleHandledToolItem accessibleHandledToolItem = this.getCompleteHandledToolItem(handledToolItem, accessibleToolbar);
			if (accessibleHandledToolItem != null) {
				accessibleToolbar.addHandledToolItem(accessibleHandledToolItem);
			}
		}
		return accessibleToolbar;
	}
	
	private AccessibleHandledToolItem getCompleteHandledToolItem(Element handledToolItem, AccessibleElement parent) {
		if (handledToolItem.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessibleHandledToolItem accessibleHandledToolItem = new AccessibleHandledToolItem(handledToolItem.getAttributeValue(AccessibleConstants.XML_ELEMENTID), parent);
		// set its properties
		if (handledToolItem.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessibleHandledToolItem.setAsDefaultElement(handledToolItem.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + handledToolItem.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		if (handledToolItem.getAttributeValue(AccessibleConstants.XML_TOOLTIP) != null) {
			accessibleHandledToolItem.setTooltip(handledToolItem.getAttributeValue(AccessibleConstants.XML_TOOLTIP));
		}
		if (handledToolItem.getAttributeValue(AccessibleConstants.XML_SHORTCUT) != null) {
			accessibleHandledToolItem.setShortcut(handledToolItem.getAttributeValue(AccessibleConstants.XML_SHORTCUT));
		}
		
		return accessibleHandledToolItem;
	}
	
	/**
	 * Method to parse given perspective and to search for complete parts.
	 * @param perspective a perspective element node
	 * @return a complete AccessiblePerspective object
	 */
	private AccessiblePerspective getCompletePerspective(Element perspective) {
		if (perspective.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessiblePerspective accessiblePerspective = new AccessiblePerspective(perspective.getAttributeValue(AccessibleConstants.XML_ELEMENTID));
		// check if defaultElement attribute exists and if it can be cast to boolean
		if (perspective.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessiblePerspective.setAsDefaultElement(perspective.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + perspective.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		List<Element> parts = perspective.getChildren(AccessibleConstants.XML_ELEMENT_PART);
		for (Element part : parts) {
			AccessiblePart accessiblePart = this.getCompletePart(part, accessiblePerspective);
			if (accessiblePart != null) {
				accessiblePerspective.addPart(accessiblePart);				
			}
		}
		return accessiblePerspective;
	}
	
	/**
	 * Method to parse given part and to search for complete Widgets
	 * @param part a part element node
	 * @return a complete AccessiblePart object
	 */
	private AccessiblePart getCompletePart(Element part, AccessibleElement parent) {
		if (part.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessiblePart accessiblePart = new AccessiblePart(part.getAttributeValue(AccessibleConstants.XML_ELEMENTID), parent);
		// check if defaultElement attribute exists and if it can be cast to boolean
		if (part.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessiblePart.setAsDefaultElement(part.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + part.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		if (part.getAttributeValue(AccessibleConstants.XML_LABEL) != null) {
			accessiblePart.setLabel(part.getAttributeValue(AccessibleConstants.XML_LABEL));
		}
		
		List<Element> subparts = part.getChildren(AccessibleConstants.XML_ELEMENT_SUBPART);
		for (Element subpart : subparts) {
			AccessibleSubpart accessibleSubpart = this.getCompleteSubpart(subpart, accessiblePart);
			if (accessibleSubpart != null) {
				accessiblePart.addSubpart(accessibleSubpart);
			}
		}
		return accessiblePart;
	}
	
	/**
	 * Method to parse given part and to search for complete Widgets
	 * @param part a part element node
	 * @return a complete AccessiblePart object
	 */
	private AccessibleSubpart getCompleteSubpart(Element subpart, AccessibleElement parent) {
		if (subpart.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessibleSubpart accessibleSubpart = new AccessibleSubpart(subpart.getAttributeValue(AccessibleConstants.XML_ELEMENTID), parent);
		// check if defaultElement attribute exists and if it can be cast to boolean
		if (subpart.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessibleSubpart.setAsDefaultElement(subpart.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + subpart.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		List<Element> widgets = subpart.getChildren(AccessibleConstants.XML_ELEMENT_WIDGET);
		for (Element widget : widgets) {
			AccessibleWidget accessibleWidget = this.getCompleteWidget(widget, accessibleSubpart);
			if (accessibleWidget != null) {
				accessibleSubpart.addWidget(accessibleWidget);
			}
		}
		return accessibleSubpart;
	}
	
	/**
	 * Method to parse given widget and to return a complete AccessibleWidget.
	 * @param widget the widget to parse
	 * @return a complete AccessibleWidget object
	 */
	private AccessibleWidget getCompleteWidget(Element widget, AccessibleElement parent) {
		if (widget.getAttributeValue(AccessibleConstants.XML_ELEMENTID) == null) {
			return null;
		}
		AccessibleWidget accessibleWidget = new AccessibleWidget(widget.getAttributeValue(AccessibleConstants.XML_ELEMENTID), parent);
		if (widget.getAttributeValue(AccessibleConstants.XML_LABEL) != null) {
			accessibleWidget.setLabel(widget.getAttributeValue(AccessibleConstants.XML_LABEL));
		}
		if (widget.getAttributeValue(AccessibleConstants.XML_ACCESSIBLENAME) != null) {
			accessibleWidget.setAccessibleName(widget.getAttributeValue(AccessibleConstants.XML_ACCESSIBLENAME));
		}
		// check if defaultElement attribute exists and if it can be cast to boolean
		if (widget.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT) != null) {
			try {
				accessibleWidget.setAsDefaultElement(widget.getAttribute(AccessibleConstants.XML_DEFAULTELEMENT).getBooleanValue());				
			} catch (Exception e) {
				logger.warn("defaultElement attribute: not a boolean: " + widget.getAttributeValue(AccessibleConstants.XML_DEFAULTELEMENT));
			}
		}
		return accessibleWidget;
	}
	
	/**
	 * Method to get JDOM document from File which is
	 * defined by contentBundle and accessiblePropertyFilePath of
	 * instance.
	 * @return a JDOM document
	 */
	private Document getDocumentFromFile() {
		Document document = null;
		File file = null;
		SAXBuilder saxBuilder = new SAXBuilder();
		Bundle bundle = Platform.getBundle(this.contentBundle);
		URL fileURL = bundle.getEntry(this.accessiblePropertyFilePath);
		URI resolvedURI = null;
		try {
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			file = new File(resolvedURI);
			document = saxBuilder.build(file);
			logger.info("document built from source: " + fileURL.toString());
		} catch (Exception e) {
			logger.info("File could not be read: fileURL: " + fileURL + " - resolvedURI: " + resolvedURI.toString() + " - " + e,e);
		}
		return document;
	}

	/**
	 * Method to get Key bindings of a property file.
	 * This method is not implemented yet.
	 */
	@Override
	public void getKeybindings() {
		// actual empty method, to be implemented in future when needed
	}
	
	/**
	 * Method to get the accessible property of an individual perspective, part or widget.
	 * No implementation yet. 
	 */
	@Override
	public void getAccessiblePropertyOf(String elementId) {
		// actual empty method, to be implemented in future when needed
	}

}
