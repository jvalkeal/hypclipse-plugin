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
package org.hyperic.hypclipse.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
import org.hyperic.hypclipse.internal.editor.build.WorkspaceBuildModel;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IPluginModelBase;


public class ClasspathComputer {

	private static Map<String, Integer> fSeverityTable = null;
	private static final int SEVERITY_ERROR = 3;
	private static final int SEVERITY_WARNING = 2;
	private static final int SEVERITY_IGNORE = 1;

	public static void setClasspath(IProject project, IPluginModelBase model) throws CoreException {
		IClasspathEntry[] entries = getClasspath(project, model, false, true);
		JavaCore.create(project).setRawClasspath(entries, null);
	}

	public static IClasspathEntry[] getClasspath(IProject project, IPluginModelBase model, boolean clear, boolean overrideCompliance) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);
		ArrayList<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
		IBuild build = getBuild(project);

//		// add JRE and set compliance options
//		String ee = getExecutionEnvironment(model.getBundleDescription());
//		result.add(createEntryUsingPreviousEntry(javaProject, ee, PDECore.JRE_CONTAINER_PATH));
//		setComplianceOptions(JavaCore.create(project), ExecutionEnvironmentAnalyzer.getCompliance(ee), overrideCompliance);
//
//		// add pde container
//		result.add(createEntryUsingPreviousEntry(javaProject, ee, PDECore.REQUIRED_PLUGINS_CONTAINER_PATH));
//
//		// add own libraries/source
//		addSourceAndLibraries(project, model, build, clear, result);
//
		IClasspathEntry[] entries = result.toArray(new IClasspathEntry[0]);
		IJavaModelStatus validation = JavaConventions.validateClasspath(javaProject, entries, javaProject.getOutputLocation());
		if (!validation.isOK()) {
			HQDEPlugin.logErrorMessage(validation.getMessage());
			throw new CoreException(validation);
		}
		return result.toArray(new IClasspathEntry[0]);
	}
//
//	public static void addSourceAndLibraries(IProject project, IPluginModelBase model, IBuild build, boolean clear, ArrayList result) throws CoreException {
//
//		HashSet paths = new HashSet();
//
//		// keep existing source folders
//		if (!clear) {
//			IClasspathEntry[] entries = JavaCore.create(project).getRawClasspath();
//			for (int i = 0; i < entries.length; i++) {
//				IClasspathEntry entry = entries[i];
//				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
//					if (paths.add(entry.getPath()))
//						result.add(entry);
//				}
//			}
//		}
//
//		IClasspathAttribute[] attrs = getClasspathAttributes(project, model);
//		IPluginLibrary[] libraries = model.getPluginBase().getLibraries();
//		for (int i = 0; i < libraries.length; i++) {
//			IBuildEntry buildEntry = build == null ? null : build.getEntry("source." + libraries[i].getName()); //$NON-NLS-1$
//			if (buildEntry != null) {
//				addSourceFolder(buildEntry, project, paths, result);
//			} else {
//				if (libraries[i].getName().equals(".")) //$NON-NLS-1$
//					addJARdPlugin(project, ClasspathUtilCore.getFilename(model), attrs, result);
//				else
//					addLibraryEntry(project, libraries[i], attrs, result);
//			}
//		}
//		if (libraries.length == 0) {
//			if (build != null) {
//				IBuildEntry buildEntry = build == null ? null : build.getEntry("source.."); //$NON-NLS-1$
//				if (buildEntry != null) {
//					addSourceFolder(buildEntry, project, paths, result);
//				}
//			} else if (ClasspathUtilCore.hasBundleStructure(model)) {
//				addJARdPlugin(project, ClasspathUtilCore.getFilename(model), attrs, result);
//			}
//		}
//	}
//
//	private static IClasspathAttribute[] getClasspathAttributes(IProject project, IPluginModelBase model) {
//		IClasspathAttribute[] attributes = new IClasspathAttribute[0];
//		if (!RepositoryProvider.isShared(project)) {
//			JavadocLocationManager manager = PDECore.getDefault().getJavadocLocationManager();
//			String javadoc = manager.getJavadocLocation(model);
//			if (javadoc != null) {
//				attributes = new IClasspathAttribute[] {JavaCore.newClasspathAttribute(IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, javadoc)};
//			}
//		}
//		return attributes;
//	}
//
//	private static void addSourceFolder(IBuildEntry buildEntry, IProject project, HashSet paths, ArrayList result) throws CoreException {
//		String[] folders = buildEntry.getTokens();
//		for (int j = 0; j < folders.length; j++) {
//			String folder = folders[j];
//			IPath path = project.getFullPath().append(folder);
//			if (paths.add(path)) {
//				if (project.findMember(folder) == null) {
//					CoreUtility.createFolder(project.getFolder(folder));
//				} else {
//					IPackageFragmentRoot root = JavaCore.create(project).getPackageFragmentRoot(path.toString());
//					if (root.exists() && root.getKind() == IPackageFragmentRoot.K_BINARY) {
//						result.add(root.getRawClasspathEntry());
//						continue;
//					}
//				}
//				result.add(JavaCore.newSourceEntry(path));
//			}
//		}
//	}
//
	protected static IBuild getBuild(IProject project) throws CoreException {
		IFile buildFile = project.getFile("hqbuild.properties"); //$NON-NLS-1$
		IBuildModel buildModel = null;
		if (buildFile.exists()) {
			buildModel = new WorkspaceBuildModel(buildFile);
			buildModel.load();
		}
		return (buildModel != null) ? buildModel.getBuild() : null;
	}
//
//	private static void addLibraryEntry(IProject project, IPluginLibrary library, IClasspathAttribute[] attrs, ArrayList result) throws JavaModelException {
//		String name = ClasspathUtilCore.expandLibraryName(library.getName());
//		IResource jarFile = project.findMember(name);
//		if (jarFile == null)
//			return;
//
//		IPackageFragmentRoot root = JavaCore.create(project).getPackageFragmentRoot(jarFile);
//		if (root.exists() && root.getKind() == IPackageFragmentRoot.K_BINARY) {
//			IClasspathEntry oldEntry = root.getRawClasspathEntry();
//			if (oldEntry.getSourceAttachmentPath() != null && !result.contains(oldEntry)) {
//				result.add(oldEntry);
//				return;
//			}
//		}
//
//		IClasspathEntry entry = createClasspathEntry(project, jarFile, name, attrs, library.isExported());
//		if (!result.contains(entry))
//			result.add(entry);
//	}
//
//	private static void addJARdPlugin(IProject project, String filename, IClasspathAttribute[] attrs, ArrayList result) {
//		String name = ClasspathUtilCore.expandLibraryName(filename);
//		IResource jarFile = project.findMember(name);
//		if (jarFile != null) {
//			IClasspathEntry entry = createClasspathEntry(project, jarFile, filename, attrs, true);
//			if (!result.contains(entry))
//				result.add(entry);
//		}
//	}
//
//	private static IClasspathEntry createClasspathEntry(IProject project, IResource library, String fileName, IClasspathAttribute[] attrs, boolean isExported) {
//		String sourceZipName = ClasspathUtilCore.getSourceZipName(fileName);
//		IResource resource = project.findMember(sourceZipName);
//		// if zip file does not exist, see if a directory with the source does.  This in necessary how we import source for individual source bundles.
//		if (resource == null && sourceZipName.endsWith(".zip")) { //$NON-NLS-1$
//			resource = project.findMember(sourceZipName.substring(0, sourceZipName.length() - 4));
//			if (resource == null)
//				// if we can't find the the source for a library, then try to find the common source location set up to share source from one jar to all libraries.
//				// see PluginImportOperation.linkSourceArchives
//				resource = project.getFile(project.getName() + "src.zip"); //$NON-NLS-1$
//		}
//		IPath srcAttachment = resource != null ? resource.getFullPath() : library.getFullPath();
//		return JavaCore.newLibraryEntry(library.getFullPath(), srcAttachment, null, new IAccessRule[0], attrs, isExported);
//	}
//
//	private static String getExecutionEnvironment(BundleDescription bundleDescription) {
//		if (bundleDescription != null) {
//			String[] envs = bundleDescription.getExecutionEnvironments();
//			if (envs.length > 0)
//				return envs[0];
//		}
//		return null;
//	}
//
	public static void setComplianceOptions(IJavaProject project, String compliance) {
		setComplianceOptions(project, compliance, true);
	}

	@SuppressWarnings("unchecked")
	public static void setComplianceOptions(IJavaProject project, String compliance, boolean overrideExisting) {
		Map<String, String> projectMap = project.getOptions(false);
		if (compliance == null) {
			if (overrideExisting && projectMap.size() > 0) {
				projectMap.remove(JavaCore.COMPILER_COMPLIANCE);
				projectMap.remove(JavaCore.COMPILER_SOURCE);
				projectMap.remove(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM);
				projectMap.remove(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER);
				projectMap.remove(JavaCore.COMPILER_PB_ENUM_IDENTIFIER);
			} else {
				return;
			}
		} else if (JavaCore.VERSION_1_6.equals(compliance)) {
			setCompliance(projectMap, JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR, overrideExisting);
		} else if (JavaCore.VERSION_1_5.equals(compliance)) {
			setCompliance(projectMap, JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR, overrideExisting);
		} else if (JavaCore.VERSION_1_4.equals(compliance)) {
			setCompliance(projectMap, JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_4, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_2, overrideExisting);
			setMinimumCompliance(projectMap, JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.WARNING, overrideExisting);
			setMinimumCompliance(projectMap, JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.WARNING, overrideExisting);
		} else if (JavaCore.VERSION_1_3.equals(compliance)) {
			setCompliance(projectMap, JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_3, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3, overrideExisting);
			setCompliance(projectMap, JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_1, overrideExisting);
			setMinimumCompliance(projectMap, JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.IGNORE, overrideExisting);
			setMinimumCompliance(projectMap, JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.IGNORE, overrideExisting);
		}
		project.setOptions(projectMap);

	}

	/**
	 * Puts the key/value pair into the map if the map can be overridden or the map doesn't
	 * already contain the key.
	 * @param map map to put the value in
	 * @param key key for the value
	 * @param value value to put in the map
	 * @param override whether existing map entries should be replaced with the value
	 */
	private static void setCompliance(Map<String, String> map, String key, String value, boolean override) {
		if (override || !map.containsKey(key)) {
			map.put(key, value);
		}
	}

	/**
	 * Checks if the current value stored in the map is less severe than the given minimum value. If
	 * the minimum value is higher, the map will be updated with the minimum.
	 * 
	 * @param map the map to check the value in
	 * @param key the key to get the current value out of the map
	 * @param minimumValue the minimum value allowed
	 * @param override whether an existing value in the map should be replaced
	 */
	private static void setMinimumCompliance(Map<String, String> map, String key, String minimumValue, boolean override) {
		if (override || !map.containsKey(key)) {
			if (fSeverityTable == null) {
				fSeverityTable = new HashMap<String, Integer>(3);
				fSeverityTable.put(JavaCore.IGNORE, new Integer(SEVERITY_IGNORE));
				fSeverityTable.put(JavaCore.WARNING, new Integer(SEVERITY_WARNING));
				fSeverityTable.put(JavaCore.ERROR, new Integer(SEVERITY_ERROR));
			}
			String currentValue = map.get(key);
			int current = currentValue != null && fSeverityTable.containsKey(currentValue) ? fSeverityTable.get(currentValue).intValue() : 0;
			int minimum = minimumValue != null && fSeverityTable.containsKey(minimumValue) ? fSeverityTable.get(minimumValue).intValue() : 0;
			if (current < minimum) {
				map.put(key, minimumValue);
			}
		}
	}
//
//	/**
//	 * Returns a new classpath container entry for the given execution environment.  If the given java project
//	 * has an existing JRE/EE classpath entry, the access rules, extra attributes and isExported settings of
//	 * the existing entry will be added to the new execution entry.
//	 *  
//	 * @param javaProject project to check for existing classpath entries
//	 * @param ee id of the execution environment to create an entry for
//	 * @param path id of the container to create an entry for
//	 * 
//	 * @return new classpath container entry
//	 * @throws CoreException if there is a problem accessing the classpath entries of the project
//	 */
//	public static IClasspathEntry createEntryUsingPreviousEntry(IJavaProject javaProject, String ee, IPath path) throws CoreException {
//		IClasspathEntry[] entries = javaProject.getRawClasspath();
//		for (int i = 0; i < entries.length; i++) {
//			if (entries[i].getPath().equals(path)) {
//				if (path.equals(PDECore.JRE_CONTAINER_PATH))
//					return JavaCore.newContainerEntry(getEEPath(ee), entries[i].getAccessRules(), entries[i].getExtraAttributes(), entries[i].isExported());
//
//				return JavaCore.newContainerEntry(path, entries[i].getAccessRules(), entries[i].getExtraAttributes(), entries[i].isExported());
//			}
//		}
//
//		if (path.equals(PDECore.JRE_CONTAINER_PATH))
//			return createJREEntry(ee);
//
//		return JavaCore.newContainerEntry(path);
//	}
//
	/**
	 * Returns a classpath container entry for the given execution environment.
	 * @param ee id of the execution environment
	 * @return classpath container entry
	 */
	public static IClasspathEntry createJREEntry(String ee) {
		return JavaCore.newContainerEntry(getEEPath(ee));
	}

	/**
	 * Returns the JRE container path for the execution environment with the given id.
	 * @param ee execution environment id
	 * @return JRE container path for the execution environment
	 */
	private static IPath getEEPath(String ee) {
		IPath path = null;
		if (ee != null) {
			IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
			IExecutionEnvironment env = manager.getEnvironment(ee);
			if (env != null)
				path = JavaRuntime.newJREContainerPath(env);
		}
		if (path == null) {
			path = JavaRuntime.newDefaultJREContainerPath();
		}
		return path;
	}

	/**
	 * @return a new classpath container entry for a required plugin container
	 */
//	public static IClasspathEntry createContainerEntry() {
//		return JavaCore.newContainerEntry(PDECore.REQUIRED_PLUGINS_CONTAINER_PATH);
//	}

}
