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
package org.hyperic.hypclipse.internal.export;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class PluginExportJob extends Job {

	String exportDir;
	String filePath;
	
	private PluginExportOperation op;
	
	public PluginExportJob(String name, String exportDir, String fileId) {
		super(name);
		this.exportDir = exportDir;
		this.filePath = fileId;
		setRule(ResourcesPlugin.getWorkspace().getRoot());
	}

	protected IStatus run(IProgressMonitor monitor) {
		String errorMessage = null;

		op = new PluginExportOperation(exportDir, filePath);
		try {
			op.run(monitor);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
		
		if (errorMessage == null && op.hasErrors())
			errorMessage = getLogFoundMessage();

		if (errorMessage != null) {
			final String em = errorMessage;
			getStandardDisplay().asyncExec(new Runnable() {
				public void run() {
					asyncNotifyExportException(em);
				}
			});
			return Job.ASYNC_FINISH;
		}
		
		return new Status(IStatus.OK, HQDEPlugin.getPluginId(), IStatus.OK, "", null);
	}
	
	private void asyncNotifyExportException(String errorMessage) {
		getStandardDisplay().beep();
		MessageDialog.openError(HQDEPlugin.getActiveWorkbenchShell(), HQDEMessages.PluginExportJob_error, errorMessage);
		done(new Status(IStatus.OK, HQDEPlugin.getPluginId(), IStatus.OK, "", null));
	}
	
	/**
	 * Returns the standard display to be used. The method first checks, if the
	 * thread calling this method has an associated disaply. If so, this display
	 * is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	protected String getLogFoundMessage() {
		return NLS.bind(HQDEMessages.PluginExportJob_error_message, op.getFBuildTempLocation());
	}




	class SchedulingRule implements ISchedulingRule {
		public boolean contains(ISchedulingRule rule) {
			return rule instanceof SchedulingRule || rule instanceof IResource;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule instanceof SchedulingRule;
		}
	}

}
