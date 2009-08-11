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
package org.hyperic.hypclipse.internal.editor.plugin;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.wizards.export.PluginExportWizard;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

/**
 * This export action handles as a starting point for user
 * to request plugin export.
 */
public class PluginExportAction extends Action {
	
	private HQDEFormEditor fEditor;

	
	/**
	 * Instantiates a new plugin export action.
	 * 
	 * @param editor the editor
	 */
	public PluginExportAction(HQDEFormEditor editor) {
		fEditor = editor;
	}

	
	protected PluginExportAction() {
		//prevent public instantiation
	}

	/**
	 * Ensure content saved.
	 */
	private void ensureContentSaved() {
		if (fEditor.isDirty()) {
			try {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						fEditor.doSave(monitor);
					}
				};
				PlatformUI.getWorkbench().getProgressService().runInUI(HQDEPlugin.getActiveWorkbenchWindow(), op, HQDEPlugin.getWorkspace().getRoot());
			} catch (InvocationTargetException e) {
				HQDEPlugin.logException(e);
			} catch (InterruptedException e) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		
		// we want editor to save content
		if (fEditor != null)
			ensureContentSaved();
		
		
		IStructuredSelection selection = null;
		IResource resource = null;
		
		// trying to find plugin name from descriptor
		IPluginModelBase fModelBase = (IPluginModelBase)fEditor.getAggregateModel();
		IPluginBase fPluginBase = fModelBase.getPluginBase();
		String fName = fPluginBase.getName();
		
		if (fEditor != null)
			resource = ((IModel) fEditor.getAggregateModel()).getUnderlyingResource();
		
		if (resource != null)
			selection = new StructuredSelection(resource);
		
		PluginExportWizard wizard = new PluginExportWizard(fName);
		wizard.init(PlatformUI.getWorkbench(), selection);
		
		WizardDialog wd = new WizardDialog(HQDEPlugin.getActiveWorkbenchShell(), wizard);
		wd.create();
		int result = wd.open();
		
		notifyResult(result == Window.OK);
	}
}
