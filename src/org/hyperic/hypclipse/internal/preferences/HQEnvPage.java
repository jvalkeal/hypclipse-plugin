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

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.SWTFactory;
import org.hyperic.hypclipse.internal.parts.StatusInfo;

/**
 *  
 *
 */
public class HQEnvPage extends WizardPage {


	private IStatus fStatus = Status.OK_STATUS;
	
	/**
	 * List of existing agent names. This list is used
	 * to check whether given agent name already exists.
	 */
	private String[] fExistingNames;

	/** Holder for agent bundle directory */
	private Text fAgentRoot;
	
	/** Holder for agent name */
	private Text fAgentName;

	/** Info for environment is stored to this handle */
	private HQEnvInfo fEnv;
	
	/**
	 * Default constructor.
	 */
	protected HQEnvPage(String pageName) {
		super(pageName);
		setTitle(HQDEMessages.HQEnvPage_title);
	}
	
	/**
	 * Fills this page with given information.
	 * @param env Environment info
	 */
	public void setSelection(HQEnvInfo env) {
		if(env == null) {
			fAgentName.clearSelection();
			fAgentRoot.clearSelection();			
		} else {
			fAgentName.setText(env.getName());
			fAgentRoot.setText(env.getLocation());
		}
	}
	
	/**
	 * Returns stored environment info.
	 * @return Environment info
	 */
	public HQEnvInfo getSelection() {
		return fEnv;
	}
	
	/**
	 * Parent wizard is calling this method before page is 
	 * disposed. Store needed form info in this method. SWT
	 * widgets may not be available after page is disposed.
	 */
	public void finish() {
		fEnv = new HQEnvInfo(fAgentName.getText(), fAgentRoot.getText());
	}
	
	/**
	 * Sets list of existing agent names.
	 * @param names List of agent names.
	 */
	public void setExistingNames(String[] names) {
		fExistingNames = names;
	}

	
	/**
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite p) {
		Composite composite = new Composite(p, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		SWTFactory.createLabel(composite, HQDEMessages.HQEnvPage_agenthome, 1);
		fAgentRoot = SWTFactory.createSingleText(composite, 1);
		Button folders = SWTFactory.createPushButton(composite, HQDEMessages.HQEnvPage_dirbutton, null);
		GridData data = (GridData) folders.getLayoutData();
		data.horizontalAlignment = GridData.END;

		SWTFactory.createLabel(composite, HQDEMessages.HQEnvPage_agentname, 1);
		fAgentName = SWTFactory.createSingleText(composite, 2);
		

		fAgentName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
//				validateAgentName();
				validateFields();
			}
		});
		fAgentRoot.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
//				validateAgentLocation();
				validateFields();
			}
		});
		folders.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				File file = new File(fAgentRoot.getText());
				String text = fAgentRoot.getText();
				if (file.isFile()) {
					text = file.getParentFile().getAbsolutePath();
				}
				dialog.setFilterPath(text);
				dialog.setMessage("Select Agent directory"); 
				String newPath = dialog.open();
				if (newPath != null) {
					fAgentRoot.setText(newPath);
					
					// determine new agent name for path.
					// just strip last segment of the dir path.
					Path p = new Path(newPath);
					if(p != null) {						
						String nameProposal = p.lastSegment();
						if(nameProposal != null)
							fAgentName.setText(nameProposal);
					}
				}
			}
		});
		
		Dialog.applyDialogFont(composite);
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.PREFERENCES_NEW_HQENV_PAGE);
		
		// since field are empty on new dialog,
		// just call location validation to 
		// get correct dialog status.
		validateFields();
	}

	/**
	 * Sets status message of this page.
	 * @param status
	 */
	protected void setStatusMessage(IStatus status) {
		if (status.isOK()) {
			setMessage(status.getMessage());
		} else {
			switch (status.getSeverity()) {
			case IStatus.ERROR:
				setMessage(status.getMessage(), IMessageProvider.ERROR);
				break;
			case IStatus.INFO:
				setMessage(status.getMessage(), IMessageProvider.INFORMATION);
				break;
			case IStatus.WARNING:
				setMessage(status.getMessage(), IMessageProvider.WARNING);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Update status message. Check if page can be finished.
	 */
	protected void updatePageStatus() {
		IStatus max = Status.OK_STATUS;
		IStatus[] envStatus = getEnvStatus();
		for (int i = 0; i < envStatus.length; i++) {
			IStatus status = envStatus[i];
			if (status.getSeverity() > max.getSeverity()) {
				max = status;
			}
		}
		if (fStatus.getSeverity() > max.getSeverity()) {
			max = fStatus;
		}
		if (max.isOK()) {
			setMessage(null, IMessageProvider.NONE);
		} else {
			setStatusMessage(max);
		}
		setPageComplete(max.isOK() || max.getSeverity() == IStatus.INFO);
	}
	
	private void validateFields() {
		IStatus locationStatus = validateAgentLocation();
		IStatus nameStatus = validateAgentName();
		if(locationStatus.getSeverity() > IStatus.OK) {
			fStatus = locationStatus;
		} else if(nameStatus.getSeverity() > IStatus.OK) {
			fStatus = nameStatus;
		} else {
			fStatus = Status.OK_STATUS;
		}
		updatePageStatus();
	}
	
	/**
	 * Validates if correct agent bundle directory is given.
	 */
	private IStatus validateAgentLocation() {
		String locationName = fAgentRoot.getText();
		IStatus s = Status.OK_STATUS;
		File file = null;
		if (locationName.length() == 0) {
			s = new StatusInfo(IStatus.WARNING, HQDEMessages.HQEnvPage_enterLocation); 
		} else {
			file = new File(locationName);
			if(!file.isDirectory()) {
				s = new StatusInfo(IStatus.ERROR, HQDEMessages.HQEnvPage_notDirectory); 
			} else {
				// testing existence of pdk/lib/hq-product.jar
				file = new File(locationName, "pdk/lib/hq-product.jar");
				if(!file.isFile()) {
					s = new StatusInfo(IStatus.ERROR, HQDEMessages.HQEnvPage_notValidDirectory); 					
				}
			}
		}
		return s;
	}

	/**
	 * Validates agent name. It must not be empty or
	 * overlaps with existing name.
	 */
	private IStatus validateAgentName() {
		String agentName = fAgentName.getText();
		IStatus s = Status.OK_STATUS;
		if(agentName.length() == 0) {
			s = new StatusInfo(IStatus.WARNING, HQDEMessages.HQEnvPage_enterName);
			return s;
		}
		
		if(fExistingNames != null) {
			for (int i = 0; i < fExistingNames.length; i++) {
				if(fExistingNames[i].equals(agentName)) {
					s = new StatusInfo(IStatus.WARNING, HQDEMessages.HQEnvPage_nameExist);		
					return s;
				}
			}
		} 
		return s;
	}

	
	/**
	 * There is only one page within this wizard.
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		return null;
	}

	/**
	 * 
	 * @return
	 */
	protected IStatus[] getEnvStatus(){
		return new IStatus[] {Status.OK_STATUS};
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean isDuplicateName(String name) {
		if (fExistingNames != null) {
			for (int i = 0; i < fExistingNames.length; i++) {
				if (name.equals(fExistingNames[i])) {
					return true;
				}
			}
		}
		return false;
	}

}
