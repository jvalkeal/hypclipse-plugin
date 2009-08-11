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
import org.hyperic.hypclipse.internal.templates.ComboChoiceOption;
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
 *  Processes to detect:
 *   [Custom PTQL]
 *   [Process Starts]
 *   [Process Contains]
 *   [Process Ends]
 *   [Process Owner]
 *    
 *  Process Tag:  {                           }
 *  PTQL:         {                           }
 *
 */
public class CustomAutoInventoryTemplate extends HQDETemplateSection {

	public static final String KEY_CLASS_NAME = "className";
	public static final String KEY_QUERY_TYPE_NAME = "queryTypeName";
	public static final String KEY_CUSTOM_PTQL_NAME = "customPtqlName";
	public static final String KEY_PTQL_NAME = "ptqlName";
	
	public static final String CLASS_NAME = "MyServerDetector";
	public static final String PROCESS_NAME = "processName";
	public static final String PTQL_NAME = "State.Name.eq=processName";
	
	private static final String CHOICE_PTQL = "ptql";
	private static final String CHOICE_PROCESS_STARTS = "pstarts";
	private static final String CHOICE_PROCESS_CONTAINS = "pcontains";
	private static final String CHOICE_PROCESS_ENDS = "pends";
	private static final String CHOICE_USER = "user";

	IPluginObject fParent;
	
	private String[][] queryTypeChoices;
	private String pluginName;

	private StringOption fFieldProcess;
	private StringOption fFieldCustomPTQL;
	private StringOption fFieldPTQL;
	private ComboChoiceOption fFieldQueryType;
	
	/**
	 * Constructor.
	 */
	public CustomAutoInventoryTemplate() {
		fFieldQueryType = null;
		fFieldProcess = null;
		fFieldCustomPTQL = null;
		fFieldPTQL = null;
		queryTypeChoices = null;
		pluginName = null;
		initQueryTypes();
		setPageCount(1);
		createOptions();
	}
	
	private void initQueryTypes() {
		queryTypeChoices = new String[5][2];
		queryTypeChoices[0][0] = CHOICE_PROCESS_STARTS;
		queryTypeChoices[0][1] = HQDEMessages.CustomAutoInventoryTemplate_ChoiceProcessStarts_name;
		queryTypeChoices[1][0] = CHOICE_PROCESS_CONTAINS;
		queryTypeChoices[1][1] = HQDEMessages.CustomAutoInventoryTemplate_ChoiceProcessContains_name;
		queryTypeChoices[2][0] = CHOICE_PROCESS_ENDS;
		queryTypeChoices[2][1] = HQDEMessages.CustomAutoInventoryTemplate_ChoiceProcessEnds_name;
		queryTypeChoices[3][0] = CHOICE_USER;
		queryTypeChoices[3][1] = HQDEMessages.CustomAutoInventoryTemplate_ChoiceUser_name;
		queryTypeChoices[4][0] = CHOICE_PTQL;
		queryTypeChoices[4][1] = HQDEMessages.CustomAutoInventoryTemplate_ChoicePTQL_name;
	}


	/**
	 * Initializes options to wizardpage.
	 */
	private void createOptions() {
		addOption(KEY_PACKAGE_NAME, HQDEMessages.CustomAutoInventoryTemplate_packageName, (String) null, 0);
		addOption(KEY_CLASS_NAME, HQDEMessages.CustomAutoInventoryTemplate_className, (String) null, 0);
		fFieldQueryType = addComboChoiceOption(KEY_QUERY_TYPE_NAME, HQDEMessages.CustomAutoInventoryTemplate_queryTypeName, queryTypeChoices, queryTypeChoices[0][0], 0);
		fFieldProcess = (StringOption)addOption(KEY_PROCESS_NAME, HQDEMessages.CustomAutoInventoryTemplate_processName, PROCESS_NAME, 0);
		fFieldCustomPTQL = (StringOption)addOption(KEY_CUSTOM_PTQL_NAME, HQDEMessages.CustomAutoInventoryTemplate_customPtqlName, PTQL_NAME, 0);
		fFieldCustomPTQL.setEnabled(false);
		fFieldPTQL = (StringOption)addOption(KEY_PTQL_NAME, HQDEMessages.CustomAutoInventoryTemplate_ptqlName, PTQL_NAME, 0);
		fFieldPTQL.setReadOnly(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.AbstractTemplateSection#addPages(org.eclipse.jface.wizard.Wizard)
	 */
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_CUSTOM_AUTO_INVENTORY);
		page.setTitle(HQDEMessages.CustomAutoInventoryTemplate_title);
		page.setDescription(HQDEMessages.CustomAutoInventoryTemplate_desc);
		wizard.addPage(page);
		markPagesAdded();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.OptionTemplateSection#getSectionId()
	 */
	public String getSectionId() {
		return "autoInventory";
	}

	public void validateOptions(TemplateOption source) {
		
		if(source == fFieldQueryType) {
			boolean set = fFieldQueryType.getChoice().equals(CHOICE_PTQL);
			fFieldCustomPTQL.setEnabled(set);
			fFieldProcess.setEnabled(!set);			
		}
		fFieldPTQL.setText(getPTQLQuery());
		
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
		initializeOption(KEY_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(data.getName()) + "Detector");
		initializeOption(KEY_PACKAGE_NAME, data.getPackageName());
		initializeOption(KEY_PLUGIN_NAME, data.getName());
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.BaseOptionTemplateSection#initializeFields(org.hyperic.hypclipse.plugin.IPluginModelBase)
	 */
	public void initializeFields(IPluginModelBase model) {
		String packageName = model.getPluginBase().getPackage();
		if(pluginName != null)
			pluginName = model.getPluginBase().getName();
		initializeOption(KEY_PACKAGE_NAME, packageName);
		initializeOption(KEY_CLASS_NAME, HQDELabelUtility.CapitalizeFirstLetter(pluginName) + "Detector");
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

	/**
	 * Creates PTQL query based on informatin what user has entered.
	 * 
	 * @return PTQL query string.
	 */
	private String getPTQLQuery() {
		String type = fFieldQueryType.getChoice();
		
		if(type.equals(CHOICE_PTQL))
			return getStringOption(KEY_CUSTOM_PTQL_NAME);
		else if(type.equals(CHOICE_PROCESS_STARTS))
			return "State.Name.sw=" + getStringOption(KEY_PROCESS_NAME);
		else if(type.equals(CHOICE_PROCESS_CONTAINS))
			return "State.Name.ct=" + getStringOption(KEY_PROCESS_NAME);
		else if(type.equals(CHOICE_PROCESS_ENDS))
			return "State.Name.ew=" + getStringOption(KEY_PROCESS_NAME);
		else if(type.equals(CHOICE_USER))
			return "CredName.User.eq=" + getStringOption(KEY_PROCESS_NAME);
		else
			return PTQL_NAME; // should never be here
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
		
		// autoinventory plugin
		IPluginElement element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "autoinventory");
		element.setAttribute("class", getStringOption(KEY_CLASS_NAME));
		server.add(element);

		// measurement plugin
		element = factory.createElement(server);
		element.setName("plugin");
		element.setAttribute("type", "measurement");
		element.setAttribute("class", "org.hyperic.hq.product.MeasurementPlugin");
		server.add(element);

		//config
		IPluginElement cElement = factory.createElement(server);
		cElement.setName("config");

		IPluginElement oElement = factory.createElement(cElement);
		oElement.setName("option");
		oElement.setAttribute("name", "process.query");
		oElement.setAttribute("description", "Process Query for " + pluginName);
		oElement.setAttribute("default", getStringOption(KEY_PTQL_NAME));
		cElement.add(oElement);		
		server.add(cElement);

		// if we are creating new project, add reference to metrics
		if(fParent == null) {
			// metrics reference coming from other template
			IPluginElement mElement = factory.createElement(server);
			mElement.setName("metrics");
			mElement.setAttribute("include", "multi-process-metrics");
			server.add(mElement);			
		}

		IPluginElement sElement = factory.createElement(server);
		sElement.setName("service");
		sElement.setAttribute("name", "Process");
		server.add(sElement);

		IPluginElement c2Element = factory.createElement(server);
		c2Element.setName("config");
		
		IPluginElement o2Element = factory.createElement(c2Element);
		o2Element.setName("option");
		o2Element.setAttribute("name", "process.query");
		o2Element.setAttribute("default", "");
		o2Element.setAttribute("description", "Process Query for " + pluginName + " Process");
		c2Element.add(o2Element);		
		
		sElement.add(c2Element);
		
		IPluginElement m2Element = factory.createElement(server);
		m2Element.setName("metric");
		m2Element.setAttribute("name", "Availability");
		m2Element.setAttribute("template", "sigar:Type=ProcState,Arg=%process.query%:State");
		m2Element.setAttribute("indicator", "true");
		sElement.add(m2Element);

		if(fParent == null) {
			IPluginElement mElement = factory.createElement(server);
			mElement.setName("metrics");
			mElement.setAttribute("include", "basic-process-metrics");
			sElement.add(mElement);						
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
