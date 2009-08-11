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
package org.hyperic.hypclipse.internal.plugin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.hqmodel.HQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.HQServer;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PluginBase extends PluginObject implements IPluginBase {

	private String fId;
	
	private ArrayList<IHQClasspath> fClasspaths;
	private ArrayList<IHQServer> fServers;
	private ArrayList<IHQMetrics> fMetrics;
	private ArrayList<IPluginElement> fElements;
	private String fPackage;
	private String fProductPlugin;
	
	public PluginBase() {
		fClasspaths = new ArrayList<IHQClasspath>();
		fServers = new ArrayList<IHQServer>();
		fMetrics = new ArrayList<IHQMetrics>();
		fElements = new ArrayList<IPluginElement>();
		fPackage = "";
	}

	public String getPackage() {
		return fPackage;
	}

	public void setPackage(String fPackage) throws CoreException {
		this.fPackage = fPackage;
	}

	public void add(IHQClasspath classPath) {
		fClasspaths.add(classPath);
//		((HQClasspath)classPath).setInTheModel(true);
//		((HQClasspath)classPath).setParent(this);
		fireStructureChanged(classPath, IModelChangedEvent.INSERT);
	}

	public void add(IHQServer server) {
		fServers.add(server);
		((HQServer)server).setInTheModel(true);
		((HQServer)server).setParent(this);
		fireStructureChanged(server, IModelChangedEvent.INSERT);
	}

	public void add(IHQMetrics metrics) {
		fMetrics.add(metrics);
		((HQMetrics)metrics).setInTheModel(true);
		((HQMetrics)metrics).setParent(this);
		fireStructureChanged(metrics, IModelChangedEvent.INSERT);
	}

	public IHQServer[] getServers() {
		return fServers.toArray(new IHQServer[0]);

	}

	public IHQMetrics[] getMetrics() {
		return fMetrics.toArray(new IHQMetrics[0]);

	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getPluginModel()
	 */
	public IPluginModelBase getPluginModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(String indent, PrintWriter writer) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.print("<plugin");

		if (getName() != null) {
			writer.println();
			writer.print("   name=\"" + getName() + "\"");
		}

		if (getPackage() != null) {
			writer.println();
			writer.print("   package=\"" + getPackage() + "\"");
		}
		
		if (getProductPlugin() != null) {
			writer.println();
			writer.print("   class=\"" + getProductPlugin() + "\"");
		}

		writer.println(">");
		writer.println();

		String firstIndent = "   ";
			
		if(fMetrics.size() > 0) {
			Object[] children = fMetrics.toArray();
			writeChildren(firstIndent, children, writer);
			writer.println();			
		}
		
		if(fClasspaths.size() > 0) {
			Object[] children = fClasspaths.toArray();
			writeChildren(firstIndent, "classpath", children, writer);
			writer.println();
		}

		if(fElements.size() > 0) {
			Object[] children = fElements.toArray();
			writeChildren(firstIndent, children, writer);
			writer.println();			
		}

		if(fServers.size() > 0) {
			Object[] children = fServers.toArray();
			writeChildren(firstIndent, children, writer);
			writer.println();			
		}



		
		writer.println("</plugin>");
	}

	protected void writeChildren(String indent, String tag, Object[] children, PrintWriter writer) {
		writer.println(indent + "<" + tag + ">");
		for (int i = 0; i < children.length; i++) {
			IPluginObject obj = (IPluginObject) children[i];
			obj.write(indent + "   ", writer);
		}
		writer.println(indent + "</" + tag + ">");
	}

	protected void writeChildren(String indent, Object[] children, PrintWriter writer) {
		for (int i = 0; i < children.length; i++) {
			IPluginObject obj = (IPluginObject) children[i];
			obj.write(indent + "   ", writer);
		}
	}

	
	public String getId() {
		return fId;
	}

	public void setId(String id) throws CoreException {
		fId = id;
	}

	
	public void load(Node node) {
		if (node == null)
			return;
//		fSchemaVersion = schemaVersion;
//		fId = getNodeAttribute(node, "id"); //$NON-NLS-1$
//		fName = getNodeAttribute(node, "name"); //$NON-NLS-1$
//		fProviderName = getNodeAttribute(node, "provider-name"); //$NON-NLS-1$
//		fVersion = getNodeAttribute(node, "version"); //$NON-NLS-1$
//
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				processChild(child);
			}
		}
		
	}

	
	protected void processChild(Node child) {
		String name = child.getNodeName().toLowerCase(Locale.ENGLISH);

	}

	public void reset() {
		fClasspaths = new ArrayList<IHQClasspath>();
		fServers = new ArrayList<IHQServer>();
//		fLibraries = new ArrayList();
//		fImports = new ArrayList();
//		fProviderName = null;
//		fSchemaVersion = null;
//		fVersion = ""; //$NON-NLS-1$
//		fName = ""; //$NON-NLS-1$
//		fId = ""; //$NON-NLS-1$
//		if (getModel() != null && getModel().getUnderlyingResource() != null) {
//			fId = getModel().getUnderlyingResource().getProject().getName();
//			fName = fId;
//			fVersion = "0.0.0"; //$NON-NLS-1$
//		}
//		super.reset();
		
	}

	public String getProductPlugin() {
		return fProductPlugin;
	}

	public void setProductPlugin(String product) throws CoreException {
		this.fProductPlugin = product;
	}

	public void add(IPluginElement element) throws CoreException {
		fElements.add(element);
		((IPluginElement)element).setInTheModel(true);
//		((IPluginElement)element).setParent(this);
		fireStructureChanged(element, IModelChangedEvent.INSERT);
	}

	public void add(IHQHelp help) {
		// TODO Auto-generated method stub
		
	}

	public void add(IHQScript script) {
		// TODO Auto-generated method stub
		
	}

	public void add(IHQPlatform platform) {
		// TODO Auto-generated method stub
		
	}


	


}
