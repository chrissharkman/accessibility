package ch.chrissharkman.accessibility.rcp.base.accessible;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class for the mother object of the accessible model. Each instance
 * contains the perspectives and the global toolbars. 
 * @author ChristianHeimann
 *
 */
public class AccessibleViewTree {

	public List<AccessibleElement> perspectives;
	public List<AccessibleElement> trimbars;
	
	private static Logger logger = Logger.getLogger(AccessibleViewTree.class);
	
	/**
	 * Constructor for AccessibleViewTree instance, sets perspectives and toolbars as its properties,
	 * even if they are null.
	 * @param perspectives The perspective list belonging to this viewTree
	 * @param trimbars The toolbar list belonging to this viewTree
	 */
	public AccessibleViewTree(List<AccessibleElement> perspectives, List<AccessibleElement> trimbars) {
		if (perspectives == null) {
			logger.warn("Given perspectives == null");
		}
		if (trimbars == null) {
			logger.warn("Given toolbars == null");
		}
		this.perspectives = perspectives;
		this.trimbars = trimbars;
	}
	
}
