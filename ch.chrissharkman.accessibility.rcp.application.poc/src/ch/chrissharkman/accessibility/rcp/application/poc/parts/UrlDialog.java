package ch.chrissharkman.accessibility.rcp.application.poc.parts;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UrlDialog extends Dialog {
	
	private static Logger logger = Logger.getLogger(UrlDialog.class);
	private Text textField;
	private BrowserViewPart browserView;

	/**
	 * Constructor of UrlDialog that contains a text input to set a new url to the browserView.
	 * @param parentShell the parent shell of the part where to set the dialog.
	 */
	public UrlDialog(Shell parentShell, BrowserViewPart browserView) {
		super(parentShell);
		this.browserView = browserView;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		textField = new Text(container, SWT.SINGLE);
		textField.setText(browserView.actualUrl);
		textField.selectAll();
		textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				browserView.actualUrl = textField.getText();
			}
		});
		
		return container;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Gehe zu URL");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	/**
	 * Function to get the content of the text entry field as String
	 * @return String the text entry in the text field
	 */
	public String getTextFromTextfield() {
		return this.textField.getText();
	}
	
}
