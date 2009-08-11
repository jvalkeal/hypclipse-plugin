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

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;

/**
 * First page in new HQ plug-in project creation wizard.
 * 
 * This page is used to ask setting for general project
 * creation such as project name, input and output folders
 * for java classes.
 */
public class NewProjectCreationPage extends WizardNewProjectCreationPage {

	// source info
	private Label fSourceLabel;
	private Text fSourceText;
	
	// where to compile
	private Label fOutputlabel;
	private Text fOutputText;
	
	// object to store values from form
	private AbstractFieldData fData;
	private IStructuredSelection fSelection;

	/**
	 * 
	 * @param pageName
	 * @param data
	 * @param selection
	 */
	public NewProjectCreationPage(String pageName, AbstractFieldData data, IStructuredSelection selection) {
		super(pageName);
		fData = data;
		fSelection = selection;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite control = (Composite) getControl();
		GridLayout layout = new GridLayout();
		control.setLayout(layout);

		createProjectTypeGroup(control);
		createWorkingSetGroup(control, fSelection,
				new String[] {
					"org.eclipse.jdt.ui.JavaWorkingSetPage",
					"org.eclipse.pde.ui.pluginWorkingSet",
					"org.eclipse.ui.resourceWorkingSetPage"}
		);
		
		Dialog.applyDialogFont(control);
		PlatformUI.getWorkbench()
			.getHelpSystem()
			.setHelp(control, IHelpContextIds.NEW_PROJECT_STRUCTURE_PAGE);
		setControl(control);
	}

	/**
	 * Helper method which creates components to enter output
	 * and input folders.
	 * 
	 * @param container
	 */
	private void createProjectTypeGroup(Composite container) {
		Group group = new Group(container, SWT.NONE);
		group.setText(HQDEMessages.ProjectStructurePage_settings);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fSourceLabel = createLabel(group, HQDEMessages.ProjectStructurePage_source);
		fSourceText = createText(group);
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		fSourceText.setText(store.getString(PreferenceConstants.SRCBIN_SRCNAME));

		fOutputlabel = createLabel(group, HQDEMessages.ProjectStructurePage_output);
		fOutputText = createText(group);
		fOutputText.setText(store.getString(PreferenceConstants.SRCBIN_BINNAME));
	}

	/**
	 * Helper method to create button component.
	 * 
	 * @param container
	 * @param style
	 * @param span
	 * @param indent
	 * @return
	 */
	private Button createButton(Composite container, int style, int span, int indent) {
		Button button = new Button(container, style);
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		gd.horizontalIndent = indent;
		button.setLayoutData(gd);
		return button;
	}

	/**
	 * Helper method to create label component.
	 * 
	 * @param container
	 * @param text
	 * @return
	 */
	private Label createLabel(Composite container, String text) {
		Label label = new Label(container, SWT.NONE);
		label.setText(text);
		GridData gd = new GridData();
		label.setLayoutData(gd);
		return label;
	}

	/**
	 * Helper method to create text component.
	 * 
	 * @param container
	 * @return
	 */
	private Text createText(Composite container) {
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		text.setLayoutData(gd);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		return text;
	}

	/**
	 * 
	 */
	public void updateData() {
//		fData.setSimple(!fJavaButton.getSelection());
		fData.setSourceFolderName(fSourceText.getText().trim());
		fData.setOutputFolderName(fOutputText.getText().trim());
		fData.setLegacy(false);
		fData.setWorkingSets(getSelectedWorkingSets());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#validatePage()
	 */
//	protected boolean validatePage() {
//		return true;
//	}

	/**
	 * 
	 * @param settings
	 */
	protected void saveSettings(IDialogSettings settings) {
		
	}

}
