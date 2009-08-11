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
package org.hyperic.hypclipse.internal.schema;

import java.util.ArrayList;
import java.util.ListIterator;

import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;

/**
 * This class can be used to fix or reconnect association
 * between xml nodes and schema expressions. 
 *
 */
public class NodeSchemaReconnector {

	ExpressionPool pool;
	PluginParentNode lastVisited;
	
	public NodeSchemaReconnector() {
		this.pool = new ExpressionPool();
		this.lastVisited = null;
	}
	
	public void connect(PluginParentNode node) {
		// if node's exression is null
		// we need to go up and refresh from
		// node which knows it's expression
		lastVisited = node;
		if(node.getElementExp() == null) {
			PluginObjectNode n = getHighestNodeWithExpression((PluginObjectNode)node.getParent());
			if(n instanceof PluginParentNode) {
				PluginParentNode p = (PluginParentNode)n;
				recursive(p, p.getElementExp());
			} else if(n instanceof PluginBaseNode) {
				connect((PluginBaseNode)n, lastVisited);
			}
				
		}
		recursive(node, node.getElementExp());
	}
	
	private PluginObjectNode getHighestNodeWithExpression(PluginObjectNode node) {
		ElementExp exp = null;
		if(node instanceof PluginParentNode) {
			lastVisited = (PluginParentNode)node;
			exp = ((PluginParentNode)node).getElementExp();
		} else if (node instanceof PluginBaseNode) {
			exp = ((PluginBaseNode)node).getElementExp();
		}
		if(exp != null)
			return node;
		else return (getHighestNodeWithExpression((PluginObjectNode)node.getParent()));
	}

	public void connect(PluginParentNode node, ElementExp exp) {
		recursive(node, exp);
	}

	public void connect(PluginBaseNode base, PluginParentNode node) {
		ElementCollector coll = new ElementCollector();
		ArrayList<ElementExp> sElements = new ArrayList<ElementExp>();
		Expression eExp = base.getElementExp().contentModel.getExpandedExp(pool);
		coll.collect(eExp, sElements);
		ListIterator<ElementExp> iter = sElements.listIterator();
		String nName = node.getXMLTagName();
		while(iter.hasNext()) {
			ElementExp nExp = iter.next();
			String eName = nExp.getNameClass().toString();
			if(eName.equals(nName))
				recursive(node, nExp);
			
		}
		
	}
	
	private void recursive(PluginParentNode node, ElementExp exp) {

		// connect with given expression
		node.setElementExp(exp);

		// get childs
		IDocumentElementNode[] nodes = node.getChildNodes();
		for (int i = 0; i < nodes.length; i++) {
			if(nodes[i] instanceof PluginParentNode) {
				// initiate collector and storage
				ElementCollector coll = new ElementCollector();
				ArrayList<ElementExp> sElements = new ArrayList<ElementExp>();

				// get model from parent
				Expression eExp = exp.contentModel.getExpandedExp(pool);

				// collect expression which are under parent model
				coll.collect(eExp, sElements);
				
				// check if matching tag is found from results.
				// if found, call recursive
				ListIterator<ElementExp> iter = sElements.listIterator();
				while(iter.hasNext()) {
					ElementExp nExp = iter.next();
					String eName = nExp.getNameClass().toString();
					String nName = nodes[i].getXMLTagName();
					if(eName.equals(nName))
						recursive((PluginParentNode)nodes[i], nExp);
				}
			}
		}
	}
	
}
