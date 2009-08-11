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
 * Classes that need to be notified on model
 * changes should implement this interface
 * and add themselves as listeners to
 * the model they want to listen to.
 * 
 */
public interface IModelChangedListener {
	/**
	 * Called when there is a change in the model
	 * this listener is registered with.
	 *
	 * @param event a change event that describes
	 * the kind of the model change
	 */
	public void modelChanged(IModelChangedEvent event);
}
