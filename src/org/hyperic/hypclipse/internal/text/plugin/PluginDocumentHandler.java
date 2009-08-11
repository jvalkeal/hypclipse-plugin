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
package org.hyperic.hypclipse.internal.text.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.text.DocumentHandler;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.SAXException;

import com.sun.msv.verifier.psvi.TypedContentHandler;

public class PluginDocumentHandler extends DocumentHandler implements TypedContentHandler {

	private PluginModelBase fModel;
	private String fSchemaVersion;
	protected PluginDocumentNodeFactory fFactory;

	/**
	 * @param model
	 */
	public PluginDocumentHandler(PluginModelBase model, boolean reconciling) {
		super(reconciling);
		fModel = model;
		fFactory = (PluginDocumentNodeFactory) getModel().getPluginFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentHandler#getDocument()
	 */
	protected IDocument getDocument() {
		return fModel.getDocument();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
//		IPluginBase pluginBase = fModel.getPluginBase();
//		try {
//			if (pluginBase != null)
//				pluginBase.setSchemaVersion(fSchemaVersion);
//		} catch (CoreException e) {
//		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	public void processingInstruction(String target, String data) throws SAXException {
//		if ("eclipse".equals(target)) { //$NON-NLS-1$
//			// Data should be of the form: version="<version>"
//			if (data.length() > 10 && data.substring(0, 9).equals("version=\"") && data.charAt(data.length() - 1) == '\"') { //$NON-NLS-1$
//				fSchemaVersion = TargetPlatformHelper.getSchemaVersionForTargetVersion(data.substring(9, data.length() - 1));
//			} else {
//				fSchemaVersion = TargetPlatformHelper.getSchemaVersion();
//			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		super.startDocument();
		fSchemaVersion = null;
	}

	protected PluginModelBase getModel() {
		return fModel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentHandler#getDocumentNode(java.lang.String, org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	protected IDocumentElementNode getDocumentNode(String name, IDocumentElementNode parent) {
		IDocumentElementNode node = null;
		if (parent == null) {
			node = (IDocumentElementNode) getModel().getPluginBase();
			if (node != null) {
				node.setOffset(-1);
				node.setLength(-1);
			}
		} else {
			IDocumentElementNode[] children = parent.getChildNodes();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getOffset() < 0) {
					if (name.equals(children[i].getXMLTagName())) {
						node = children[i];
					}
					break;
				}
			}
		}

		if (node == null)
			return fFactory.createDocumentNode(name, parent);

		IDocumentAttributeNode[] attrs = node.getNodeAttributes();
		for (int i = 0; i < attrs.length; i++) {
			attrs[i].setNameOffset(-1);
			attrs[i].setNameLength(-1);
			attrs[i].setValueOffset(-1);
			attrs[i].setValueLength(-1);
		}

		for (int i = 0; i < node.getChildNodes().length; i++) {
			IDocumentElementNode child = node.getChildAt(i);
			child.setOffset(-1);
			child.setLength(-1);
		}

		// clear text nodes if the user is typing on the source page
		// they will be recreated in the characters() method
		if (isReconciling()) {
			node.removeTextNode();
			node.setIsErrorNode(false);
		}

		return node;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentHandler#getDocumentAttribute(java.lang.String, java.lang.String, org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	protected IDocumentAttributeNode getDocumentAttribute(String name, String value, IDocumentElementNode parent) {
		IDocumentAttributeNode attr = parent.getDocumentAttribute(name);
		try {
			if (attr == null) {
				attr = fFactory.createAttribute(name, value, parent);
			} else {
				if (!name.equals(attr.getAttributeName()))
					attr.setAttributeName(name);
				if (!value.equals(attr.getAttributeValue()))
					attr.setAttributeValue(value);
			}
		} catch (CoreException e) {
		}
		return attr;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentHandler#getDocumentTextNode(java.lang.String, org.hyperic.hypclipse.internal.text.IDocumentElementNode)
	 */
	protected IDocumentTextNode getDocumentTextNode(String content, IDocumentElementNode parent) {

		IDocumentTextNode textNode = parent.getTextNode();
		if (textNode == null) {
			if (content.trim().length() > 0) {
				textNode = fFactory.createDocumentTextNode(content, parent);
			}
		} else {
//			String newContent = textNode.getText() + content;
//			textNode.setText(newContent);
			textNode.setText(content);
		}
		return textNode;
	}


//	@Override
//	public void endAttributePart() throws SAXException {
//		// TODO Auto-generated method stub
//		
//	}


	public void startDocument(ValidationContext arg0) throws SAXException {
		super.startDocument();
		fSchemaVersion = null;
	}


}
