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

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.hyperic.hypclipse.internal.ClasspathComputer;
import org.hyperic.hypclipse.internal.ExecutionEnvironmentAnalyzer;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.build.WorkspaceBuildModel;
import org.hyperic.hypclipse.internal.editor.build.WorkspacePluginModelBase;
import org.hyperic.hypclipse.internal.preferences.PreferencesManager;
import org.hyperic.hypclipse.internal.util.CoreUtility;
import org.hyperic.hypclipse.internal.wizards.IProjectProvider;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModelFactory;
import org.hyperic.hypclipse.plugin.IFieldData;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginContentWizard;
import org.hyperic.hypclipse.plugin.IPluginFieldData;

/**
 * Handles the new project creation based on information
 * gathered from wizard.
 *
 */
public class NewProjectCreationOperation extends WorkspaceModifyOperation {

	private IFieldData fData;
	private IPluginContentWizard fContentWizard;
	private IProjectProvider fProjectProvider;
	private WorkspacePluginModelBase fModel;
//	private boolean fResult;

	/**
	 * 
	 * @param data
	 * @param provider
	 * @param contentWizard
	 */
	public NewProjectCreationOperation(IFieldData data, IProjectProvider provider, IPluginContentWizard contentWizard) {
		fData = data;
		fProjectProvider = provider;
		fContentWizard = contentWizard;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {

		monitor.beginTask(HQDEMessages.NewProjectCreationOperation_creating, getNumberOfWorkUnits());
		monitor.subTask(HQDEMessages.NewProjectCreationOperation_project);

		// first create project to eclipse
		IProject project = createProject();
		monitor.worked(1);

		// adding java nature to project
		// also modify classpaths
		if (project.hasNature(JavaCore.NATURE_ID)) {
			monitor.subTask(HQDEMessages.NewProjectCreationOperation_setClasspath);
			setClasspath(project, fData);
			monitor.worked(1);
		}

		monitor.subTask("PDEUIMessages.NewProjectCreationOperation_manifestFile");
		createPluginDescriptor(project);
		monitor.worked(1);
	
		// generate the build.properties file
		monitor.subTask("PDEUIMessages.NewProjectCreationOperation_buildPropertiesFile");
		createBuildPropertiesFile(project, fData);
		monitor.worked(1);
		
		

		
//		boolean contentWizardResult = true;
		if (fContentWizard != null) {
//			contentWizardResult = fContentWizard.performFinish(project, fModel, new SubProgressMonitor(monitor, 1));
			fContentWizard.performFinish(project, fModel, new SubProgressMonitor(monitor, 1));
		}

		fModel.save();
		openFile((IFile) fModel.getUnderlyingResource());
		monitor.worked(1);

//		fResult = contentWizardResult;

	}

	/**
	 * 
	 * @param project
	 * @throws CoreException
	 */
	private void createPluginDescriptor(IProject project) throws CoreException {
		fModel = new WorkspacePluginModelBase(project.getFile("hq-plugin.xml"), false);
		IPluginBase pluginBase = fModel.getPluginBase();
		
		pluginBase.setPackage(fData.getPackageName());
		pluginBase.setName(fData.getName());
		if(((IPluginFieldData)fData).doGenerateClass())
			pluginBase.setProductPlugin(((IPluginFieldData)fData).getClassname());
	}

	private void createBuildPropertiesFile(IProject project, IFieldData data) throws CoreException {
		IFile file = project.getFile("hqbuild.properties");
		if (!file.exists()) {
			WorkspaceBuildModel model = new WorkspaceBuildModel(file);
			IBuildModelFactory factory = model.getFactory();

			// BIN.INCLUDES
			IBuildEntry binEntry = factory.createEntry(IBuildEntry.BIN_INCLUDES);
//			fillBinIncludes(project, binEntry);
			createSourceOutputBuildEntries(model, factory, project, data);
			model.getBuild().add(binEntry);
			model.save();
		}
	}

	protected void createSourceOutputBuildEntries(WorkspaceBuildModel model, IBuildModelFactory factory,IProject project, IFieldData data) throws CoreException {
		String srcFolder = fData.getSourceFolderName();
		if (srcFolder != null) {
			
			IBuildEntry entry = factory.createEntry(IBuildEntry.JAR_PREFIX + ".");
			if (srcFolder.length() > 0)
				entry.addToken(new Path(srcFolder).addTrailingSeparator().toString());

			model.getBuild().add(entry);
			
			entry = factory.createEntry(IBuildEntry.OUTPUT_PREFIX + ".");
			String outputFolder = fData.getOutputFolderName().trim();
			if (outputFolder.length() > 0)
				entry.addToken(new Path(outputFolder).addTrailingSeparator().toString());

			model.getBuild().add(entry);

			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] extraEntries = getHQDefaultClassPathEntries(javaProject, data);
			entry = factory.createEntry(IBuildEntry.JARS_EXTRA_CLASSPATH);
			
			if(extraEntries != null) {
				for (int i = 0; i < extraEntries.length; i++) {
					entry.addToken(extraEntries[i].getPath().toOSString());
				}
			}
			model.getBuild().add(entry);
			
			
		}

	}


	/**
	 * Sets project classpath.
	 * 
	 * @param project
	 * @param data
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private void setClasspath(IProject project, IFieldData data) throws JavaModelException, CoreException {
		IJavaProject javaProject = JavaCore.create(project);
		// Set output folder
		if (data.getOutputFolderName() != null) {
			IPath path = project.getFullPath().append(data.getOutputFolderName());
			javaProject.setOutputLocation(path, null);
		}
		IClasspathEntry[] entries = getClassPathEntries(javaProject, data);
		javaProject.setRawClasspath(entries, null);
	}


	/**
	 * Constructs complete array of classpath entries which is
	 * inserted to projects classpath.
	 * 
	 * @param project
	 * @param data
	 * @return
	 */
	private IClasspathEntry[] getClassPathEntries(IJavaProject project, IFieldData data) {
		IClasspathEntry[] internalClassPathEntries = getInternalClassPathEntries(project, data);
		IClasspathEntry[] hqClassPathEntries = getHQDefaultClassPathEntries(project, data);
		
		IClasspathEntry[] entries = new IClasspathEntry[internalClassPathEntries.length + 1 + hqClassPathEntries.length];
		
		System.arraycopy(internalClassPathEntries, 0, entries, 1, internalClassPathEntries.length);

		System.arraycopy(hqClassPathEntries, 0, entries, 1+internalClassPathEntries.length, hqClassPathEntries.length);

		
		
		// Set EE of new project
		String executionEnvironment = null;
		if (data instanceof AbstractFieldData) {
			executionEnvironment = ((AbstractFieldData) data).getExecutionEnvironment();
		}
		ClasspathComputer.setComplianceOptions(project, ExecutionEnvironmentAnalyzer.getCompliance(executionEnvironment));
		entries[0] = ClasspathComputer.createJREEntry(executionEnvironment);
//		entries[1] = ClasspathComputer.createContainerEntry();

		return entries;
	}

	/**
	 * Example directory from agent side where some of the client libraries 
	 * are located is:
	 * C:\Program Files\Hyperic HQ Enterprise 4.0.1\agent-4.0.1-EE\bundles\agent-4.0.1-EE-905\pdk\lib
	 * 
	 * Jar libraries which are directly under this lib directory are 
	 * automatically added to agents classpath. Since agent will always see
	 * these libraries, we will add these files automatically to projects 
	 * classpath.
	 * 
	 * This method will return array of classpath entries which
	 * contains these libraries.
	 * 
	 * @param project
	 * @param data hq bundle name to match from stored preferences
	 * @return
	 */
	protected IClasspathEntry[] getHQDefaultClassPathEntries(IJavaProject project, IFieldData data) {
		PreferencesManager pManager = PreferencesManager.getInstance();
		String hqLibHome = pManager.getEnvironmentLocation(data.getHQExecutionEnvironment());
		
		if(hqLibHome == null) {
			return new IClasspathEntry[0];			
		}
		
		
		File lDir = new File(hqLibHome + "/pdk/lib");
		String[] jars = lDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.endsWith(".jar"))
					return true;
				return false;
			}
		});
		
		IClasspathEntry[] entries = new IClasspathEntry[jars.length];
		for (int i = 0; i < jars.length; i++) {
			File jar = new File(lDir, jars[i]);
			entries[i] = JavaCore.newLibraryEntry(new Path(jar.getAbsolutePath()), null, null, new IAccessRule[0], new IClasspathAttribute[0], false);
		}
		
		return entries;
	}

	/**
	 * 
	 * @param project
	 * @param data
	 * @return
	 */
	protected IClasspathEntry[] getInternalClassPathEntries(IJavaProject project, IFieldData data) {
		if (data.getSourceFolderName() == null) {
			return new IClasspathEntry[0];
		}
		IClasspathEntry[] entries = new IClasspathEntry[1];
		IPath path = project.getProject().getFullPath().append(data.getSourceFolderName());
		entries[0] = JavaCore.newSourceEntry(path);
		return entries;
	}

	/**
	 * This helper method creates actual eclipse project.
	 * Needed natures are attached to project. If there are
	 * other basic resources like directories, those
	 * are also created.
	 * 
	 * @return Handle to eclipse project
	 * @throws CoreException
	 */
	private IProject createProject() throws CoreException {
		IProject project = fProjectProvider.getProject();
		if (!project.exists()) {
			CoreUtility.createProject(project, fProjectProvider.getLocationPath(), null);
			project.open(null);
		}
		
		if (!project.hasNature("org.hyperic.hypclipse.PluginNature"))
			CoreUtility.addNatureToProject(project, "org.hyperic.hypclipse.PluginNature", null);
		if (!project.hasNature(JavaCore.NATURE_ID))
			CoreUtility.addNatureToProject(project, JavaCore.NATURE_ID, null);
		if (fData.getSourceFolderName() != null && fData.getSourceFolderName().trim().length() > 0) {
			IFolder folder = project.getFolder(fData.getSourceFolderName());
			if (!folder.exists())
				CoreUtility.createFolder(folder);
		}
		return project;
	}

	/**
	 * This method is calculating work units which is used in
	 * progress monitor. During project creation, progress 
	 * feedback is given back to user.
	 * 
	 * @return Number of work units
	 */
	protected int getNumberOfWorkUnits() {
		int numUnits = 4;
		if (fData instanceof IPluginFieldData) {
			IPluginFieldData data = (IPluginFieldData) fData;
			if (data.doGenerateClass())
				numUnits++;
			if (fContentWizard != null)
				numUnits++;
		}
		return numUnits;
	}

	/**
	 * Opens file to eclipse framework. This function is usually
	 * called after project is created and some created 
	 * file wants to be opened right away.
	 * 
	 * @param file Resource to open
	 */
	private void openFile(final IFile file) {
		final IWorkbenchWindow ww = HQDEPlugin.getActiveWorkbenchWindow();
		final IWorkbenchPage page = ww.getActivePage();
		if (page == null)
			return;
		final IWorkbenchPart focusPart = page.getActivePart();
		ww.getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (focusPart instanceof ISetSelectionTarget) {
					ISelection selection = new StructuredSelection(file);
					((ISetSelectionTarget) focusPart).selectReveal(selection);
				}
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
	}

}
