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
 * Editable model is an editable object that can be saved. The classes
 * that implement this interface are responsible for calling the
 * method <code>save</code> of <code>IEditable</code> and supplying
 * the required <code>PrintWriter</code> object.
 * 
 */
public interface IEditableModel extends IEditable {
	/**
	 * Saves the editable model using the mechanism suitable for the 
	 * concrete model implementation. It is responsible for 
	 * wrapping the <code>IEditable.save(PrintWriter)</code> operation
	 * and providing the print writer.
	 */
	void save();
}
