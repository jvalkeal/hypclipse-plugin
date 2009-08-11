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
public class BasicProcessMetricsTemplate extends HQDETemplateSection {

	IPluginObject fParent;
	
	public static final String KEY_SIGAR_ARG_NAME = "sigarArgName";
	public static final String SIGAR_ARG_NAME = "process.query";

	
	public BasicProcessMetricsTemplate() {
		setPageCount(1);
		createOptions();
	}
	
	private void createOptions() {
		addOption(KEY_SIGAR_ARG_NAME, HQDEMessages.BasicProcessMetricsTemplate_sigarArgName, SIGAR_ARG_NAME, 0);
	}

	
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_BASIC_PROCESS_METRICS);
		page.setTitle(HQDEMessages.BasicProcessMetricsTemplate_title);
		page.setDescription(HQDEMessages.BasicProcessMetricsTemplate_desc);
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
		metrics.setAttribute("name", "basic-process-metrics");
		
		IPluginElement metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Availability");
//		metric.setAttribute("alias", "Availability");
		metric.setAttribute("template", "sigar:Type=ProcState,Arg=%" + sigarArg + "%:State");
		metric.setAttribute("category", "AVAILABILITY");
		metric.setAttribute("indicator", "true");
		metric.setAttribute("units", "percentage");
		metric.setAttribute("collectionType", "dynamic");		
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Virtual Memory Size");
//		metric.setAttribute("alias", "VirtualMemSize");
		metric.setAttribute("template", "sigar:Type=ProcMem,Arg=%" + sigarArg + "%:Size");
		metric.setAttribute("category", "UTILIZATION");
		metric.setAttribute("indicator", "true");
		metric.setAttribute("units", "B");
		metric.setAttribute("collectionType", "dynamic");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Resident Memory Size");
		metric.setAttribute("template", "sigar:Type=ProcMem,Arg=%" + sigarArg + "%:Resident");
		metric.setAttribute("units", "B");
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Page Faults");
		metric.setAttribute("template", "sigar:Type=ProcMem,Arg=%" + sigarArg + "%:PageFaults");
		metric.setAttribute("collectionType", "trendsup");		
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Cpu System Time");
		metric.setAttribute("template", "sigar:Type=ProcCpu,Arg=%" + sigarArg + "%:Sys");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");		
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Cpu User Time");
		metric.setAttribute("template", "sigar:Type=ProcCpu,Arg=%" + sigarArg + "%:User");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");		
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Cpu Total Time");
		metric.setAttribute("template", "sigar:Type=ProcCpu,Arg=%" + sigarArg + "%:Total");
		metric.setAttribute("units", "ms");
		metric.setAttribute("collectionType", "trendsup");		
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Cpu Usage");
		metric.setAttribute("template", "sigar:Type=ProcCpu,Arg=%" + sigarArg + "%:Percent");
		metric.setAttribute("indicator", "true");
		metric.setAttribute("units", "percentage");		
		metrics.add(metric);

		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Start Time");
		metric.setAttribute("template", "sigar:Type=ProcTime,Arg=%" + sigarArg + "%:StartTime");
		metric.setAttribute("category", "AVAILABILITY");
		metric.setAttribute("units", "epoch-millis");
		metric.setAttribute("collectionType", "static");		
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Open File Descriptors");
		metric.setAttribute("template", "sigar:Type=ProcFd,Arg=%" + sigarArg + "%:Total");
		metrics.add(metric);
		
		metric = factory.createElement(metrics);
		metric.setName("metric");
		metric.setAttribute("name", "Process Threads");
		metric.setAttribute("template", "sigar:Type=ProcState,Arg=%" + sigarArg + "%:Threads");
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
