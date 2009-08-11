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

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.schema.ISchemaAttribute;
import org.hyperic.hypclipse.internal.text.DocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.w3c.dom.Node;

import com.sun.msv.grammar.AttributeExp;

public class PluginNodeAttribute extends PluginObjectNode implements IPluginAttribute, IDocumentAttributeNode, ISchemaAttribute {


	private static final long serialVersionUID = 1L;

	// The plugin attribute interface requires this class to extend PluginObjectNode
	// However, by doing that this class also extends the document
	// element node class - which is wrong when implementing 
	// the document attribute node interface
	// To work around this issue, we use an adaptor.
	private DocumentAttributeNode fAttribute;

	private String fValue;

	private AttributeExp aExp;
	
	/**
	 * 
	 */
	public PluginNodeAttribute() {
		super();
		fAttribute = new DocumentAttributeNode();
		fValue = null;
	}
	
	void load(Node node) {
		fName = node.getNodeName();
		fValue = node.getNodeValue();
	}

	void load(String name, String value) {
		fName = name;
		fValue = value;
	}


	public String getValue() {
		return fValue;
	}

	public void setValue(String value) throws CoreException {
		fValue = value;
	}

	public void setEnclosingElement(IDocumentElementNode node) {
		fAttribute.setEnclosingElement(node);
	}

	public IDocumentElementNode getEnclosingElement() {
		return fAttribute.getEnclosingElement();
	}

	public void setNameOffset(int offset) {
		fAttribute.setNameOffset(offset);
	}

	public int getNameOffset() {
		return fAttribute.getNameOffset();
	}

	public void setNameLength(int length) {
		fAttribute.setNameLength(length);
	}

	public int getNameLength() {
		return fAttribute.getNameLength();
	}

	public void setValueOffset(int offset) {
		fAttribute.setValueOffset(offset);
	}

	public int getValueOffset() {
		return fAttribute.getValueOffset();
	}

	public void setValueLength(int length) {
		fAttribute.setValueLength(length);
	}

	public int getValueLength() {
		return fAttribute.getValueLength();
	}

	public String getAttributeName() {
		return getName();
	}

	public String getAttributeValue() {
		return getValue();
	}

	public String write() {
		return getName() + "=\"" + getWritableString(getValue()) + "\"";
	}

	public String getWritableString(String source) {
		return super.getWritableString(source).replaceAll("\\r", "&#x0D;")
				.replaceAll("\\n", "&#x0A;");
	}

	public void setAttributeName(String name) throws CoreException {
		setName(name);
	}

	public void setAttributeValue(String value) throws CoreException {
		setValue(value);
	}

	public void reconnect(IDocumentElementNode parent) {
		// Inconsistency in model
		// A document attribute node should not extend plugin object because plugin object extends 
		// document element node
		super.reconnect(parent, getModel());
		fAttribute.reconnect(parent);
	}

	public void write(String indent, PrintWriter writer) {
		// Used for text transfers for copy, cut, paste operations
		// Although attributes cannot be copied directly
		writer.write(write());
	}

	public AttributeExp getAttributeExp() {
		return aExp;
	}

	public void setAttributeExp(AttributeExp exp) {
		this.aExp = exp;
	}

}
