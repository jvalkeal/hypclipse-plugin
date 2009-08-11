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
 * Classes that implement this interface are
 * capable of containing other plug-in objects.
 */
public interface IPluginParent extends IPluginObject {
	/**
	 * A property that will be used when firing notification
	 * of the sibling swap.
	 */
	String P_SIBLING_ORDER = "sibling_order"; //$NON-NLS-1$

	/**
	 * Adds a child object at the specified index.
	 * This method may throw a CoreException if
	 * the model is not editable.
	 *
	 * @param index the location of the child
	 * @param child the object to add
	 */
	void add(int index, IPluginObject child) throws CoreException;

	/**
	 * Adds a child object.
	 * This method may throw a CoreException if
	 * the model is not editable.
	 *
	 * @param child the object to add
	 */
	void add(IPluginObject child) throws CoreException;

	/**
	 * Returns the number of children
	 * currently owned by this parent.  Returns 0 if this is a lightweight model.
	 *
	 * @return the number of children
	 */
	int getChildCount();

	/**
	 * Returns the position of the child in this parent.
	 * @param child a child of this parent
	 * @return a 0-based index of the child
	 */
	int getIndexOf(IPluginObject child);

	/**
	 * Swaps the position of of the provided siblings 
	 * in the parent.
	 * @param child1 the first child
	 * @param child2 the second child
	 * @throws CoreException thrown if the model is not editable.
	 */
	void swap(IPluginObject child1, IPluginObject child2) throws CoreException;

	/**
	 * Returns the children owned by this parent.  Returns an empty array
	 * if this is a lightweight model.
	 *
	 * @return an array of children
	 */
	IPluginObject[] getChildren();

	/**
	 * Removes a child object.
	 * This method may throw a CoreException if
	 * the model is not editable.
	 *
	 * @param child the object to remove
	 */
	void remove(IPluginObject child) throws CoreException;
}
