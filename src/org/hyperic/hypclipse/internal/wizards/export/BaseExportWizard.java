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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;

/**
 * 
 *
 */
public abstract class BaseExportWizard extends Wizard implements IExportWizard/*, IPreferenceConstants*/ {

	/** 
	 * The selection. Contains resource which can be 
	 * used to track other project relates stuff.
	 */
	protected IStructuredSelection fSelection;
	
	/** 
	 * The build temp location. This location is used 
	 * to store build.xml file and plugin is build and
	 * assembled to this location. Also build log
	 * files are written to this directory.
	 */
	protected String fBuildTempLocation;

	/**
	 * Instantiates a new base export wizard.
	 */
	public BaseExportWizard() {
//		PDEPlugin.getDefault().getLabelProvider().connect(this);
		IDialogSettings masterSettings = HQDEPlugin.getDefault().getDialogSettings();
		setNeedsProgressMonitor(true);
		setDialogSettings(getSettingsSection(masterSettings));
		setWindowTitle(HQDEMessages.BaseExportWizard_wtitle);
		fBuildTempLocation = HQDEPlugin.getDefault().getStateLocation().append("temp").toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	public void dispose() {
//		PDEPlugin.getDefault().getLabelProvider().disconnect(this);
		super.dispose();
	}

	/**
	 * Gets the selection.
	 * 
	 * @return the selection
	 */
	public IStructuredSelection getSelection() {
		return fSelection;
	}

	/**
	 * Gets the settings section.
	 * 
	 * @param master the master
	 * 
	 * @return the settings section
	 */
	public IDialogSettings getSettingsSection(IDialogSettings master) {
		String name = getSettingsSectionName();
		IDialogSettings settings = master.getSection(name);
		if (settings == null)
			settings = master.addNewSection(name);
		return settings;
	}

	/**
	 * Gets the settings section name.
	 * 
	 * @return the settings section name
	 */
	protected abstract String getSettingsSectionName();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fSelection = selection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		saveSettings();
		if (!PlatformUI.getWorkbench().saveAllEditors(true))
			return false;

		if (!performPreliminaryChecks())
			return false;

		if (!confirmDelete())
			return false;

		scheduleExportJob();
		return true;
	}

	/**
	 * Save settings. Iterates through wizard pages and asks
	 * them to save their current settings.
	 */
	protected void saveSettings() {
		IDialogSettings settings = getDialogSettings();
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			((AbstractExportWizardPage) pages[i]).saveSettings(settings);
		}
	}

	/**
	 * Perform preliminary checks.
	 * 
	 * @return true, if successful
	 */
	protected abstract boolean performPreliminaryChecks();

	/**
	 * Confirm delete.
	 * 
	 * @return true, if successful
	 */
	protected abstract boolean confirmDelete();

	/**
	 * Schedule export job.
	 */
	protected abstract void scheduleExportJob();

}
