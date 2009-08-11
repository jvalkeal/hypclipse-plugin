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
package org.hyperic.hypclipse.internal.wizards.structure;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginReference;
import org.hyperic.hypclipse.plugin.IStructureWizard;
import org.hyperic.hypclipse.plugin.ITemplateSection;

/**
 * This wizard should be used as a base class for 
 * wizards that provide new plug-in templates. 
 * These wizards are loaded during new plug-in or fragment
 * creation and are used to provide initial
 * content (Java classes, directory structure and
 * extensions).
 * <p>
 * This plug-in will be passed on to the templates to generate additional
 * content. After all templates have executed, 
 * the wizard will use the collected list of required
 * plug-ins to set up Java buildpath so that all the
 * generated Java classes can be resolved during the build.
 */

public class NewStructureTemplateWizard extends Wizard implements IStructureWizard {
	private ITemplateSection fSection;
	private IProject fProject;
	private IPluginModelBase fModel;
	private IPluginObject fParent;
	private boolean fUpdatedDependencies;

	/**
	 * Creates a new template wizard.
	 */

	public NewStructureTemplateWizard(ITemplateSection section) {
		Assert.isNotNull(section);
		setDialogSettings(HQDEPlugin.getDefault().getDialogSettings());
		setDefaultPageImageDescriptor(HQDEPluginImages.DESC_NEWEX_WIZ);
		setNeedsProgressMonitor(true);
		fSection = section;
	}

	public void init(IProject project, IPluginModelBase model, IPluginObject parent) {
		this.fProject = project;
		this.fModel = model;
		this.fParent = parent;//MMM
	}

	public void addPages() {
		fSection.addPages(this);
		setWindowTitle(fSection.getLabel());
		if (fSection instanceof BaseOptionTemplateSection) {
			((BaseOptionTemplateSection) fSection).initializeFields(fModel);
			((BaseOptionTemplateSection) fSection).initializeFields(fParent);//MMM
		}
	}

	public boolean performFinish() {
		IRunnableWithProgress operation = new WorkspaceModifyOperation() {
			public void execute(IProgressMonitor monitor) {
				try {
					int totalWork = fSection.getNumberOfWorkUnits();
					monitor.beginTask("HQDEMessages.NewExtensionTemplateWizard_generating", totalWork);
					updateDependencies();
					fSection.execute(fProject, fModel, monitor); // nsteps
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, true, operation);
		} catch (InvocationTargetException e) {
			HQDEPlugin.logException(e);
			return false;
		} catch (InterruptedException e) {
			HQDEPlugin.logException(e);
			return false;
		}
		return true;
	}

	private void updateDependencies() throws CoreException {
//		IPluginReference[] refs = fSection.getDependencies(fModel.getPluginBase().getSchemaVersion());
//		for (int i = 0; i < refs.length; i++) {
//			IPluginReference ref = refs[i];
//			if (!modelContains(ref)) {
//				IPluginImport iimport = fModel.getPluginFactory().createImport();
//				iimport.setId(ref.getId());
//				iimport.setMatch(ref.getMatch());
//				iimport.setVersion(ref.getVersion());
//				fModel.getPluginBase().add(iimport);
//				fUpdatedDependencies = true;
//			}
//		}
	}

	private boolean modelContains(IPluginReference ref) {
//		IPluginBase plugin = fModel.getPluginBase();
//		IPluginImport[] imports = plugin.getImports();
//		for (int i = 0; i < imports.length; i++) {
//			IPluginImport iimport = imports[i];
//			if (iimport.getId().equals(ref.getId())) {
//				// good enough
//				return true;
//			}
//		}
		return false;
	}

	public boolean updatedDependencies() {
		return fUpdatedDependencies;
	}
}
