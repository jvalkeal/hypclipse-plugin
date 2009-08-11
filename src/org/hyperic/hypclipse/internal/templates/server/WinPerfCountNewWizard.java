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

import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.templates.NewPluginTemplateWizard;
import org.hyperic.hypclipse.plugin.IFieldData;
import org.hyperic.hypclipse.plugin.ITemplateSection;

public class WinPerfCountNewWizard extends NewPluginTemplateWizard {

	public WinPerfCountNewWizard() {
		super();
	}
	
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle(HQDEMessages.WinPerfCountNewWizard_wtitle);
	}
	
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] {new WinPerfCountTemplate()};
	}


}
