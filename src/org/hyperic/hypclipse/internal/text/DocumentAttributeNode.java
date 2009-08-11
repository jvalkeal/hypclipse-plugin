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

import org.hyperic.hypclipse.internal.util.HQDEXMLHelper;


public class DocumentAttributeNode extends DocumentXMLNode implements IDocumentAttributeNode {

	private static final long serialVersionUID = 1L;

	private transient IDocumentElementNode fEnclosingElement;
	private transient int fNameOffset;
	private transient int fNameLength;
	private transient int fValueOffset;
	private transient int fValueLength;

	private String fValue;
	private String fName;

	/**
	 * 
	 */
	public DocumentAttributeNode() {
		fEnclosingElement = null;
		fNameOffset = -1;
		fNameLength = -1;
		fValueOffset = -1;
		fValueLength = -1;
		fValue = null;
		fName = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getAttributeName()
	 */
	public String getAttributeName() {
		return fName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getAttributeValue()
	 */
	public String getAttributeValue() {
		return fValue;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getEnclosingElement()
	 */
	public IDocumentElementNode getEnclosingElement() {
		return fEnclosingElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getNameLength()
	 */
	public int getNameLength() {
		return fNameLength;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getNameOffset()
	 */
	public int getNameOffset() {
		return fNameOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getValueLength()
	 */
	public int getValueLength() {
		return fValueLength;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#getValueOffset()
	 */
	public int getValueOffset() {
		return fValueOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setAttributeName(java.lang.String)
	 */
	public void setAttributeName(String name) {
		fName = name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setAttributeValue(java.lang.String)
	 */
	public void setAttributeValue(String value) {
		fValue = value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setEnclosingElement(org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	public void setEnclosingElement(IDocumentElementNode node) {
		fEnclosingElement = node;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setNameLength(int)
	 */
	public void setNameLength(int length) {
		fNameLength = length;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setNameOffset(int)
	 */
	public void setNameOffset(int offset) {
		fNameOffset = offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setValueLength(int)
	 */
	public void setValueLength(int length) {
		fValueLength = length;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#setValueOffset(int)
	 */
	public void setValueOffset(int offset) {
		fValueOffset = offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentXMLNode#write()
	 */
	public String write() {
		return fName + "=\"" + HQDEXMLHelper.getWritableAttributeString(fValue) + "\""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentAttributeNode#reconnect(org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	public void reconnect(IDocumentElementNode parent) {
		// Transient field:  Enclosing element
		// Essentially is the parent (an element)
		// Note: Parent field from plugin document node parent seems to be 
		// null; but, we will set it any ways
		fEnclosingElement = parent;
		// Transient field:  Name Length
		fNameLength = -1;
		// Transient field:  Name Offset
		fNameOffset = -1;
		// Transient field:  Value Length
		fValueLength = -1;
		// Transient field:  Value Offset
		fValueOffset = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentRange#getLength()
	 */
	public int getLength() {
		// Implemented for backwards compatibility with utility methods that
		// assume that an attribute is a document range.
		// Stems from the problem that attributes are considered as elements
		// in the hierarchy in the manifest model

		// Includes:  name length + equal + start quote
		int len1 = getValueOffset() - getNameOffset();
		// Includes:  value length
		int len2 = getValueLength();
		// Includes:  end quote
		int len3 = 1;
		// Total
		int length = len1 + len2 + len3;

		return length;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentRange#getOffset()
	 */
	public int getOffset() {
		// Implemented for backwards compatibility with utility methods that
		// assume that an attribute is a document range.
		// Stems from the problem that attributes are considered as elements
		// in the hierarchy in the manifest model
		return getNameOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentXMLNode#getXMLType()
	 */
	public int getXMLType() {
		return F_TYPE_ATTRIBUTE;
	}

}
