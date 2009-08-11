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
package org.hyperic.hypclipse.internal.editor.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.ElementExp;

public class NewStructureAction  extends Action  {

	private IPluginParent parent;
	private ElementExp exp;
	public NewStructureAction(IPluginParent parent, ElementExp exp) {
		this.parent = parent;
		this.exp = exp;
	}
	
	public void run() {
		try {
			IPluginModelFactory factory = parent.getPluginModel().getPluginFactory();		
			IPluginElement element = factory.createElement(parent);
			((PluginElementNode)element).setElementExp(exp);
			((PluginElementNode) element).setParentNode((IDocumentElementNode) parent);
			element.setName(exp.getNameClass().toString());
			parent.add(element);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}				


	}


}
