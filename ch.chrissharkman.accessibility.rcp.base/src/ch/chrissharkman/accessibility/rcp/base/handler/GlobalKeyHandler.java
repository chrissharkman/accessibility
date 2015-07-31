package ch.chrissharkman.accessibility.rcp.base.handler;

/**
 * This interface defines the necessary global key handler methods. It allows
 * to use the delegates pattern to handle keys in a central place.
 * @author ChristianHeimann
 *
 */
public interface GlobalKeyHandler {
	public void handleNavigationJump();
	public void handleChangePart();
	
	
}
