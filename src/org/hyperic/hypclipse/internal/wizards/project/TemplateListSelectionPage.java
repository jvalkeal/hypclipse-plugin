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
package org.hyperic.hypclipse.internal.wizards.project;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.elements.ElementList;
import org.hyperic.hypclipse.internal.wizards.WizardElement;
import org.hyperic.hypclipse.internal.wizards.WizardListSelectionPage;
import org.hyperic.hypclipse.internal.wizards.WizardNode;
import org.hyperic.hypclipse.plugin.IBasePluginWizard;
import org.hyperic.hypclipse.plugin.IPluginContentWizard;

public class TemplateListSelectionPage extends WizardListSelectionPage implements ISelectionChangedListener, IExecutableExtension {

	private ContentPage fContentPage;
	private Button fUseTemplate;
	private WizardElement defaultWizardElement;
	
	public TemplateListSelectionPage(ElementList wizardElements, WizardElement defaultWizardElement, ContentPage page, String message) {
		super(wizardElements, message);
		fContentPage = page;
		this.defaultWizardElement = defaultWizardElement;
		setTitle(HQDEMessages.WizardListSelectionPage_title);
		setDescription(HQDEMessages.WizardListSelectionPage_desc);
	}


	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.WizardListSelectionPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.NEW_PROJECT_CODE_GEN_PAGE);
	}
	
	public void createAbove(Composite container, int span) {
		fUseTemplate = new Button(container, SWT.CHECK);
		fUseTemplate.setText(HQDEMessages.WizardListSelectionPage_label);
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		fUseTemplate.setLayoutData(gd);
		fUseTemplate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				wizardSelectionViewer.getControl().setEnabled(fUseTemplate.getSelection());
				if (!fUseTemplate.getSelection())
					setDescription("");
				setDescriptionEnabled(fUseTemplate.getSelection());
				getContainer().updateButtons();
			}
		});
		fUseTemplate.setSelection(true);
	}

	
	protected IWizardNode createWizardNode(WizardElement element) {
		return new WizardNode(this, element) {
			public IBasePluginWizard createWizard() throws CoreException {
				IPluginContentWizard wizard = (IPluginContentWizard) wizardElement.createExecutableExtension();
				wizard.init(fContentPage.getData());
				wizard.initTemplates();
				return wizard;
			}
		};
	}

	public boolean isPageComplete() {
//		PluginFieldData data = (PluginFieldData) fContentPage.getData();
//		boolean rcp = data.isRCPApplicationPlugin();

//		return (fUseTemplate.getSelection() && getSelectedNode() != null);
		return true;
	}

	public boolean canFlipToNextPage() {
		IStructuredSelection ssel = (IStructuredSelection) wizardSelectionViewer.getSelection();
		return fUseTemplate.getSelection() && ssel != null && !ssel.isEmpty();
	}

	
	public void setVisible(boolean visible) {
		if (visible) {
			fContentPage.updateData();
			if (((PluginFieldData) fContentPage.getData()).isRCPApplicationPlugin()) {
				fUseTemplate.setSelection(true);
				fUseTemplate.setEnabled(false);
				wizardSelectionViewer.getControl().setEnabled(true);

			} else {
				fUseTemplate.setEnabled(true);
			}
			wizardSelectionViewer.refresh();
		}
		super.setVisible(visible);
	}

	public IPluginContentWizard createDefaultWizard() {
		if (defaultWizardElement == null)
			return null;
		return (IPluginContentWizard)createWizardNode(defaultWizardElement).getWizard();
	}

	

}
