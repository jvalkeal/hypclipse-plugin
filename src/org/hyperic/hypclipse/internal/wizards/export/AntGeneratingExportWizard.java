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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.util.XMLPrintHandler;
import org.w3c.dom.Document;


/**
 * The Class AntGeneratingExportWizard.
 */
public abstract class AntGeneratingExportWizard extends BaseExportWizard {

	/** The page. */
	protected BaseExportWizardPage fPage;

	/** This may contain hint for file name. */
	protected String fFileName;


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		fPage = createPage1();
		addPage(fPage);
	}

	/**
	 * Creates the page1.
	 * 
	 * @return the base export wizard page
	 */
	protected abstract BaseExportWizardPage createPage1();

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizard#performPreliminaryChecks()
	 */
	protected boolean performPreliminaryChecks() {
		
		generateAntBuildFile(fBuildTempLocation + "/build.xml");
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizard#confirmDelete()
	 */
	protected boolean confirmDelete() {
		File zipFile = new File(fPage.getFilePath());
		
		if (zipFile.exists()) {
			
			if(fPage.canOverwrite()) {
				if (!MessageDialog.openQuestion(getContainer().getShell(), HQDEMessages.BaseExportWizard_confirmReplace_title, NLS.bind(HQDEMessages.BaseExportWizard_confirmReplace_desc, zipFile.getAbsolutePath())))
					return false;
				zipFile.delete();				
			} else {
				zipFile.delete();
			}
			
		}
		return true;
	}

	/**
	 * Generate ant task.
	 * 
	 * @return the document
	 */
	protected abstract Document generateAntTask();

	/**
	 * Generate ant build file.
	 * 
	 * @param filename the filename
	 */
	protected void generateAntBuildFile(String filename) {
		String parent = new Path(filename).removeLastSegments(1).toOSString();
		String buildFilename = new Path(filename).lastSegment();
		if (!buildFilename.endsWith(".xml"))
			buildFilename += ".xml";
		File dir = new File(new File(parent).getAbsolutePath());
		if (!dir.exists())
			dir.mkdirs();

		try {
			Document task = generateAntTask();
			if (task != null) {
				File buildFile = new File(dir, buildFilename);
				XMLPrintHandler.writeFile(task, buildFile, false);
				generateAntTask();
				setDefaultValues(dir, buildFilename);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Sets the default values.
	 * 
	 * @param dir the dir
	 * @param buildFilename the build filename
	 */
	private void setDefaultValues(File dir, String buildFilename) {
		try {
			IContainer container = HQDEPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(dir.toString()));
			if (container != null && container.exists()) {
				IProject project = container.getProject();
				if (project != null) {
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
					IFile file = container.getFile(new Path(buildFilename));
//					if (file.exists())
//						BaseBuildAction.setDefaultValues(file);
				}
			}
		} catch (CoreException e) {
		}
	}


}
