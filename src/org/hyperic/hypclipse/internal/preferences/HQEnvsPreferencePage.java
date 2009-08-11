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
package org.hyperic.hypclipse.internal.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.SWTFactory;

/**
 * Installed HQ agents preference page. This page
 * is used to handle list of installed agent bundles.
 * This information is later used to access jar
 * packages for compilation.
 */
public class HQEnvsPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	
	private InstalledHQEnvsBlock fHQBlock;
	
	/**
	 * 
	 */
	public HQEnvsPreferencePage() {
		super("");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		initializeDialogUnits(ancestor);
		noDefaultAndApplyButton();
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		SWTFactory.createWrapLabel(ancestor, HQDEMessages.HQEnvsPreferencePage_desc, 1, 300);
		SWTFactory.createVerticalSpacer(ancestor, 1);

		fHQBlock = new InstalledHQEnvsBlock();
		fHQBlock.createControl(ancestor);
		Control control = fHQBlock.getControl();
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		//fHQBlock.restoreColumnSettings(settings, qualifier)
		
		applyDialogFont(ancestor);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.PREFERENCE_ENVS_PAGE);
		return ancestor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		PreferencesManager.getInstance().save();
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performCancel()
	 */
	public boolean performCancel() {
		PreferencesManager.getInstance().reload();
		return super.performCancel();
	}
	
	
	
	
}