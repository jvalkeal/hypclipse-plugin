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
 * Classes implement this interface if
 * their instances need to be uniquely identified
 * using an id.
 * 
 */
public interface IIdentifiable {
	/**
	 * A property that will be carried by the change event
	 * if 'id' field of this object is changed.
	 */
	public static final String P_ID = "id"; //$NON-NLS-1$

	/**
	 * Returns a unique id of this object.
	 * @return the id of this object
	 */
	public String getId();

	/**
	 * Sets the id of this IIdentifiable to the provided value.
	 * This method will throw CoreException if
	 * object is not editable.
	 *
	 *@param id a new id of this object
	 */
	void setId(String id) throws CoreException;
}
