package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import java.awt.KeyboardFocusManager;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.events.KeyEvent;

import ch.chrissharkman.accessibility.rcp.base.handler.GlobalKeyHandler;

/**
 * Class to handle all the keyEvents with the delegate pattern.
 * Each part has its own implementation or delegates it to its parent.
 * 
 * KeyCodes:
 * Tab: 9
 * Ctrl: 262144
 * Alt: 65536
 * Enter: 13
 * Esc: 27
 * CapsLock: 16777298
 * Space: 32
 * 
 * @author ChristianHeimann
 *
 */
public class PocKeyHandler {

	private static Logger logger = Logger.getLogger(PocKeyHandler.class);
	private static PocKeyHandler pocKeyHandler = new PocKeyHandler();
	
	private PocKeyHandler() {
		// Exists only to defeat instatiation.
	}
	
	public static PocKeyHandler instance() {
		return pocKeyHandler;
	}
	
	
	public void handleKey(GlobalKeyHandler handler, KeyEvent e) {
		logger.info("statemask: " + e.stateMask);
		switch (e.keyCode) {
			case 9:
				if (e.stateMask == 262144) {
					handler.handleNavigationJump();					
				} else {
					logger.info("only tab pressed");
					KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
					logger.info(kfm.toString());
				}
				break;
			case 27:
				logger.info("esc pressed");
				break;
			default:
				logger.info("default action");
		}
	};

}
