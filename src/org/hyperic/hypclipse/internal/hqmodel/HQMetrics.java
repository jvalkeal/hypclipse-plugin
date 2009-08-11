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

import org.hyperic.hypclipse.internal.plugin.PluginBase;
import org.hyperic.hypclipse.internal.text.plugin.PluginElement;
import org.hyperic.hypclipse.internal.text.plugin.PluginParent;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;

public class HQMetrics extends PluginParent implements IHQMetrics {

	public void setInTheModel(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setParent(PluginBase pluginBase) {
		// TODO Auto-generated method stub
		
	}
	
	public void write(String indent, PrintWriter writer) {
		writer.print(indent);
		writer.print("<metrics");
		String attIndent = indent + PluginElement.ATTRIBUTE_SHIFT;
		if (getName() != null) {
			writer.println();
			writer.print(attIndent + "name=\"" + getName() + "\"");
		}
		writer.println(">");
		IPluginObject[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			if(children[i] instanceof IHQMetric) {
				HQMetric child = (HQMetric) children[i];
				child.write(indent + PluginElement.ELEMENT_SHIFT, writer);								
			} else {
				IPluginElement child = (IPluginElement) children[i];
				child.write(indent + PluginElement.ELEMENT_SHIFT, writer);				
			}
		}
		writer.println(indent + "</metrics>");

	}

	public IPluginModelBase getPluginModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInclude() {
		// TODO Auto-generated method stub
		return null;
	}



}
