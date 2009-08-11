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
package org.hyperic.hypclipse.internal.templates.server;

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
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginReference;

public class WinPerfCountTemplate extends HQDETemplateSection {
	
	IPluginObject fParent;
	
	public static final String KEY_USE_FILTERS_NAME = "useFiltersName";
	public static final boolean USE_FILTERS_NAME = true;

	public WinPerfCountTemplate() {
		setPageCount(1);
		createOptions();		
	}
	
	private void createOptions() {
		addOption(KEY_USE_FILTERS_NAME, HQDEMessages.WinPerfCountTemplate_useFiltersName, true, 0);
	}
	
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_WIN_PERF_COUNT);
		page.setTitle(HQDEMessages.WinPerfCountTemplate_title);
		page.setDescription(HQDEMessages.WinPerfCountTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();

		IHQServer server = getBooleanOption(KEY_USE_FILTERS_NAME) ?
				getWithFilters() : getWithoutFilters();
		
		if(fParent != null && fParent instanceof PluginParentNode) {
			PluginParentNode parent = (PluginParentNode)fParent;
			parent.add(server);
		} else {
			plugin.add(server);
		}

	}
	
	private IHQServer getWithFilters() throws CoreException {
		IPluginModelFactory factory = model.getPluginFactory();
		IHQServer server = factory.createServer();
		
		server.setName("Windows Performance Counters");
		server.setPlatforms("Win32");
		
		IPluginElement element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "measurement");
		element.setAttribute("class", "org.hyperic.hq.product.MeasurementPlugin");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Availability");
		element.setAttribute("template", "win32:Service=Eventlog:Availability");
		element.setAttribute("indicator", "true");
		server.add(element);

		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "template");
		element.setAttribute("value", "win32:Object=${object}:${alias}");
		server.add(element);
		
		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "object");
		element.setAttribute("value", "Memory");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Memory pages/sec");
		element.setAttribute("alias", "Pages/sec");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Memory avail bytes");
		element.setAttribute("alias", "Available Bytes");
		server.add(element);

		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "object");
		element.setAttribute("value", "System");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Processor queue length");
		element.setAttribute("alias", "Processor Queue Length");
		server.add(element);

		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "object");
		element.setAttribute("value", "Processor");
		server.add(element);

		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "instance");
		element.setAttribute("value", "_Total");
		server.add(element);
		
		element = factory.createElement(server);
		element.setName("filter");
		element.setAttribute("name", "template");
		element.setAttribute("value", "win32:Object=${object},Instance=${instance}:${alias}");
		server.add(element);
		
		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Processor idle time");
		element.setAttribute("alias", "% Idle Time");
		server.add(element);
		
		return server;
	}
	
	private IHQServer getWithoutFilters() throws CoreException {
		IPluginModelFactory factory = model.getPluginFactory();
		IHQServer server = factory.createServer();
		
		server.setName("Windows Performance Counters");
		
		IPluginElement element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "measurement");
		element.setAttribute("class", "org.hyperic.hq.product.MeasurementPlugin");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Availability");
		element.setAttribute("template", "win32:Service=Eventlog:Availability");
		element.setAttribute("indicator", "true");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Memory pages/sec");
		element.setAttribute("template", "win32:Object=Memory:Pages/sec");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Memory avail bytes");
		element.setAttribute("template", "win32:Object=Memory:Available Bytes");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Processor Queue Length");
		element.setAttribute("template", "win32:Object=System:Processor Queue Length");
		server.add(element);

		element = factory.createElement(server);
		element.setName("metric");
		element.setAttribute("name", "Processor idle time");
		element.setAttribute("template", "win32:Object=Processor,Instance=_Total:% Idle Time");
		server.add(element);
		
		return server;
	}
	
	public void initializeFields(IPluginObject parent) {
		this.fParent = parent;
	}


	public String getSectionId() {
		return null;
	}

	protected ResourceBundle getPluginResourceBundle() {
		return null;
	}

	public IPluginReference[] getDependencies(String schemaVersion) {
		return null;
	}

	public String getUsedExtensionPoint() {
		return null;
	}

}
