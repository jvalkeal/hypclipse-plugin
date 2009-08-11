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
import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.plugin.PluginObject;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;

public class HQClasspath extends PluginObject implements IHQClasspath {

	private ArrayList<String> fPaths;
	
	public HQClasspath() {
		fPaths = new ArrayList<String>();
	}

	public void add(String path) {
		fPaths.add(path);
	}	
	
	public IPluginModelBase getPluginModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(String indent, PrintWriter writer) {
		writer.print(indent);
//		writer.println("<classpath>");
		
		ListIterator<String> iter = fPaths.listIterator();
		while(iter.hasNext()) {
			writer.print(indent);
			writer.print(indent);
			writer.print("<include name=\"");
			writer.print(iter.next());
			writer.print("\"/>");
			writer.println();
		}
		
		writer.print(indent);
//		writer.println("</classpath>");
	}


	public void add(int index, IPluginObject child) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void add(IPluginObject child) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public int getChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IPluginObject[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIndexOf(IPluginObject child) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void remove(IPluginObject child) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void swap(IPluginObject child1, IPluginObject child2)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}



	
	
}
