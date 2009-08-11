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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.*;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.internal.util.HQDEXMLHelper;
import org.relaxng.datatype.Datatype;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;

public abstract class DocumentHandler extends DefaultHandler {

	protected Stack<IDocumentElementNode> fDocumentNodeStack = new Stack<IDocumentElementNode>();
	protected int fHighestOffset = 0;
	
	protected IDocumentElementNode current;
	protected int currentNodeOffset = 0;
	protected IDocumentAttributeNode currentAttribute;
	private Locator fLocator;
	private IDocumentElementNode fLastError;
	private boolean fReconciling;
	private String fLiteral = "";

	public DocumentHandler(boolean reconciling) {
		fReconciling = reconciling;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		fDocumentNodeStack.clear();
		fHighestOffset = 0;
		fLastError = null;
	}

	protected IDocumentElementNode getLastParsedDocumentNode() {
		if (fDocumentNodeStack.isEmpty()) {
			return null;
		}
		return (IDocumentElementNode) fDocumentNodeStack.peek();
	}


	/**
	 * NEW METHOD
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws SAXException
	 */
	public void startElement(String uri, String localName, String qName) throws SAXException {
		IDocumentElementNode parent = getLastParsedDocumentNode();
		IDocumentElementNode node = getDocumentNode(qName, parent);
		current = node;
		try {
			int nodeOffset = currentNodeOffset = getStartOffset(qName);
			node.setOffset(nodeOffset);
			IDocument doc = getDocument();
			int line = doc.getLineOfOffset(nodeOffset);
			node.setLineIndent(node.getOffset() - doc.getLineOffset(line));
			removeOrphanAttributes(node);
		} catch (BadLocationException e) {
		}
		if (parent != null && node != null && node.getParentNode() == null) {
			if (fReconciling) {
				// find right place for the child
				// this is necessary to save as much as possible from the model
				// we do not want an xml element with one tag to overwrite an element
				// with a different tag
				int position = 0;
				IDocumentElementNode[] children = parent.getChildNodes();
				for (; position < children.length; position++) {
					if (children[position].getOffset() == -1)
						break;
				}
				parent.addChildNode(node, position);
			} else {
				parent.addChildNode(node);
			}
		}
		fDocumentNodeStack.push(node);

	}

	public void startAttribute(String namespaceUri, String localName, String qName) throws SAXException {
		IDocumentAttributeNode attribute = getDocumentAttribute(qName, "", current);
		currentAttribute = attribute;
		
	}


	public void endAttribute(String namespaceUri, String localName, String qName, AttributeExp type) throws SAXException {
		try {
			if (currentAttribute != null) {
				String attValue = currentAttribute.getAttributeValue();
				String attName = currentAttribute.getAttributeName();
				IRegion region = getAttributeRegion(currentAttribute.getAttributeName(), attValue, currentNodeOffset);
				if (region == null) {
					attValue = HQDEXMLHelper.getWritableString(attValue);
					region = getAttributeRegion(attName, attValue, currentNodeOffset);
				}
				if (region != null) {
					currentAttribute.setNameOffset(region.getOffset());
					currentAttribute.setNameLength(attName.length());
					currentAttribute.setValueOffset(region.getOffset() + region.getLength() - 1 - attValue.length());
					currentAttribute.setValueLength(attValue.length());
				}
				current.setXMLAttribute(currentAttribute);
			}
		} catch (BadLocationException e) {
		}
		currentAttribute = null;
	}



	protected abstract IDocumentElementNode getDocumentNode(String name, IDocumentElementNode parent);

	protected abstract IDocumentAttributeNode getDocumentAttribute(String name, String value, IDocumentElementNode parent);

	protected abstract IDocumentTextNode getDocumentTextNode(String content, IDocumentElementNode parent);

	private int getStartOffset(String elementName) throws BadLocationException {
		int line = fLocator.getLineNumber();
		int col = fLocator.getColumnNumber();
		IDocument doc = getDocument();
		if (col < 0)
			col = doc.getLineLength(line);

		int endOffset;
		if (line < doc.getNumberOfLines()) {
			endOffset = doc.getLineOffset(line);
		} else {
			line = doc.getNumberOfLines() - 1;
			IRegion lineInfo = doc.getLineInformation(line);
			endOffset = lineInfo.getOffset() + lineInfo.getLength();
		}
		String text = doc.get(fHighestOffset + 1, endOffset - fHighestOffset - 1);

		ArrayList<Position> commentPositions = new ArrayList<Position>();
		for (int idx = 0; idx < text.length();) {
			idx = text.indexOf("<!--", idx);
			if (idx == -1)
				break;
			int end = text.indexOf("-->", idx);
			if (end == -1)
				break;

			commentPositions.add(new Position(idx, end - idx));
			idx = end + 1;
		}

		int idx = 0;
		for (; idx < text.length(); idx += 1) {
			idx = text.indexOf("<" + elementName, idx); //$NON-NLS-1$
			if (idx == -1)
				break;
			boolean valid = true;
			for (int i = 0; i < commentPositions.size(); i++) {
				Position pos = (Position) commentPositions.get(i);
				if (pos.includes(idx)) {
					valid = false;
					break;
				}
			}
			if (valid)
				break;
		}
		if (idx > -1)
			fHighestOffset += idx + 1;
		return fHighestOffset;
	}

	private int getElementLength(IDocumentElementNode node, int line, int column) throws BadLocationException {
		int endIndex = node.getOffset();
		IDocument doc = getDocument();
		int start = Math.max(doc.getLineOffset(line), node.getOffset());
		column = doc.getLineLength(line);
		String lineText = doc.get(start, column - start + doc.getLineOffset(line));

		int index = lineText.indexOf("</" + node.getXMLTagName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		if (index == -1) {
			index = lineText.indexOf(">"); //$NON-NLS-1$
			if (index == -1) {
				endIndex = column;
			} else {
				endIndex = index + 1;
			}
		} else {
			endIndex = index + node.getXMLTagName().length() + 3;
		}
		return start + endIndex - node.getOffset();
	}

	private IRegion getAttributeRegion(String name, String value, int offset) throws BadLocationException {
		FindReplaceDocumentAdapter fFindReplaceAdapter = new FindReplaceDocumentAdapter(getDocument());
		IRegion nameRegion = fFindReplaceAdapter.find(offset, name + "\\s*=\\s*\"", true, true, false, true); //$NON-NLS-1$
		if (nameRegion != null) {
			if (getDocument().get(nameRegion.getOffset() + nameRegion.getLength(), value.length()).equals(value))
				return new Region(nameRegion.getOffset(), nameRegion.getLength() + value.length() + 1);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
//	public void endElement(String uri, String localName, String qName) throws SAXException {
//		if (fDocumentNodeStack.isEmpty())
//			return;
//
//		IDocumentElementNode node = (IDocumentElementNode) fDocumentNodeStack.pop();
//		try {
//			node.setLength(getElementLength(node, fLocator.getLineNumber() - 1, fLocator.getColumnNumber()));
//			setTextNodeOffset(node);
//		} catch (BadLocationException e) {
//		}
//		removeOrphanElements(node);
//	}

	/**
	 * NEW METHOD
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param type
	 */
	public void endElement(String uri, String localName, String qName, ElementExp type){
		if (fDocumentNodeStack.isEmpty())
			return;
		
//		if (!fReconciling || fDocumentNodeStack.isEmpty())
//			return;
//
//		IDocumentElementNode parent = (IDocumentElementNode) fDocumentNodeStack.peek();
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(ch, start, length);
//		HQDEPlugin.logErrorMessage(buffer.toString());
//		getDocumentTextNode(buffer.toString(), parent);


		IDocumentElementNode node = (IDocumentElementNode) fDocumentNodeStack.pop();
		
		// TODO we could check from schema if node may contain text
		if(qName.equals("help") || qName.equals("script")) {
			getDocumentTextNode(fLiteral, node);
		}
		
		try {
			if(node instanceof ISchemaElement)
				((ISchemaElement)node).setElementExp(type);
			else if(node instanceof PluginBaseNode) {
				((PluginBaseNode)node).setElementExp(type);
			}
			node.setLength(getElementLength(node, fLocator.getLineNumber() - 1, fLocator.getColumnNumber()));
			setTextNodeOffset(node);
		} catch (BadLocationException e) {
		}
		node.connectAttributeSchemas();
		removeOrphanElements(node);
	}

    public void endAttributePart() {
    	fLiteral = "";
    }

	
	protected void setTextNodeOffset(IDocumentElementNode node) throws BadLocationException {
		IDocumentTextNode textNode = node.getTextNode();
		if (textNode != null && textNode.getText() != null) {
			if (textNode.getText().trim().length() == 0) {
				node.removeTextNode();
				return;
			}
			IDocument doc = getDocument();
			String text = doc.get(node.getOffset(), node.getLength());
			// 1st char of text node
			int relativeStartOffset = text.indexOf('>') + 1;
			// last char of text node
			int relativeEndOffset = text.lastIndexOf('<') - 1;

			if ((relativeStartOffset < 0) || (relativeStartOffset >= text.length())) {
				return;
			} else if ((relativeEndOffset < 0) || (relativeEndOffset >= text.length())) {
				return;
			}

			// trim whitespace
			while (Character.isWhitespace(text.charAt(relativeStartOffset)))
				relativeStartOffset += 1;
			while (Character.isWhitespace(text.charAt(relativeEndOffset)))
				relativeEndOffset -= 1;

			textNode.setOffset(node.getOffset() + relativeStartOffset);
			textNode.setLength(relativeEndOffset - relativeStartOffset + 1);
			textNode.setText(textNode.getText().trim());
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		generateErrorElementHierarchy();
	}

	/**
	 * 
	 */
	private void generateErrorElementHierarchy() {
		while (!fDocumentNodeStack.isEmpty()) {
			IDocumentElementNode node = (IDocumentElementNode) fDocumentNodeStack.pop();
			node.setIsErrorNode(true);
			removeOrphanAttributes(node);
			removeOrphanElements(node);
			if (fLastError == null)
				fLastError = node;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		generateErrorElementHierarchy();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
		fLocator = locator;
	}

	protected abstract IDocument getDocument();

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		// Prevent the resolution of external entities in order to
		// prevent the parser from accessing the Internet
		// This will prevent huge workbench performance degradations and hangs
		return new InputSource(new StringReader("")); //$NON-NLS-1$
	}

	public IDocumentElementNode getLastErrorNode() {
		return fLastError;
	}

    public void characterChunk( String literal, Datatype type ) {
    	this.fLiteral = literal;
    	try {
    		if(currentAttribute != null) 
    			currentAttribute.setAttributeValue(literal);
		} catch (CoreException e) {
			HQDEPlugin.log(e);
		}
    }

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length){
		if (!fReconciling || fDocumentNodeStack.isEmpty())
			return;

		IDocumentElementNode parent = (IDocumentElementNode) fDocumentNodeStack.peek();
		StringBuffer buffer = new StringBuffer();
		buffer.append(ch, start, length);
		getDocumentTextNode(buffer.toString(), parent);
	}

	private void removeOrphanAttributes(IDocumentElementNode node) {
		// when typing by hand, one element may overwrite a different existing one
		// remove all attributes from previous element, if any.
		if (fReconciling) {
			IDocumentAttributeNode[] attrs = node.getNodeAttributes();
			for (int i = 0; i < attrs.length; i++) {
				if (attrs[i].getNameOffset() == -1)
					node.removeDocumentAttribute(attrs[i]);
			}
		}
	}

	private void removeOrphanElements(IDocumentElementNode node) {
		// when typing by hand, one element may overwrite a different existing one
		// remove all excess children elements, if any.
		if (fReconciling) {
			IDocumentElementNode[] children = node.getChildNodes();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getOffset() == -1) {
					node.removeChildNode(children[i]);
				}
			}
		}
	}

	protected boolean isReconciling() {
		return fReconciling;
	}

}
