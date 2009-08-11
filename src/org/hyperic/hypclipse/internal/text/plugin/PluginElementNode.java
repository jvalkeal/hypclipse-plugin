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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.schema.AnnotationCollector;
import org.hyperic.hypclipse.internal.schema.ISchemaAttribute;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.text.DocumentTextNode;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginElement;


import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;

public class PluginElementNode extends PluginParentNode implements IPluginElement {

	private static final long serialVersionUID = 1L;
	
	public void connectAttributeSchemas() {
		IDocumentAttributeNode[] nodes = getNodeAttributes();
		AnnotationCollector col = new AnnotationCollector();
		ArrayList<AttributeExp> results = new ArrayList<AttributeExp>();
		col.collect(elementExp.contentModel,results);
		ListIterator<AttributeExp> iter = results.listIterator();
		while(iter.hasNext()) {
			AttributeExp aExp = iter.next();
			for (int i = 0; i < nodes.length; i++) {
				if(nodes[i].getAttributeName().equals(aExp.getNameClass().toString())) {
					if(nodes[i] instanceof ISchemaAttribute) {
						((ISchemaAttribute)nodes[i]).setAttributeExp(aExp);
					}
				}
			}
		}
	}

	
	/*
	 * 
	 */
	public IPluginElement createCopy() {
		return null;
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttribute(java.lang.String)
//	 */
//	public IPluginAttribute getAttribute(String name) {
//		return (IPluginAttribute) getNodeAttributesMap().get(name);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.hyperic.hypclipse.plugin.IPluginElement#setAttribute(java.lang.String, java.lang.String)
//	 */
//	public void setAttribute(String name, String value) throws CoreException {
//		setXMLAttribute(name, value);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttributes()
//	 */
//	public IPluginAttribute[] getAttributes() {
//		return (IPluginAttribute[]) getNodeAttributesMap().values().toArray(new IPluginAttribute[getNodeAttributesMap().size()]);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttributeCount()
//	 */
//	public int getAttributeCount() {
//		return getNodeAttributesMap().size();
//	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getText()
	 */
	public String getText() {
		IDocumentTextNode node = getTextNode();
		return node == null ? "" : node.getText(); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#setText(java.lang.String)
	 */
	public void setText(String text) throws CoreException {
		IDocumentTextNode node = getTextNode();
		String oldText = node == null ? null : node.getText();
		if (node == null) {
			node = new DocumentTextNode();
			node.setEnclosingElement(this);
			addTextNode(node);
		}
		node.setText(text.trim());
		firePropertyChanged(node, P_TEXT, oldText, text);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#write(boolean)
	 */
	public String write(boolean indent) {
		String sep = getLineDelimiter();
		StringBuffer buffer = new StringBuffer();
		if (indent)
			buffer.append(getIndent());

		IDocumentElementNode[] children = getChildNodes();
		String text = getText();
		buffer.append(writeShallow(false));
		if (getAttributeCount() > 0 || children.length > 0 || text.length() > 0)
			buffer.append(sep);
		if (children.length > 0 || text.length() > 0) {
			if (text.length() > 0) {
				buffer.append(getIndent());
				buffer.append("   "); //$NON-NLS-1$
				buffer.append(text);
				buffer.append(sep);
			}
			for (int i = 0; i < children.length; i++) {
				children[i].setLineIndent(getLineIndent() + 3);
				buffer.append(children[i].write(true));
				buffer.append(sep);
			}
		}
		if (getAttributeCount() > 0 || children.length > 0 || text.length() > 0)
			buffer.append(getIndent());

		buffer.append("</" + getXMLTagName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$	
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#writeShallow(boolean)
	 */
	public String writeShallow(boolean terminate) {
		String sep = getLineDelimiter();
		StringBuffer buffer = new StringBuffer("<" + getXMLTagName()); //$NON-NLS-1$

		IDocumentAttributeNode[] attrs = getNodeAttributes();
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i].getAttributeValue().length() > 0)
				buffer.append(sep + getIndent() + "      " + attrs[i].write()); //$NON-NLS-1$
		}
		if (terminate)
			buffer.append("/"); //$NON-NLS-1$
		buffer.append(">"); //$NON-NLS-1$
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#getName()
	 */
	public String getName() {
		return getXMLTagName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#setName(java.lang.String)
	 */
	public void setName(String name) throws CoreException {
		setXMLTagName(name);
	}

//	public Object getElementInfo() {
//		if (elementInfo == null) {
//			IDocumentElementNode node = getParentNode();
//			for (;;) {
//				if (node == null || node instanceof IPluginExtension)
//					break;
//				node = node.getParentNode();
//			}
//			if (node != null) {
//				IPluginExtension extension = (IPluginExtension) node;
//				ISchema schema = (ISchema) extension.getSchema();
//				if (schema != null) {
//					elementInfo = schema.findElement(getName());
//				}
//			}
//		}
//		return elementInfo;
//	}

	/*
	 * 
	 */
	public void reconnect(IDocumentElementNode parent, IModel model) {
		super.reconnect(parent, model);
		// Transient Field:  Element Info
		// Not necessary to reconnect schema.
		// getElementInfo will retrieve the schema on demand if it is null	
//		elementInfo = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#write(java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
		// Used for text transfers for copy, cut, paste operations
		writer.write(write(true));
	}

}
