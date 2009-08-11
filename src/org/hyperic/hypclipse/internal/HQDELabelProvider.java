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
package org.hyperic.hypclipse.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.preferences.HQEnvInfo;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.internal.util.SharedLabelProvider;

public class HQDELabelProvider extends SharedLabelProvider {
	
	public Image resolveObjectImage(Object obj) {
		if(obj instanceof IHQServer)
			return get(HQDEPluginImages.DESC_TREE_SERVER);
		if(obj instanceof IHQMetrics)
			return get(HQDEPluginImages.DESC_TREE_METRICS);
		if(obj instanceof IHQClasspath)
			return get(HQDEPluginImages.DESC_TREE_CLASSPATH);
		if(obj instanceof IHQHelp)
			return get(HQDEPluginImages.DESC_TREE_HELP);
		if(obj instanceof IHQScript)
			return get(HQDEPluginImages.DESC_TREE_SCRIPT);
		if(obj instanceof IHQPlatform)
			return get(HQDEPluginImages.DESC_TREE_PLATFORM);
		if(obj instanceof PluginElementNode){
			PluginElementNode node = (PluginElementNode)obj;
			String name = node.getName();
			return getStructureImage(name);
		}
		return get(HQDEPluginImages.DESC_GENERIC_XML_OBJ);
	}
	
	public Image getStructureImage(String name) {
		return get(getStructureImageDescriptor(name));
	}

	public ImageDescriptor getStructureImageDescriptor(String name) {
		if(name.equals("metric"))
			return HQDEPluginImages.DESC_TREE_METRIC;
		else if(name.equals("service"))
			return HQDEPluginImages.DESC_TREE_SERVICE;
		else if(name.equals("help"))
			return HQDEPluginImages.DESC_TREE_HELP;
		else if(name.equals("plugin"))
			return HQDEPluginImages.DESC_TREE_PLUGIN;
		else if(name.equals("config"))
			return HQDEPluginImages.DESC_TREE_CONFIG;
		else if(name.equals("option"))
			return HQDEPluginImages.DESC_TREE_OPTION;
		else if(name.equals("scan"))
			return HQDEPluginImages.DESC_TREE_SCAN;
		else if(name.equals("include"))
			return HQDEPluginImages.DESC_TREE_INCLUDE;
		else if(name.equals("property"))
			return HQDEPluginImages.DESC_TREE_PROPERTY;
		else if(name.equals("filter"))
			return HQDEPluginImages.DESC_TREE_FILTER;
		else if(name.equals("properties"))
			return HQDEPluginImages.DESC_TREE_PROPERTIES;
		else if(name.equals("actions"))
			return HQDEPluginImages.DESC_TREE_ACTIONS;
		else if(name.equals("metrics"))
			return HQDEPluginImages.DESC_TREE_METRICS;
		else if(name.equals("script"))
			return HQDEPluginImages.DESC_TREE_SCRIPT;
		else if(name.equals("platform"))
			return HQDEPluginImages.DESC_TREE_PLATFORM;
		else if(name.equals("classpath"))
			return HQDEPluginImages.DESC_TREE_CLASSPATH;
		else if(name.equals("server"))
			return HQDEPluginImages.DESC_TREE_SERVER;
		else
			return HQDEPluginImages.DESC_GENERIC_XML_OBJ;

	}
	
	public String getText(Object obj) {
		if (obj instanceof HQEnvInfo) {
			return getObjectText((HQEnvInfo) obj);
		}
		return super.getText(obj);
	}

	private String getObjectText(HQEnvInfo environment) {
		return preventNull(environment.getName());
	}

	private String preventNull(String text) {
		return text != null ? text : "";
	}


	public Image getImage(Object obj) {
		if (obj instanceof HQEnvInfo) {
			return getObjectImage((HQEnvInfo) obj);
		}
		return super.getImage(obj);
	}
	
	private Image getObjectImage(HQEnvInfo environment) {
		return get(HQDEPluginImages.DESC_JAVA_LIB_OBJ);
	}

}
