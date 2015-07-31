package ch.chrissharkman.accessibility.rcp.base.helper;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.swt.widgets.Control;

import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleElement;

public class StringMatcher {
	
	private static StringMatcher instance;
	public static Logger logger = Logger.getLogger(StringMatcher.class);
	
	private StringMatcher() {
		// empty, private to prevent instatiation.
	}
	
	public static StringMatcher instance() {
		if (instance == null) {
			instance = new StringMatcher();
		}
		return instance;
	}
	
	/**
	 * Method to check if idGUI matches the regular expression of the idModel.
	 * The idModel string is changed: full stops are escaped, wildcards * and placeholders ? are
	 * replaced with fully valid regular expression.
	 * @param idGUI
	 * @param idModel
	 * @return true if idGUI matches idModel-regexed-string, false if not.
	 */
	public boolean checkIdMatch(String idGUI, String idModel) {
		if (idGUI == null || idModel == null) {
			return false;
		}
		boolean match = false;
		
		String idModelRegex = getIdModelRegex(idModel);
		match = idGUI.matches(idModelRegex);
		
//		if (match) {
//			System.out.println("Match: idGUI: " + idGUI + " matches " + idModelRegex + " origin: " + idModel);
//		} else {
//			System.out.println("No match: idGUI: " + idGUI + " does not match " + idModelRegex + " origin: " + idModel);
//		}
		return match;
	}
	
	/**
	 * Method replaces first * into (.+) and then ? into (.*?) regex expressions.
	 * The sequence is important.
	 * @param idModel
	 * @return a regex expression
	 */
	public String getIdModelRegex(String idModel) {
		String idModelReplaced = idModel;
		idModelReplaced = escapeFullStopSign(idModelReplaced);
//		System.out.println("regex: " + idModelReplaced);

		idModelReplaced = changeWildcardToRegex(idModelReplaced);
//		System.out.println("regex: " + idModelReplaced);
		idModelReplaced = changePlaceholderToRegex(idModelReplaced);
//		System.out.println("regex: " + idModelReplaced);
		return idModelReplaced;
	}
	
	/**
	 * Method to escape full stop
	 * @param string
	 * @return
	 */
	public String escapeFullStopSign(String string) {
		String changedString = string;
		changedString = changedString.replaceAll("\\.", "\\\\.");
		return changedString;
	}
	
	/**
	 * Method to replace all * signs into regex (.+)
	 * @param string
	 * @return
	 */
	public String changeWildcardToRegex(String string) {
		String changedString = string;
		changedString = changedString.replaceAll("\\*", "(.+)");
		return changedString;
	}
	
	/**
	 * Method to replace all ? characters into regex (.*?)
	 * @param string
	 * @return
	 */
	public String changePlaceholderToRegex(String string) {
		String changedString = string;
		changedString = changedString.replaceAll("\\?", "(.*?)");
		return changedString;
	}
	
	/**
	 * Method to check, if a full id exists in given list.
	 * @param accessibleElements
	 * @param guiObject
	 * @return true if gui object element id exists in one of the accessible elements
	 */
	public boolean fullIdExistsIn(List<AccessibleElement> accessibleElements, MUIElement guiObject) {
		boolean fullIdExists = false;
		if (accessibleElements != null && accessibleElements.size() > 0 && guiObject != null) {
			for (AccessibleElement ae : accessibleElements) {
				if (ae.getElementId().equalsIgnoreCase(guiObject.getElementId())) {
					fullIdExists = true;
					break;
				}
			}
		}
		return fullIdExists;
	}
	
	/**
	 * Method to check, if a full accessibleId exists in the given list.
	 * @param accessibleElements
	 * @param control
	 * @return true if control contains in Data accessible id and this id exists in one of the accessible elements 
	 */
	public boolean fullIdExistsIn(List<AccessibleElement> accessibleElements, Control control) {
		boolean fullIdExists = false;
		if (accessibleElements != null && accessibleElements.size() > 0 && control != null && control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT) != null) {
			for (AccessibleElement ae : accessibleElements) {
				if (ae.getElementId().equalsIgnoreCase(control.getData(AccessibleConstants.KEY_ID_GUI_ELEMENT).toString())) {
					fullIdExists = true;
					break;
				}
			}
		}
		return fullIdExists;
	}
	
}
