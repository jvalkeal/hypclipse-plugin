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
package org.hyperic.hypclipse.internal.templates;

/**
 * The classes that implement this interface are responsible for providing value
 * of variables when asked. Variables are defined by templates and represent the
 * current value of the template options set by the users.
 */
public interface IVariableProvider {
	
	/**
	 * Returns the value of the variable with a given name.
	 * 
	 * @param variable
	 *            the name of the variable
	 * @return the value of the specified variable
	 */
	public Object getValue(String variable);

}
