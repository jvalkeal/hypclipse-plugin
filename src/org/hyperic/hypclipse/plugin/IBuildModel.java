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
 * This model is created from the "hqbuild.properties" file
 * that defines what source folders in the plug-in are
 * to be used to build require plug-in Jars.
 * <p>
 * If this model is editable, isEditable() will return
 * true and the model instance will implement IEditable
 * interface. The model is capable of providing
 * change notification for the registered listeners.
 */
public interface IBuildModel extends IModel, IModelChangeProvider {
	/**
	 * Returns the top-level model object of this model.
	 *
	 * @return a hqbuild.properties top-level model object
	 */
	IBuild getBuild();

	/**
	 * Returns the factory that should be used
	 * to create new instance of model objects.
	 * @return the hqbuild.properties model factory
	 */
	IBuildModelFactory getFactory();

	/**
	 * Returns the location of the file
	 * used to create the model.
	 *
	 * @return the location of the hqbuild.properties file
	 * or <samp>null</samp> if the file
	 * is in a workspace.
	 */
	String getInstallLocation();
}
