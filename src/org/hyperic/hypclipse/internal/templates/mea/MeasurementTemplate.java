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
package org.hyperic.hypclipse.internal.templates.mea;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hyperic.hypclipse.internal.templates.HQDETemplateSection;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginReference;

public class MeasurementTemplate extends HQDETemplateSection {

	
	/**
	 * 
	 */
	public MeasurementTemplate() {
		createOptions();
	}
	
	private void createOptions() {
		
	}

	public String getSectionId() {
		return null;
	}

	

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
	}

	
	
	
	
	public IPluginReference[] getDependencies(String schemaVersion) {
		return null;
	}

	public String getUsedExtensionPoint() {
		return null;
	}

	protected ResourceBundle getPluginResourceBundle() {
		return null;
	}

	
}
