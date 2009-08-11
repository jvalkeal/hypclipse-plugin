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
package org.hyperic.hypclipse.internal.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.graphics.Point;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.plugin.IBasePluginWizard;

public abstract class WizardNode implements IWizardNode {
	private IWizard wizard;
	private WizardSelectionPage parentWizardPage;
	protected WizardElement wizardElement;

	public WizardNode(WizardSelectionPage parentPage, WizardElement element) {
		parentWizardPage = parentPage;
		wizardElement = element;
	}

	protected abstract IBasePluginWizard createWizard() throws CoreException;

	public void dispose() {
		if (wizard != null) {
			wizard.dispose();
			wizard = null;
		}
	}

	public WizardElement getElement() {
		return wizardElement;
	}

	public Point getExtent() {
		return new Point(-1, -1);
	}

	public IWizard getWizard() {
		if (wizard != null)
			return wizard; // we've already created it

		IBasePluginWizard pluginWizard;
		try {
			pluginWizard = createWizard(); // create instance of target wizard
		} catch (CoreException e) {
			if (parentWizardPage instanceof BaseWizardSelectionPage)
				((BaseWizardSelectionPage) parentWizardPage).setDescriptionText(""); //$NON-NLS-1$
			HQDEPlugin.logException(e);
			parentWizardPage.setErrorMessage(HQDEMessages.Errors_CreationError_NoWizard);
			MessageDialog.openError(parentWizardPage.getWizard().getContainer().getShell(), HQDEMessages.Errors_CreationError, HQDEMessages.Errors_CreationError_NoWizard);
			return null;
		}
		wizard = pluginWizard;
		//wizard.setUseContainerState(false);
		return wizard;
	}

	public boolean isContentCreated() {
		return wizard != null;
	}
}
