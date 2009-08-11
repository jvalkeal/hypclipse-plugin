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

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.text.plugin.PluginElement;

public class HQMetric extends PluginElement implements IHQMetric {


//	public void write(String indent, PrintWriter writer) {
//		writer.print(indent);
//		writer.print("<metric name=\"");
//		writer.print(getName());
//		writer.print("\">");
//		writer.println();
//		writer.print("</metric>");
//		writer.println();
//		writer.print(indent);		
//	}

	public void setAlias(String alias) throws CoreException {
		setAttribute(IHQMetric.A_ALIAS, alias);
	}

	public void setAName(String name) throws CoreException {
		setAttribute("name", name);
	}

	public void setCategory(String category) throws CoreException {
		setAttribute(IHQMetric.A_CATEGORY, category);
	}

	public void setCollectionType(String collectionType) throws CoreException {
		setAttribute(IHQMetric.A_COLLECTIONTYPE, collectionType);
	}

	public void setIndicator(String indicator) throws CoreException {
		setAttribute(IHQMetric.A_INDICATOR, indicator);
		
	}

	public void setTemplate(String template) throws CoreException {
		setAttribute(IHQMetric.A_TEMPLATE, template);	
	}

	public void setUnits(String units) throws CoreException {
		setAttribute(IHQMetric.A_UNITS, units);	
	}

}
