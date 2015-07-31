package ch.chrissharkman.accessibility.rcp.base.extensions;

/**
 * Default class for AccessibleExtension interface implementation.
 * All method return simply the given value, no additional logic
 * is implemented.
 * @author ChristianHeimann
 *
 */
public class DefaultAccessibleExtension implements AccessibleExtension {

	/**
	 * Given value is returned.
	 */
	@Override
	public String getAccessibleNameString(String value) {
		return value;
	}

	/**
	 * Given value is returned.
	 */
	@Override
	public String getLabelString(String value) {
		return value;
	}

	/**
	 * Given value is returned.
	 */
	@Override
	public String getTooltipString(String value) {
		return value;
	}

}
