package ch.chrissharkman.accessibility.rcp.base.helper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Widget;
import org.osgi.framework.Bundle;

import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;
import ch.chrissharkman.accessibility.rcp.base.extensions.AccessibleExtension;

public class AccessibleHelper {

	private static Logger logger = Logger.getLogger(AccessibleHelper.class);
	
	/**
	 * Method to set the given accessibleId into the data object of the given widget
	 * as accessibleId.
	 * @param widget
	 * @param accessibleId
	 */
	public static void bindAccessibleId(Widget widget, String accessibleId) {
		if (widget == null || accessibleId == null) {
			return;
		}
		widget.setData(AccessibleConstants.KEY_ID_GUI_ELEMENT, accessibleId);
	}
	
	/**
	 * Method to get an accessibleExtension instance of given bundleName and from given class.
	 * @param bundleName
	 * @param canonicalClassName
	 * @return an instance of an accessibleExtension Class
	 */
	public static AccessibleExtension getAccessibleExtensionInstance(String bundleName, String canonicalClassName) {
		AccessibleExtension accessibleExtension = null;
		Bundle bundle = null;
		try {
			bundle = Platform.getBundle(bundleName);
			Class<?> clazz = bundle.loadClass(canonicalClassName);
			accessibleExtension = (AccessibleExtension) clazz.newInstance();
		} catch (Exception e) {
			logger.info("AccessibleExtension not loaded. Exception: " + e.getMessage());
		}
		return accessibleExtension;
	}
	
	/**
	 * Method to get the Id of the active part from given partService
	 * @param partService
	 * @return String the elementId of the active part.
	 */
	public static String getActivePartId(EPartService partService) {
		String elementId = null;
		if (partService != null && partService.getActivePart() != null) {
			elementId = partService.getActivePart().getElementId();
		}
		return elementId;
	}
	
}
