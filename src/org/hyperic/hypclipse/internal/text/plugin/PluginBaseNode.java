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

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.hqmodel.HQClasspathNode;
import org.hyperic.hypclipse.internal.hqmodel.HQHelpNode;
import org.hyperic.hypclipse.internal.hqmodel.HQMetricsNode;
import org.hyperic.hypclipse.internal.hqmodel.HQPlatformNode;
import org.hyperic.hypclipse.internal.hqmodel.HQScriptNode;
import org.hyperic.hypclipse.internal.hqmodel.HQServerNode;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.ElementExp;

public abstract class PluginBaseNode extends PluginObjectNode implements IPluginBase {

	private static final long serialVersionUID = 1L;

	private String fSchemaVersion;

	// base schema expression
	protected ElementExp elementExp;
	
	public void setElementExp(ElementExp exp) {
		this.elementExp = exp;
	}
	
	public ElementExp getElementExp() {
		return elementExp;
	}
	
	


	private IDocumentElementNode getEnclosingElement(String elementName, boolean create) {
		PluginElementNode element = null;
		IDocumentElementNode[] children = getChildNodes();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof IPluginElement) {
				if (((PluginElementNode) children[i]).getXMLTagName().equals(elementName)) {
					element = (PluginElementNode) children[i];
					break;
				}
			}
		}
		if (element == null && create) {
			element = new PluginElementNode();
			element.setXMLTagName(elementName);
			element.setParentNode(this);
			element.setModel(getModel());
			element.setInTheModel(true);
			if (elementName.equals("runtime")) { //$NON-NLS-1$
				addChildNode(element, 0);
			} else if (elementName.equals("requires")) { //$NON-NLS-1$
				if (children.length > 0 && children[0].getXMLTagName().equals("runtime")) { //$NON-NLS-1$
					addChildNode(element, 1);
				} else {
					addChildNode(element, 0);
				}
			}
		}
		return element;
	}



	/*
	 * 
	 */
	public String getProviderName() {
		return getXMLAttributeValue(P_PROVIDER);
	}

	/*
	 * 
	 */
	public String getVersion() {
		return getXMLAttributeValue(P_VERSION);
	}


	/*
	 * 
	 */
	public void setProviderName(String providerName) throws CoreException {
		setXMLAttribute(P_PROVIDER, providerName);
	}

	/*
	 * 
	 */
	public void setVersion(String version) throws CoreException {
		setXMLAttribute(P_VERSION, version);
	}


	/*
	 * 
	 */
	public String getSchemaVersion() {
		return fSchemaVersion;
	}

	/*
	 * 
	 */
	public void setSchemaVersion(String schemaVersion) throws CoreException {
		fSchemaVersion = schemaVersion;
	}

	
	public void add(IHQServer server) {
		if(server instanceof HQServerNode) {
			HQServerNode node = (HQServerNode)server;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(server, IModelChangedEvent.INSERT);
		}
		
	}

	public void add(IHQMetrics metrics) {
		if(metrics instanceof HQMetricsNode) {
			HQMetricsNode node = (HQMetricsNode)metrics;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(metrics, IModelChangedEvent.INSERT);
		}		
	}

	public void add(IHQClasspath classpath) {
		if(classpath instanceof HQClasspathNode) {
			HQClasspathNode node = (HQClasspathNode)classpath;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(classpath, IModelChangedEvent.INSERT);
		}		
	}

	public void add(IHQHelp help) {
		if(help instanceof HQHelpNode) {
			HQHelpNode node = (HQHelpNode)help;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(help, IModelChangedEvent.INSERT);
		}		
	}

	public void add(IHQScript script) {
		if(script instanceof HQScriptNode) {
			HQScriptNode node = (HQScriptNode)script;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(script, IModelChangedEvent.INSERT);
		}		
	}

	public void add(IHQPlatform platform) {
		if(platform instanceof HQPlatformNode) {
			HQPlatformNode node = (HQPlatformNode)platform;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(platform, IModelChangedEvent.INSERT);
		}		
	}

	public void add(IPluginElement element) throws CoreException {
		if (element instanceof PluginElementNode) {
			PluginElementNode node = (PluginElementNode) element;
			node.setModel(getModel());
			addChildNode(node);
			fireStructureChanged(element, IModelChangedEvent.INSERT);
		}
	}
	

	public void remove(IPluginObject node) {
		if (node instanceof IDocumentElementNode) {
			removeChildNode((IDocumentElementNode) node);
			node.setInTheModel(false);
			fireStructureChanged(node, IModelChangedEvent.REMOVE);
		}
	}

//	public void swap(IPluginElement e1, IPluginElement e2) throws CoreException {
	public void swap(IPluginParent e1, IPluginParent e2) throws CoreException {
		swap((IDocumentElementNode)e1, (IDocumentElementNode)e2);
		firePropertyChanged(this, IPluginParent.P_SIBLING_ORDER, e1, e2);
//		fireStructureChanged(this, IModelChangedEvent.CHANGE);
	}

	/*
	 * 
	 */
	public String getId() {
		return getXMLAttributeValue(P_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IIdentifiable#setId(java.lang.String)
	 */
	public void setId(String id) throws CoreException {
		setXMLAttribute(P_ID, id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#getName()
	 */
	public String getName() {
		return getXMLAttributeValue(P_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#setName(java.lang.String)
	 */
	public void setName(String name) throws CoreException {
		setXMLAttribute(P_NAME, name);
	}

	public String getPackage() {
		return getXMLAttributeValue(P_PACKAGE);
	}
	public void setPackage(String fPackage) throws CoreException {
		setXMLAttribute(P_PACKAGE, fPackage);
	}

	public String getProductPlugin() {
		return getXMLAttributeValue(P_CLASS);
	}
	public void setProductPlugin(String fProduct) throws CoreException {
		setXMLAttribute(P_CLASS, fProduct);
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#write(boolean)
	 */
	public String write(boolean indent) {
		String newLine = getLineDelimiter();

		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine); //$NON-NLS-1$
		buffer.append("<?eclipse version=\"3.0\"?>" + newLine); //$NON-NLS-1$

		buffer.append(writeShallow(false) + newLine);

		IDocumentElementNode runtime = getEnclosingElement("runtime", false); //$NON-NLS-1$
		if (runtime != null) {
			runtime.setLineIndent(getLineIndent() + 3);
			buffer.append(runtime.write(true) + newLine);
		}

		IDocumentElementNode requires = getEnclosingElement("requires", false); //$NON-NLS-1$
		if (requires != null) {
			requires.setLineIndent(getLineIndent() + 3);
			buffer.append(requires.write(true) + newLine);
		}


		buffer.append("</" + getXMLTagName() + ">");
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode#writeShallow(boolean)
	 */
	public String writeShallow(boolean terminate) {
		String newLine = System.getProperty("line.separator");
		StringBuffer buffer = new StringBuffer();
		buffer.append("<" + getXMLTagName());
		buffer.append(newLine);

		String id = getId();
		if (id != null && id.trim().length() > 0)
			buffer.append("   " + P_ID + "=\"" + getWritableString(id) + "\"" + newLine);

		String name = getName();
		if (name != null && name.trim().length() > 0)
			buffer.append("   " + P_NAME + "=\"" + getWritableString(name) + "\"" + newLine);

		String fPackage = getPackage();
		if (fPackage != null && fPackage.trim().length() > 0)
			buffer.append("   " + P_PACKAGE + "=\"" + getWritableString(fPackage) + "\"" + newLine);

		String clazz = getProductPlugin();
		if (clazz != null && clazz.trim().length() > 0) {
			buffer.append("   " + P_CLASS + "=\"" + getWritableString(clazz) + "\"");
		}

		String[] specific = getSpecificAttributes();
		for (int i = 0; i < specific.length; i++)
			buffer.append(newLine + specific[i]);
		if (terminate)
			buffer.append("/");
		buffer.append(">");

		return buffer.toString();
	}

	protected abstract String[] getSpecificAttributes();

	public boolean isRoot() {
		return true;
	}
}
