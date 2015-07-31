package ch.chrissharkman.accessibility.rcp.base.accessible;

public class AccessibleConstants {

	// Event Topic identifier for all accessible handling topics
	public static final String BASE_TOPIC = "ACCESSIBLE";
	public static final String SEPARATOR = "/";
	
	// Event Triggers
	public static final String VIEWTREE_COMPLETE = BASE_TOPIC + SEPARATOR + "VT" + SEPARATOR + "COMPLETE";
	public static final String GLOBAL_GUI_LIVE_TREE_READY = BASE_TOPIC + SEPARATOR + "GUI" + SEPARATOR + "LIVETREEREADY";
	public static final String TAB_WAS_PRESSED = BASE_TOPIC + SEPARATOR + "KEYEVENT" + SEPARATOR + "TABWASPRESSED";
	
	// Focus Events
	public static final String BASE_FOCUSEVENT = "FOCUSEVENT";
	public static final String FOCUS_NEXT_SUBPART = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "NEXTSUBPART"; 
	public static final String FOCUS_PREVIOUS_SUBPART = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "PREVIOUSSUBPART";
	public static final String FOCUS_NEXT_WIDGET = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "NEXTWIDGET";
	public static final String FOCUS_PREVIOUS_WIDGET = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "PREVIOUSWIDGET";
	public static final String FOCUS_PART_JUMP = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "PARTJUMP";
	public static final String FOCUS_ESCAPE = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "ESCAPE";
	
	public static final String SUBPART_LOST_FOCUS = BASE_TOPIC + SEPARATOR + BASE_FOCUSEVENT + SEPARATOR + "SUBPARTLOST";
	
	// Structural Constants for part recognition
	public static final String CLASS_PERSPECTIVE = "AccessiblePerspective";
	public static final String CLASS_PART = "AccessiblePart";
	public static final String CLASS_SUBPART = "AccessibleSubpart";
	public static final String CLASS_WIDGET = "AccessibleWidget";
	public static final String CLASS_TOOLBAR = "AccessibleToolbar";
	public static final String CLASS_HANDLEDTOOLITEM = "AccessibleHandledToolItem";
	
	// Structural Constants for subpart recognition
	public static final String CLASS_BROWSER = "Browser";
	public static final String CLASS_COMPOSITE = "Composite";
	
	// Structural Constants for part/stack recognition
	public static final String CLASS_RCP_PARTSTACK = "PartStackImpl";
	public static final String CLASS_RCP_PART = "PartImpl";
	public static final String CLASS_RCP_PERSPECTIVE = "PerspectiveImpl";
	
	// Key Codes and State Masks
	public static final int KEY_CODE_TAB = 9;
	public static final int KEY_CODE_ESC = 27;
	public static final int NO_STATE_MASK = 0;
	public static final int STATE_MASK_SHIFT = 131072;
	public static final int STATE_MASK_CTRL = 262144;
	public static final int STATE_MASK_CTRL_SHIFT = 393216;
	
	// Key IDs for AccessibleElements
	public static final String KEY_ID_GUI_ELEMENT = "accessibleId";
	
	// Key Values for XML Parser
	public static final String XML_ELEMENTID = "elementId";
	public static final String XML_ACCESSIBLENAME = "accessibleName";
	public static final String XML_DEFAULTELEMENT = "defaultElement";
	public static final String XML_LABEL = "label";
	public static final String XML_TOOLTIP = "tooltip";
	public static final String XML_SHORTCUT = "shortcut";
	
	// Values for XML Elements
	public static final String XML_ELEMENT_PERSPECTIVESTACK = "perspectivestack";
	public static final String XML_ELEMENT_PERSPECTIVE = "perspective";
	public static final String XML_ELEMENT_PART = "part";
	public static final String XML_ELEMENT_SUBPART = "subpart";
	public static final String XML_ELEMENT_WIDGET = "widget";
	public static final String XML_ELEMENT_TRIMBARS = "trimbars";
	public static final String XML_ELEMENT_TOOLBAR = "toolbar";
	public static final String XML_ELEMENT_HANDLED_TOOL_ITEM = "handledtoolitem";
	
	// Maximum delay since last tab in milliseconds
	public static final int	MAX_LATENCY_TAB = 200;
	
	
}
