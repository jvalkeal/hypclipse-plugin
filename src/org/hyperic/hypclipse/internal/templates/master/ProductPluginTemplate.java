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
package org.hyperic.hypclipse.internal.templates.master;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.templates.HQDETemplateSection;
import org.hyperic.hypclipse.plugin.IFieldData;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginReference;

public class ProductPluginTemplate extends HQDETemplateSection {

	public static final String KEY_CLASS_NAME = "className";
	public static final String CLASS_NAME = "MyProductPlugin";


	public ProductPluginTemplate() {
		setPageCount(0);
		createOptions();
	}
	
	private void createOptions() {
//		addOption(KEY_PACKAGE_NAME, HQDEMessages.AutoInventoryTemplate_packageName, (String) null, 0);
//		addOption(KEY_CLASS_NAME, HQDEMessages.AutoInventoryTemplate_className, (String) null, 0);

	}
	public String getSectionId() {
		return "productPlugin";
	}

	protected ResourceBundle getPluginResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
//		model.getPluginBase().setProductPlugin(getStringOption(KEY_CLASS_NAME));
	}
	
	public boolean isDependentOnParentWizard() {
		return true;
	}

	public IPluginReference[] getDependencies(String schemaVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsedExtensionPoint() {
		// TODO Auto-generated method stub
		return null;
	}
	protected void initializeFields(IFieldData data) {
		initializeOption(KEY_PACKAGE_NAME, data.getPackageName());
		initializeOption(KEY_CLASS_NAME, data.getName() + "ProductPlugin");
	}
	
	public void initializeFields(IPluginModelBase model) {
		String packageName = model.getPluginBase().getPackage();
		String name = model.getPluginBase().getName();
		initializeOption(KEY_PACKAGE_NAME, packageName);
		initializeOption(KEY_CLASS_NAME, model.getPluginBase().getProductPlugin());

	}

}
