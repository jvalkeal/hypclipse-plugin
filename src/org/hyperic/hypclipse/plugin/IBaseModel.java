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
 * A base generic model. Classes that implement this
 * interface are expected to be able to:
 * <ul>
 * <li>Dispose (clear all the data and reset)</li>
 * <li>Tell if they are editable</li>
 * <li>Tell if they contain valid data</li>
 * </ul>
 */
public interface IBaseModel {
	
	// TODO should this class extend IAdaptable?
	
	/**
	 * Releases all the data in this model and
	 * clears the state. A disposed model
	 * can be returned to the normal state
	 * by reloading.
	 */
	void dispose();

	/**
	 * Tests if this model has been disposed.
	 * Disposed model cannot be used until
	 * it is loaded/reloaded.
	 * @return <code>true</code> if the model has been disposed
	 */
	boolean isDisposed();

	/**
	 * Tests if this model can be modified. Modification
	 * of a model that is not editable will result
	 * in CoreException being thrown.
	 * @return <code>true</code> if this model can be modified
	 */
	boolean isEditable();

	/**
	 * Tests if this model valid. When models
	 * are loaded from the file, they may pass the
	 * syntax error checking and load all the model objects.
	 * However, some of the objects may contain invalid
	 * values that make the model unusable.
	 * @return <code>true</code> only if the model can be safely used in all
	 * computations.
	 */
	boolean isValid();
}
