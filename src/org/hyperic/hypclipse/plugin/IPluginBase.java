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

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;

public interface IPluginBase extends IPluginObject, IIdentifiable {

	/**
	 * A property that will be used to notify that
	 * the provider name has changed.
	 */
	String P_CLASS = "class";
	String P_PROVIDER = "provider-name"; //$NON-NLS-1$
	/**
	 * A property that will be used to notify
	 * that a version has changed.
	 */
	String P_PACKAGE = "package";
	String P_VERSION = "version"; //$NON-NLS-1$

	/**
	 * A property that will be used to notify
	 * that library order in a plug-in has changed. 
	 */
	String P_LIBRARY_ORDER = "library_order"; //$NON-NLS-1$

	/**
	 * A property that will be used to notify
	 * that import order in a plug-in has changed. 
	 */
	String P_IMPORT_ORDER = "import_order"; //$NON-NLS-1$

	/**
	 * A property that will be used to notify
	 * that 3.0 release compatibility flag has been changed. 
	 */
	String P_SCHEMA_VERSION = "schema-version"; //$NON-NLS-1$
	
	public String getPackage();
	public void setPackage(String fPackage) throws CoreException;

	public String getProductPlugin();
	public void setProductPlugin(String fProduct) throws CoreException;

	IHQServer[] getServers();
	IHQMetrics[] getMetrics();
	
	
	void add(IHQServer server);
	void add(IHQClasspath classPath);
	void add(IHQMetrics metrics);
//	void add(IHQMetric metric);
	void add(IHQHelp help);
	void add(IHQScript script);
	void add(IHQPlatform platform);
	void add(IPluginElement element) throws CoreException;

}
