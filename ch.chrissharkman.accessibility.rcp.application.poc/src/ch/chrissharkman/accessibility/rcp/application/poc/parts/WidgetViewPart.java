package ch.chrissharkman.accessibility.rcp.application.poc.parts;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ch.chrissharkman.accessibility.rcp.base.AccessibleView;
import ch.chrissharkman.accessibility.rcp.base.accessible.AccessibleConstants;

public class WidgetViewPart implements AccessibleView {

	private static Logger logger = Logger.getLogger(WidgetViewPart.class);
	private Composite viewComposite;
	private int cWidth = 300;
	private Label labelSlider;
	private Label labelScale;
	private GridData gdFillFill = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

	@PostConstruct
	public void createComposite(Composite parent) {
		this.viewComposite = parent;
		
		
		
		// prepare the part to be scrollable. Important here is to define a size
		// of an inner composite or component element!
		parent.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setLayout(new GridLayout(1, true));

		Composite compControls = new Composite(sc, SWT.NONE);
		GridLayout glCompControls = new GridLayout(1, true);
		glCompControls.marginHeight = 10;
		glCompControls.marginWidth = 20;
		compControls.setLayout(glCompControls);

		sc.setContent(compControls);

		// Introduction Text
		Composite compIntro = getWidgetComposite(compControls);
		Text textIntro = new Text(compIntro, SWT.WRAP | SWT.READ_ONLY);
		textIntro.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		textIntro.setText("Hier finden Sie eine Reihe von gängigen Kontroll-Elementen, die in RCP-Applikationen Verwendung finden.");

		// Element Button: Button which opens Dialog-Popup
		Composite compButton = getWidgetComposite(compControls);
		final Button button = new Button(compButton, SWT.PUSH);
		button.setText("Zum Gewinnspiel");
		GridData gdButton = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		button.setLayoutData(gdButton);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(((Button) e.getSource()).getShell(), "Erwischt", "Klicken Sie auf alles, wo «gewinnen» draufsteht? Seien Sie vorsichtig!");				
			}
		});
		
		// Element RadioButton: group of three radio buttons
		Composite compRadio = getWidgetComposite(compControls);
		Group groupRButtons = new Group(compRadio, SWT.NONE);
		GridData gdGroupRButtons = new GridData(SWT.FILL, SWT.FILL, true, true);
		groupRButtons.setLayoutData(gdGroupRButtons);
		groupRButtons.setLayout(new GridLayout(1, true));
		groupRButtons.setText("Wie ist das aktuelle Wetter?");

		Button rButtonA = new Button(groupRButtons, SWT.RADIO);
		Button rButtonB = new Button(groupRButtons, SWT.RADIO);
		Button rButtonC = new Button(groupRButtons, SWT.RADIO);
		rButtonA.setText("sonnig");
		rButtonB.setText("bewölkt");
		rButtonC.setText("regnerisch");
		rButtonA.setSelection(true);

		// Element CheckBox: two listed check box buttons, and a group with two
		// other check box buttons
		Composite compCheck = getWidgetComposite(compControls);
		Group groupCButtons = new Group(compCheck, SWT.NONE);
		GridData gdGroupCButtons = new GridData(SWT.FILL, SWT.FILL, true, true);
		groupCButtons.setLayoutData(gdGroupCButtons);
		groupCButtons.setLayout(new GridLayout(1, true));
		groupCButtons.setText("Welche der folgenden mobilen Geräte besitzen Sie?");
		
		Button cButtonA = new Button(groupCButtons, SWT.CHECK);
		Button cButtonB = new Button(groupCButtons, SWT.CHECK);
		cButtonA.setText("Smartphone");
		cButtonB.setText("Laptop");

		Group groupContract = new Group(compCheck, SWT.SHADOW_ETCHED_IN);
		GridData gdGroupContract = new GridData(SWT.FILL, SWT.FILL, true, true);
		groupContract.setLayoutData(gdGroupContract);
		groupContract.setLayout(new GridLayout(2, true));
		groupContract.setText("Vertragsbestimmungen");
		
		GridData gdCButtons = new GridData(SWT.FILL, SWT.FILL, true, true);
		Button cButtonC = new Button(groupContract, SWT.CHECK | SWT.WRAP);
		cButtonC.setLayoutData(gdCButtons);
		Button cButtonD = new Button(groupContract, SWT.CHECK | SWT.WRAP);
		cButtonD.setLayoutData(gdCButtons);
		cButtonC.setText("AGBs gelesen");
		cButtonD.setText("Newsletter zusenden");
		cButtonD.setSelection(true);
		
		// Element Slider
		Composite compSlider = getWidgetComposite(compControls);
		this.labelSlider = new Label(compSlider, SWT.LEFT);
		Slider slider = new Slider(compSlider, SWT.HORIZONTAL);
		GridData gdSlider = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		this.labelSlider.setLayoutData(gdSlider);
		slider.setLayoutData(gdSlider);
		
		int actualSliderSelection = 30;
		slider.setIncrement(1);
		slider.setMinimum(0);
		slider.setMaximum(60);
		slider.setThumb(20);
		slider.setSelection(actualSliderSelection);
		setActualSliderSelectionLabel(actualSliderSelection);
		slider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setActualSliderSelectionLabel(((Slider)event.widget).getSelection());
			}
		});

		// Element Scale 
		Composite compScale = getWidgetComposite(compControls);
		this.labelScale = new Label(compScale, SWT.LEFT);
		Scale scale = new Scale(compScale, SWT.HORIZONTAL);
		GridData gdScale = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdScale.minimumHeight = 40;
		this.labelScale.setLayoutData(gdScale);
		scale.setLayoutData(gdScale);
		int actualScaleSelection = 0;
		scale.setIncrement(1);
		scale.setMinimum(0);
		scale.setMaximum(3);
		scale.setSelection(actualScaleSelection);
		setActualScaleSelectionLabel(actualScaleSelection);
		scale.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setActualScaleSelectionLabel(((Scale)event.widget).getSelection());
			}
		});
		
		// Element Spinner
		Composite compSpinner = getWidgetComposite(compControls);
		GridLayout glCompSpinner = (GridLayout) compSpinner.getLayout();
		glCompSpinner.numColumns = 2;
		Label labelSpinner = new Label(compSpinner, SWT.LEFT);
		labelSpinner.setText("Menge");
		Spinner spinner = new Spinner(compSpinner, SWT.WRAP);
		spinner.setIncrement(100);
		spinner.setMinimum(0);
		spinner.setMaximum(10000);
		spinner.setLayoutData(this.gdFillFill);
		
		// Element Combo/Dropdown
		Composite compCombo = getWidgetComposite(compControls);
		GridLayout glCompCombo = (GridLayout) compCombo.getLayout();
		glCompCombo.numColumns = 2;
		Label textCombo = new Label(compCombo, SWT.LEFT);
		textCombo.setText("Modus");
		Combo combo = new Combo(compCombo, SWT.READ_ONLY);
		combo.add("Markierungen");
		combo.add("Notizen");
		combo.add("Aufgaben");
		combo.setLayoutData(this.gdFillFill);
		
		// Element Calendar/Date/Time
		Composite compDate = getWidgetComposite(compControls);
		Label textDate = new Label(compDate, SWT.LEFT);
		textDate.setText("Wählen Sie das Vorschaudatum: ");
		DateTime dateCalendar = new DateTime(compDate, SWT.CALENDAR);
		dateCalendar.setDate(2017, 01, 01);
		
		// Element Text entry field
		Composite compText = getWidgetComposite(compControls);
		Label labelTextStory = new Label(compText, SWT.READ_ONLY);
		labelTextStory.setText("Ihre Rückmeldung an uns:");
		Text textStory = new Text(compText, SWT.MULTI);
		GridData gdTextStory = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gdTextStory.minimumHeight = 60;
		textStory.setLayoutData(gdTextStory);
		// Tab shall and will traverse out from this text entry field
		textStory.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					e.doit = true;				
				}
			}
		});
		
		// fit the size of compControls composite
		compControls.pack();
		logger.info("WidgetView created");
		
	}

	/**
	 * Function to set an actual label for slider selection.
	 * @param value int the font size
	 */
	protected void setActualSliderSelectionLabel(int value) {
		this.labelSlider.setText("Schriftgrösse: " + value);
	}

	/**
	 * Function to set an actual label for scale selection
	 * @param value int a value from 0 to 3
	 */
	private void setActualScaleSelectionLabel(int value) {
		String difficulty = "";
		switch(value) {
		case 0:
			difficulty = "leicht";
			break;
		case 1:
			difficulty = "mittel";
			break;
		case 2:
			difficulty = "schwer";
			break;
		case 3:
			difficulty = "sehr schwer";
			break;
		}
		this.labelScale.setText("Schwierigkeit: " + difficulty);
	}
	
	/**
	 * Function to create a standard composite for widget presentation
	 * 
	 * @param parent
	 *            the parent composite where in to set the new composite
	 * @return Composite the created composite.
	 */
	private Composite getWidgetComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		GridData gridDataComposite = new GridData();
		gridDataComposite.widthHint = this.cWidth;
		composite.setLayoutData(gridDataComposite);
		GridLayout gridLayoutComposite = new GridLayout(1, true);
		gridLayoutComposite.marginHeight = 10;
		gridLayoutComposite.marginWidth = 10;
		composite.setLayout(gridLayoutComposite);
		return composite;
	}

	@Override
	public Composite getViewComposite() {
		return this.viewComposite;
	}


}
