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

import com.sun.msv.grammar.ElementExp;

public class HQClasspathNode extends PluginParentNode implements IHQClasspath {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void add(String path) {
		// TODO Auto-generated method stub
		
	}

	
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
		buffer.append(sep + getIndent() + "</classpath>");
		return buffer.toString();
	}

	public void write(String indent, PrintWriter writer) {
		// Used for text transfers for copy, cut, paste operations
		writer.write(write(true));
	}

	public String writeShallow(boolean terminate) {
		String sep = getLineDelimiter();
		String attrIndent = "      ";
		StringBuffer buffer = new StringBuffer("<classpath");
		
//		IDocumentAttributeNode attr = getDocumentAttribute(P_NAME);
//		if (attr != null && attr.getAttributeValue().trim().length() > 0)
//			buffer.append(sep + getIndent() + attrIndent + attr.write());
		
//		attr = getDocumentAttribute(P_INCLUDE);
//		if (attr != null && attr.getAttributeValue().trim().length() > 0)
//			buffer.append(sep + getIndent() + attrIndent + attr.write());

		if (terminate)
			buffer.append("/");
		buffer.append(">");
		return buffer.toString();
	}

}
