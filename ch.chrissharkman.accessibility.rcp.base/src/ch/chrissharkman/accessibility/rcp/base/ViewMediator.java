package ch.chrissharkman.accessibility.rcp.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindowElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleElement;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleHandledToolItem;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessiblePart;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessiblePerspective;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleSubpart;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleToolbar;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleViewTree;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleWidget;
import ch.chrissharkman.accessibility.rcp.base.handler.PartJumpListener;
import ch.chrissharkman.accessibility.rcp.base.handler.TraverseKeyListener;
import ch.chrissharkman.accessibility.rcp.base.helper.StringMatcher;

public class ViewMediator {
	
	private AccessibleViewTree templateViewTree;
	private AccessibleViewTree liveViewTree;
	
	private static long lastFocusWidgetEventTimestamp;
	
	@Inject
	private static MApplication application;
	
	@Inject
	private static EModelService modelService;
	
	@Inject
	private static EPartService partService;
	
	@Inject @Optional
	private static MWindow mWindow;
	
	private static ViewMediator instance;
	
	private static Logger logger = Logger.getLogger(ViewMediator.class);
	
	/**
	 * Empty constructor
	 */
	public ViewMediator() {
		if (instance == null) {
			instance = this;
		}
		this.liveViewTree = new AccessibleViewTree(new ArrayList<AccessibleElement>(), new ArrayList<AccessibleElement>());
	}
	
	/**
	 * Method to get singleton instance of ViewMediator.
	 * @return
	 */
	public static ViewMediator instance() {
		if (instance == null) {
			instance = new ViewMediator();
		}
		return instance;
	}
	
	/**
	 * Method to set a complete viewTree as property of a viewMediator and to set necessary listener
	 * concerning the navigation events for the viewTree.
	 * @param list a complete hierarchy of perspectives, parts and widgets.
	 */
	public void setTemplateViewTree(AccessibleViewTree viewTree) {
		if (viewTree == null) {
			logger.info("viewTree to set for ViewMediator is null");
			return;
		}
		this.templateViewTree = viewTree;
		this.setFocusEventHandler();

	}
	
	/**
	 * Method to connect all global GUI objects (perspective, parts, trimbars with its handledtoolitems) with
	 * their model in the viewTree, starting from model, not from GUI.
	 */
	public void connectGlobalGuiObjects() {
		for (AccessibleElement ae : this.templateViewTree.perspectives) {
			this.connectGuiObjectWith(ae);
			if (ae.getAccessibleChildren() != null && ae.getAccessibleChildren().size() > 0) {
				for (AccessibleElement aPart : ae.getAccessibleChildren()) {
					this.connectGuiObjectWith(aPart);
				}
			}
		}
		for (AccessibleElement toolbar : this.templateViewTree.trimbars) {
			this.connectGuiObjectWith(toolbar);
			if (toolbar.getAccessibleChildren() != null && toolbar.getAccessibleChildren().size() > 0) {
				for (AccessibleElement handledToolItem : toolbar.getAccessibleChildren()) {
					this.connectGuiObjectWith(handledToolItem);
				}
			}
		}
		AccessibleManager.getBroker().post(UIEvents.UILifeCycle.ACTIVATE, null);
		AccessibleManager.getBroker().post(AccessibleConstants.GLOBAL_GUI_LIVE_TREE_READY, null);
		logger.info("Global GUI Objects connected.");
	}
	
	/**
	 * Method to connect a GUI Object with an AccessibleElement model object.
	 * @param element the AccessibleElement object to connect.
	 */
	public void connectGuiObjectWith(AccessibleElement element) {
		if (element instanceof AccessiblePerspective || element instanceof AccessiblePart || element instanceof AccessibleToolbar || element instanceof AccessibleHandledToolItem) {
			try {
				MUIElement guiObject = modelService.find(element.getElementId(), application);
				if (guiObject != null) {
					element.setGuiObject(guiObject);
				}
			} catch (Exception e) {
				logger.info("Error when searching element with modelService.");
			}
		}
	}
	
	/**
	 * Method to connect all visible GUI controls (composites, widgets) with the
	 * corresponding elements in the given part. This is a convenience method.
	 * @param part the part in which GUI controls should be connected.
	 */
	public void connectVisibleGuiControlsWith(AccessibleElement part) {
		List<Control> accessibleControls = new ArrayList<>();
		accessibleControls = getVisibleAccessibleControls(Display.getCurrent().getActiveShell(), accessibleControls);
		if (accessibleControls.size() < 1) {
			logger.info("No visible accessible controls found in active shell with accessibleId.");
		}
		for (Control control : accessibleControls) {
			connectControlWithin(part, control);
		}
	}
	
	/**
	 * Method to connect the given control (composite, widget) with the corresponding accessibleElement
	 * in the given part.
	 * @param part the part within the control should be connected
	 * @param controlToConnect the control to connect
	 */
	public void connectControlWithin(AccessibleElement part, Control controlToConnect) {
		AccessibleElement accessibleElement =this.getAccessibleElementBy(controlToConnect.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString(), part.getAccessibleChildren());
		if (accessibleElement != null) {
			accessibleElement.setGuiObject(controlToConnect);
		}
	}
	
	/**
	 * Method to get all widgets with a declared accessibleId, that are actually visible.
	 * Basic idea from: http://stackoverflow.com/questions/13721105/automatically-generate-ids-on-swt-widgets
	 */
	private List<Control> getVisibleAccessibleControls(Composite c, List<Control> accessibleControls) {
		Control[] children = c.getChildren();
		for (Control control : children) {
			if (control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null && control.isVisible()) {
				accessibleControls.add(control);
			}
			if (control instanceof Composite) {
				getVisibleAccessibleControls((Composite) control, accessibleControls);
			}
		}
		return accessibleControls;
	}

	/**
	 * Method is entry point for AccessibleManager: the controls
	 * in the given AccessiblePart object will be connected, if
	 * AccessiblePart has Subparts.
	 * @param part the AccessiblePart object which controls
	 * will be connected.
	 */
	public void connectGuiControlsIn(AccessiblePart part) {
		if (part.getAccessibleChildren().size() > 0) {
			Control[] controls = getControlsFrom(part);
			connectGuiControls(controls, part);
		}	
	}	

	
/**
 * Method to connect Controls that has an accessibleId with its
 * reference models (subparts, widgets) in AccessiblePart.
 * @param controls
 * @param part
 */
	public void connectGuiControls(Control[] controls, AccessiblePart part) {
		if (controls == null || part == null) {
			return;
		}
		for (Control control : controls) {
			if (control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
				connectControlWithin(part, control);
			}
			if (control instanceof Composite) {
				Control[] controlsChildren = ((Composite) control).getChildren();
				connectGuiControls(controlsChildren, part);
			}
		}
	}
	
	/**
	 * Method to get all controls form the given AccessiblePart
	 * @param part An AccessiblePart object with a set guiObject.
	 * @return an array of Control objects 
	 */
	protected Control[] getControlsFrom(AccessiblePart part) {
		Control[] controls = null;
		if (part == null) {
			return controls;
		}
		try {
			MContribution guiObjectCast = (MContribution) part.getGuiObject();
			controls = getControlsFrom(guiObjectCast);
		} catch (Exception e) {
			logger.info("Cast in getControlsFrom not possible with Part: " + part.getElementId());
		}
		return controls;
	}
	
	/**
	 * Method to get all controls from the given MContribution part,
	 * which is a casted rcp part object.
	 * @param part Usually a cast rcp part object.
	 * @return an array of Control objects
	 */
	private Control[] getControlsFrom(MContribution part) {
		Control[] controls = null;
		try {
			AccessibleView accessibleView = (AccessibleView) part.getObject();
			Composite parent = accessibleView.getViewComposite();
			controls = parent.getChildren();
		} catch (Exception e) {
			logger.info("Cast in getControlsFrom not possible with Part: " + part.getElementId());
		}
		return controls;
	}
	
	private Control[] getControlsFrom(MPart part) {
		Control[] controls = null;
		try {
			AccessibleView accessibleView = (AccessibleView) part.getObject();
			Composite parent = accessibleView.getViewComposite();
			controls = parent.getChildren();
		} catch (Exception e) {
			logger.info("Exception when getting Controls from given part: " +  part.getElementId() 
			+ " - check if part has implemented interface AccessibleView.");
		}
		return controls;
	}
	
	/**
	 * Method to complete live view tree with elements having accessibleId.
	 * Controls in active part are searched and then iterated, looked up in
	 * the templateView if they match and then liveView model is built.
	 */
	protected void completePartGuiLiveViewTree() {
		MPart guiActivePart = partService.getActivePart();
		AccessiblePart activeLivePart = (AccessiblePart) getLiveAccessibleElementBy(guiActivePart);
		Control[] controls = getControlsFrom(guiActivePart);
		// create the missing elements in the liveViewTree, first subparts, then widgets
		createGuiSubpartsInLiveViewTree(controls, activeLivePart);
		createGuiWidgetsInLiveViewTree(controls, activeLivePart);
	}



	private void createGuiWidgetsInLiveViewTree(Control[] controls, AccessiblePart activeLivePart) {
		if (controls == null || activeLivePart == null) {
			return;
		}
		for (Control control : controls) {
			if (control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null && !(control instanceof Composite)) {
				// search for template match, copy to live tree
				copyWidgetIntoLiveViewTree(control, activeLivePart);
			}
			if (control instanceof Composite) {
				Control[] controlsChildren = ((Composite) control).getChildren();
				createGuiWidgetsInLiveViewTree(controlsChildren, activeLivePart);
			}
		}
	}

	/**
	 * Method to connect and copy a control into the liveViewTree.
	 * TODO: Needs to be refactored in future.
	 * @param control
	 * @param activeLivePart
	 */
	private void copyWidgetIntoLiveViewTree(Control control, AccessibleElement activeLivePart) {
		try {
			String subpartAccessibleId = getParentAccessibleId(control);
			String widgetAccessibleId = control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString();
			AccessibleElement activeLivePerspective = activeLivePart.getParent();
			AccessibleElement activeTemplatePerspective = getTemplatePerspectiveOf(activeLivePerspective);
			AccessibleElement activeTemplatePart = getTemplatePartOf((AccessiblePart) activeLivePart, activeTemplatePerspective);
			AccessibleElement activeLiveSubpart = getAccessibleElementBy(subpartAccessibleId, activeLivePart.getAccessibleChildren());
			AccessibleElement activeTemplateSubpart = getTemplateElementOf(activeLiveSubpart, activeTemplatePart.getAccessibleChildren());
			
			int indexPositionWidgetInLiveTree = 0;
			boolean staticMatch = StringMatcher.instance().fullIdExistsIn(activeTemplateSubpart.getAccessibleChildren(), control);
			for (AccessibleElement templateWidget : activeTemplateSubpart.getAccessibleChildren()) {
				// iterate to get position in list for widget
				for (AccessibleElement liveWidget : activeLiveSubpart.getAccessibleChildren()) {
					if (StringMatcher.instance().checkIdMatch(liveWidget.getElementId(), templateWidget.getElementId())) {
						indexPositionWidgetInLiveTree++;
					}
				}
				// check if match, then create new widget and insert it at correct index position
				if (staticMatch) {
					if (templateWidget.getElementId().equalsIgnoreCase(widgetAccessibleId)) {
						AccessibleWidget newLiveWidget = new AccessibleWidget(widgetAccessibleId, activeLiveSubpart);
						newLiveWidget.setGuiObject(control);
						newLiveWidget.setAccessibleName(((AccessibleWidget) templateWidget).getAccessibleName());
						newLiveWidget.setAsDefaultElement(templateWidget.isDefaultElement());
						newLiveWidget.setLabel(((AccessibleWidget) templateWidget).getLabel());
						newLiveWidget.setDefaultTabAction(((AccessibleWidget) templateWidget).needDefaultTabAction());
						activeLiveSubpart.getAccessibleChildren().add(indexPositionWidgetInLiveTree, newLiveWidget);
						return;						
					}
				} else if (StringMatcher.instance().checkIdMatch(widgetAccessibleId, templateWidget.getElementId())) {
					AccessibleWidget newLiveWidget = new AccessibleWidget(widgetAccessibleId, activeLiveSubpart);
					newLiveWidget.setGuiObject(control);
					newLiveWidget.setAccessibleName(((AccessibleWidget) templateWidget).getAccessibleName());
					newLiveWidget.setAsDefaultElement(templateWidget.isDefaultElement());
					newLiveWidget.setLabel(((AccessibleWidget) templateWidget).getLabel());
					newLiveWidget.setDefaultTabAction(((AccessibleWidget) templateWidget).needDefaultTabAction());
					activeLiveSubpart.getAccessibleChildren().add(indexPositionWidgetInLiveTree, newLiveWidget);
					return;
				}
			}
			
		} catch (Exception e) {
			logger.info("Set widget into live view tree failed: " + e.getMessage());
		}
	}

	private String getParentAccessibleId(Control control) {
		String accessibleId = null;
		if (control.getParent() != null && control.getParent().getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
			accessibleId = control.getParent().getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString();
		} else if (control.getParent() != null) {
			accessibleId = getParentAccessibleId(control);
		}
		return accessibleId;
	}

	/**
	 * Method to connect Controls that has an accessibleId with its
	 * reference models (subparts, widgets) in AccessiblePart.
	 * @param controls a list of controls for which subparts and widgets
	 * in the liveViewTree must be created.
	 * @param activeLivePart the existing live active part
	 */
	public void createGuiSubpartsInLiveViewTree(Control[] controls, AccessiblePart activeLivePart) {
		if (controls == null || activeLivePart == null) {
			return;
		}
		for (Control control : controls) {
			if (control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
				// search for template match, copy to live tree
				copySubpartIntoLiveViewTree(control, activeLivePart);
			}
			if (control instanceof Composite) {
				Control[] controlsChildren = ((Composite) control).getChildren();
				createGuiSubpartsInLiveViewTree(controlsChildren, activeLivePart);
			}
		}
	}
	
	/**
	 * Method to create an AccessibleSubpart or AccessibleWidget (a copy of template),
	 * connect the model with the given control and set it at the correct position.
	 * TODO: Needs to be refactored in future.
	 * @param control
	 * @param activeLivePart
	 */
	private void copySubpartIntoLiveViewTree(Control control, AccessibleElement activeLivePart) {
		try {
			String controlAccessibleId = control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString();
			AccessibleElement activeLivePerspective = activeLivePart.getParent();
			AccessibleElement activeTemplatePerspective = getTemplatePerspectiveOf(activeLivePerspective);
			AccessibleElement activeTemplatePart = getTemplatePartOf((AccessiblePart) activeLivePart, activeTemplatePerspective);
			
			int indexPositionSubpartInLiveTree = 0;
			boolean staticMatch = StringMatcher.instance().fullIdExistsIn(activeTemplatePart.getAccessibleChildren(), control);
			for (AccessibleElement templateSubpart : activeTemplatePart.getAccessibleChildren()) {
				// iterate to get position in list for subpart
				for (AccessibleElement liveSubpart : activeLivePart.getAccessibleChildren()) {
					if (StringMatcher.instance().checkIdMatch(liveSubpart.getElementId(), templateSubpart.getElementId())) {
						indexPositionSubpartInLiveTree++;
					}
				}
				// check if match, then copy to create a new subpart
				if (staticMatch) {
					if (templateSubpart.getElementId().equalsIgnoreCase(controlAccessibleId)) {
						AccessibleSubpart newLiveSubpart = new AccessibleSubpart(controlAccessibleId, activeLivePart);
						newLiveSubpart.setGuiObject(control);
						newLiveSubpart.setDefaultTabAction(((AccessibleSubpart) templateSubpart).isDefaultTabAction());
						newLiveSubpart.setAsDefaultElement(templateSubpart.isDefaultElement());
						activeLivePart.getAccessibleChildren().add(indexPositionSubpartInLiveTree, newLiveSubpart);
						return;						
					}
				} else if (StringMatcher.instance().checkIdMatch(controlAccessibleId, templateSubpart.getElementId())) {
					AccessibleSubpart newLiveSubpart = new AccessibleSubpart(controlAccessibleId, activeLivePart);
					newLiveSubpart.setGuiObject(control);
					newLiveSubpart.setDefaultTabAction(((AccessibleSubpart) templateSubpart).isDefaultTabAction());
					newLiveSubpart.setAsDefaultElement(templateSubpart.isDefaultElement());
					activeLivePart.getAccessibleChildren().add(indexPositionSubpartInLiveTree, newLiveSubpart);
					return;
				}
			}
		} catch (Exception e) {
			logger.info("Set subpart into live view tree failed: " + e.getMessage());
		}
		
		
	}

	/**
	 * Method to get the corresponding template part of the given activeLivePart.
	 * @param activeLivePart
	 * @param activeTemplatePerspective
	 * @return
	 */
	private AccessibleElement getTemplatePartOf(AccessiblePart activeLivePart, AccessibleElement activeTemplatePerspective) {
		AccessibleElement activeTemplatePart = null;
		List<AccessibleElement> templateParts = activeTemplatePerspective.getAccessibleChildren();
		activeTemplatePart = getTemplateElementOf(activeLivePart, templateParts);
		return activeTemplatePart;
	}

	/**
	 * Method to get a template perspective which is the template of the actual and given activeLivePerspective.
	 * @param activeLivePerspective
	 * @return the template perspective of the given activeLivePerspective
	 */
	private AccessibleElement getTemplatePerspectiveOf(AccessibleElement activeLivePerspective) {
		AccessibleElement activeTemplatePerspective = null;
		List<AccessibleElement> templatePerspectives = this.templateViewTree.perspectives;
		activeTemplatePerspective = getTemplateElementOf(activeLivePerspective, templatePerspectives);
		return activeTemplatePerspective;
	}
	
	/**
	 * Method to get a matching template element in a given list, the match must match the activeLiveElement.
	 * @param activeLiveElement
	 * @param templateElements
	 * @return an AccessibleElement which is the reference in the templateViewTree structure.
	 */
	private AccessibleElement getTemplateElementOf(AccessibleElement activeLiveElement, List<AccessibleElement> templateElements) {
		AccessibleElement templateElementToReturn = null;
		for (AccessibleElement templateElement : templateElements) {
			if (StringMatcher.instance().checkIdMatch(activeLiveElement.getElementId(), templateElement.getElementId())) {
				templateElementToReturn = templateElement;
				break;
			}
		}
		return templateElementToReturn;
	}

	/**
	 * Method to return the liveViewTree of the ViewMediator object.
	 * @return An ArrayList with AccessibleElements that describes the viewTree.
	 */
	protected AccessibleViewTree getLiveViewTree() {
		return this.liveViewTree;
	}
	
	/**
	 * Method to get an AccessibleElement object with the given id
	 * looked for in all perspectives of the viewTree. If an AccessibleElement cannot
	 * be found, it will search with the elementId of its GUI parent element.
	 * @param elementId id of the wanted AccessibleElement
	 * @return the first AccessibleElement with given id, null if nothing is found.
	 */
	protected AccessibleElement getAccessibleElementBy(String elementId) {
		AccessibleElement accessibleElementToReturn = null;
		accessibleElementToReturn = getAccessibleElementBy(elementId, this.getLiveViewTree().perspectives);
		if (accessibleElementToReturn == null) {
			// if no element found, look for parent (e.g. for partStack)
			accessibleElementToReturn = getAccessibleElementByParent(elementId);
			if (accessibleElementToReturn != null) {
				return accessibleElementToReturn;
			}
		}
		return accessibleElementToReturn;
	}
	

	
	/**
	 * Method to get the AccessibleElement object with the given id
	 * in the given List of AccessibleElements and its children.
	 * @param elementId the elementId of the searched AccessibleElement 
	 * @param inList 
	 * @return an AccessibleElement if found, null if nothing is found.
	 */
	protected AccessibleElement getAccessibleElementBy(String elementId, List<AccessibleElement> inList) {
		AccessibleElement accessibleElementToReturn = null;
		if (inList != null && inList.size() > 0 && elementId != null && elementId.length() > 0) {
			for (AccessibleElement ae : inList) {
				if (elementId.equalsIgnoreCase(ae.getElementId())) {
					return ae;
				} else {
					accessibleElementToReturn = getAccessibleElementBy(elementId, ae.getAccessibleChildren());
					if (accessibleElementToReturn != null) {
						return accessibleElementToReturn;
					}
				}
			}
			
		}
		return accessibleElementToReturn;
	}
	
	/**
	 * Method to get the AccessibleElement of the parent element from given elementId.
	 * @param elementId the element whose parent will be searched for.
	 * @return an AccessibleElement if found, null if nothing is found.
	 */
	protected AccessibleElement getAccessibleElementByParent(String elementId) {
		AccessibleElement accessibleElementToReturn = null;
		try {
			String parentElementId = modelService.find(elementId, application).getParent().getElementId();
			accessibleElementToReturn = getAccessibleElementBy(parentElementId);
		} catch (Exception e) {
			// no action needed
		}
		return accessibleElementToReturn;
	}
	
	/**
	 * Method to get the AccessibleElement which contains the given guiObject
	 * as guiObject. It searches the AccessibleElement object in all perspectives
	 * of the liveViewTree. 
	 * @param guiObject the guiObject that is looked for in the AccessibleElements
	 * @return the accessibleElement or null
	 */
	protected AccessibleElement getAccessibleElementBy(Object guiObject) {
		AccessibleElement accessibleElementToReturn = null;
		accessibleElementToReturn = getAccessibleElementBy(guiObject, this.liveViewTree.perspectives);
		return accessibleElementToReturn;
	}
	
	/**
	 * Method to get the AccessibleElement object with the given guiObject
	 * in the given List of AccessibleElements and its children, grand children etc.
	 * @param guiObject the GUI element that is contained by the searched AccessibleElement 
	 * @param inList 
	 * @return an AccessibleElement if found, null if nothing is found.
	 */
	protected AccessibleElement getAccessibleElementBy(Object guiObject, List<AccessibleElement> inList) {
		AccessibleElement accessibleElementToReturn = null;
		if (inList != null && inList.size() > 0 && guiObject != null) {
			for (AccessibleElement ae : inList) {
				if (ae.getGuiObject() == guiObject) {
					return ae;
				} else {
					accessibleElementToReturn = getAccessibleElementBy(guiObject, ae.getAccessibleChildren());
					if (accessibleElementToReturn != null) {
						return accessibleElementToReturn;
					}
				}
			}	
		}
		return accessibleElementToReturn;
	}
	
	/**
	 * Method to get the AccessibleElement object of LiveViewTree with the given
	 * guiObject and the given perspective.
	 * @param guiObject the GUI element that is contained by the searched AccessibleElement.
	 * @return
	 */
	protected AccessibleElement getLiveAccessibleElementBy(Object guiObject) {
		AccessibleElement accessibleElementToReturn = null;
		if (guiObject != null) {
			accessibleElementToReturn = getAccessibleElementBy(guiObject, this.liveViewTree.perspectives);
		}
		return accessibleElementToReturn;
	}
	
	
	/**
	 * Method to set handler, which are called for all the focus events for navigation.
	 */
	private void setFocusEventHandler() {
		
		// Set handler for focus on next subpart event: then set focus on next subpart in the viewTree.
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_NEXT_SUBPART, new EventHandler() {
			
			@Override
			public void handleEvent(Event event) {
				try {
					AccessibleElement activePart = getAccessibleElementBy(partService.getActivePart().getElementId());
					Control control = Display.getCurrent().getFocusControl();
					setFocusOnNextSubpartOrPart(activePart, control);
				} catch (Exception e) {
					logger.info("focus next subpart handleEvent failed: " + e);
				}
			}
		});
		
		
		// Set handler for focus on previous subpart event: then set focus on previous subpart in the viewTree.
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_PREVIOUS_SUBPART, new EventHandler() {
			
			@Override
			public void handleEvent(Event event) {
				logger.info("focus on previous subpart event, not implemented yet");
			}
		});

		
		// Set handler for focus on next widget event: then set focus on next widget in the viewTree, 
		// if accessibleId is set on control. If not, default behavior produced.
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_NEXT_WIDGET, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				lastFocusWidgetEventTimestamp = System.currentTimeMillis();
				try {					
					AccessibleElement activePart = getAccessibleElementBy(partService.getActivePart().getElementId());
					Control control = Display.getCurrent().getFocusControl();
					if (control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
						AccessibleElement focusedWidget = getAccessibleElementBy(
							control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString(),
							activePart.getAccessibleChildren());
						setFocusOnNextWidget(activePart, focusedWidget);
					} else {
						setFocusOnNextDefaultWidget(activePart);
					}
					
				} catch (Exception e) {
					logger.info("Exception in focus next widget handleEvent: " + e + " - Default eventhandler will handle it.");
				}
			}
		});
		
		
		// Set handler for focus on previous widget event: then set focus on previous widget in the viewTree.
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_PREVIOUS_WIDGET, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				logger.info("focus on previous widget event, not implemented yet");
			}
			
		});
		
		// Set handler for part jump, with two possibilities: respecting subpart order of viewTree, or just taking
		// active parts (as some kind of fallback strategy).
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_PART_JUMP, new EventHandler() {
			
			/**
			 * Method to handle focus part jump event. It chooses setFocusOnNextSubpartOrPart when traverseKeyListener
			 * is enabled, activateNextVisiblePart() when only jumpPartListener is enabled.
			 */
			@Override
			public void handleEvent(Event event) {
				try {
					if (((TraverseKeyListener) AccessibleManager.instance().getTraverseKeyListener()).isEnabled()) {
						AccessibleElement activePart = getAccessibleElementBy(partService.getActivePart().getElementId());
						Control control = Display.getCurrent().getFocusControl();
						setFocusOnNextSubpartOrPart(activePart, control);
					} else {						
						activateNextVisiblePart();
					}
					
				} catch (Exception e) {
					logger.info("FocusPartJump Event not handled correctly: " + e.getMessage());
				}
			}

		});
		
		// Handler to set focus on default widget of part and then on default widget default part.
		AccessibleManager.getBroker().subscribe(AccessibleConstants.FOCUS_ESCAPE, new EventHandler() {

			/**
			 * Method to handle focus escape event: setting the focus on the first child of viewTree trimbars.
			 */
			@Override
			public void handleEvent(Event event) {
				try {
					if (liveViewTree.trimbars != null && liveViewTree.trimbars.size() > 0) {
						MToolBar mainToolBar = (MToolBar) liveViewTree.trimbars.get(0).getGuiObject();
						ToolBar toolbar = (ToolBar) mainToolBar.getWidget();
						toolbar.setFocus();
					}
				} catch (Exception e) {
					logger.info("Error in handleEvent of focus escape: " + e);
				}
			}
			
		});
		
	}
	
	/**
	 * Method to create the global parts of live view tree (perspectives + parts).
	 */
	protected void createGlobalGuiLiveViewTree() {
		try {
			List<MPerspective> perspectives = getAllPerspectives();
			for (MPerspective perspective : perspectives) {
				setPerspectiveIntoLiveViewTree(perspective);
				List<MPart> parts = getAllPartsIn(perspective);
				for (MPart part : parts) {
					setPartIntoLiveViewTree(part, perspective);
				}
			}
			List<MToolBar> globalToolBars = getAllTrimBars();
			for (MToolBar toolBar : globalToolBars) {
				setToolBarIntoLiveViewTree(toolBar);
				List<MHandledToolItem> handledToolItems = getAllHandledToolItemsIn(toolBar);
				for (MHandledToolItem toolItem : handledToolItems) {
					setToolItemIntoLiveViewTree(toolItem, toolBar);
				}
			}
			AccessibleManager.getBroker().post(UIEvents.UILifeCycle.ACTIVATE, null);
			AccessibleManager.getBroker().post(AccessibleConstants.GLOBAL_GUI_LIVE_TREE_READY, null);
			logger.info("liveTree created with global GUI elements");
		} catch (Exception e) {
			logger.info("createGlobalGuiLiveViewTree failed: " + e.getMessage());
		}
		
	}


	/**
	 * Method to add a part into the liveViewTree, at the correct position.
	 * TODO: Needs to be refactored in future.
	 * @param guiPart a MPart object which is searched to place in liveViewTree
	 * @param guiPerspective the parent MPerspective gui object to know where
	 * to place the part.
	 */
	private void setPartIntoLiveViewTree(MUIElement guiPart, MUIElement guiPerspective) {
		AccessibleElement livePerspective = getLivePerspectiveOf(guiPerspective);
		AccessibleElement templatePerspective = getTemplatePerspectiveOf(guiPerspective);
		
		// iteration over the templateParts in templatePerspective to find matching element
		int indexToPosition = 0;
		boolean staticMatch = StringMatcher.instance().fullIdExistsIn(templatePerspective.getAccessibleChildren(), guiPart);
		if (templatePerspective.getAccessibleChildren() != null && templatePerspective.getAccessibleChildren().size() > 0) {
			for (AccessibleElement templatePart : templatePerspective.getAccessibleChildren()) {
				// iteration over already existing liveParts, to know the position index where to set the new part
				if (livePerspective.getAccessibleChildren() != null && livePerspective.getAccessibleChildren().size() > 0) {
					for (AccessibleElement livePart : livePerspective.getAccessibleChildren()) {
						if (StringMatcher.instance().checkIdMatch(livePart.getElementId(), templatePart.getElementId())) {
							if (livePerspective.getAccessibleChildren().size() > indexToPosition) {
								indexToPosition++;
							}	
						}
					}
				}
				if (staticMatch) {
					if (templatePart.getElementId().equalsIgnoreCase(guiPart.getElementId())) {
						AccessiblePart newPart = new AccessiblePart(guiPart.getElementId(), livePerspective);
						newPart.setGuiObject(guiPart);
						newPart.setAsDefaultElement(templatePart.isDefaultElement());
						newPart.setDefaultTabAction(((AccessiblePart) templatePart).isDefaultTabAction());
						newPart.setAccessibleFeaturesSet(false);
						livePerspective.getAccessibleChildren().add(indexToPosition, newPart);
						return;
					}
				} else if (StringMatcher.instance().checkIdMatch(guiPart.getElementId(), templatePart.getElementId())) {
					AccessiblePart newPart = new AccessiblePart(guiPart.getElementId(), livePerspective);
					newPart.setGuiObject(guiPart);
					newPart.setAsDefaultElement(templatePart.isDefaultElement());
					newPart.setDefaultTabAction(((AccessiblePart) templatePart).isDefaultTabAction());
					newPart.setAccessibleFeaturesSet(false);
					livePerspective.getAccessibleChildren().add(indexToPosition, newPart);
					return;
				}
				
			}
		} else {
			// logger.info("setPartIntoLiveViewTree not possible: templatePerspective.parts is null or empty");
		}
	}

	/**
	 * Method to return templatePerspective model which contains
	 * the matches the given guiPerspective's id.
	 * @param guiPerspective a MPerspective object which model should be returned
	 * @return the templatePerspective AccessibleElement or null, if no correct perspective model was found
	 */
	private AccessibleElement getTemplatePerspectiveOf(MUIElement guiPerspective) {
		if (this.templateViewTree.perspectives != null && this.templateViewTree.perspectives.size() > 0) {
			for (AccessibleElement templatePerspective : this.templateViewTree.perspectives) {
				if (StringMatcher.instance().checkIdMatch(guiPerspective.getElementId(), templatePerspective.getElementId())) {
					return templatePerspective;
				}
			}
		}
		return null;
	}

	/**
	 * Method to return the livePerspective model which contains
	 * the given guiPerspective.
	 * @param guiPerspective a MPerspective object which model should be returned
	 * @return the livePerspective AccessibleElement or null, if no correct perspective model was found
	 */
	private AccessibleElement getLivePerspectiveOf(MUIElement guiPerspective) {
		if (this.liveViewTree.perspectives != null && this.liveViewTree.perspectives.size() > 0) {
			for (AccessibleElement livePerspective : this.liveViewTree.perspectives) {
				if (livePerspective.getGuiObject() == guiPerspective) {
					return livePerspective;
				}
			}
		}
		return null;
	}

	/**
	 * Method to return the template model of the given guiToolBar.
	 * @param guiToolBar
	 * @return template AccessibleElement model or null, if no match was found.
	 */
	private AccessibleElement getTemplateToolBarOf(MToolBar guiToolBar) {
		if (this.templateViewTree.trimbars != null && this.templateViewTree.trimbars.size() > 0) {
			for (AccessibleElement templateTrimBar : this.templateViewTree.trimbars) {
				if (StringMatcher.instance().checkIdMatch(guiToolBar.getElementId(), templateTrimBar.getElementId())) {
					return templateTrimBar;
				}
			}
		}
		return null;
	}
	
	/**
	 * Method to return the liveToolbar model which contains
	 * the given guiToolBar.
	 * @param guiToolBar
	 * @return the liveToolBar AccessibleElement or null, if no correct live toolBar model was found
	 */
	private AccessibleElement getLiveToolBarOf(MUIElement guiToolBar) {
		if (this.liveViewTree.trimbars != null && this.liveViewTree.trimbars.size() > 0) {
			for (AccessibleElement liveToolBar : this.liveViewTree.trimbars) {
				if (liveToolBar.getGuiObject() == guiToolBar) {
					return liveToolBar;
				}
			}
		}
		return null;
	}
	
	/**
	 * Method to add a perspective into the liveViewTree, at the correct position.
	 * TODO: Needs to be refactored in future.
	 * @param guiObject perspective object to insert
	 */
	private void setPerspectiveIntoLiveViewTree(MUIElement guiObject) {
		int indexToPosition = 0;
		boolean staticMatch = StringMatcher.instance().fullIdExistsIn(this.templateViewTree.perspectives, guiObject);
		// iteration over all template perspectives, searching for a match
		if (this.templateViewTree.perspectives != null && this.templateViewTree.perspectives.size() > 0) {
			for (AccessibleElement templatePerspective : this.templateViewTree.perspectives) {
				// iteration over existing live perspectives (if they exist), to find correct position to insert
				// check how many elements exist already
				if (this.liveViewTree.perspectives != null && this.liveViewTree.perspectives.size() > 0) {
					for (AccessibleElement livePerspective : this.liveViewTree.perspectives) {
						if (StringMatcher.instance().checkIdMatch(livePerspective.getElementId(), templatePerspective.getElementId())) {
							if (this.liveViewTree.perspectives.size() > indexToPosition) {
								indexToPosition++;
							}	
						}
					}
				}
				// look for match, when true, insert copy of AccessiblePerspective into viewTree.
				if (staticMatch) {
					if (templatePerspective.getElementId().equalsIgnoreCase(guiObject.getElementId())) {
						AccessiblePerspective newPerspective = new AccessiblePerspective(guiObject.getElementId());
						newPerspective.setGuiObject(guiObject);
						newPerspective.setAsDefaultElement(templatePerspective.isDefaultElement());
						this.liveViewTree.perspectives.add(indexToPosition, newPerspective);
						return;
					}
				} else if (StringMatcher.instance().checkIdMatch(guiObject.getElementId(), templatePerspective.getElementId())) {
					AccessiblePerspective newPerspective = new AccessiblePerspective(guiObject.getElementId());
					newPerspective.setGuiObject(guiObject);
					newPerspective.setAsDefaultElement(templatePerspective.isDefaultElement());
					this.liveViewTree.perspectives.add(indexToPosition, newPerspective);
					return;
				}
			}
		} else {
			logger.info("setPerspectiveIntoLiveViewTree not possible: templateViewTree.perspectives is null or empty");
		}
	}

	/**
	 * Method to set a toolItem into the LiveViewTree
	 * TODO: Needs to be refactored in future.
	 * @param toolItem
	 * @param handledToolItems
	 */
	private void setToolItemIntoLiveViewTree(MHandledToolItem guiToolItem, MToolBar guiToolBar) {
		
		AccessibleElement liveToolBar = getLiveToolBarOf(guiToolBar);
		AccessibleElement templateToolBar = getTemplateToolBarOf(guiToolBar);
		
		// iteration over the templateItems in templateToolBar to find matching element
		int indexToPosition = 0;
		boolean staticMatch = StringMatcher.instance().fullIdExistsIn(templateToolBar.getAccessibleChildren(), guiToolItem);
		if (templateToolBar.getAccessibleChildren() != null && templateToolBar.getAccessibleChildren().size() > 0) {
			for (AccessibleElement templateToolItem : templateToolBar.getAccessibleChildren()) {
				// iteration over already existing liveParts, to know the position index where to set the new part
				if (liveToolBar.getAccessibleChildren() != null && liveToolBar.getAccessibleChildren().size() > 0) {
					for (AccessibleElement liveToolItem : liveToolBar.getAccessibleChildren()) {
						if (StringMatcher.instance().checkIdMatch(liveToolItem.getElementId(), templateToolItem.getElementId())) {
							if (liveToolBar.getAccessibleChildren().size() > indexToPosition) {
								indexToPosition++;
							}	
						}
					}
				}
				if (staticMatch) {
					if (templateToolItem.getElementId().equalsIgnoreCase(guiToolItem.getElementId())) {
						AccessibleHandledToolItem newHandledToolItem = new AccessibleHandledToolItem(guiToolItem.getElementId(), liveToolBar);
						newHandledToolItem.setGuiObject(guiToolItem);
						newHandledToolItem.setAsDefaultElement(templateToolItem.isDefaultElement());
						newHandledToolItem.setTooltip(((AccessibleHandledToolItem) templateToolItem).getTooltip());
						liveToolBar.getAccessibleChildren().add(indexToPosition, newHandledToolItem);
						return;	
					}
				} else if (StringMatcher.instance().checkIdMatch(guiToolItem.getElementId(), templateToolItem.getElementId())) {
					AccessibleHandledToolItem newHandledToolItem = new AccessibleHandledToolItem(guiToolItem.getElementId(), liveToolBar);
					newHandledToolItem.setGuiObject(guiToolItem);
					newHandledToolItem.setAsDefaultElement(templateToolItem.isDefaultElement());
					newHandledToolItem.setTooltip(((AccessibleHandledToolItem) templateToolItem).getTooltip());
					liveToolBar.getAccessibleChildren().add(indexToPosition, newHandledToolItem);
					return;
				}
				
			}
		}
		
	}
	


	/**
	 * Method to set a Toolbar (global Trimbar) into the liveViewTree.
	 * @param guiObject
	 */
	private void setToolBarIntoLiveViewTree(MUIElement guiObject) {
		if (this.templateViewTree.trimbars != null && this.templateViewTree.trimbars.size() > 0) {
			for (AccessibleElement templateToolBar : this.templateViewTree.trimbars) {
				if (StringMatcher.instance().checkIdMatch(guiObject.getElementId(), templateToolBar.getElementId())) {
					AccessibleToolbar newToolbar = new AccessibleToolbar(guiObject.getElementId());
					newToolbar.setGuiObject(guiObject);
					newToolbar.setAsDefaultElement(templateToolBar.isDefaultElement());
					this.liveViewTree.trimbars.add(newToolbar);
					return;
				}
			}
		}
	}

	/**
	 * Method to get a List with all MPerspective objects of the application.
	 * @return a List with all the MPerspective objects of the application.
	 */
	private List<MPerspective> getAllPerspectives() {
		return modelService.findElements(application, null, MPerspective.class, null);
	}

	private List<MPart> getAllPartsIn(MUIElement perspective) {
		return modelService.findElements(perspective, null, MPart.class, null);
	}
	
	private List<MToolBar> getAllTrimBars() {
		return modelService.findElements(application, null, MToolBar.class, null);
	}
	
	private List<MHandledToolItem> getAllHandledToolItemsIn(MUIElement toolBar) {
		return modelService.findElements(toolBar, null, MHandledToolItem.class, null);
	}	

	private void setFocusOnNextSubpartOrPart(AccessibleElement activePart, Control activeControl) {
		if (activePart == null || activeControl == null) {
			return;
		}
		AccessibleElement elementToFocus = null;
		AccessibleElement activeSubpart = getParentSubpartOf(activeControl);
		elementToFocus = getNextSubpart(activeSubpart);
		if (elementToFocus == null) {
			elementToFocus = getNextPart(activePart);
		}
		
		if (elementToFocus != null) {
			setFocusOnElement(elementToFocus);
		}
		
	}
	
	/**
	 * Method to get the next subpart in the list of subparts of parent part.
	 * The returned subpart is a sibling of given subpart. This method does NOT
	 * iterate, so if given subpart is the last of the list, null is returned.
	 * @param activeSubpart the subpart of which next sibling is searched
	 * @return the next subpart in the list or null if actualSubpart is not valid or last element of list.
	 */
	private AccessibleElement getNextSubpart(AccessibleElement activeSubpart) {
		AccessibleElement nextSubpart = null;
		if (activeSubpart != null) {
			List<AccessibleElement> subpartList = activeSubpart.getParent().getAccessibleChildren();
			int actualIndex = subpartList.indexOf(activeSubpart);
			if (actualIndex + 1 < subpartList.size()) {
				nextSubpart = subpartList.get(actualIndex + 1);
			}
		}
		return nextSubpart;
	}
	
	/**
	 * Method to get next part in the list of parts of parent perspective.
	 * The returned subpart is a sibling of given subpart. This method DOES
	 * iterate.
	 * @param activePart the part of which next sibling is searched
	 * @return the next part in the list
	 */
	private AccessibleElement getNextPart(AccessibleElement activePart) {
		AccessibleElement nextPart = null;
		int nextIndex = getNextIndex(activePart.getParent().getAccessibleChildren(), activePart.getParent().getAccessibleChildren().indexOf(activePart));
		nextPart = activePart.getParent().getAccessibleChildren().get(nextIndex);
		return nextPart;
	}
	
	/**
	 * Method to set Focus on given element. When a part is given,
	 * part is activated and default control/widget/subpart is searched to set focus on.
	 * @param elementToFocus the AccessibleElement object which contains
	 * the GUI object to set focus on.
	 */
	private void setFocusOnElement(AccessibleElement elementToFocus) {
		if (elementToFocus.getGuiObject() instanceof Button) {
			logger.info("elementToFocus is Button");
			Button button = (Button) elementToFocus.getGuiObject();
			button.setFocus();

		} else if (elementToFocus.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_WIDGET)) {
			logger.info("elementToFocus is Widget");
		} else if (elementToFocus.getGuiObject() instanceof Browser) {
			Browser guiNextElement = (Browser) elementToFocus.getGuiObject();
			logger.info("elementToFocus is Browser");
			guiNextElement.setFocus();
		} else if (elementToFocus.getGuiObject() instanceof Composite) {
			if (elementToFocus.getAccessibleChildren() != null && elementToFocus.getAccessibleChildren().size() > 0) {
				AccessibleElement elementToFocusWidget = getDefaultElement(elementToFocus.getAccessibleChildren());
				setFocusOnElement(elementToFocusWidget);
			} else {
				Composite guiNextElement = (Composite) elementToFocus.getGuiObject();	
				logger.info("elementToFocus is Composite");
				guiNextElement.setFocus();
			}
		} else if (elementToFocus.getGuiObject() instanceof MPart) {
			logger.info("elementToFocus is MPart: " + elementToFocus.getElementId());
			MPart part = (MPart) elementToFocus.getGuiObject();
			partService.showPart(part, PartState.ACTIVATE);
			partService.activate(part, false);
			if (elementToFocus.getAccessibleChildren() != null && elementToFocus.getAccessibleChildren().size() > 0) {
				AccessibleElement elementToFocusSubpart = getDefaultElement(elementToFocus.getAccessibleChildren());
				logger.info("setFocusOnElement subpart: " + elementToFocusSubpart.getElementId());
				setFocusOnElement(elementToFocusSubpart);
			}
		}
	}
	
	/**
	 * Method to activate the next existing part, iteration over all parts,
	 * starts again at beginning if at last part.
	 */
	private void activateNextVisiblePart() {
		// iterate about existing visible parts
		// minimal solution for navigation enhancement
		Collection<MPart> parts = partService.getParts();
		MPart activePart = partService.getActivePart();
		Iterator<MPart> partIterator = parts.iterator();
		boolean nextPartFound = false;
		MPart nextPart = null;
		MPart firstPart = null;
		while (partIterator.hasNext() && !nextPartFound) {
			nextPart = (MPart) partIterator.next();
			if (firstPart == null) {
				firstPart = nextPart;
			}
			if (nextPart == activePart) {
				if (partIterator.hasNext()) {
					nextPart = (MPart) partIterator.next();
					nextPartFound = true;
				} else {
					nextPart = firstPart;
				}
			}
		}
		partService.activate(nextPart);
	}
	
	/**
	 * Method to set focus on next widget in the same subpart. This
	 * convenience method loops for the next enabled widget control
	 * and sets focus on this. If no other focusedWidget is found,
	 * again focusedWidget gets focus.
	 * @param activePart the active part of the application
	 * @param focusedWidget the actual focused widget
	 */
	private void setFocusOnNextWidget(AccessibleElement activePart, AccessibleElement focusedWidget) {
		AccessibleElement nextWidget = this.getNextWidget(activePart, focusedWidget);
		while (focusedWidget != nextWidget && nextWidget.getGuiObject() != null && !guiObjectIsEnabled(nextWidget.getGuiObject())) {
			nextWidget = this.getNextWidget(activePart, nextWidget);
		}
		setFocusOn(nextWidget);
	}
	
	private void setFocusOnNextDefaultWidget(AccessibleElement activePart) {
		AccessibleElement defaultSubpart = getDefaultElementFrom(activePart.getAccessibleChildren());
		AccessibleElement defaultWidget = null;
		if (defaultSubpart != null) {
			defaultWidget = getDefaultElementFrom(defaultSubpart.getAccessibleChildren());
		}
		if (defaultWidget != null) {
			setFocusOn(defaultWidget);
		}
	}
	
	/**
	 * Method to get the first default element from a given accessibleElement list.
	 * If no element has explicitly set to be the default element, then the first element
	 * of the list will be returned as default element.
	 * @param accessibleElements The list of AccessibleElement objects to be iterated to find the default
	 * @return an AccessibleElement
	 */
	private AccessibleElement getDefaultElementFrom(List<AccessibleElement> accessibleElements) {
		AccessibleElement defaultElement = null;
		if (accessibleElements != null && accessibleElements.size() > 0) {
			for (int index = 0; index < accessibleElements.size() && defaultElement == null; index++) {
				AccessibleElement element = accessibleElements.get(index);
				if (element.isDefaultElement()) {
					defaultElement = element;
				}
			}
			// if no element has default indication, first element in list becomes default.
			if (defaultElement == null) {
				defaultElement = accessibleElements.get(0);
			}	
		}
		return defaultElement;
	}
	
	/**
	 * Method to get the next widget in the same subpart in the viewTree.
	 * @param activePart active part is needed because IDs of widgets can be the same in different parts.
	 * @param focusedWidget the widget model object with the actual focus.
	 * @return the next accessibleWidget in the viewTree, if the last one of subpart was focused, first one
	 * gets again focused. (no jump to next subpart).
	 */
	private AccessibleElement getNextWidget(AccessibleElement activePart, AccessibleElement focusedWidget) {
		AccessibleElement actualAccessibleSubpart = null;
		AccessibleElement nextWidget = null;
		int indexFocusedWidget = -1;
		int nextIndex = -1;
		
		if (focusedWidget == null) {
			logger.info("focusedWidget == null");
			return null;
		}
		
		// find actualAccessibleSubpart and index of focusedWidget
		for (AccessibleElement accessibleSubpart : activePart.getAccessibleChildren()) {
			List<AccessibleElement> widgets = accessibleSubpart.getAccessibleChildren();
			for (int indexWidget = 0; indexWidget < widgets.size(); indexWidget++) {
				if (widgets.get(indexWidget).equals(focusedWidget)) {
					indexFocusedWidget = indexWidget;
					actualAccessibleSubpart = accessibleSubpart;
					break;
				}
			}
		}
		
		nextIndex = getNextIndex(actualAccessibleSubpart.getAccessibleChildren(), indexFocusedWidget);
		nextWidget = actualAccessibleSubpart.getAccessibleChildren().get(nextIndex);
		return nextWidget;
	}
	
	private boolean guiObjectIsEnabled(Object guiObject) {
		boolean isEnabled = true;
		if (guiObject instanceof Button) {
			Button button = (Button) guiObject;
			isEnabled = button.isEnabled();
		}
		return isEnabled;
	}
	
	/**
	 * Method to set focus on guiObject of given widget.
	 * @param widget
	 */
	private void setFocusOn(AccessibleElement widget) {
		if (widget.getGuiObject() instanceof Control) {
			Control control = (Control) widget.getGuiObject();
			control.setFocus();
			logger.info("control.setFocus: " + widget.getElementId());
		} else {
			logger.info("guiObject is not a control.");
			// set focus where?
		}
	}
	
	/**
	 * Method to get nextIndex of the list, if actualIndex is the last index
	 * of the list, so 0 is returned.
	 * @param list a List with AccessibleElement
	 * @param actualIndex the actual index to find its next value
	 * @return 0 if actual index is smaller than 0 or bigger than list size - 1, the value of its incremented index, or -1 if actualIndex is negative
	 */
	private int getNextIndex(List<AccessibleElement> list, int actualIndex) {
		int nextIndex = 0;
		if (0 <= actualIndex && actualIndex < list.size() - 1) {
			nextIndex = actualIndex + 1;
		} else if (actualIndex < 0) {
			nextIndex = -1;
		}
		return nextIndex;
	}
	
	/**
	 * Handler to set focus again in subpart that just lost focus, if it was lost by a pressed Tab key
	 * and not through clicking.
	 * @param data
	 */
	@Inject @Optional
	public void subscribeSubpartLostFocus(@UIEventTopic(AccessibleConstants.SUBPART_LOST_FOCUS) Control controlLostFocus) {
		long inTime = lastFocusWidgetEventTimestamp + AccessibleConstants.MAX_LATENCY_TAB;
		if (System.currentTimeMillis() < inTime) {
			if (controlLostFocus != null) {
				logger.info("subpart lost focus called. try to set focus on: " + controlLostFocus.toString());
				controlLostFocus.setFocus();
			}
			
		}
		
	}
	
	/**
	 * Method to find a declared subpart parent of the given control
	 * @param control the control which ancestors are checked to be a subpart
	 * @return the subpart AccessibleElement or null
	 */
	private AccessibleElement getParentSubpartOf(Control control) {
		AccessibleElement subpart = null;
		Composite parent = control.getParent();
		while (parent != null && subpart == null) {
			if (parent instanceof Composite) {
				if (parent.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
					subpart = getAccessibleElementBy(parent);
					logger.info("subpart: " + subpart.getElementId());
				}
				parent = parent.getParent();
			} else {
				parent = null;
			}
		}
		
		return subpart;
	}
	
	/**
	 * Method to get the default element of a AccessibleElement array list.
	 * @param elements an array list of AccessibleElement objects
	 * @return null if elements' size < 1, the first element of the array list if no default is set,
	 * or the last as default declared element in the array list
	 */
	private AccessibleElement getDefaultElement(List<AccessibleElement> elements) {
		if (elements.size() < 1) {
			logger.info("no element found, elements' size: " + elements.size());
			return null;
		}
		AccessibleElement element = null;
		for (AccessibleElement e : elements) {
			if (e.isDefaultElement()) {
				element = e;
			}
		}
		
		if (element == null) {
			element = elements.get(0);
		}
		return element;
	}

	
	
}
