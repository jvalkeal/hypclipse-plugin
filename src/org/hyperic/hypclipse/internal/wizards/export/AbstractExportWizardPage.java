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
package org.hyperic.hypclipse.internal.wizards.export;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;


/**
 * The Class AbstractExportWizardPage.
 */
public abstract class AbstractExportWizardPage extends WizardPage {

	/**
	 * Instantiates a new abstract export wizard page.
	 * 
	 * @param pageName the page name
	 */
	protected AbstractExportWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * Page changed.
	 */
	protected abstract void pageChanged();

	/**
	 * Save settings.
	 * 
	 * @param settings the settings
	 */
	protected abstract void saveSettings(IDialogSettings settings);


}
