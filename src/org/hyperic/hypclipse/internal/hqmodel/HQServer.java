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
import org.hyperic.hypclipse.internal.text.plugin.PluginElement;
import org.hyperic.hypclipse.internal.text.plugin.PluginParent;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;


public class HQServer extends PluginParent implements IHQServer {

	private String virtual = null;
	
	public HQServer() {
	}

	public IPluginModelBase getPluginModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(String indent, PrintWriter writer) {
		writer.print(indent);
		writer.print("<server name=\"");
		writer.print(getName());
		writer.print("\"");
		if(virtual != null) {
			writer.println();
			writer.print(indent);
			writer.print("virtual=\"");
			writer.print(virtual);
			writer.print("\"");
		}			
		writer.print(">");
		writer.println();
		IPluginObject[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			IPluginElement child = (IPluginElement) children[i];
			child.write(indent + PluginElement.ELEMENT_SHIFT, writer);
		}

		
		writer.print(indent);		
		writer.print("</server>");
		writer.println();
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInclude() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setInclude(String include) {
	}

	public void setVersion(String value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setDescription(String value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setPlatforms(String value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setVirtual(String value) throws CoreException {
		this.virtual = value;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlatforms() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVirtual() {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
