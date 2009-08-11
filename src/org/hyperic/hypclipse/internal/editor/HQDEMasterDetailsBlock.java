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
package org.hyperic.hypclipse.internal.editor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.Section;

/**
 *  
 *
 */
public abstract class HQDEMasterDetailsBlock extends MasterDetailsBlock {

	private HQDEFormPage fPage;
	private HQDESection fSection;

	/**
	 * 
	 * @param page
	 */
	public HQDEMasterDetailsBlock(HQDEFormPage page) {
		fPage = page;
	}

	/**
	 * 
	 * @return
	 */
	public HQDEFormPage getPage() {
		return fPage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
	 */
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		Composite container = managedForm.getToolkit().createComposite(parent);
		container.setLayout(FormLayoutFactory.createMasterGridLayout(false, 1));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		fSection = createMasterSection(managedForm, container);
		managedForm.addPart(fSection);
		Section section = fSection.getSection();
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	/**
	 * 
	 * @param managedForm
	 * @param parent
	 * @return
	 */
	protected abstract HQDESection createMasterSection(IManagedForm managedForm, Composite parent);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createContent(org.eclipse.ui.forms.IManagedForm)
	 */
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);
		managedForm.getForm().getBody().setLayout(FormLayoutFactory.createFormGridLayout(false, 1));
	}

	/**
	 * 
	 * @return
	 */
	public DetailsPart getDetailsPart() {
		return detailsPart;
	}

}
