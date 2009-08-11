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

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.schema.NodeSchemaReconnector;
import org.hyperic.hypclipse.internal.schema.OptionalAttributeCollector;
import org.hyperic.hypclipse.internal.text.DocumentTextNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;

public class PluginParentNode extends PluginObjectNode implements IPluginParent, ISchemaElement {

	private static final long serialVersionUID = 1L;

	
	protected ElementExp elementExp;
	
	private String optionalAttributes[] = null;
	
	public void setElementExp(ElementExp exp) {
		this.elementExp = exp;
	}
	
	public ElementExp getElementExp() {
		return elementExp;
	}
	
	public String[] getOptionalAttibutes() {
		
		if(optionalAttributes == null) {
			ArrayList<AttributeExp> result = new ArrayList<AttributeExp>();
			OptionalAttributeCollector coll = new OptionalAttributeCollector();
			coll.collect(elementExp, result);

			ArrayList<String> opts = new ArrayList<String>();
			ListIterator<AttributeExp> iter = result.listIterator();
			while(iter.hasNext()) {
				AttributeExp exp = iter.next();
				opts.add(exp.nameClass.toString());
			}
			optionalAttributes = opts.toArray(new String[0]);
		}
		return optionalAttributes;
	}

	public void add(int index, IPluginObject child) throws CoreException {
		addChildNode((IDocumentElementNode) child, index);
		fireStructureChanged(child, IModelChangedEvent.INSERT);
	}

	public void add(IPluginObject child) throws CoreException {
		add(getChildCount(), child);
		child.setInTheModel(true);
		((PluginObjectNode) child).setModel(getModel());
	}

	public int getChildCount() {
		return getChildNodes().length;
	}

	public int getIndexOf(IPluginObject child) {
		return indexOf((IDocumentElementNode) child);
	}

	public void swap(IPluginObject child1, IPluginObject child2) throws CoreException {
		swap((IDocumentElementNode) child1, (IDocumentElementNode) child2);
		firePropertyChanged(this, P_SIBLING_ORDER, child1, child2);
	}

	public IPluginObject[] getChildren() {
		ArrayList<IDocumentElementNode> result = new ArrayList<IDocumentElementNode>();
		IDocumentElementNode[] nodes = getChildNodes();
		for (int i = 0; i < nodes.length; i++)
			result.add(nodes[i]);

		return (IPluginObject[]) result.toArray(new IPluginObject[result.size()]);
	}

	public void remove(IPluginObject child) throws CoreException {
		removeChildNode((IDocumentElementNode) child);
		child.setInTheModel(false);
		fireStructureChanged(child, IModelChangedEvent.REMOVE);
	}
	
	public String getText() {
		IDocumentTextNode node = getTextNode();
		return node == null ? "" : node.getText();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttribute(java.lang.String)
	 */
	public IPluginAttribute getAttribute(String name) {
		return (IPluginAttribute) getNodeAttributesMap().get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value) throws CoreException {
		setXMLAttribute(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttributes()
	 */
	public IPluginAttribute[] getAttributes() {
		return (IPluginAttribute[]) getNodeAttributesMap().values().toArray(new IPluginAttribute[getNodeAttributesMap().size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginElement#getAttributeCount()
	 */
	public int getAttributeCount() {
		return getNodeAttributesMap().size();
	}

	
	public void setText(String text) throws CoreException {
		IDocumentTextNode node = getTextNode();
		String oldText = node == null ? null : node.getText();
		if (node == null) {
			node = new DocumentTextNode();
			node.setEnclosingElement(this);
			addTextNode(node);
		}
		node.setText(text.trim());
		firePropertyChanged(node, IPluginElement.P_TEXT, oldText, text);
	}

}
