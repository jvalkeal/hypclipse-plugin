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
import java.util.ArrayList;
import java.util.TreeMap;

import org.hyperic.hypclipse.plugin.IModel;


public interface IDocumentElementNode extends Serializable, IDocumentRange, IDocumentXMLNode {

	public static final String F_PROPERTY_CHANGE_TYPE_SWAP = "type_swap";

	void connectAttributeSchemas();
	
	IDocumentElementNode getParentNode();

	void setParentNode(IDocumentElementNode node);

	void addChildNode(IDocumentElementNode child);

	void addChildNode(IDocumentElementNode child, int position);

	IDocumentElementNode removeChildNode(IDocumentElementNode child);

	// Not used by text edit operations
	IDocumentElementNode removeChildNode(int index);

	IDocumentElementNode[] getChildNodes();

	void addTextNode(IDocumentTextNode textNode);

	IDocumentTextNode getTextNode();

	void removeTextNode();

	// Not used by text edit operations
	int indexOf(IDocumentElementNode child);

	IDocumentElementNode getChildAt(int index);

	IDocumentElementNode getPreviousSibling();

	void setPreviousSibling(IDocumentElementNode sibling);

	// Not used by text edit operations
	void swap(IDocumentElementNode child1, IDocumentElementNode child2);

	void setXMLTagName(String tag);

	String getXMLTagName();

	void setXMLAttribute(IDocumentAttributeNode attribute);

	// Not used by text edit operations
	public boolean setXMLAttribute(String name, String value);

	// Not used by text edit operations
	String getXMLAttributeValue(String name);

	IDocumentAttributeNode getDocumentAttribute(String name);

	IDocumentAttributeNode[] getNodeAttributes();

	void removeDocumentAttribute(IDocumentAttributeNode attr);

	boolean isErrorNode();

	void setIsErrorNode(boolean isErrorNode);

	boolean isRoot();

	void setOffset(int offset);

	void setLength(int length);

	void setLineIndent(int indent);

	int getLineIndent();

	// Not used by text edit operations
	public String getIndent();

	String write(boolean indent);

	String writeShallow(boolean terminate);

	// Not used by text edit operations
	public boolean canTerminateStartTag();

	// Not used by text edit operations
	public int getChildCount();

	// Not used by text edit operations
	public int getNodeAttributesCount();

	// Not used by text edit operations
	public TreeMap getNodeAttributesMap();

	// Not used by text edit operations
	public ArrayList getChildNodesList();

	// Not used by text edit operations
	public void reconnect(IDocumentElementNode parent, IModel model);

	// Not used by text edit operations
	/**
	 * @param text String already trimmed and formatted
	 * @return
	 */
	public boolean setXMLContent(String text);

	// Not used by text edit operations
	public String getXMLContent();

	// Not used by text edit operations
	public boolean isContentCollapsed();

	// Not used by text edit operations
	public boolean isLeafNode();

	// Not used by text edit operations
	public boolean hasXMLChildren();

	// Not used by text edit operations
	public boolean hasXMLContent();

	// Not used by text edit operations
	public boolean hasXMLAttributes();

	// TODO: MP: TEO: LOW: Space out, comment and rename methods

}
