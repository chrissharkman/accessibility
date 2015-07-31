package ch.chrissharkman.accessibility.rcp.base.extensions;

/**
 * Interface for extension points of the accessible module.
 * @author ChristianHeimann
 *
 */
public interface AccessibleExtension {

	/**
	 * Method to get the correct accessibleName string
	 * @param value The given String is the value from the model,
	 * this can be a key or a value
	 * @return the definitive String value to set as accessibleName
	 */
	public String getAccessibleNameString(String value);
	
	/**
	 * Method to get the correct label string
	 * @param value The given String is a value from the model,
	 * this can be a key or a value
	 * @return the definitive String value to set as label
	 */
	public String getLabelString(String value);
	
	/**
	 * Method to get the correct tooltip string
	 * @param value The given String is a value from the model,
	 * this can be a key or a value
	 * @return the definitive String value to set as tooltip
	 */
	public String getTooltipString(String value);
	
}
