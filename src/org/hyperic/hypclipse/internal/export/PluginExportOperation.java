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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class PluginExportOperation implements IWorkspaceRunnable {

	/** The build temp location. */
	protected String fBuildTempLocation;
	
	/** The export dir. */
	String exportDir;
	
	/** The File id. */
	String FileId;

	/** write to the ant build listener log */
	private static boolean fHasErrors;

	/**
	 * Instantiates a new plugin export operation.
	 * 
	 * @param exportDir the export dir
	 * @param fileId the file id
	 */
	public PluginExportOperation(String exportDir, String fileId) {
		fBuildTempLocation = HQDEPlugin.getDefault().getStateLocation().append("temp").toString();
		this.exportDir = exportDir;
		this.FileId = fileId;

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("", 3);
//		for(int i = 1; i<4; i++) {
//
////			new SubProgressMonitor(monitor, 9)
//			monitor.setTaskName(Integer.toString(i));
//			try {
////				HQDEPlugin.logErrorMessage(Integer.toString(i));
//				Thread.sleep(1000);
//				monitor.worked(1);
//			} catch (InterruptedException e) {
//			}
//
//
//		}
		
		try {
			runScript(
					fBuildTempLocation + "/build.xml",
					new String[]{"package"},
					createAntBuildProperties(), monitor);
		} catch (InvocationTargetException e) {
			HQDEPlugin.logException(e);
		}
		
		
		monitor.done();
	}
	
	public void doExport() {
		
	}
	
	/**
	 * Creates the ant build properties.
	 * 
	 * @return the hash map< string, string>
	 */
	protected HashMap<String, String> createAntBuildProperties() {
		HashMap<String, String>	fAntBuildProperties = new HashMap<String, String>(15);
		
		fAntBuildProperties.put("build.dir", fBuildTempLocation + "/bin");
		fAntBuildProperties.put("assemble.dir", fBuildTempLocation + "/assemble");
		fAntBuildProperties.put("build.compiler", "org.eclipse.jdt.core.JDTCompilerAdapter");
		fAntBuildProperties.put("package.file", FileId);
//		fAntBuildProperties.put("javacFailOnError", "false");
//		fAntBuildProperties.put("javacVerbose", "false");
//		fAntBuildProperties.put("javacDebugInfo", "on");
						
		
		return fAntBuildProperties;
	}

	/**
	 * Run script.
	 * 
	 * @param location the location
	 * @param targets the targets
	 * @param properties the properties
	 * @param monitor the monitor
	 * 
	 * @throws InvocationTargetException the invocation target exception
	 * @throws CoreException the core exception
	 */
	protected void runScript(String location, String[] targets, Map properties, IProgressMonitor monitor) throws InvocationTargetException, CoreException {
		fHasErrors = false;
		AntRunner runner = new AntRunner();
		runner.addUserProperties(properties);
		runner.setAntHome(location);
		runner.setBuildFileLocation(location);
		runner.addBuildListener("org.hyperic.hypclipse.internal.export.ExportBuildListener");
		runner.setExecutionTargets(targets);
		runner.run(monitor);

	}


	/**
	 * Checks for errors.
	 * 
	 * @return true, if successful
	 */
	public boolean hasErrors() {
		return fHasErrors;
	}

	/**
	 * Error found.
	 */
	public static void errorFound() {
		fHasErrors = true;
	}

	/**
	 * Gets the f build temp location.
	 * 
	 * @return the f build temp location
	 */
	public String getFBuildTempLocation() {
		return fBuildTempLocation;
	}

	
}
