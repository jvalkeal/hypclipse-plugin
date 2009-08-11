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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.wizards.IProjectProvider;
import org.hyperic.hypclipse.plugin.IFieldData;

/**
 * 
 *
 */
public abstract class ContentPage extends WizardPage {

	// boolean value to indicate if widgets are initialized
	protected boolean fInitialized = false;

	// component to enter name
	protected Text fNameText;
	
	// component to enter package name
	protected Text fPackageText;

	// handle to main page (page 1)
	protected NewProjectCreationPage fMainPage;
	
	// object to store data from forms
	protected AbstractFieldData fData;
	
	// helper object 
	protected IProjectProvider fProjectProvider;

	protected final static int PROPERTIES_GROUP = 1;

	protected int fChangedGroups = 0;

	// listener to receive notifications for component modify events
	protected ModifyListener propertiesListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			if (fInitialized)
				fChangedGroups |= PROPERTIES_GROUP;
			validatePage();
		}
	};

	/**
	 * Constructor
	 * 
	 * @param pageName
	 * @param provider
	 * @param page
	 * @param data
	 */
	public ContentPage(String pageName, IProjectProvider provider, NewProjectCreationPage page, AbstractFieldData data) {
		super(pageName);
		fMainPage = page;
		fProjectProvider = provider;
		fData = data;
	}

	/**
	 * Helper method to create text component.
	 * 
	 * @param parent
	 * @param listener
	 * @return
	 */
	protected Text createText(Composite parent, ModifyListener listener) {
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(listener);
		return text;
	}

	/**
	 * Helper method to create text component.
	 * 
	 * @param parent
	 * @param listener
	 * @param horizSpan
	 * @return
	 */
	protected Text createText(Composite parent, ModifyListener listener, int horizSpan) {
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = horizSpan;
		text.setLayoutData(data);
		text.addModifyListener(listener);
		return text;
	}

	/**
	 * Validates that page widgets contains correct data. Sets messages
	 * to page header to indicate what is wrong. Handles whether user
	 * can go forward from this page.
	 */
	protected abstract void validatePage();

	/**
	 * Validates page widgets and returns error message. This method
	 * returns null if validation is ok.
	 * 
	 * @return Message if something is wrong, null if page is ok.
	 */
	protected String validateProperties() {

//		if (!fInitialized) {
//			if (!fIdText.getText().trim().equals(fProjectProvider.getProjectName())) {
//				setMessage(HQDEMessages.ContentPage_illegalCharactersInID, INFORMATION);
//			} else {
//				setMessage(null);
//			}
//			return null;
//		}

		setMessage(null);
		String errorMessage = null;

		// Validate ID
//		errorMessage = validateId();
//		if (errorMessage != null) {
//			return errorMessage;
//		}

		// Validate Version
//		errorMessage = validateVersion(fVersionText);
		if (errorMessage != null) {
			return errorMessage;
		}

		// Validate Name
		errorMessage = validateName();
		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}


	/**
	 * @return
	 */
	private String validateName() {
		if (fNameText.getText().trim().length() == 0) {
			return HQDEMessages.ContentPage_noname;
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		updateData();
		return super.getNextPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
//			String id = computeId();
//			// properties group
//			if ((fChangedGroups & PROPERTIES_GROUP) == 0) {
//				int oldfChanged = fChangedGroups;
//				fIdText.setText(id);
//				fVersionText.setText("1.0.0"); //$NON-NLS-1$
//				fNameText.setText(IdUtil.getValidName(id, getNameFieldQualifier()));
//				fProviderText.setText(IdUtil.getValidProvider(id));
//				fChangedGroups = oldfChanged;
//			}
			if (fInitialized)
				validatePage();
			else
				fInitialized = true;
		}
		super.setVisible(visible);
	}


	/**
	 * 
	 * @return
	 */
	protected abstract String getNameFieldQualifier();

	/**
	 * 
	 */
	public void updateData() {
		fData.setName(fNameText.getText().trim());
		fData.setPackageName(fPackageText.getText().trim());
	}

	/**
	 * 
	 * @return
	 */
	public IFieldData getData() {
		return fData;
	}

}
