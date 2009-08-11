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
package org.hyperic.hypclipse.internal.hqmodel;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.plugin.IPluginParent;

public interface IHQMetric extends IPluginParent {
	
	public static final String A_ALIAS = "alias";
	public static final String A_TEMPLATE = "template";
	public static final String A_CATEGORY = "category";
	public static final String A_INDICATOR = "indicator";
	public static final String A_UNITS = "units";
	public static final String A_COLLECTIONTYPE = "collectionType";
	
	void setAName(String name) throws CoreException;
	void setAlias(String alias) throws CoreException;
	void setTemplate(String template) throws CoreException;
	void setCategory(String category) throws CoreException;
	void setIndicator(String indicator) throws CoreException;
	void setUnits(String units) throws CoreException;
	void setCollectionType(String collectionType) throws CoreException;

}
