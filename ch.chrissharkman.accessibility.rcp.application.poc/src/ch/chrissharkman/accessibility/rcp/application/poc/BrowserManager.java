package ch.chrissharkman.accessibility.rcp.application.poc;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

public class BrowserManager {

	private static Logger logger = Logger.getLogger(BrowserManager.class);
	private static BrowserManager instance = null;

	public static final String DEFAULT_URL = "https://www.duckduckgo.com";

	private BrowserManager() {
		// Exists only to defeat instantiation.
	}

	/**
	 * Function to get Singleton Instance of BrowserManager.
	 * 
	 * @return BrowserManager instance.
	 */
	public static BrowserManager instance() {
		if (instance == null) {
			instance = new BrowserManager();
		}
		return instance;
	}

	/**
	 * Function to create a Browser instance with a predefined GridData setup:
	 * Style SWT.NONE, Horizontal and Vertical Alignment SWT.FILL,
	 * GrabExcessHorizontalSpace and Vertical true, Horizontal and Vertical Span
	 * 1.
	 * 
	 * @param parent
	 *            Composite parent where to place the new browser.
	 * @return browser the new preconfigured browser instance.
	 */
	public Browser createBrowser(Composite parent) {
		Browser browser = new Browser(parent, SWT.NONE);
		if (parent.getLayout().getClass().getSimpleName().equals("GridLayout")) {
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			browser.setLayoutData(gridData);
		}
		
		// DOC-BA
		browser.setData(AccessibleConstants.KEY_ID_GUI_ELEMENT, "poc.browserpart.browser");
				
		return browser;
	}

	/**
	 * Function to display a new content in the given browser.
	 * 
	 * @param browser
	 *            the concerned Browser instance.
	 * @param path
	 *            the path to a file or an URL of the content to display.
	 */
	public void display(Browser browser, String path) {
		try {
			if (UrlValidator.getInstance().isValid(path)) {
				browser.setUrl(path);
			} else if (UrlValidator.getInstance().isValid("http://" + path)) {
				browser.setUrl("http://" + path);
			} else {
				String fulltext = ContentManager.instance().getContent(path);
				browser.setText(fulltext);
			}
		} catch (Exception e) {
			logger.info("exception in display content in browser: " + e, e);
		}
	}
	
	/**
	 * Function to trigger gain focus on Browser.
	 */
	@Focus
	public void gainFocus() {
		logger.info("focus gained on browser.");
	}

}
