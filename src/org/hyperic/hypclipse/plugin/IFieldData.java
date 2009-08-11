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
 * The class that implements this interface is used to provide
 * information captured in the 'New HQ Plug-in Project' wizard pages
 * as entered by the user. The information is the provided 
 * to other consumers when generating content so that the content 
 * can be configured/customized according to the data.
 */
public interface IFieldData {
	/**
	 * Plug-in identifier field.
	 * 
	 * @return plug-in identifier as entered in the wizard
	 */
	String getId();

	String getHQExecutionEnvironment();
	/**
	 * Plug-in version field.
	 * 
	 * @return plug-in version as entered in the wizard
	 */
//	String getVersion();

	/**
	 * Plug-in name field
	 * 
	 * @return plug-in name as entered in the wizard
	 */
	String getName();
	String getPackageName();

	/**
	 * Plug-in provider field
	 * 
	 * @return plug-in provider as entered in the wizard
	 */
//	String getProvider();

	/**
	 * Plug-in library field
	 * 
	 * @return the name of the initial Java library
	 */
	String getLibraryName();

	/**
	 * Source folder field
	 * 
	 * @return the name of the Java source folder
	 */
	String getSourceFolderName();

	/**
	 * Output folder field
	 * 
	 * @return the name of the Java output folder
	 */
	String getOutputFolderName();

	/**
	 * Simple project selection
	 * 
	 * @return <code>true</code> if the plug-in should have no Java code and
	 *         nature, <code>false</code> otherwise.
	 */
//	boolean isSimple();
}
