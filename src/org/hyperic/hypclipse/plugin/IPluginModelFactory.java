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

import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetric;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;

/**
 * This factory should be used to create
 * instances of the plug-in model objects.
 */
public interface IPluginModelFactory extends IHQModelFactory {
	
	
 	IHQClasspath createClasspath();
 	IHQServer createServer();
 	IHQMetrics createMetrics();
 	IHQMetric createMetric();
 	IHQHelp createHelp();
 	IHQScript createScript();
 	IHQPlatform createPlatform();
	/**
	 * Creates a new plug-in import
	 * @return a new plug-in import instance
	 */
//	IPluginImport createImport();

	/**
	 * Creates a new library instance
	 *
	 *@return a new library instance
	 */
// 	IPluginLibrary createLibrary();
	
	/**
	 * Creates a new attribute instance for the
	 * provided element.
	 *
	 * @param element the parent element
	 * @return the new attribute instance
	 */
	IPluginAttribute createAttribute(IPluginElement element);

	/**
	 * Creates a new element instance for the
	 * provided parent.
	 *
	 * @param parent the parent element
	 * @return the new element instance
	 */
	IPluginElement createElement(IPluginObject parent);

	/**
	 * Creates a new extension instance.
	 * @return the new extension instance
	 */
//	IPluginExtension createExtension();

	/**
	 * Creates a new extension point instance
	 *
	 * @return a new extension point 
	 */
//	IPluginExtensionPoint createExtensionPoint();

}
