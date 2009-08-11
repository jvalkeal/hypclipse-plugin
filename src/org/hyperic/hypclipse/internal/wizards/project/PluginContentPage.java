/**********************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Icons under 'icons/fugue' are made by Yusuke Kamiyamane http://www.pinvoke.com/.
 * Licensed under a Creative Commons Attribution 3.0 license.
 * <http://creativecommons.org/licenses/by/3.0/>
 *
 *********************************************************************************/
package org.hyperic.hypclipse.internal.wizards.project;

import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.SWTFactory;
import org.hyperic.hypclipse.internal.preferences.HQEnvInfo;
import org.hyperic.hypclipse.internal.preferences.HQPreferencesChangedEvent;
import org.hyperic.hypclipse.internal.preferences.IHQPreferenceChangedListener;
import org.hyperic.hypclipse.internal.preferences.PreferencesManager;
import org.hyperic.hypclipse.internal.util.HQDEJavaHelper;
import org.hyperic.hypclipse.internal.util.HQDELabelUtility;
import org.hyperic.hypclipse.internal.util.VMUtil;
import org.hyperic.hypclipse.internal.wizards.IProjectProvider;

/**
 * Second page in new HQ plug-in project creation wizard.
 * 
 * This page is used to ask various settings related
 * to HQ plug-in project. 
 */
public class PluginContentPage extends ContentPage implements IHQPreferenceChangedListener {

	// components for product plug-in
	private Text fClassText;
	private Button fGenerateClass;
	private Label fClassLabel;
	
	// java execution env components
	private Label fEELabel;
	private Button fExeEnvButton;
	private Combo fEEChoice;

	// hq environment components
	private Label fHQELabel;
	private Button fHQExeEnvButton;
	private Combo fHQEChoice;


	/**
	 * Dialog settings constants
	 */
	private final static String S_GENERATE_PRODUCTPLUGIN = "generateProductPlugin";

	
	protected final static int P_CLASS_GROUP = 2;
	
	private final static String NO_EXECUTION_ENVIRONMENT = HQDEMessages.PluginContentPage_noEE;
	private final static String NO_HQ_ENVIRONMENT = HQDEMessages.PluginContentPage_noHQE;

	/**
	 * default tText modify listener
	 */
	private ModifyListener classListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			if (fInitialized)
				fChangedGroups |= P_CLASS_GROUP;
			validatePage();
		}
	};

	/**
	 * Constructor
	 * @param pageName
	 * @param provider
	 * @param page
	 * @param data
	 */
	public PluginContentPage(String pageName, IProjectProvider provider, NewProjectCreationPage page, AbstractFieldData data) {
		super(pageName, provider, page, data);
		setTitle(HQDEMessages.ContentPage_title);
		setDescription(HQDEMessages.ContentPage_desc);
		PreferencesManager.getInstance().addPreferencesChangedListener(this);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		// plug-in properties
		createPluginPropertiesGroup(container);
		
		// plug-in options
		createPluginClassGroup(container);

		Dialog.applyDialogFont(container);
		setControl(container);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.NEW_PROJECT_REQUIRED_DATA);
	}

	/**
	 * Creates all of the plugin properties widgets
	 * @param container
	 */
	private void createPluginPropertiesGroup(Composite container) {
		Group propertiesGroup = SWTFactory.createGroup(container, HQDEMessages.ContentPage_pGroup, 3, 1, GridData.FILL_HORIZONTAL);

		Label label = new Label(propertiesGroup, SWT.NONE);
		label.setText(HQDEMessages.ContentPage_pname);
		fNameText = createText(propertiesGroup, propertiesListener, 2);

		label = new Label(propertiesGroup, SWT.NONE);
		label.setText(HQDEMessages.ContentPage_ppackage);
		fPackageText = createText(propertiesGroup, propertiesListener, 2);

		// java selection
		createExecutionEnvironmentControls(propertiesGroup);
		
		// hq environment selection
		createHQEnvironmentControls(propertiesGroup);
	}

	/**
	 * Creates controls for selecting HQ environment.
	 * @param container
	 */
	private void createHQEnvironmentControls(Composite container) {
		fHQELabel = new Label(container, SWT.NONE);
		fHQELabel.setText(HQDEMessages.NewProjectCreationPage_HQExecutionEnvironments_label);
		
		fHQEChoice = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		fHQEChoice.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		fillHQEnvs();

		fHQEChoice.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});

		// Create button
		fHQExeEnvButton = new Button(container, SWT.PUSH);
		fHQExeEnvButton.setLayoutData(new GridData());
		fHQExeEnvButton.setText(HQDEMessages.NewProjectCreationPage_environmentsButton);
		fHQExeEnvButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), "org.hyperic.hypclipse.HQEnvsPreferencePage",
						new String[] {"org.hyperic.hypclipse.HQEnvsPreferencePage"}, null).open(); 
			}
		});

	}
	
	/**
	 * Gets available HQ environments from manager and
	 * fills combobox.
	 */
	private void fillHQEnvs() {
		PreferencesManager fManager = PreferencesManager.getInstance();
		HQEnvInfo[] hqEnvs = fManager.getEnvironments();

		TreeSet<String> availableHQEs = new TreeSet<String>();
		if(hqEnvs != null) {
			for (int i = 0; i < hqEnvs.length; i++) {
				availableHQEs.add(hqEnvs[i].getName());
			}			
		}
		availableHQEs.add(NO_HQ_ENVIRONMENT);
		fHQEChoice.setItems(availableHQEs.toArray(new String[0]));
			
		HQEnvInfo def = fManager.getDefaultEnvironment();
		if(def != null) {
			String[] HQChoises = fHQEChoice.getItems();
			for (int i = 0; i < HQChoises.length; i++) {
				if(HQChoises[i].equals(def.getName())) {
					fHQEChoice.select(i);
					break;
				}
			}
		}
		// it seems that there's no hq environments available,
		// so select 'no hq environment'. Also fall back
		// if default are not selected.
		if(availableHQEs.size() == 1 || def == null)
			fHQEChoice.select(0);

	}

	/**
	 * Creates controls to select java execution environment
	 * @param container
	 */
	private void createExecutionEnvironmentControls(Composite container) {
		// Create label
		fEELabel = new Label(container, SWT.NONE);
		fEELabel.setText(HQDEMessages.NewProjectCreationPage_executionEnvironments_label);

		// Create combo
		fEEChoice = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		fEEChoice.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Gather EEs 
		IExecutionEnvironment[] exeEnvs = VMUtil.getExecutionEnvironments();
		TreeSet<String> availableEEs = new TreeSet<String>();
		for (int i = 0; i < exeEnvs.length; i++) {
			availableEEs.add(exeEnvs[i].getId());
		}
		availableEEs.add(NO_EXECUTION_ENVIRONMENT);

		// Set data 
		fEEChoice.setItems(availableEEs.toArray(new String[0]));
		fEEChoice.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});

		// Set default EE based on strict match to default VM
		IVMInstall defaultVM = JavaRuntime.getDefaultVMInstall();
		String[] EEChoices = fEEChoice.getItems();
		for (int i = 0; i < EEChoices.length; i++) {
			if (!EEChoices[i].equals(NO_EXECUTION_ENVIRONMENT)) {
				if (VMUtil.getExecutionEnvironment(EEChoices[i]).isStrictlyCompatible(defaultVM)) {
					fEEChoice.select(i);
					break;
				}
			}
		}

		// Create button
		fExeEnvButton = new Button(container, SWT.PUSH);
		fExeEnvButton.setLayoutData(new GridData());
		fExeEnvButton.setText(HQDEMessages.NewProjectCreationPage_environmentsButton);
		fExeEnvButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), "org.eclipse.jdt.debug.ui.jreProfiles",
						new String[] {"org.eclipse.jdt.debug.ui.jreProfiles"}, null).open();
			}
		});
	}

	/**
	 * Creates options to select/use product plugin
	 * @param container
	 */
	private void createPluginClassGroup(Composite container) {
		Group classGroup = SWTFactory.createGroup(container, HQDEMessages.ContentPage_pClassGroup, 2, 1, GridData.FILL_HORIZONTAL);

		IDialogSettings settings = getDialogSettings();

		fGenerateClass = SWTFactory.createCheckButton(classGroup, HQDEMessages.ContentPage_generate, null, (settings != null) ? !settings.getBoolean(S_GENERATE_PRODUCTPLUGIN) : true, 2);
		fGenerateClass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fClassLabel.setEnabled(fGenerateClass.getSelection());
				fClassText.setEnabled(fGenerateClass.getSelection());
				updateData();
				validatePage();
			}
		});

		fClassLabel = new Label(classGroup, SWT.NONE);
		fClassLabel.setText(HQDEMessages.ContentPage_classname);
		GridData gd = new GridData();
		gd.horizontalIndent = 20;
		fClassLabel.setLayoutData(gd);
		fClassText = createText(classGroup, classListener);

	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.project.ContentPage#updateData()
	 */
	public void updateData() {
		super.updateData();
		PluginFieldData data = (PluginFieldData) fData;
		data.setClassname(fClassText.getText().trim());
		data.setDoGenerateClass(fGenerateClass.isEnabled() && fGenerateClass.getSelection());
		if (fEEChoice.isEnabled() && !fEEChoice.getText().equals(NO_EXECUTION_ENVIRONMENT)) {
			fData.setExecutionEnvironment(fEEChoice.getText().trim());
		} else {
			fData.setExecutionEnvironment(null);
		}
		
		if (fHQEChoice.isEnabled() && !fHQEChoice.getText().equals(NO_HQ_ENVIRONMENT)) {
			fData.setHQExecutionEnvironment(fHQEChoice.getText().trim());
		} else {
			fData.setHQExecutionEnvironment(null);
		}
		
	}


	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.project.ContentPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			fMainPage.updateData();
//			fGenerateClass.setEnabled(!fData.isSimple());
//			fClassLabel.setEnabled(!fData.isSimple() && fGenerateClass.getSelection());
//			fClassText.setEnabled(!fData.isSimple() && fGenerateClass.getSelection());
			fGenerateClass.setEnabled(true);
			fGenerateClass.setSelection(false);
			fClassLabel.setEnabled(false);
			fClassText.setEnabled(false);

			// plugin class group
			if (((fChangedGroups & P_CLASS_GROUP) == 0)) {
				int oldfChanged = fChangedGroups;
				fClassText.setText(formatClassName(fProjectProvider.getProjectName()));
				fChangedGroups = oldfChanged;
			}
			
			fNameText.setText(fProjectProvider.getProjectName());
			fPackageText.setText("org.hyperic.hq.plugin." + fProjectProvider.getProjectName());

//			boolean allowEESelection = !fData.isSimple();
			boolean allowEESelection = true;
			fEELabel.setEnabled(allowEESelection);
			fEEChoice.setEnabled(allowEESelection);
			fExeEnvButton.setEnabled(allowEESelection);

		}
		super.setVisible(visible);
	}

	private String formatClassName(String name) {
		if(name == null)
			return "MyCustomProductPlugin";
		return HQDELabelUtility.CapitalizeFirstLetter(name) + "ProductPlugin";
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.project.ContentPage#validatePage()
	 */
	protected void validatePage() {
		String errorMessage = validateProperties();
		if (errorMessage == null && fGenerateClass.isEnabled() && fGenerateClass.getSelection()) {
			IStatus status = JavaConventions.validateJavaTypeName(fClassText.getText().trim(), HQDEJavaHelper.getJavaSourceLevel(null), HQDEJavaHelper.getJavaComplianceLevel(null));
			if (status.getSeverity() == IStatus.ERROR) {
				errorMessage = status.getMessage();
			} else if (status.getSeverity() == IStatus.WARNING) {
				setMessage(status.getMessage(), IMessageProvider.WARNING);
			}
		}
		if (errorMessage == null) {
			String eeid = fEEChoice.getText();
			if (fEEChoice.isEnabled()) {
				IExecutionEnvironment ee = VMUtil.getExecutionEnvironment(eeid);
				if (ee != null && ee.getCompatibleVMs().length == 0) {
					errorMessage = HQDEMessages.NewProjectCreationPage_invalidEE;
				}
			}
		}
		if (errorMessage == null) {
			String hqeid = fHQEChoice.getText();
			if(fHQEChoice.isEnabled()) {
				if(hqeid.equals(NO_HQ_ENVIRONMENT)) {
					if(fHQEChoice.getItems().length < 2)
						errorMessage = HQDEMessages.NewProjectCreationPage_missingHQE;
					else
						errorMessage = HQDEMessages.NewProjectCreationPage_invalidHQE;
				}
			}
		}
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.project.ContentPage#getNameFieldQualifier()
	 */
	protected String getNameFieldQualifier() {
		return HQDEMessages.ContentPage_plugin;
	}

	/**
	 * Saves the current state of widgets of interest in the dialog settings for the wizard
	 * @param settings
	 */
	protected void saveSettings(IDialogSettings settings) {
		settings.put(S_GENERATE_PRODUCTPLUGIN, !fGenerateClass.getSelection());
	}
	
	public void dispose() {
		super.dispose();
		PreferencesManager.getInstance().removePreferencesChangedListener(this);
	}

	
	public void preferencesChanged(HQPreferencesChangedEvent event) {
		fillHQEnvs();
		validatePage();
	}
}
