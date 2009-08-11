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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkingSet;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.elements.ElementList;
import org.hyperic.hypclipse.internal.wizards.IProjectProvider;
import org.hyperic.hypclipse.internal.wizards.NewWizard;
import org.hyperic.hypclipse.internal.wizards.WizardElement;
import org.hyperic.hypclipse.plugin.IPluginContentWizard;

/**
 * This wizard implementation is handling new custom project
 * creation for HQ plug-ins. Class is attached to eclipse 
 * through org.eclipse.ui.newWizards extension point.
 * 
 * Wizard will steer user through the process by asking 
 * different question and later creating actual project
 * structure.
 */
public class NewPluginProjectWizard extends NewWizard implements IExecutableExtension {

	// pages in this wizard
	private NewProjectCreationPage fMainPage;
	private PluginContentPage fContentPage;
	private TemplateListSelectionPage fWizardListPage;
	
	// object to store values from form
	private PluginFieldData fPluginData;
	
	// implementation to get access to some methods
	private IProjectProvider fProjectProvider;
	
	// holder for default wizard element if found
	private WizardElement defaultWizardElement;
	
	public static final String PLUGIN_POINT = "pluginContent";
	public static final String TAG_WIZARD = "wizard";

	/**
	 * Default constructor
	 */
	public NewPluginProjectWizard() {
		setDialogSettings(HQDEPlugin.getDefault().getDialogSettings());
		setWindowTitle(HQDEMessages.NewProjectWizard_title);
		fPluginData = new PluginFieldData();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		fMainPage = new NewProjectCreationPage("main", fPluginData, getSelection());
		fMainPage.setTitle(HQDEMessages.NewProjectWizard_MainPage_title);
		fMainPage.setDescription(HQDEMessages.NewProjectWizard_MainPage_desc);
		addPage(fMainPage);

		fProjectProvider = new IProjectProvider() {
			public String getProjectName() {
				return fMainPage.getProjectName();
			}

			public IProject getProject() {
				return fMainPage.getProjectHandle();
			}

			public IPath getLocationPath() {
				return fMainPage.getLocationPath();
			}
		};

		
		fContentPage = new PluginContentPage("page2", fProjectProvider, fMainPage, fPluginData);
		addPage(fContentPage);
		
		ElementList codegenWizards = getAvailableCodegenWizards();
		fWizardListPage = new TemplateListSelectionPage(codegenWizards, defaultWizardElement, fContentPage, HQDEMessages.WizardListSelectionPage_templates);
		addPage(fWizardListPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		return super.canFinish() && page != fMainPage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.NewWizard#performFinish()
	 */
	public boolean performFinish() {
		try {
			fMainPage.updateData();
			fContentPage.updateData();
			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				fMainPage.saveSettings(settings);
				fContentPage.saveSettings(settings);
			}
			IPluginContentWizard contentWizard = fWizardListPage.getSelectedWizard();
			getContainer().run(false, true, new NewProjectCreationOperation(fPluginData, fProjectProvider, contentWizard));

			IWorkingSet[] workingSets = fMainPage.getSelectedWorkingSets();
			if (workingSets.length > 0)
				getWorkbench().getWorkingSetManager().addToWorkingSets(fProjectProvider.getProject(), workingSets);

			return true;
		} catch (InvocationTargetException e) {
			HQDEPlugin.logException(e);
		} catch (InterruptedException e) {
		}
		return false;
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	public void dispose() {
		super.dispose();
//		PDEPlugin.getDefault().getLabelProvider().disconnect(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	}

	/**
	 * 
	 * @param config
	 * @return
	 */
	protected WizardElement createWizardElement(IConfigurationElement config) {
		String name = config.getAttribute(WizardElement.ATT_NAME);
		String id = config.getAttribute(WizardElement.ATT_ID);
		String ddefault = config.getAttribute(WizardElement.ATT_DEFAULT);
		
		String className = config.getAttribute(WizardElement.ATT_CLASS);
		if (name == null || id == null || className == null)
			return null;
		
		// if marked as default, don't use it. Just store reference
		if(ddefault != null && ddefault.equals("true")) {
			defaultWizardElement = new WizardElement(config);
			return null;
		}
		
		WizardElement element = new WizardElement(config);
//		String imageName = config.getAttribute(WizardElement.ATT_ICON);
//		if (imageName != null) {
//			String pluginID = config.getNamespaceIdentifier();
//			Image image = HQDEPlugin.getDefault().getLabelProvider().getImageFromPlugin(pluginID, imageName);
//			element.setImage(image);
//		}
		return element;
	}

	/**
	 * Finds list of available template wizards.
	 * 
	 * Code generation wizards are hooked to this plug-in
	 * through custom extension point org.hyperic.hypclipse.pluginContent.
	 * @return List of available template wizards.
	 */
	public ElementList getAvailableCodegenWizards() {
		ElementList wizards = new ElementList("CodegenWizards");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(HQDEPlugin.getPluginId(), PLUGIN_POINT);
		if (point == null)
			return wizards;
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				if (elements[j].getName().equals(TAG_WIZARD)) {
					WizardElement element = createWizardElement(elements[j]);
					if (element != null) {
						wizards.add(element);
					}
				}
			}
		}		
		return wizards;
	}

}
