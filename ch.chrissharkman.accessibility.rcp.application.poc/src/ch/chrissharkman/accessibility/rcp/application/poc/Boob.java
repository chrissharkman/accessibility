package ch.chrissharkman.accessibility.rcp.application.poc;

import org.apache.log4j.Logger;

import ch.chrissharkman.accessibility.rcp.base.extensions.AccessibleExtension;

/**
 * Demonstration Class to show functionality
 * of AccessibleExtension in Proof of Concept
 * @author ChristianHeimann
 *
 */
public class Boob implements AccessibleExtension {
	
	private static Logger logger = Logger.getLogger(Boob.class);

	@Override
	public String getAccessibleNameString(String value) {
		logger.info("getAcc works in Boob.");
		return "boob";
	}

	@Override
	public String getLabelString(String value) {
		logger.info("getLab works in Boob.");
		return "boob";
	}

	@Override
	public String getTooltipString(String value) {
		logger.info("getTooltip works in Boob.");
		return "boob";
	}

}
