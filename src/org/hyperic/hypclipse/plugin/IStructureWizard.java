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
package org.hyperic.hypclipse.plugin;

import org.eclipse.core.resources.IProject;

/**
 * An interface for structure wizards. Clients should implement this interface
 * if they are plugging into HQDE using <samp>org.hyperic.hypclipse.newExtension
 * </samp> extension point.
 * 
 */
public interface IStructureWizard extends IBasePluginWizard {
	/**
	 * Initializes the wizard with the project of the plug-in and the model
	 * object for the plug-in manifest file. Java code and other resorrces should
	 * be created in the source folder under the provided project. Changes in
	 * the plug-in manifest should be made using the APIs of the provided model.
	 * Changing the model will make the model dirty. This will show up in the UI
	 * indicating that the currently opened manifest file is modified and needs
	 * to be saved.
	 * <p>
	 * Although the wizard is launched to create a structure, there is no
	 * reason a wizard cannot create several at once.
	 * 
	 * @param project
	 *            the plug-in project resource where the new code and resources
	 *            should go
	 * @param pluginModel
	 *            the model instance that should be used to modify the plug-in
	 *            manifest
	 */
//	public void init(IProject project, IPluginModelBase pluginModel); //MMM
	public void init(IProject project, IPluginModelBase pluginModel, IPluginObject parent);
}
