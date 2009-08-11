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

import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;

public class PluginNode extends PluginBaseNode /*implements IPlugin*/ {

	private static final long serialVersionUID = 1L;



	public boolean hasExtensibleAPI() {
		return false;
	}


	protected String[] getSpecificAttributes() {
		return new String[0];
	}

	
	
	
	public IHQMetrics[] getMetrics() {
		ArrayList result = new ArrayList();
		IDocumentElementNode[] children = getChildNodes();
		for (int i = 0; i < children.length; i++) {
			if(children[i] instanceof IHQMetrics)
				result.add(children[i]);
		}
		return (IHQMetrics[]) result.toArray(new IHQMetrics[result.size()]);
	}


	public IHQServer[] getServers() {
		ArrayList result = new ArrayList();
		IDocumentElementNode[] children = getChildNodes();
		for (int i = 0; i < children.length; i++) {
			if(children[i] instanceof IHQServer)
				result.add(children[i]);
		}
		return (IHQServer[]) result.toArray(new IHQServer[result.size()]);
	}



}
