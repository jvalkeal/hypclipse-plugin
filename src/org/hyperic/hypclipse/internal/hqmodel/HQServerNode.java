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
package org.hyperic.hypclipse.internal.hqmodel;

import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IModel;

import com.sun.msv.grammar.ElementExp;

/**
 * 
 *
 */
public class HQServerNode extends PluginParentNode implements IHQServer{

	private static final long serialVersionUID = 1L;
	
	private static final String P_INCLUDE = "include";
	private static final String P_VERSION = "version";
	private static final String P_DESCRIPTION = "description";
	private static final String P_VIRTUAL = "virtual";
	private static final String P_PLATFORMS = "platforms";
	
	public void setName(String name) throws CoreException {
		setXMLAttribute(P_NAME, name);
	}

	public String getName() {
		return getXMLAttributeValue(P_NAME);
	}

	public void setInclude(String include) throws CoreException {
		setXMLAttribute(P_INCLUDE, include);
	}

	public String getInclude() {
		return getXMLAttributeValue(P_INCLUDE);
	}

	public void setVersion(String version) throws CoreException {
		setXMLAttribute(P_VERSION, version);
	}

	public String getVersion() {
		return getXMLAttributeValue(P_VERSION);
	}

	public void setDescription(String description) throws CoreException {
		setXMLAttribute(P_DESCRIPTION, description);		
	}

	public String getDescription() {
		return getXMLAttributeValue(P_DESCRIPTION);		
	}

	public void setPlatforms(String platforms) throws CoreException {
		setXMLAttribute(P_PLATFORMS, platforms);	
	}

	public String getPlatforms() {
		return getXMLAttributeValue(P_PLATFORMS);		
	}	

	public void setVirtual(String virtual) throws CoreException {
		setXMLAttribute(P_VIRTUAL, virtual);		
	}
	
	public String getVirtual() {
		return getXMLAttributeValue(P_VIRTUAL);		
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
		buffer.append(writeShallow(false));
		IDocumentElementNode[] children = getChildNodes();
		for (int i = 0; i < children.length; i++) {
			children[i].setLineIndent(getLineIndent() + 3);
			buffer.append(sep + children[i].write(true));
		}
		buffer.append(sep + getIndent() + "</server>");
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#write(java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
		// Used for text transfers for copy, cut, paste operations
		writer.write(write(true));
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#writeShallow(boolean)
	 */
	public String writeShallow(boolean terminate) {
		String sep = getLineDelimiter();
		String attrIndent = "      ";
		StringBuffer buffer = new StringBuffer("<server");
		
		IDocumentAttributeNode attr = getDocumentAttribute(P_NAME);
		if (attr != null && attr.getAttributeValue().trim().length() > 0)
			buffer.append(sep + getIndent() + attrIndent + attr.write());
		
		attr = getDocumentAttribute(P_INCLUDE);
		if (attr != null && attr.getAttributeValue().trim().length() > 0)
			buffer.append(sep + getIndent() + attrIndent + attr.write());

		attr = getDocumentAttribute(P_VERSION);
		if (attr != null && attr.getAttributeValue().trim().length() > 0)
			buffer.append(sep + getIndent() + attrIndent + attr.write());

		if (terminate)
			buffer.append("/");
		buffer.append(">");
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#reconnect(org.hyperic.hypclipse.internal.text.IDocumentElementNode, org.hyperic.hypclipse.plugin.IModel)
	 */
	public void reconnect(IDocumentElementNode parent, IModel model) {
		super.reconnect(parent, model);
		// Transient Field:  Schema
		// Not necessary to reconnect schema.
		// getSchema will retrieve the schema on demand if it is null		
//		fSchema = null;
	}


}
