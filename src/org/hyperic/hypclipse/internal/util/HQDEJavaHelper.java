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
package org.hyperic.hypclipse.internal.util;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.launching.JavaRuntime;

public class HQDEJavaHelper {



	public static IJavaSearchScope getSearchScope(IJavaProject project) {
		return SearchEngine.createJavaSearchScope(getNonJRERoots(project));
	}

	public static IJavaSearchScope getSearchScope(IProject project) {
		return getSearchScope(JavaCore.create(project));
	}

	public static IPackageFragmentRoot[] getNonJRERoots(IJavaProject project) {
		ArrayList<IPackageFragmentRoot> result = new ArrayList<IPackageFragmentRoot>();
		try {
			IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (!isJRELibrary(roots[i])) {
					result.add(roots[i]);
				}
			}
		} catch (JavaModelException e) {
		}
		return (IPackageFragmentRoot[]) result.toArray(new IPackageFragmentRoot[result.size()]);
	}

	public static boolean isJRELibrary(IPackageFragmentRoot root) {
		try {
			IPath path = root.getRawClasspathEntry().getPath();
			if (path.equals(new Path(JavaRuntime.JRE_CONTAINER)) || path.equals(new Path(JavaRuntime.JRELIB_VARIABLE))) {
				return true;
			}
		} catch (JavaModelException e) {
		}
		return false;
	}



	/**
	 * @param project
	 * @return
	 */
	public static String getJavaSourceLevel(IProject project) {
		return getJavaLevel(project, JavaCore.COMPILER_SOURCE);
	}

	/**
	 * @param project
	 * @return
	 */
	public static String getJavaComplianceLevel(IProject project) {
		return getJavaLevel(project, JavaCore.COMPILER_COMPLIANCE);
	}

	/**
	 * Precedence order from high to low:  (1) Project specific option;
	 * (2) General preference option; (3) Default option; (4) Java 1.3
	 * @param project
	 * @param optionName
	 * @return
	 */
	public static String getJavaLevel(IProject project, String optionName) {
		// Returns the corresponding java project
		// No need to check for null, will return null		
		IJavaProject javaProject = JavaCore.create(project);
		String value = null;
		// Preferred to use the project
		if ((javaProject != null) && javaProject.exists()) {
			// Get the project specific option if one exists. Rolls up to the 
			// general preference option if no project specific option exists.
			value = javaProject.getOption(optionName, true);
			if (value != null) {
				return value;
			}
		}
		// Get the general preference option
		value = JavaCore.getJavaCore().getPluginPreferences().getString(optionName);
		if (value != null) {
			return value;
		}
		// Get the default option
		value = JavaCore.getOption(optionName);
		if (value != null) {
			return value;
		}
		// Return the default
		return JavaCore.VERSION_1_3;
	}

}
