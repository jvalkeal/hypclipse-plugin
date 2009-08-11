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

/**
 * An attribute of XML elements found in the plug-in.
 */
public interface IPluginAttribute extends IPluginObject {
	
	/**
	 * This property will be used to notify that the value
	 * of the attribute has changed.
	 */
	String P_VALUE = "value";

	/**
	 * Returns the value of this attribute.
	 *
	 * @return the string value of the attribute
	 */
	String getValue();

	/**
	 * Sets the value of this attribute.
	 * This method will throw a CoreExeption
	 * if the model is not editable.
	 *
	 * @param value the new attribute value
	 */
	void setValue(String value) throws CoreException;
}
