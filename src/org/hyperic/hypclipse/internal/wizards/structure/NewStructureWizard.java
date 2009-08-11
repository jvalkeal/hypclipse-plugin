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

import org.eclipse.core.resources.IProject;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.editor.HQDEPluginEditor;
import org.hyperic.hypclipse.internal.wizards.NewWizard;
import org.hyperic.hypclipse.internal.wizards.WizardCollectionElement;
import org.hyperic.hypclipse.internal.wizards.WizardElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

/**
 * This wizard is used to add new platform, server or service
 * structure. It either adds empty stucture or more complex
 * one using templates.
 */
public class NewStructureWizard extends NewWizard {
	public static final String PLUGIN_POINT = "newStructure";
	private StructureSelectionPage fStructurePage;
	private IPluginModelBase fModel;
	private IProject fProject;
	private HQDEPluginEditor fEditor;
	private WizardCollectionElement fWizardCollection;

	/**
	 * 
	 * @param project
	 * @param model
	 * @param editor
	 */
	public NewStructureWizard(IProject project, IPluginModelBase model, HQDEPluginEditor editor) {
		setDialogSettings(HQDEPlugin.getDefault().getDialogSettings());
		setDefaultPageImageDescriptor(HQDEPluginImages.DESC_NEWEX_WIZ);
		fModel = model;
		fProject = project;
		fEditor = editor;
		setForcePreviousAndNextButtons(true);
		setWindowTitle(HQDEMessages.NewStructureWizard_wtitle);
		loadWizardCollection();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		fStructurePage = new StructureSelectionPage(fProject, fModel, fWizardCollection, getTemplates(), this);
		addPage(fStructurePage);
	}

	/**
	 * 
	 */
	private void loadWizardCollection() {
		NewStructureRegistryReader reader = new NewStructureRegistryReader();
		fWizardCollection = (WizardCollectionElement) reader.readRegistry(HQDEPlugin.getPluginId(), PLUGIN_POINT, false);
	}

	/**
	 * 
	 * @return
	 */
	public WizardCollectionElement getTemplates() {
		WizardCollectionElement templateCollection = new WizardCollectionElement("", "", null);
		collectTemplates(fWizardCollection.getChildren(), templateCollection);
		return templateCollection;
	}

	/**
	 * 
	 */
	private void collectTemplates(Object[] children, WizardCollectionElement list) {
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof WizardCollectionElement) {
				WizardCollectionElement element = (WizardCollectionElement) children[i];
				collectTemplates(element.getChildren(), list);
				collectTemplates(element.getWizards().getChildren(), list);
			} else if (children[i] instanceof WizardElement) {
				WizardElement wizard = (WizardElement) children[i];
				if (wizard.isTemplate())
					list.getWizards().add(wizard);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.NewWizard#performFinish()
	 */
	public boolean performFinish() {
//		fStructurePage.checkModel();
		if (fStructurePage.canFinish())
			return fStructurePage.finish();
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public HQDEPluginEditor getEditor() {
		return fEditor;
	}

}
