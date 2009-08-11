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
 * A base interface for all the objects in the plug-in model.
 */
public interface IPluginObject extends IWritable/*, IAdaptable*/ {
	
	/**
	 * A property name that will be used to notify
	 * that the "name" field has changed.
	 */
	String P_NAME = "name";

	/**
	 * Returns the model that owns this object.
	 * @return the model instance
	 */
	ISharedPluginModel getModel();

	/**
	 * Returns the model that owns this object.
	 * @return the model instance
	 */
	IPluginModelBase getPluginModel();

	/**
	 * Returns the name of this model object
	 *@return the object name
	 */
	String getName();

	/**
	 * Returns true if this object is currently part of a model.
	 * It is useful to ignore modification events of objects
	 * that have not yet being added to the model or if they
	 * have been removed.
	 */
	boolean isInTheModel();

	/**
	 * Set the value indicating whether the object is currently part of a model.
	 * It is useful to ignore modification events of objects
	 * that have not yet being added to the model or if they
	 * have been removed.
	 */
	void setInTheModel(boolean inModel);

	/**
	 * Returns the translated name of this model object using
	 * the result of 'getName()' call as a resource key.
	 * @return the translated name or the original name if not found
	 */
	String getTranslatedName();

	/**
	 * Returns the parent of this model object.
	 *
	 * @return the object's parent
	 */
	IPluginObject getParent();

	/**
	 * Returns the top-level model object.
	 *
	 * @return the top-level model object
	 */
	IPluginBase getPluginBase();

	/**
	 * Returns a string by locating the provided
	 * key in the resource bundle associated with
	 * the model.
	 *
	 * @param key the name to use for resource bundle lookup
	 * @return value in the resource bundle for
	 * the provided key, or the key itself if
	 * not found.
	 */
	String getResourceString(String key);

	/**
	 * Chances the name of this model object.
	 * This method may throw a CoreException
	 * if the model is not editable.
	 *
	 * @param name the new object name
	 */
	void setName(String name) throws CoreException;

	/**
	 * Returns <samp>true</samp> if this object has all
	 * the required attributes set, <samp>false</samp> otherwise.
	 * @return <samp>true</samp> if all the required attributes are set.
	 */
	boolean isValid();
}
