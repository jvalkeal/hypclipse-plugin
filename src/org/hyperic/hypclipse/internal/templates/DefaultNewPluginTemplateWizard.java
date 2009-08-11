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
package org.hyperic.hypclipse.internal.templates;

import org.hyperic.hypclipse.plugin.IFieldData;
import org.hyperic.hypclipse.plugin.ITemplateSection;

/**
 * This is a default implementation of template wizard. 
 * Template wizard framework in new plug-in wizard is executing
 * wizard selected by user. User has an option not to use any 
 * wizards during project creation. If this is the case, 
 * this default implementation is executed.
 * 
 * There is a possibility that some content must be created
 * using templates and if no wizards are executed, template
 * framework is not executed.
 * 
 * Example of available template is a product plugin, which 
 * usage is asked in before any wizards/templates are introduced.
 */
public class DefaultNewPluginTemplateWizard extends NewPluginTemplateWizard {

	public DefaultNewPluginTemplateWizard() {
		super();
	}
	
	public void init(IFieldData data) {
		super.init(data);
	}

	public ITemplateSection[] createTemplateSections() {
		return null;
	}
	
}
