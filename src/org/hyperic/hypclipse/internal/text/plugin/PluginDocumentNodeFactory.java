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
import org.hyperic.hypclipse.internal.hqmodel.HQMetricNode;
import org.hyperic.hypclipse.internal.hqmodel.HQMetricsNode;
import org.hyperic.hypclipse.internal.hqmodel.HQPlatformNode;
import org.hyperic.hypclipse.internal.hqmodel.HQScriptNode;
import org.hyperic.hypclipse.internal.hqmodel.HQServerNode;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetric;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.DocumentTextNode;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentNodeFactory;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;

public class PluginDocumentNodeFactory implements IPluginModelFactory, IDocumentNodeFactory {

	private PluginModelBase fModel;

	public PluginDocumentNodeFactory(PluginModelBase model) {
		fModel = model;
	}

	public IDocumentElementNode createDocumentNode(String name, IDocumentElementNode parent) {
		// first tag
		if (parent == null)
			return createPluginBase(name);

		if (parent instanceof PluginBaseNode) {
			if ("classpath".equals(name))
				return (IDocumentElementNode) createClasspath();
			else if ("server".equals(name))
				return (IDocumentElementNode) createServer();
			else if ("metrics".equals(name))
				return (IDocumentElementNode) createMetrics();
			else if ("help".equals(name))
				return (IDocumentElementNode) createHelp();
			else if ("script".equals(name))
				return (IDocumentElementNode) createScript();
			else if ("platform".equals(name))
				return (IDocumentElementNode) createPlatform();
			
			
		} else {
			if (name.equals("import") && parent instanceof PluginElementNode) { //$NON-NLS-1$
				if (((PluginElementNode) parent).getName().equals("requires")) { //$NON-NLS-1$
					IDocumentElementNode ancestor = parent.getParentNode();
//					if (ancestor != null && ancestor instanceof PluginBaseNode) {
//						return (IDocumentElementNode) createImport();
//					}
				}
			} else if (name.equals("library") && parent instanceof PluginElementNode) { //$NON-NLS-1$
				if (((PluginElementNode) parent).getName().equals("runtime")) { //$NON-NLS-1$
					IDocumentElementNode ancestor = parent.getParentNode();
//					if (ancestor != null && ancestor instanceof PluginBaseNode) {
//						return (IDocumentElementNode) createLibrary();
//					}
				}
			}
		}
//		IDocumentElementNode node = (IDocumentElementNode) createElement((IPluginObject) parent);
//		node.setXMLTagName(name);
		IDocumentElementNode node = (IDocumentElementNode) createElement((IPluginObject) parent, name);
		return node;
	}

	public IDocumentAttributeNode createAttribute(String name, String value, IDocumentElementNode enclosingElement) {
		PluginNodeAttribute attribute = new PluginNodeAttribute();
		try {
			attribute.setName(name);
			attribute.setValue(value);
		} catch (CoreException e) {
		}
		attribute.setEnclosingElement(enclosingElement);
		attribute.setModel(fModel);
		attribute.setInTheModel(true);
		return attribute;
	}

	private PluginBaseNode createPluginBase(String name) {
		return (PluginBaseNode) fModel.createPluginBase(); //$NON-NLS-1$

	}

	/*
	 * 
	 */
	public IPluginAttribute createAttribute(IPluginElement element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginModelFactory#createElement(org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public IPluginElement createElement(IPluginObject parent) {
		PluginElementNode node = new PluginElementNode();
		node.setModel(fModel);
		return node;
	}
	public IPluginElement createElement(IPluginObject parent, String name) {
		
		if(name.equals("service")) {
			return createServiceElement(parent, name);
		} else if(name.equals("plugin")) {
			return createPluginElement(parent, name);
		} /*else if(name.equals("property") && !parent.getName().equals("properties")) {
			return createPropertyElement(parent, name);
		}*/

		PluginElementNode node = new PluginElementNode();
		node.setXMLTagName(name);
		node.setModel(fModel);
				
		return node;		
	}

	private IPluginElement createPropertyElement(IPluginObject parent, String name) {
		PluginElementNode node = new PluginElementNode();
		node.setXMLTagName(name);
		node.setModel(fModel);
		
//		ArrayList<ISchemaAttribute> atts = new ArrayList<ISchemaAttribute>();
//		atts.add(new SchemaAttribute("name", ISchemaAttribute.REQUIRED));
//		atts.add(new SchemaAttribute("value", ISchemaAttribute.REQUIRED));
//		node.setSchemaAttributes(atts);
		
		return node;		
	}

	
	private IPluginElement createPluginElement(IPluginObject parent, String name) {
		PluginElementNode node = new PluginElementNode();
		node.setXMLTagName(name);
		node.setModel(fModel);
		
//		ArrayList<ISchemaAttribute> atts = new ArrayList<ISchemaAttribute>();
//		atts.add(new SchemaAttribute("type", ISchemaAttribute.REQUIRED));
//		atts.add(new SchemaAttribute("class", ISchemaAttribute.REQUIRED));
//		node.setSchemaAttributes(atts);
		
		return node;		
	}

	
	private IPluginElement createServiceElement(IPluginObject parent, String name) {
		PluginElementNode node = new PluginElementNode();
		node.setXMLTagName(name);
		node.setModel(fModel);
		
//		ArrayList<ISchemaAttribute> atts = new ArrayList<ISchemaAttribute>();
//		atts.add(new SchemaAttribute("name", ISchemaAttribute.REQUIRED));
//		atts.add(new SchemaAttribute("version", ISchemaAttribute.OPTIONAL));
//		atts.add(new SchemaAttribute("description", ISchemaAttribute.OPTIONAL));
//		node.setSchemaAttributes(atts);
		
		return node;		
	}
	

	/*
	 * 
	 */
	public IDocumentTextNode createDocumentTextNode(String content, IDocumentElementNode parent) {
		DocumentTextNode textNode = new DocumentTextNode();
		textNode.setEnclosingElement(parent);
		parent.addTextNode(textNode);
		textNode.setText(content.trim());
		return textNode;
	}

	public IHQClasspath createClasspath() {
		HQClasspathNode node = new HQClasspathNode();
		node.setXMLTagName("classpath");
		node.setModel(fModel);
		return node;
	}

	public IHQScript createScript() {
		HQScriptNode node = new HQScriptNode();
		node.setXMLTagName("script");
		node.setModel(fModel);
		return node;
	}

	public IHQHelp createHelp() {
		HQHelpNode node = new HQHelpNode();
		node.setXMLTagName("help");
		node.setModel(fModel);
		return node;
	}

	public IHQServer createServer() {
		HQServerNode node = new HQServerNode();
		node.setXMLTagName("server");
		node.setModel(fModel);
		return node;
	}

	public IHQPlatform createPlatform() {
		HQPlatformNode node = new HQPlatformNode();
		node.setXMLTagName("platform");
		node.setModel(fModel);
		return node;
	}

	public IHQMetrics createMetrics() {
		HQMetricsNode node = new HQMetricsNode();
		node.setXMLTagName("metrics");
		node.setModel(fModel);
		return node;
	}

	public IHQMetric createMetric() {
		HQMetricNode node = new HQMetricNode();
		node.setXMLTagName("metric");
		node.setModel(fModel);
		return node;
	}

}
