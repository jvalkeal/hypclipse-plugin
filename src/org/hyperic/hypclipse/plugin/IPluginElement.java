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
 * Classes that implement this interface model the
 * XML elements found in the plug-in model.
 */
public interface IPluginElement extends IPluginParent {
	/**
	 * A property name that will be used to notify
	 * about element body text change.
	 */
	String P_TEXT = "text";
	/**
	 * A property name that will be used to notify
	 * about global replacement of the element's attributes.
	 */
	String P_ATTRIBUTES = "attributes";

	/**
	 * A property name that will be used to notify individual
	 * change in an element's attribute.
	 */
	String P_ATTRIBUTE = "attribute";

	/**
	 * Creates an identical copy of this XML element.
	 * The new element will share the same model and
	 * the parent.
	 *
	 * @return a copy of this element
	 */
	IPluginElement createCopy();

	/**
	 * Returns an attribute object whose name
	 * matches the provided name.
	 * @param name the name of the attribute
	 * @return the attribute object, or <samp>null</samp> if not found
	 */
	IPluginAttribute getAttribute(String name);

	/**
	 * Returns all attributes currently defined in this element
	 * @return an array of attribute objects that belong to this element
	 */
	IPluginAttribute[] getAttributes();

	/**
	 * Returns the number of attributes in this element.
	 * @return number of attributes defined in this element
	 */
	int getAttributeCount();

	/**
	 * Returns the body text of this element.
	 *
	 * @return body text of this element or <samp>null</samp> if not set.
	 */
	String getText();

	/**
	 * Sets the attribute with the provided name
	 * to the provided value. If attribute object
	 * is not found, a new one will be created and
	 * its value set to the provided value.
	 * This method will throw a CoreException if
	 * the model is not editable.
	 *
	 * @param name the name of the attribute
	 * @param value the value to be set 
	 */
	void setAttribute(String name, String value) throws CoreException;

	/**
	 * Sets the body text of this element
	 * to the provided value. This method
	 * will throw a CoreException if the
	 * model is not editable.
	 *
	 * @param text the new body text of this element
	 */
	void setText(String text) throws CoreException;
}
