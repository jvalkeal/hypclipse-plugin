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

/**
 * Classes that implement this interface are contributed via the extension point
 * <code>org.hyperic.hypclipse.pluginContent</code>. The expectation is that
 * classes also extend JFace Wizard class.  This wizard must be used when plug-in 
 * dependencies are to be specified via the Import-Package header of a manifest.mf.  
 * The role of this wizard is to provide additional plug-in content after the 
 * project and the critical plug-in project files have been created. 
 * The wizard is nested in the overall 'New' wizard and can contribute one or 
 * more pages that allow users to configure how this content will be generated. 
 * A typical implementation of this interface would be a template wizard that 
 * populates the plug-in project with content that can be useful right away 
 * (for example, a view or an editor extension).
 * <p>
 * Due to the call order of the method <code>performFinish</code> in nested
 * wizards, classes that implement this interface should not place the code that
 * generates new content in the implementation of the abstract method
 * <code>Wizard.performFinish()</code>. Instead, they should simply return
 * <code>true</code> and have all the real code in <code>performFinish</code>
 * defined in this interface. This version of the method passes all the context
 * required for the content generation and is called AFTER the project and vital
 * plug-in files have been already created.
 * 
 * @since 3.2
 */
public interface IBundleContentWizard extends IPluginContentWizard {

	/**
	 * Returns names of packages that are required by this wizard. 
	 * This information will be used to compose the Import-Package header of 
	 * the manifest.mf being generated, so that the plug-in compiles without 
	 * errors in the first build after creation.
	 * 
	 * @return an array of package names required by this wizard
	 */
	String[] getImportPackages();

}
