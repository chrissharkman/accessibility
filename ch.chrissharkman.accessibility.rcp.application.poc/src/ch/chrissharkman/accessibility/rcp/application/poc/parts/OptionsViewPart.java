package ch.chrissharkman.accessibility.rcp.application.poc.parts;

import java.security.AccessControlContext;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ch.chrissharkman.accessibility.rcp.base.AccessibleView;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

public class OptionsViewPart implements AccessibleView {

	
	private static Logger logger = Logger.getLogger(OptionsViewPart.class);
	private Composite viewComposite;
	

	@PostConstruct
	public void createComposite(Composite parent) {
		this.viewComposite = parent;
		
		parent.setLayout(new FillLayout());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
	    parent.setLayoutData(gridData);
		
		
		Composite compositeOptions = new Composite(parent, SWT.NONE);
		GridData gridDataOpt = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		compositeOptions.setLayoutData(gridDataOpt);
		compositeOptions.setLayout(new GridLayout(2, false));
		
		Button btnBigger = new Button(compositeOptions, SWT.PUSH);
		btnBigger.setText("Grösser");
		Button btnSmaller = new Button(compositeOptions, SWT.PUSH);
		btnSmaller.setText("Kleiner");
		
		compositeOptions.setData(AccessibleConstants.KEY_ID_GUI_ELEMENT, "poc.options.actions");
		btnBigger.setData(AccessibleConstants.KEY_ID_GUI_ELEMENT, "poc.options.actions.btnbigger");
		btnSmaller.setData(AccessibleConstants.KEY_ID_GUI_ELEMENT, "poc.options.actions.btnsmaller");
	}


	@Override
	public Composite getViewComposite() {
		return this.viewComposite;
	}
}
