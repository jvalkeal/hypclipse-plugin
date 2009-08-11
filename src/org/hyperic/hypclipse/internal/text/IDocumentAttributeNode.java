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
package org.hyperic.hypclipse.internal.text;

import java.io.Serializable;

import org.eclipse.core.runtime.CoreException;

public interface IDocumentAttributeNode extends IDocumentRange, Serializable, IDocumentXMLNode {

	// Used by text edit operations

	void setEnclosingElement(IDocumentElementNode node);

	IDocumentElementNode getEnclosingElement();

	void setNameOffset(int offset);

	int getNameOffset();

	void setNameLength(int length);

	int getNameLength();

	void setValueOffset(int offset);

	int getValueOffset();

	void setValueLength(int length);

	int getValueLength();

	String getAttributeName();

	String getAttributeValue();

	void setAttributeName(String name) throws CoreException;

	void setAttributeValue(String value) throws CoreException;

	String write();

	// Not used by text edit operations
	public void reconnect(IDocumentElementNode parent);

}
