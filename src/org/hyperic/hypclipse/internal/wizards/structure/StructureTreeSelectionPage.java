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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.wizard.IWizardNode;
import org.hyperic.hypclipse.internal.wizards.WizardCollectionElement;
import org.hyperic.hypclipse.internal.wizards.WizardElement;
import org.hyperic.hypclipse.internal.wizards.WizardNode;
import org.hyperic.hypclipse.internal.wizards.WizardTreeSelectionPage;
import org.hyperic.hypclipse.plugin.IBasePluginWizard;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IStructureWizard;
import org.hyperic.hypclipse.plugin.ITemplateSection;

/**
 * second tab in new structure wizard. NOT USED AT THIS MOMENT... consider to remove.
 */
public class StructureTreeSelectionPage extends WizardTreeSelectionPage {
	private IProject fProject;
	private IPluginBase fPluginBase;

	/**
	 * @param categories
	 * @param baseCategory
	 * @param message
	 */
	public StructureTreeSelectionPage(WizardCollectionElement categories, String baseCategory, String message) {
		super(categories, baseCategory, message);
	}

	public void init(IProject project, IPluginBase pluginBase) {
		this.fProject = project;
		this.fPluginBase = pluginBase;
	}

	protected IWizardNode createWizardNode(WizardElement element) {
		return new WizardNode(this, element) {
			public IBasePluginWizard createWizard() throws CoreException {
				IStructureWizard wizard = createWizard(wizardElement);
				wizard.init(fProject, fPluginBase.getPluginModel(),null); //MMM
				return wizard;
			}

			protected IStructureWizard createWizard(WizardElement element) throws CoreException {
				if (element.isTemplate()) {
					IConfigurationElement template = element.getTemplateElement();
					if (template == null)
						return null;
					ITemplateSection section = (ITemplateSection) template.createExecutableExtension("class");
					return new NewStructureTemplateWizard(section);
				}
				return (IStructureWizard) element.createExecutableExtension();
			}
		};
	}

	public ISelectionProvider getSelectionProvider() {
		return wizardSelectionViewer;
	}
}
