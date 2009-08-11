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
package org.hyperic.hypclipse.internal.preferences;

import org.eclipse.jface.wizard.Wizard;
import org.hyperic.hypclipse.internal.HQDEMessages;

/**
 * 
 *
 */
public class HQEnvWizard extends Wizard {

	private String[] fExistingNames;

	private HQEnvPage page;
	
	private String[] names;
	
	public HQEnvWizard(String[] names) {
		setWindowTitle(HQDEMessages.HQEnvWizard_wtitle);
		this.names = names;
	}

	/**
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		page = new HQEnvPage("");
		page.setExistingNames(names);
		addPage(page);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		page.finish();
		return true;
	}

	public HQEnvInfo getResult() {
		return page.getSelection();
	}
}
