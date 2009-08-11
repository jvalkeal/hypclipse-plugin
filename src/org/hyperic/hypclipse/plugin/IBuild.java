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
 * The top-level model object of the model that is created from
 * "build.properties" file.
 *  
 */
public interface IBuild extends IWritable {
	
	/**
	 * Adds a new build entry. This method can throw a CoreException if the
	 * model is not editable.
	 * 
	 * @param entry
	 *            an entry to be added
	 */
	void add(IBuildEntry entry) throws CoreException;

	/**
	 * Returns all the build entries in this object.
	 * 
	 * @return an array of build entries
	 */
	IBuildEntry[] getBuildEntries();

	/**
	 * Returns the build entry with the specified name.
	 * 
	 * @param name
	 *            name of the desired entry
	 * @return the entry object with the specified name, or <samp>null</samp>
	 *         if not found.
	 */
	IBuildEntry getEntry(String name);

	/**
	 * Removes a build entry. This method can throw a CoreException if the model
	 * is not editable.
	 * 
	 * @param entry
	 *            an entry to be removed
	 */
	void remove(IBuildEntry entry) throws CoreException;
}
