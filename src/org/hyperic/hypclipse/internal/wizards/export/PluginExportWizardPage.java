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

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

/**
 * The Class PluginExportWizardPage.
 */
public class PluginExportWizardPage extends BaseExportWizardPage {

	/**
	 * Instantiates a new plugin export wizard page.
	 * 
	 * @param selection the selection
	 */
	public PluginExportWizardPage(IStructuredSelection selection) {
		super(selection, "pluginExport",
				HQDEMessages.ExportWizard_Plugin_pageBlock);
		setTitle(HQDEMessages.ExportWizard_Plugin_pageTitle);
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizardPage#hookHelpContext(org.eclipse.swt.widgets.Control)
	 */
	protected void hookHelpContext(Control control) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, IHelpContextIds.PLUGIN_EXPORT_WIZARD);
	}

	/**
	 * Checks for build properties.
	 * 
	 * @param model the model
	 * 
	 * @return true, if successful
	 */
	private boolean hasBuildProperties(IPluginModelBase model) {
//		File file = new File(model.getInstallLocation(), "build.properties");
//		return file.exists();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizardPage#isValidModel(org.hyperic.hypclipse.plugin.IModel)
	 */
	protected boolean isValidModel(IModel model) {
		return model != null && model instanceof IPluginModelBase;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizardPage#findModelFor(org.eclipse.core.runtime.IAdaptable)
	 */
	protected IModel findModelFor(IAdaptable object) {
//		if (object instanceof IJavaProject)
//			object = ((IJavaProject) object).getProject();
//		if (object instanceof IProject)
//			return PluginRegistry.findModel((IProject) object);
//		if (object instanceof PersistablePluginObject) {
//			IPluginModelBase model = PluginRegistry.findModel(((PersistablePluginObject) object).getPluginID());
//			if (model != null && model.getUnderlyingResource() != null) {
//				return model;
//			}
//		}
		return null;
	}

}
