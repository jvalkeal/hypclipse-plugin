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
 * This type of model is created by parsing the hq descriptor file.
 * It serves as a base interface for plug-in holding data.
 * If the file is a workspace resource, it will be
 * available as the underlying resource of the model.
 * The model may be read-only or editable.
 * It will also make a reference to the plugin.properties
 * model when created. The reference will be of the
 * same type as the model itself: if the model is
 * editable, it will attempt to obtain an exclusive
 * editable copy of plugin.properties model.
 * <p>
 * The plug-in model can be disabled. Disabling the
 * model will not change its data. Users of the
 * model will have to decide if the disabled state
 * if of any importance to them or not.
 * <p>
 * The model is capable of notifying listeners
 * about changes. An attempt to change a read-only
 * model will result in a CoreException.
 */
public interface IPluginModelBase extends ISharedPluginModel, IModelChangeProvider {
	/**
	 * Returns a factory object that should be used
	 * to create new instances of the model objects.
	 */
	// TODO need to understand difference between getFactory and getPluginFactory
	//IPluginModelFactory getFactory();

	/**
	 * Creates and return a top-level plugin model object
	 *  
	 * @return a top-level model object representing a plug-in.
	 */
	IPluginBase createPluginBase();

	/**
	 * Returns an associated plugin.properties model
	 * that works in conjunction with this model.
	 *
	 * @return the matching plugin.properties model
	 */
	IBuildModel getBuildModel();
	

	IPluginBase getPluginBase();
	
	/**
	 * Returns the factory that can be used to
	 * create new objects for this model
	 * @return the plug-in model factory
	 */
	IPluginModelFactory getPluginFactory();



}
