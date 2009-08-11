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
package org.hyperic.hypclipse.internal.templates.platserv;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.templates.HQDETemplateSection;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.internal.util.HQDELabelUtility;
import org.hyperic.hypclipse.internal.wizards.project.PluginFieldData;
import org.hyperic.hypclipse.plugin.IFieldData;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginReference;

public class CustomPlatformServiceTemplate extends HQDETemplateSection {

	public static final String KEY_COLLECTOR_CLASS_NAME = "collectorClassName";
	public static final String KEY_DETECTOR_CLASS_NAME = "detectorClassName";

	private IPluginObject fParent;
	private String pluginName;

	public CustomPlatformServiceTemplate() {
		setPageCount(1);
		createOptions();
		this.pluginName = null;
	}
	
	private void createOptions() {
		addOption(KEY_PACKAGE_NAME, HQDEMessages.CustomPlatformServiceTemplate_packageName, (String) null, 0);
		addOption(KEY_COLLECTOR_CLASS_NAME, HQDEMessages.CustomPlatformServiceTemplate_collectorClassName, (String) null, 0);
		addOption(KEY_DETECTOR_CLASS_NAME, HQDEMessages.CustomPlatformServiceTemplate_detectorClassName, (String) null, 0);
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_CUSTOM_PLATFORM_SERVICE);
		page.setTitle(HQDEMessages.CustomPlatformServiceTemplate_title);
		page.setDescription(HQDEMessages.CustomPlatformServiceTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}

	public String getSectionId() {
		return "collectorPlugin";
	}

	public boolean isDependentOnParentWizard() {
		return true;
	}

	protected void initializeFields(IFieldData data) {
		pluginName = ((PluginFieldData)data).getName();
		initializeOption(KEY_COLLECTOR_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(data.getName()) + "Collector");
		initializeOption(KEY_DETECTOR_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(data.getName()) + "Detector");
		initializeOption(KEY_PACKAGE_NAME, data.getPackageName());
		initializeOption(KEY_PLUGIN_NAME, data.getName());
		
	}
	
	public void initializeFields(IPluginModelBase model) {
		String packageName = model.getPluginBase().getPackage();
		if(pluginName != null)
			pluginName = model.getPluginBase().getName();
		initializeOption(KEY_PACKAGE_NAME, packageName);
		initializeOption(KEY_COLLECTOR_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(pluginName) + "Collector");
		initializeOption(KEY_DETECTOR_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(pluginName) + "Detector");
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection#initializeFields(org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public void initializeFields(IPluginObject parent) {
		this.fParent = parent;
	}


	protected ResourceBundle getPluginResourceBundle() {
		return null;
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();

		// filter
		IPluginElement element = factory.createElement(plugin);
		element.setName("filter");
		element.setAttribute("name", "template");
		element.setAttribute("value", pluginName + ":newline=%newline%,stringexpect=%stringexpect%,stringtowrite=%stringtowrite%,hostname=%hostname%,port=%port%,timeout=%timeout%:${alias}");
		plugin.add(element);
		
		// metrics
		IPluginElement metrics = factory.createElement(plugin);
		metrics.setName("metrics");
		metrics.setAttribute("name", pluginName + "-metrics");

		IPluginElement metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Availability");
		metric.setAttribute("indicator", "true");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Response Waiting Time");
		metric.setAttribute("alias", "ResponseWaitingTime");
		metric.setAttribute("defaultOn", "true");
		metric.setAttribute("units", "ms");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Connection Waiting Time");
		metric.setAttribute("alias", "ConnectionWaitingTime");
		metric.setAttribute("defaultOn", "false");
		metric.setAttribute("units", "ms");
		metrics.add(metric);
		
		plugin.add(metrics);

		// server
		IHQServer server = factory.createServer();
		server.setName(HQDELabelUtility.CapitalizeFirstLetter(pluginName) + " Services");
		server.setVirtual("true");

		// plugin
		IPluginElement pElement = factory.createElement(server);
		pElement.setName("plugin");
		pElement.setAttribute("type", "autoinventory");
		pElement.setAttribute("class", getStringOption(KEY_DETECTOR_CLASS_NAME));
		server.add(pElement);


		// service
		IPluginElement servElement = factory.createElement(server);
		servElement.setName("service");
		String name = HQDELabelUtility.CapitalizeFirstLetter(pluginName);
		servElement.setAttribute("name", name + " Service");
		servElement.setAttribute("description", name + " Service");

		IPluginElement epElement = factory.createElement(servElement);
		epElement.setName("plugin");
		epElement.setAttribute("class", getStringOption(KEY_COLLECTOR_CLASS_NAME));
		epElement.setAttribute("type", "collector");
		servElement.add(epElement);

		IPluginElement metricsElement = factory.createElement(servElement);
		metricsElement.setName("metrics");
		metricsElement.setAttribute("include", pluginName + "-metrics");
		servElement.add(metricsElement);

		// config
		IPluginElement cElement = factory.createElement(element);
		cElement.setName("config");

		// option1
		IPluginElement oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "hostname");
		oElement.setAttribute("description", "Hostname");
		oElement.setAttribute("default", "localhost");
		oElement.setAttribute("type", "string");
		cElement.add(oElement);		

		// option2
		oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "port");
		oElement.setAttribute("description", "Port");
		oElement.setAttribute("default", "80");
		oElement.setAttribute("type", "int");
		cElement.add(oElement);		

		// option3
		oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "stringtowrite");
		oElement.setAttribute("description", "String to write after connected");
		oElement.setAttribute("type", "string");
		cElement.add(oElement);		

		// option4
		oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "stringexpect");
		oElement.setAttribute("description", "Regex pattern to match against for received output");
		oElement.setAttribute("type", "string");
		cElement.add(oElement);		

		// option5
		oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "timeout");
		oElement.setAttribute("description", "Timeout for connection");
		oElement.setAttribute("type", "int");
		cElement.add(oElement);		

		// option6
		oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "newline");
		oElement.setAttribute("description", "Line termination");
		oElement.setAttribute("type", "enum");
		
		IPluginElement iElement = factory.createElement(oElement);
		iElement.setName("include");
		iElement.setAttribute("name", "LF");
		oElement.add(iElement);		

		iElement = factory.createElement(oElement);
		iElement.setName("include");
		iElement.setAttribute("name", "CRLF");
		oElement.add(iElement);		

		iElement = factory.createElement(oElement);
		iElement.setName("include");
		iElement.setAttribute("name", "CR");
		oElement.add(iElement);		

		cElement.add(oElement);		

		servElement.add(cElement);
		
		
		server.add(servElement);

		plugin.add(server);
		
//		if(fParent != null && fParent instanceof PluginParentNode) {
//			PluginParentNode parent = (PluginParentNode)fParent;
//			parent.add(server);
//		} else {
//			plugin.add(server);
//		}
	}

// <server
//       name="pserv"
//       virtual="true">
//    <service
//          description="Test Platform Service"
//          name="Test Plat Service">
//       <plugin
//             class="PsCollector"
//             type="collector">
//       </plugin>
//       <metrics
//             include="ps-metrics">
//       </metrics>
//    </service>
// </server>

	
	public IPluginReference[] getDependencies(String schemaVersion) {
		return null;
	}

	public String getUsedExtensionPoint() {
		return null;
	}

}
