package ch.chrissharkman.accessibility.rcp.application.poc.parts;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.ToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleActionAdapter;
import org.eclipse.swt.accessibility.AccessibleActionEvent;
import org.eclipse.swt.accessibility.AccessibleActionListener;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleControlListener;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.internal.part.Part;

import ch.chrissharkman.accessibility.rcp.application.poc.BrowserManager;
import ch.chrissharkman.accessibility.rcp.application.poc.ContentManager;
import ch.chrissharkman.accessibility.rcp.application.poc.handlers.PocKeyHandler;
import ch.chrissharkman.accessibility.rcp.base.AccessibleManager;
import ch.chrissharkman.accessibility.rcp.base.AccessibleView;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;
import ch.chrissharkman.accessibility.rcp.base.handler.GlobalKeyHandler;
import ch.chrissharkman.accessibility.rcp.base.helper.AccessibleHelper;

/**
 * Class to define the Browser part in the application: a zone with a toolbar and a second zone with an embedded browser.
 * @author ChristianHeimann
 */
public class BrowserViewPart implements AccessibleView {
	
	private static Logger logger = Logger.getLogger(BrowserViewPart.class);
	private Composite viewComposite;
	private Browser browser;
	private Button buttonWeb;
	private Button buttonGoto;
	
	/**
	 * The value of the actual set url. This value
	 * is public to make it accessible from other classes
	 * and through the user.
	 */
	public String actualUrl = BrowserManager.DEFAULT_URL;
	
	
	@Inject
	private IEventBroker broker;
	
	@Inject
	private MApplication application;
	
	@Inject
	private MWindow mainWindow;
	
	@Inject
	private EModelService modelService;
	
	
	@Inject @Named("actualContent") private String actualContent;
	
	/**
	 * Function to create the complete composite of the part.
	 * Argument composite parent comes from framework, so it is the Part
	 * which loads this classURI in application.e4xmi
	 * @param parent
	 */
	@PostConstruct
	public void createComposite(Composite parent) {
		this.viewComposite = parent;
		// Layout of the composite element
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
	    parent.setLayoutData(gridData);
	    
	    // add components
		createToolbar(parent);
		this.browser = BrowserManager.instance().createBrowser(parent);
		
		// display content
		BrowserManager.instance().display(this.browser, this.actualContent);

	}
	
	/**
	 * Function to create a Toolbar set into given composite.
	 * Toolbar contains two buttons in a grid layout with 5 columns.
	 * @param parent
	 */
	public void createToolbar(Composite parent) {
		Composite toolbar = new Composite(parent, SWT.NONE);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		toolbar.setLayoutData(gridData);
		toolbar.setLayout(new GridLayout(5, false));
		toolbar.setBackground(new Color(null, 102,102,102));
		buttonWeb = new Button(toolbar, SWT.TOGGLE);
		buttonWeb.setText("Web");
		buttonGoto = new Button(toolbar, SWT.PUSH);
		buttonGoto.setText("Gehe zu");
		buttonGoto.setEnabled(false);
		buttonGoto.setGrayed(true);
		
		Device device = Display.getCurrent();
		String contentBundle = ContentManager.instance().getContentBundle();
		URL imageUrlOne = ContentManager.instance().getResolvedFileURL(contentBundle, "icons/icon-world-40.png");
		Image img_buttonOne = new Image(device, imageUrlOne.getFile());
		buttonWeb.setImage(img_buttonOne);
		URL imageUrlTwo = ContentManager.instance().getResolvedFileURL(contentBundle, "icons/icon-look-40.png");
		Image img_buttonTwo = new Image(device,  imageUrlTwo.getFile());
		buttonGoto.setImage(img_buttonTwo);
		
		buttonWeb.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getEventBrowsing((Button) e.getSource());		
			}
		});

		buttonGoto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getEventSetUrl((Button)e.getSource());
			}
		});
		
		// Additional Buttons for tab navigation demonstration:
		// Button 2 is insert as before button 1, but tab order
		// indicated in xml first address test1 and then test 2
		Button buttonNavitest4 = new Button(toolbar, SWT.PUSH);
		buttonNavitest4.setText("Vierter");
		Button buttonNavitest3 = new Button(toolbar, SWT.PUSH);
		buttonNavitest3.setText("Dritter");
		URL img4Url = ContentManager.instance().getResolvedFileURL(contentBundle, "icons/icon-4-40.png");
		URL img3Url = ContentManager.instance().getResolvedFileURL(contentBundle, "icons/icon-3-40.png");
		Image img_buttonNavi4 = new Image(device, img4Url.getFile());
		buttonNavitest4.setImage(img_buttonNavi4);
		Image img_buttonNavi3 = new Image(device, img3Url.getFile());
		buttonNavitest3.setImage(img_buttonNavi3);
		
		// set the id of the subpart and its widgets
		AccessibleHelper.bindAccessibleId(toolbar, "poc.browserpart.toolbar");
		AccessibleHelper.bindAccessibleId(buttonWeb, "poc.browserpart.toolbar.btnweb");
		AccessibleHelper.bindAccessibleId(buttonGoto, "poc.browserpart.toolbar.btnurl");
		AccessibleHelper.bindAccessibleId(buttonNavitest3, "poc.browserpart.toolbar.btnTest3");
		AccessibleHelper.bindAccessibleId(buttonNavitest4, "poc.browserpart.toolbar.btnTest4");

		
			
	}
	
	/**
	 * Function to detect UIEvent "browsing". Toggles from browser url to static content.
	 * @param button Button the receiver of the event.
	 */
	@Inject @Optional
	public void getEventBrowsing(Button button) {
		if (button.getSelection()) {
			BrowserManager.instance().display(this.browser, this.actualUrl);
			getEventGrayButton(button);
		} else {
			BrowserManager.instance().display(this.browser, this.actualContent);
			getEventGrayButton(button);
		}
	}
	
	/**
	 * Function to toggle enabled/colored and disabled/grayed of the buttonGoto
	 * @param button the button which invokes the toggling.
	 */
	public void getEventGrayButton(Button button) {
		if (this.buttonGoto.getEnabled()) {
			this.buttonGoto.setEnabled(false);
			this.buttonGoto.setGrayed(true);
		} else {
			this.buttonGoto.setEnabled(true);
			this.buttonGoto.setGrayed(false);
		}
	}
	
	/**
	 * Function to create an UrlDialog where a new url can be set.
	 * If Dialog is confirmed (OK), then the result of the new url will be displayed.
	 * If Dialog is canceled (Cancel), then the url will be kept in memory,
	 * but the visible page in the browser will not be changed.
	 * @param button Button which received the event. 
	 */
	public void getEventSetUrl(Button button) {
		UrlDialog urlDialog = new UrlDialog(button.getShell(), this);
		urlDialog.open(); 
		// ReturnCode 0 = ok, 1 = cancel
		if (urlDialog.getReturnCode() == 0) {
			BrowserManager.instance().display(this.browser, this.actualUrl);			
		}
	}
	
	/**
	 * Function to read the actual selected text in browser part.
	 * This function is called when edit event is sent.
	 * @param string
	 */
	@Inject
	@Optional
	public void editText(@UIEventTopic("edit") String string) {
		if (this.browser.isVisible()) {
			logger.info("call in edit");
			this.browser.setJavascriptEnabled(true);
			String script = new String("return getSelectedText(); function getSelectedText () {if (window.getSelection) {var range = window.getSelection (); return (range.toString ());} else {if (document.selection.createRange) {var range = document.selection.createRange (); return (range.text);}}}");
			String result = (String) this.browser.evaluate(script);
			MessageDialog.openInformation(this.browser.getShell(), "Bearbeiten-Funktion", "Der selektierte Text lautet:\n" + result);	
		}
	}

	@Override
	public Composite getViewComposite() {
		return this.viewComposite;
	}

}
