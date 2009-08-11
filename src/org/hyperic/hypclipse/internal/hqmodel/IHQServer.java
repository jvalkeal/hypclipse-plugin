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

public interface IHQServer extends IPluginParent{

	String getName();
	String getVersion();
	String getInclude();
	String getPlatforms();
	String getVirtual();
	String getDescription();
	void setInclude(String include) throws CoreException;
	void setVersion(String value) throws CoreException;
	void setDescription(String value) throws CoreException;
	void setPlatforms(String value) throws CoreException;
	void setVirtual(String value) throws CoreException;
}
