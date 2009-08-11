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


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.SWTFactory;
import org.hyperic.hypclipse.plugin.IPreferenceConstants;

public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Button fOverwritePluginFile;

	public MainPreferencePage() {
		setPreferenceStore(HQDEPlugin.getDefault().getPreferenceStore());
		setDescription(HQDEMessages.Preferences_MainPage_Description);
	}

	protected Control createContents(Composite parent) {
		IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 15;
		composite.setLayout(layout);


		Group group = SWTFactory.createGroup(composite, HQDEMessages.MainPreferencePage_exportingGroup, 1, 1, GridData.FILL_HORIZONTAL);

		fOverwritePluginFile = new Button(group, SWT.CHECK);
		fOverwritePluginFile.setText(HQDEMessages.MainPreferencePage_promptBeforeOverwrite);
		fOverwritePluginFile.setSelection(!MessageDialogWithToggle.ALWAYS.equals(store.getString(IPreferenceConstants.OVERWRITE_PLUGIN_FILE_ON_EXPORT)));

		return composite;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		Dialog.applyDialogFont(getControl());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.PREFERENCE_MAIN_PAGE);
	}

	public boolean performOk() {
		IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.OVERWRITE_PLUGIN_FILE_ON_EXPORT, fOverwritePluginFile.getSelection() ? MessageDialogWithToggle.PROMPT : MessageDialogWithToggle.ALWAYS);
		HQDEPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}

	protected void performDefaults() {
//		IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();
		fOverwritePluginFile.setSelection(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}
