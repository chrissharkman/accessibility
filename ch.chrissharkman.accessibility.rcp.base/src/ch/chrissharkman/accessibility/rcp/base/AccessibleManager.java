package ch.chrissharkman.accessibility.rcp.base;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleElement;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleHandledToolItem;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessiblePart;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessiblePropertyReader;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessiblePropertyReaderXML;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleViewTree;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleWidget;
import ch.chrissharkman.accessibility.rcp.base.extensions.AccessibleExtension;
import ch.chrissharkman.accessibility.rcp.base.extensions.DefaultAccessibleExtension;
import ch.chrissharkman.accessibility.rcp.base.handler.AccessibleAdapterWidget;
import ch.chrissharkman.accessibility.rcp.base.handler.AccessibleNavigationListener;
import ch.chrissharkman.accessibility.rcp.base.handler.PartJumpListener;
import ch.chrissharkman.accessibility.rcp.base.handler.SingleEscapeListener;
import ch.chrissharkman.accessibility.rcp.base.handler.SubpartFocusListener;
import ch.chrissharkman.accessibility.rcp.base.handler.SuppressTraverseListener;
import ch.chrissharkman.accessibility.rcp.base.handler.TraverseKeyListener;
import ch.chrissharkman.accessibility.rcp.base.helper.AccessibleHelper;

/**
 * Main class for Accessible Module use.
 * AccessibleManager initializes module with viewMediator, sets PropertyReader and Listener.
 * Contains functionality to enhance gui elements with accessible labels and features.
 * @author ChristianHeimann
 *
 */
@Creatable
public class AccessibleManager {

	private String structureBundleName;
	private String structureResourcePath;
	private String accessibleExtensionClass;
	private String accessibleExtensionBundleName;
	private String accessibleExtensionCanonicalClassName;
	private AccessiblePropertyReader accessiblePropertyReader;
	private AccessibleExtension accessibleExtension;
	private boolean traverseNavigationEnabled;
	private boolean partJumpNavigationEnabled;

	private ViewMediator viewMediator;

	private Listener traverseKeyListener = new TraverseKeyListener();
	private Listener partJumpListener = new PartJumpListener();
	private Listener singleEscapeListener = new SingleEscapeListener();

	@Inject
	@Optional
	private MWindow mWindow;

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	private EPartService partService;

	@Inject
	private static IEventBroker broker;

	private static AccessibleManager instance;
	private static Logger logger = Logger.getLogger(AccessibleManager.class);

	// When true, the accessibility manager is disabled and will not start
	public static boolean disabled;

	/**
	 * Constructor is empty, usually only called by dependency injection Public
	 * modifier needed for dependency injection.
	 */
	public AccessibleManager() {
		logger.info("AccessibleManager constructor called");
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * Method to get singleton instance of AccessibleManager.
	 * 
	 * @return
	 */
	public static AccessibleManager instance() {
		if (instance == null) {
			instance = new AccessibleManager();
		}
		return instance;
	}

	/**
	 * Method to initialize the accessible Manager after class initialization
	 * and after all fields have been injected. Therefore the AccessibleManager
	 * must be called in the activator class of the project as dependency
	 * injection.
	 */
	@PostConstruct
	public void init(MAddon addon) {

		if (disabled) {
			return;
		}

		this.viewMediator = new ViewMediator();

		try {
			Map<String, String> state = addon.getPersistedState();
			this.structureBundleName = state.get("structureBundleName");
			this.structureResourcePath = state.get("structureResourcePath");
			this.accessibleExtensionClass = state.get("accessibleExtensionClass");
			this.accessibleExtensionBundleName = state.get("accessibleExtensionBundleName");
			this.accessibleExtensionCanonicalClassName = state.get("accessibleExtensionCanonicalClassName");
			if (state.get("traverseNavigationEnabled") != null
					&& state.get("traverseNavigationEnabled").equalsIgnoreCase("true")) {
				this.traverseNavigationEnabled = true;
			} else {
				this.traverseNavigationEnabled = false;
			}
			if (state.get("partJumpNavigationEnabled") != null
					&& state.get("partJumpNavigationEnabled").equalsIgnoreCase("true")) {
				this.partJumpNavigationEnabled = true;
			} else {
				this.partJumpNavigationEnabled = false;
			}

			if (structureBundleName == null || structureBundleName.equalsIgnoreCase("")) {
				logger.error("invalid or empty contentBUndle: " + structureBundleName);
				return;
			}
			if (structureResourcePath == null || structureResourcePath.equalsIgnoreCase("")) {
				logger.error("invalid or empty accessiblePropertyFilePath: " + structureResourcePath);
				return;
			}
			// initialize accessibleExtension
			if (accessibleExtensionBundleName != null && !accessibleExtensionBundleName.equalsIgnoreCase("")
					&& accessibleExtensionCanonicalClassName != null && !accessibleExtensionCanonicalClassName.equalsIgnoreCase("")) {
				try {
					this.accessibleExtension = AccessibleHelper.getAccessibleExtensionInstance(
							accessibleExtensionBundleName, accessibleExtensionCanonicalClassName);
				} catch (Exception e) {
					logger.info(
							"in catch after Class.forName() when inizializing accessibleExtension: because of exception: "
									+ e.getMessage() + " - default extension will be set.");
					this.accessibleExtension = new DefaultAccessibleExtension();
				}
			} else {
				this.accessibleExtension = new DefaultAccessibleExtension();
			}

			this.initializeAccessiblePropertiesReader();
			AccessibleViewTree templateViewTree = this.accessiblePropertyReader.getTemplateViewTree();
			this.viewMediator.setTemplateViewTree(templateViewTree);

			// Initialize Listener
			this.initializeAppCompleteListener();
			this.initializeTraverseKeyListener(this.traverseNavigationEnabled);
			this.initializePartJumpListener(this.partJumpNavigationEnabled);
			this.initializeSingleEscapeListener(true);
			this.initializeGlobalGuiElementsConnectedListener();

		} catch (Exception e) {
			logger.error("Error in AccessibleManager: " + e);
		}
	}

	/**
	 * Method adds Filter for SWT-Traverse-Events, a traverseKeyListener will be
	 * called. As this TraverseKeyListener can be active or not, this method
	 * enables the TraverseKeyListener.
	 */
	private void initializeTraverseKeyListener(boolean enable) {
		Display.getCurrent().addFilter(SWT.Traverse, traverseKeyListener);
		if (enable) {
			enableAccessibleNavigationListener((AccessibleNavigationListener) traverseKeyListener);
		}
	}

	/**
	 * Method to initialize part jump listener: a listener, only conceived for
	 * part jump with a non traverse key event.
	 * 
	 * @param enable
	 *            set true to use the listener, false to disable
	 */
	private void initializePartJumpListener(boolean enable) {
		Display.getCurrent().addFilter(SWT.KeyUp, partJumpListener);
		if (enable) {
			enableAccessibleNavigationListener((AccessibleNavigationListener) partJumpListener);
		}
	}

	/**
	 * Method to initialize single escape listener: a listener, only conceived
	 * for escaping with escape key event.
	 * 
	 * @param enable
	 *            set true to use the listener, false to disable
	 */
	private void initializeSingleEscapeListener(boolean enable) {
		Display.getCurrent().addFilter(SWT.KeyUp, singleEscapeListener);
		if (enable) {
			enableAccessibleNavigationListener((AccessibleNavigationListener) singleEscapeListener);
		}
	}

	/**
	 * Method to set an EventHandler for part activation after
	 * AppStartupComplete event is received.
	 */
	private void initializeAppCompleteListener() {
		AccessibleManager.getBroker().subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {

			/**
			 * Method to handle the AppStartupComplete event. This causes
			 * initialize the PartActivationEventHandler
			 * PerspectiveActivationListener, also creates global GUI live view
			 * tree.
			 */
			@Override
			public void handleEvent(Event event) {
				initializePartActivationListener();
				viewMediator.createGlobalGuiLiveViewTree();
				for (AccessibleElement perspective : viewMediator.getLiveViewTree().perspectives) {
					addAccessibleFeaturesWithin(perspective);
				}
			}
		});
	}

	/**
	 * Method to set the part activation listener and its handler. The included
	 * handler checks if the new, activated part has already its accessible
	 * features, and if not, make them set, connects its widgets with their
	 * models.
	 * 
	 */
	private void initializePartActivationListener() {
		broker.subscribe(UIEvents.UILifeCycle.ACTIVATE, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				try {
					AccessiblePart part = (AccessiblePart) viewMediator.getAccessibleElementBy(AccessibleHelper.getActivePartId(partService));
					
					logger.info(
							"activate part: " + part.getElementId() + " accFeatSet: " + part.isAccessibleFeaturesSet());
					// check if accessible features are already set
					if (part != null && !part.isAccessibleFeaturesSet()) {

						// take existing elements and match them with the model
						// instead of relaying on templateViewTree
						viewMediator.completePartGuiLiveViewTree();

						// Add accessible features to all connected subparts and widgets
						addAccessibleFeaturesWithin(part);

						// Set accessibleFeatures flag of part to true
						part.setAccessibleFeaturesSet(true);
					}
					// clear existing Tab lists
					Control[] emptyTablist = {};
					Display.getCurrent().getActiveShell().setTabList(emptyTablist);
				} catch (Exception e) {
					// do nothing, next activate event will come
				}
			}
		});

	}

	public void initializeGlobalGuiElementsConnectedListener() {
		broker.subscribe(AccessibleConstants.GLOBAL_GUI_LIVE_TREE_READY, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
				for (AccessibleElement perspective : viewMediator.getLiveViewTree().perspectives) {
					addAccessibleFeaturesWithin(perspective);
				}
				for (AccessibleElement toolbar : viewMediator.getLiveViewTree().trimbars) {
					addAccessibleFeaturesWithin(toolbar);
				}
			}
		});
	}

	/**
	 * Method to iterate about children of given accessibleElement and to set
	 * accessible functionality to them when GUI object is set.
	 * 
	 * @param parent
	 *            the accessibleElement within all children should receive
	 *            accessible functionality.
	 */
	public void addAccessibleFeaturesWithin(AccessibleElement parent) {
		for (AccessibleElement element : parent.getAccessibleChildren()) {
			if (element.getGuiObject() != null) {
				addAccessibleFeaturesTo(element);
			}
			if (element.getAccessibleChildren() != null) {
				addAccessibleFeaturesWithin(element);
			}
		}
	}

	/**
	 * Method to add accessible functionality to the given AccessibleElement
	 * 
	 * @param accessibleElement
	 *            to which accessible functionality will be added
	 */
	public void addAccessibleFeaturesTo(AccessibleElement accessibleElement) {
		if (accessibleElement.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_WIDGET)) {
			// logger.info("A Widget is found: " +
			// accessibleElement.getElementId());
			AccessibleWidget accessibleWidget = (AccessibleWidget) accessibleElement;
			// set getName Listener
			if (accessibleWidget.getAccessibleName() != null) {
				this.setAccessibleListenerGetNameTo(accessibleWidget);
			}
			// set TraverseListener
			if (this.isTraverseNavigationEnabled()) {
				this.setAccessibleTraverseListenerTo(accessibleWidget);
			}

		} else if (accessibleElement.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_SUBPART)) {
			// set FocusListener (for not leaving subpart)
			if (accessibleElement.getGuiObject().getClass().getSimpleName()
					.equalsIgnoreCase(AccessibleConstants.CLASS_BROWSER)) {
				logger.info("A Browser object is found.");
				Control webSite = getWebsiteControl((Composite) accessibleElement.getGuiObject());
				webSite.addFocusListener(new SubpartFocusListener());
			} else if (accessibleElement.getGuiObject().getClass().getSimpleName()
					.equalsIgnoreCase(AccessibleConstants.CLASS_COMPOSITE)) {
				logger.info("a subpart is found, it's not a browser: " + accessibleElement.getElementId());
				Composite composite = (Composite) accessibleElement.getGuiObject();
				composite.addFocusListener(new SubpartFocusListener());
			}
		} else if (accessibleElement.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_PART)) {
			// logger.info("A Part is found: " +
			// accessibleElement.getElementId());
			// set Label
			AccessiblePart accessiblePart = (AccessiblePart) accessibleElement;
			if (accessiblePart.getLabel() != null && accessiblePart.getLabel().length() > 0) {
				MPart part = (MPart) accessiblePart.getGuiObject();
				String fullLabel = accessibleExtension.getLabelString(accessiblePart.getLabel());
				part.setLabel(fullLabel);
			}
		} else
			if (accessibleElement.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_PERSPECTIVE)) {
			// ready for future accessibility enhancements
		} else if (accessibleElement.getClass().getSimpleName().equalsIgnoreCase(AccessibleConstants.CLASS_TOOLBAR)) {
			// ready for future accessibility enhancements
		} else if (accessibleElement.getClass().getSimpleName()
				.equalsIgnoreCase(AccessibleConstants.CLASS_HANDLEDTOOLITEM)) {
			// set Tooltip
			AccessibleHandledToolItem handledToolItem = (AccessibleHandledToolItem) accessibleElement;
			if (handledToolItem.getTooltip() != null) {
				MHandledToolItem guiItem = (MHandledToolItem) handledToolItem.getGuiObject();
				String fullTooltip = accessibleExtension.getTooltipString(handledToolItem.getTooltip());
				guiItem.setTooltip(fullTooltip);
			}
		}
	}

	private Control getWebsiteControl(Composite browser) {
		Control webSite = null;
		try {
			Control[] controls = browser.getChildren();
			// TODO: check the arrays, to be sure to get the correct website
			// control
			Composite frame = (Composite) controls[0];
			webSite = (Control) frame.getChildren()[0];
		} catch (Exception e) {
			logger.info("Exception in getWebsiteControl: " + e);
		}
		return webSite;
	}

	/**
	 * Method to remove accessible functionality from a perspective/part/widget.
	 * Usually this method is called before a part is disposed.
	 * 
	 * @param elementId
	 *            the part from which accessible functionality will be removed
	 */
	public void removeAccessibleFeaturesFrom(String elementId) {
		logger.info("in removeFunctionality");
	}

	/**
	 * Method to set an AccessibleListener to the connected GUI object control
	 * that contains the getName method. This method returns the given
	 * accessibleName description to screenreaders.
	 * 
	 * @param widget
	 */
	private void setAccessibleListenerGetNameTo(AccessibleWidget widget) {
		if (widget.getGuiObject() instanceof Control) {
			Control control = (Control) widget.getGuiObject();
			control.getAccessible().addAccessibleListener(new AccessibleAdapterWidget(widget));
		}
	}

	/**
	 * Method to set an traverse listener to the connected GUI object control
	 * which suppress the default traverse handling for this control.
	 * 
	 * @param widget
	 *            the accessibleWidget of which connected control receives the
	 *            SuppressTraverseListener
	 */
	private void setAccessibleTraverseListenerTo(AccessibleWidget widget) {
		if (!widget.needDefaultTabAction()) {
			if (widget.getGuiObject() instanceof Control) {
				Control control = (Control) widget.getGuiObject();
				control.addTraverseListener(new SuppressTraverseListener());
			}
		}
	}

	/**
	 * Method to set the file path of an accessible properties input. The file
	 * suffix determines which AccessiblePropertyReader will be taken e.g. .xml
	 * so the AccessiblePropertyReaderXML will be set for parsing. Actual
	 * supported file formats: .xml
	 */
	private void initializeAccessiblePropertiesReader() {
		String[] supportedFormats = { "xml" };
		String[] splittedPath = this.structureResourcePath.split("\\.");
		String suffix = splittedPath[splittedPath.length - 1];
		if (containsEqualString(supportedFormats, suffix)) {
			switch (suffix) {
			case "xml":
				this.accessiblePropertyReader = new AccessiblePropertyReaderXML(this.structureBundleName,
						this.structureResourcePath);
				break;
			}

		} else {
			logger.error("File format not supported for accessible properties: " + suffix);
		}
	}

	/**
	 * Method to check if a string to compare is contained in a StringArray.
	 * 
	 * @param stringArray
	 *            an array of strings
	 * @param stringToCompare
	 *            the string to find in the array.
	 * @return true if an equal string (ignoreCase) is in the array, false if
	 *         not
	 */
	private boolean containsEqualString(String[] stringArray, String stringToCompare) {
		boolean containsEqualString = false;
		for (String string : stringArray) {
			if (stringToCompare.equalsIgnoreCase(string)) {
				containsEqualString = true;
			}
		}
		return containsEqualString;
	}

	/**
	 * Method to check if traverse navigation is enabled. This flag is set with
	 * a passed parameter from addon persist state.
	 * 
	 * @return true if traverse navigation is enabled, false if not.
	 */
	public boolean isTraverseNavigationEnabled() {
		return this.traverseNavigationEnabled;
	}

	/**
	 * Method to get the actual accessibleExtension object.
	 * 
	 * @return an accessibleExtension object
	 */
	public AccessibleExtension getAccessibleExtension() {
		return accessibleExtension;
	}

	/**
	 * Method to set a new accessibleExtension object.
	 * 
	 * @param accessibleExtension
	 *            the accessibleExtension element to set
	 */
	public void setAccessibleExtension(AccessibleExtension accessibleExtension) {
		if (accessibleExtension == null) {
			return;
		}
		this.accessibleExtension = accessibleExtension;
	}

	public Listener getTraverseKeyListener() {
		return this.traverseKeyListener;
	}

	public Listener getPartJumpListener() {
		return this.partJumpListener;
	}

	public Listener getSingleEscapeListener() {
		return this.singleEscapeListener;
	}

	public void enableAccessibleNavigationListener(AccessibleNavigationListener listener) {
		listener.setEnabled(true);
	}

	public void disableAccessibleNavigationListener(AccessibleNavigationListener listener) {
		listener.setEnabled(false);
	}

	/**
	 * Method to get static broker instance. This avoids to make an injection of
	 * every class in this module.
	 * 
	 * @return the event broker instance
	 */
	public static IEventBroker getBroker() {
		return broker;
	}

}
