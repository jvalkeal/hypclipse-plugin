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

public interface IDocumentTextNode extends IDocumentRange, Serializable, IDocumentXMLNode {

	public static final String F_PROPERTY_CHANGE_TYPE_PCDATA = "type_pcdata"; //$NON-NLS-1$

	// Used by text edit operations
	void setEnclosingElement(IDocumentElementNode node);

	IDocumentElementNode getEnclosingElement();

	void setText(String text);

	String getText();

	void setOffset(int offset);

	void setLength(int length);

	// Not used by text edit operations
	public void reconnect(IDocumentElementNode parent);

	// Not used by text edit operations
	public String write();

}
