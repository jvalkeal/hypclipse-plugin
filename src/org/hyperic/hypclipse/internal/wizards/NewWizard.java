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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * 
 *
 */
public class NewWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
//	private Dictionary defaultValues;

	public NewWizard() {
		super();
		setWindowTitle("PDEUIMessages.NewWizard_wtitle");
	}

	public org.eclipse.jface.viewers.IStructuredSelection getSelection() {
		return selection;
	}

	public IWorkbench getWorkbench() {
		return workbench;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	public boolean performFinish() {
		return true;
	}

//	/*
//	 * 
//	 */
//	public String getDefaultValue(String key) {
//		if (defaultValues == null)
//			return null;
//		return (String) defaultValues.get(key);
//	}
//
//	/*
//	 * 
//	 */
//	public void init(Dictionary defaultValues) {
//		this.defaultValues = defaultValues;
//	}

}
