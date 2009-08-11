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
package org.hyperic.hypclipse.internal.templates.ai;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.templates.HQDETemplateSection;
import org.hyperic.hypclipse.internal.templates.RadioChoiceOption;
import org.hyperic.hypclipse.internal.templates.StringOption;
import org.hyperic.hypclipse.internal.templates.TemplateOption;
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

/**
 * 
 *
 */
public class AutoInventoryTemplate extends HQDETemplateSection {

	public static final String PROCESS_NAME = "processName";
	public static final String PTQL_NAME = "State.Name.eq=processName";
	public static final String KEY_QUERY_TYPE_NAME = "queryTypeName";
	public static final String KEY_PTQL_NAME = "ptqlName";
	private String[][] queryTypeChoices;
	private static final String fChoicePTQL = "ptql";
	private static final String fChoiceProcess = "process";
	
	private String pluginName;
	
	private RadioChoiceOption fFieldQueryType;
	private StringOption fFieldProcess;
	private StringOption fFieldPTQL;
	
	IPluginObject fParent;
	
	/**
	 * Constructor.
	 */
	public AutoInventoryTemplate() {
		fFieldQueryType = null;
		fFieldProcess = null;
		fFieldPTQL = null;
		queryTypeChoices = null;
		pluginName = null;
		initQueryTypes();
		setPageCount(1);
		createOptions();
	}
	
	private void initQueryTypes() {
		queryTypeChoices = new String[2][2];
		queryTypeChoices[0][0] = fChoiceProcess;
		queryTypeChoices[0][1] = HQDEMessages.AutoInventoryTemplate_ChoiceProcess_name;
		queryTypeChoices[1][0] = fChoicePTQL;
		queryTypeChoices[1][1] = HQDEMessages.AutoInventoryTemplate_ChoicePTQL_name;
	}

	/**
	 * Initializes options to wizardpage.
	 */
	private void createOptions() {
		fFieldQueryType = (RadioChoiceOption)addOption(KEY_QUERY_TYPE_NAME, HQDEMessages.AutoInventoryTemplate_queryTypeName, queryTypeChoices, queryTypeChoices[0][0], 0);
		fFieldProcess = (StringOption)addOption(KEY_PROCESS_NAME, HQDEMessages.AutoInventoryTemplate_processName, PROCESS_NAME, 0);
		fFieldPTQL = (StringOption)addOption(KEY_PTQL_NAME, HQDEMessages.AutoInventoryTemplate_ptqlName, PTQL_NAME, 0);
		
		fFieldPTQL.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.AbstractTemplateSection#addPages(org.eclipse.jface.wizard.Wizard)
	 */
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_AUTO_INVENTORY);
		page.setTitle(HQDEMessages.AutoInventoryTemplate_title);
		page.setDescription(HQDEMessages.AutoInventoryTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.OptionTemplateSection#getSectionId()
	 */
	public String getSectionId() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.OptionTemplateSection#validateOptions(org.hyperic.hypclipse.internal.templates.TemplateOption)
	 */
	public void validateOptions(TemplateOption source) {
		
		if(source == fFieldQueryType) {
			boolean set = fFieldQueryType.getChoice().equals(fChoiceProcess);
			fFieldPTQL.setEnabled(!set);
			fFieldProcess.setEnabled(set);
		}
		
		super.validateOptions(source);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection#isDependentOnParentWizard()
	 */
	public boolean isDependentOnParentWizard() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection#initializeFields(org.hyperic.hypclipse.plugin.IFieldData)
	 */
	protected void initializeFields(IFieldData data) {
		pluginName = ((PluginFieldData)data).getName();		
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection#initializeFields(org.hyperic.hypclipse.plugin.IPluginModelBase)
	 */
	public void initializeFields(IPluginModelBase model) {
		if(pluginName != null)
			pluginName = model.getPluginBase().getName();
	}

	public void initializeFields(IPluginObject parent) {
		this.fParent = parent;
	}


	protected ResourceBundle getPluginResourceBundle() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.AbstractTemplateSection#updateModel(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();
		
		// server
		IHQServer server = factory.createServer();
		server.setName(pluginName);
		
		// build-in daemon detector
		IPluginElement element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "autoinventory");
		element.setAttribute("class", "org.hyperic.hq.product.DaemonDetector");
		server.add(element);

		// measurement plugin
		element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "measurement");
		element.setAttribute("class", "org.hyperic.hq.product.MeasurementPlugin");
		server.add(element);

		// property for proc query for autodiscovery
		element = factory.createElement(server);
		element.setName("property");
		element.setAttribute("name", "PROC_QUERY");
		if(fFieldQueryType.getChoice().equals(fChoiceProcess))
			element.setAttribute("value", "State.Name.eq=" + getStringOption(KEY_PROCESS_NAME));
		else
			element.setAttribute("value", getStringOption(KEY_PTQL_NAME));
		server.add(element);
		
		
		//config
		IPluginElement cElement = factory.createElement(server);
		cElement.setName("config");

		IPluginElement oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "process.query");
		oElement.setAttribute("description", "Process Query for " + pluginName);
		if(fFieldQueryType.getChoice().equals(fChoiceProcess))
			oElement.setAttribute("default", "State.Name.eq=" + getStringOption(KEY_PROCESS_NAME));
		else
			oElement.setAttribute("default", getStringOption(KEY_PTQL_NAME));
			
		cElement.add(oElement);
		
		server.add(cElement);

		// if we are creating new project, add reference to metrics
		if(fParent == null) {
			// metrics reference coming from other template
			IPluginElement mElement = factory.createElement(server);
			mElement.setName("metrics");
			mElement.setAttribute("include", "basic-process-metrics");
			server.add(mElement);			
		}
		
		if(fParent != null && fParent instanceof PluginParentNode) {
			PluginParentNode parent = (PluginParentNode)fParent;
			parent.add(server);
		} else {
			plugin.add(server);
		}

	}

	public IPluginReference[] getDependencies(String schemaVersion) {
		return null;
	}

	public String getUsedExtensionPoint() {
		return null;
	}

}
