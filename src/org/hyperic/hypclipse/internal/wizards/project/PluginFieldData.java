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
package org.hyperic.hypclipse.internal.wizards.project;

import java.util.ArrayList;

import org.hyperic.hypclipse.plugin.IPluginFieldData;
import org.hyperic.hypclipse.plugin.ITemplateSection;

public class PluginFieldData extends AbstractFieldData implements IPluginFieldData {

	private String fClassname;
	private boolean fIsUIPlugin = true;
	private boolean fDoGenerateClass = true;
	private boolean fRCPAppPlugin = false;
	private boolean fSetupAPITooling = false;
	private ArrayList<ITemplateSection> templates = new ArrayList<ITemplateSection>();

	public String getClassname() {
		return fClassname;
	}

	public void setClassname(String classname) {
		fClassname = classname;
	}

	public boolean isUIPlugin() {
		return fIsUIPlugin;
	}

	public void setUIPlugin(boolean isUIPlugin) {
		fIsUIPlugin = isUIPlugin;
	}

	public void addTemplate(ITemplateSection section) {
		if (!templates.contains(section))
			templates.add(section);
	}

	public ITemplateSection[] getTemplateSections() {
		return (ITemplateSection[]) templates.toArray(new ITemplateSection[templates.size()]);
	}

	public void setDoGenerateClass(boolean doGenerate) {
		fDoGenerateClass = doGenerate;
	}

	public boolean doGenerateClass() {
		return fDoGenerateClass;
	}

	public void setRCPApplicationPlugin(boolean isRCPAppPlugin) {
		fRCPAppPlugin = isRCPAppPlugin;
	}

	public boolean isRCPApplicationPlugin() {
		return fRCPAppPlugin;
	}

	/**
	 * @return whether API Tooling should be enabled in the plugin when created
	 */
	public boolean doEnableAPITooling() {
		return fSetupAPITooling;
	}

	/**
	 * Set whether API Tooling should be enabled in the plugin when created
	 * @param enable whether to enable API tooling
	 */
	public void setEnableAPITooling(boolean enable) {
		fSetupAPITooling = enable;
	}

}
