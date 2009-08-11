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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.text.ColorManager;
import org.hyperic.hypclipse.plugin.IPreferenceConstants;

public class EditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPreferenceConstants {

	private XMLSyntaxColorTab fXMLTab;
//	private BuildSyntaxColorTab fManifestTab;
	private ColorManager fColorManager;

	public EditorPreferencePage() {
		setDescription(HQDEMessages.EditorPreferencePage_colorSettings);
		fColorManager = new ColorManager();
	}

	public boolean performOk() {
		fXMLTab.performOk();
//		fManifestTab.performOk();
		HQDEPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}

	public void dispose() {
		fColorManager.disposeColors(false);
		fXMLTab.dispose();
//		fManifestTab.dispose();
		super.dispose();
	}

	protected void performDefaults() {
		fXMLTab.performDefaults();
//		fManifestTab.performDefaults();
		super.performDefaults();
	}

	public void init(IWorkbench workbench) {
	}

	protected Control createContents(Composite parent) {
		final Link link = new Link(parent, SWT.NONE);
		final String target = "org.eclipse.ui.preferencePages.GeneralTextEditor"; //$NON-NLS-1$
		link.setText(HQDEMessages.EditorPreferencePage_link);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(link.getShell(), target, null, null);
			}
		});

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);

		Button foldingButton = new Button(parent, SWT.CHECK | SWT.LEFT | SWT.WRAP);
		foldingButton.setText(HQDEMessages.EditorPreferencePage_folding);
		foldingButton.setLayoutData(gd);
		foldingButton.setSelection(HQDEPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.EDITOR_FOLDING_ENABLED));
		foldingButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();
				store.setValue(IPreferenceConstants.EDITOR_FOLDING_ENABLED, ((Button) e.getSource()).getSelection());
			}
		});

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		createXMLTab(folder);
//		createManifestTab(folder);

		Dialog.applyDialogFont(getControl());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.PREFERENCE_EDITORS_PAGE);

		return parent;
	}

	private void createXMLTab(TabFolder folder) {
		fXMLTab = new XMLSyntaxColorTab(fColorManager);
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(HQDEMessages.EditorPreferencePage_xml);
		item.setControl(fXMLTab.createContents(folder));
	}

//	private void createManifestTab(TabFolder folder) {
//		fManifestTab = new BuildSyntaxColorTab(fColorManager);
//		TabItem item = new TabItem(folder, SWT.NONE);
//		item.setText(HQDEMessages.EditorPreferencePage_build);
//		item.setControl(fManifestTab.createContents(folder));
//	}

}
