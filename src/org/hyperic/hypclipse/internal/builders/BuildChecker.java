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
package org.hyperic.hypclipse.internal.builders;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 *
 */
public class BuildChecker extends IncrementalProjectBuilder {

	private static IProject[] EMPTY_LIST = new IProject[0];
	
	private int BUILD = 0x1;
	private int PLUGIN = 0x2;

	
	public BuildChecker() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		if(monitor.isCanceled())
			return EMPTY_LIST;
		
		IProject project = getProject();
		int type = getDeltaType(project);
		
		validateProject(type, monitor);
//		validateBuildProperties(new SubProgressMonitor(monitor, 1));

		return EMPTY_LIST;
	}
	
	/**
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private int getDeltaType(IProject project) throws CoreException {
		IResourceDelta delta = getDelta(project);
		if (delta == null) {
			return BUILD | PLUGIN;
		}
		return BUILD | PLUGIN;
	}
	
	/**
	 * 
	 * @param type
	 * @param monitor
	 */
	private void validateProject(int type, IProgressMonitor monitor) {
		monitor.beginTask("HQDEMessages.ManifestConsistencyChecker_builderTaskName", getWorkAmount(type));
		if ((type & PLUGIN) != 0) {
			IProject project = getProject();
			IFile file = project.getFile("hq-plugin.xml");
			if (file.exists()) {
				validateFiles(file, type, monitor);
			}
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param type
	 * @param monitor
	 */
	private void validateFiles(IFile file, int type, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		XMLErrorReporter reporter = null;
		
		reporter = new XMLErrorReporter(file);
		
		if (reporter != null) {
			RelaxParser.parse(file, reporter, reporter);
			reporter.validateContent(monitor);
		}
		monitor.done();
	}
	
	/**
	 * 
	 * @param monitor
	 */
	private void validateBuildProperties(IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;
		IProject project = getProject();
		IFile file = project.getFile("hqbuild.properties");
		if (file.exists()) {
			monitor.subTask("ManifestConsistencyChecker_buildPropertiesSubtask");
			BuildErrorReporter ber = new BuildErrorReporter(file);
			ber.validateContent(monitor);
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private int getWorkAmount(int type) {
		int work = 1;
		if ((type & PLUGIN) != 0)
			++work;
		if ((type & BUILD) != 0)
			++work;
		return work;
	}



}
