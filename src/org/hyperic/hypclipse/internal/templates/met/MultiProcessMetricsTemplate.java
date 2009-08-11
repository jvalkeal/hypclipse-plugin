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
package org.hyperic.hypclipse.internal.templates.met;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.templates.HQDETemplateSection;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginReference;

/**
 * This template adds <metrics> section to model. These metrics can
 * be used to do basic single process monitoring. 
 *
 */
public class MultiProcessMetricsTemplate extends HQDETemplateSection {

	IPluginObject fParent;
	
	public static final String KEY_SIGAR_ARG_NAME = "sigarArgName";
	public static final String SIGAR_ARG_NAME = "process.query";

	
	public MultiProcessMetricsTemplate() {
		setPageCount(1);
		createOptions();
	}
	
	private void createOptions() {
		addOption(KEY_SIGAR_ARG_NAME, HQDEMessages.MultiProcessMetricsTemplate_sigarArgName, SIGAR_ARG_NAME, 0);
	}

	
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_MULTI_PROCESS_METRICS);
		page.setTitle(HQDEMessages.MultiProcessMetricsTemplate_title);
		page.setDescription(HQDEMessages.MultiProcessMetricsTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}
	

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();
		
		String sigarArg = getStringOption(KEY_SIGAR_ARG_NAME);
		
		IPluginElement metrics = factory.createElement(plugin);
//		IHQMetrics metrics = factory.createMetrics();
		metrics.setName("metrics");
		metrics.setAttribute("name", "multi-process-metrics");
		
		IPluginElement metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Availability");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:Availability");
		metric.setAttribute("category", "AVAILABILITY");
		metric.setAttribute("indicator", "true");
		metric.setAttribute("units", "percentage");
		metric.setAttribute("collectionType", "dynamic");		
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Number of Processes");
		metric.setAttribute("alias", "NumProcesses");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:Processes");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "none");
		metric.setAttribute("collectionType", "dynamic");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Memory Size");
		metric.setAttribute("alias", "MemSize");
		metric.setAttribute("template", "sigar:Type=MultiProcMem,Arg=%" + sigarArg + "%:Size");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "B");
		metric.setAttribute("collectionType", "dynamic");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Resident Memory Size");
		metric.setAttribute("alias", "ResidentMemSize");
		metric.setAttribute("template", "sigar:Type=MultiProcMem,Arg=%" + sigarArg + "%:Resident");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "B");
		metric.setAttribute("collectionType", "dynamic");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Cpu System Time");
		metric.setAttribute("alias", "SystemTime");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:Sys");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Cpu User Time");
		metric.setAttribute("alias", "UserTime");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:User");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Cpu Total Time");
		metric.setAttribute("alias", "TotalTime");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:Total");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Cpu Usage");
		metric.setAttribute("alias", "Usage");
		metric.setAttribute("template", "sigar:Type=MultiProcCpu,Arg=%" + sigarArg + "%:Percent");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("indicator", "true");
		metric.setAttribute("units", "percentage");
		metric.setAttribute("collectionType", "dynamic");
		metrics.add(metric);
		
		
		// we need to know the parent tag where
		// to place these metrics.
		if(fParent != null && fParent instanceof PluginParentNode) {
			PluginParentNode parent = (PluginParentNode)fParent;
			parent.add(metrics);
		} else {
			plugin.add(metrics);
		}



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
