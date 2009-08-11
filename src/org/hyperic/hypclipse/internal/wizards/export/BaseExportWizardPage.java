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
package org.hyperic.hypclipse.internal.wizards.export;

import java.io.File;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.util.SWTUtil;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IPreferenceConstants;

/**
 * The Class BaseExportWizardPage.
 */
public abstract class BaseExportWizardPage extends AbstractExportWizardPage {

	private IStructuredSelection fSelection;

	/** The directory label. */
	protected Label fDirectoryLabel;
	
	/** Component for directory. */
	protected Combo fDirectoryCombo;
	
	/** Button to launch directory selector. */
	protected Button fBrowseDirectory;
	
	/** Checkbox to overwrite exported file. */
	protected Button fOverwritePluginFile;
	
	/** The file label. */
	protected Label fFileLabel;
	
	/** Text component to show exported file path. */
	protected Text fFilePath;
	
	/** The Constant S_DESTINATION key for store setting . */
	protected static final String S_DESTINATION = "destination";

	protected String fFileName;
	
	/**
	 * Instantiates a new base export wizard page.
	 * 
	 * @param selection the selection
	 * @param name the name
	 * @param choiceLabel the choice label
	 */
	public BaseExportWizardPage(IStructuredSelection selection, String name, String choiceLabel) {
		super(name);
		fSelection = selection;
		fFileName = null;
		setDescription(HQDEMessages.ExportWizard_Plugin_description);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
		// two columns
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// directory label, span 2
		fDirectoryLabel = new Label(composite,SWT.NONE);
		fDirectoryLabel.setText(HQDEMessages.ExportWizard_directory);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		fDirectoryLabel.setLayoutData(gd);

		// combo
		fDirectoryCombo = new Combo(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fDirectoryCombo.setLayoutData(gd);

		// dir select button
		fBrowseDirectory = new Button(composite, SWT.PUSH);
		fBrowseDirectory.setText(HQDEMessages.ExportWizard_browse);
		fBrowseDirectory.setLayoutData(new GridData());
		SWTUtil.setButtonDimensionHint(fBrowseDirectory);

		// file label, span 2
		fFileLabel = new Label(composite, SWT.NONE);
		fFileLabel.setText("Export Path:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fFileLabel.setLayoutData(gd);

		// file path, span 2
		fFilePath = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fFilePath.setLayoutData(gd);
		
		fOverwritePluginFile = new Button(composite, SWT.CHECK);
		fOverwritePluginFile.setText(HQDEMessages.MainPreferencePage_promptBeforeOverwrite);
		fOverwritePluginFile.setLayoutData(gd);

		// we could do this in initialize function.
		IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();
		fOverwritePluginFile.setSelection(!MessageDialogWithToggle.ALWAYS.equals(store.getString(IPreferenceConstants.OVERWRITE_PLUGIN_FILE_ON_EXPORT)));

		
		initialize(getDialogSettings());

		if (getErrorMessage() != null) {
			setMessage(getErrorMessage());
			setErrorMessage(null);
		}
		
		hookListeners();
		setControl(composite);
		hookHelpContext(composite);
		Dialog.applyDialogFont(composite);
	}
	
	/**
	 * Hook listeners.
	 */
	protected void hookListeners() {
		fDirectoryCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pageChanged();
			}
		});

		fBrowseDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseDestination();
			}
		});

	}
	
	/**
	 * Choose destination.
	 */
	private void chooseDestination() {
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
		String path = fDirectoryCombo.getText();
		if (path.trim().length() == 0)
			path = HQDEPlugin.getWorkspace().getRoot().getLocation().toString();
		dialog.setFilterPath(path);
		dialog.setText(HQDEMessages.ExportWizard_dialog_title);
		dialog.setMessage(HQDEMessages.ExportWizard_dialog_message);
		String res = dialog.open();
		if (res != null) {
			if (fDirectoryCombo.indexOf(res) == -1)
				fDirectoryCombo.add(res, 0);
			fDirectoryCombo.setText(res);
		}
		updateFilePath();
	}

	/**
	 * Update file path.
	 */
	private void updateFilePath() {
		File f = new File(fDirectoryCombo.getText(), fFileName);
		fFilePath.setText(f.getAbsolutePath() + "-plugin.jar");
	}
	
	/**
	 * Sets the file name.
	 * 
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		fFileName = fileName;
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.AbstractExportWizardPage#pageChanged()
	 */
	protected void pageChanged() {
		updateFilePath();
		
		String error = null;
		setMessage(null);
		File dir = new File(getDestination());
		if(!dir.isDirectory())
			error = HQDEMessages.BaseExportWizardPage_dest_dir_notexist;
			
		setErrorMessage(error);
		setPageComplete(error == null);
		
	}


	/**
	 * Hook help context.
	 * 
	 * @param control the control
	 */
	protected abstract void hookHelpContext(Control control);

	/**
	 * Checks if is valid model.
	 * 
	 * @param model the model
	 * 
	 * @return true, if is valid model
	 */
	protected abstract boolean isValidModel(IModel model);

	protected abstract IModel findModelFor(IAdaptable object);

	/**
	 * Initialize page.
	 * 
	 * @param settings the settings
	 */
	protected void initialize(IDialogSettings settings) {
		initializeCombo(settings, S_DESTINATION, fDirectoryCombo);
		
		pageChanged();
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.AbstractExportWizardPage#saveSettings(org.eclipse.jface.dialogs.IDialogSettings)
	 */
	protected void saveSettings(IDialogSettings settings) {
		saveCombo(settings, S_DESTINATION, fDirectoryCombo);
		
		// we save overwrite setting here
		IPreferenceStore store = HQDEPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.OVERWRITE_PLUGIN_FILE_ON_EXPORT, fOverwritePluginFile.getSelection() ? MessageDialogWithToggle.PROMPT : MessageDialogWithToggle.ALWAYS);
		HQDEPlugin.getDefault().savePluginPreferences();
	}


	/**
	 * Gets the destination.
	 * 
	 * @return the destination
	 */
	protected String getDestination() {
		File dir = new File(fDirectoryCombo.getText().trim());
		return dir.getAbsolutePath();
	}
	
	/**
	 * Can overwrite.
	 * 
	 * @return true, if successful
	 */
	protected boolean canOverwrite() {
		return fOverwritePluginFile.getSelection();
	}
	
	/**
	 * Gets the file path.
	 * 
	 * @return the file path
	 */
	protected String getFilePath() {
		File path = new File(fFilePath.getText().trim());
		return path.getAbsolutePath();
		
	}

	/**
	 * Save destination dialog setting.
	 * 
	 * @param settings the settings
	 * @param key the key
	 * @param combo the combo
	 */
	protected void saveCombo(IDialogSettings settings, String key, Combo combo) {
		if (combo.getText().trim().length() > 0) {
			settings.put(key + String.valueOf(0), combo.getText().trim());
			String[] items = combo.getItems();
			int nEntries = Math.min(items.length, 5);
			for (int i = 0; i < nEntries; i++) {
				settings.put(key + String.valueOf(i + 1), items[i].trim());
			}
		}
	}

	/**
	 * Initialize dialog combo.
	 * 
	 * @param settings the settings
	 * @param key the key
	 * @param combo the combo
	 */
	protected void initializeCombo(IDialogSettings settings, String key, Combo combo) {
		for (int i = 0; i < 6; i++) {
			String curr = settings.get(key + String.valueOf(i));
			if (curr != null && combo.indexOf(curr) == -1) {
				combo.add(curr);
			}
		}
		if (combo.getItemCount() > 0)
			combo.setText(combo.getItem(0));
	}

	/**
	 * Save button state.
	 * 
	 * @param settings the settings
	 * @param key the key
	 * @param button the button
	 */
	protected void saveButton(IDialogSettings settings, String key, Button button) {
		boolean selected = button.getSelection();
		settings.put(key, selected);
	}
	
	/**
	 * Initialize button.
	 * 
	 * @param setting the setting
	 * @param key the key
	 * @param button the button
	 */
	protected void initializeButton(IDialogSettings setting, String key, Button button) {
		boolean selected = setting.getBoolean(key);
		button.setSelection(selected);
	}

}
