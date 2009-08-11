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

import java.util.HashMap;

import org.hyperic.hypclipse.internal.util.HQDETextHelper;


public class DocumentTextNode extends DocumentXMLNode implements IDocumentTextNode {

	private static final long serialVersionUID = 1L;

	protected static final HashMap<Character, String> SUBSTITUTE_CHARS = new HashMap<Character, String>(5);

	static {
		SUBSTITUTE_CHARS.put(new Character('&'), "&amp;"); //$NON-NLS-1$
		SUBSTITUTE_CHARS.put(new Character('<'), "&lt;"); //$NON-NLS-1$
		SUBSTITUTE_CHARS.put(new Character('>'), "&gt;"); //$NON-NLS-1$
		SUBSTITUTE_CHARS.put(new Character('\''), "&apos;"); //$NON-NLS-1$
		SUBSTITUTE_CHARS.put(new Character('\"'), "&quot;"); //$NON-NLS-1$
	}

	private transient int fOffset;
	private transient int fLength;
	private transient IDocumentElementNode fEnclosingElement;

	private String fText;

	/**
	 * 
	 */
	public DocumentTextNode() {
		fOffset = -1;
		fLength = 0;
		fEnclosingElement = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#setEnclosingElement(org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	public void setEnclosingElement(IDocumentElementNode node) {
		fEnclosingElement = node;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#getEnclosingElement()
	 */
	public IDocumentElementNode getEnclosingElement() {
		return fEnclosingElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#setText(java.lang.String)
	 */
	public void setText(String text) {
		fText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#getText()
	 */
	public String getText() {
		return fText == null ? "" : fText; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#setOffset(int)
	 */
	public void setOffset(int offset) {
		fOffset = offset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentRange#getOffset()
	 */
	public int getOffset() {
		return fOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentRange#getLength()
	 */
	public int getLength() {
		return fLength;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#setLength(int)
	 */
	public void setLength(int length) {
		fLength = length;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentTextNode#reconnect(org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	public void reconnect(IDocumentElementNode parent) {
		// Transient field:  Enclosing Element
		// Essentially the parent (an element)
		fEnclosingElement = parent;
		// Transient field:  Length
		fLength = -1;
		// Transient field:  Offset
		fOffset = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentXMLNode#write()
	 */
	public String write() {
		// TODO make this CDATA stuff more generic. Here we don't need it
		// since we can convert strings. Buf for example, 'script' tag needs
		// to me under cdata
		String content = getText();
//		String content = getText().trim();
		//<![CDATA[    ]]>
		
		return "<![CDATA[\n" + content + "\n]]>";
		
//		return HQDETextHelper.translateWriteText(content, SUBSTITUTE_CHARS);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.IDocumentXMLNode#getXMLType()
	 */
	public int getXMLType() {
		return F_TYPE_TEXT;
	}

}
