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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.*;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Class used to handle compiler related preferences. 
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CompilerFlags {

	/**
	 * Compiler flag options as integers
	 */
	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int IGNORE = 2;

	/**
	 * categories of flags 
	 */
	public static final int PLUGIN_FLAGS = 0;
	public static final int SCHEMA_FLAGS = 1;
	public static final int FEATURE_FLAGS = 2;
	public static final int SITE_FLAGS = 3;

	/**
	 * plugin preferences
	 */
	public static final String P_UNRESOLVED_IMPORTS = "compilers.p.unresolved-import"; //$NON-NLS-1$
	public static final String P_UNRESOLVED_EX_POINTS = "compilers.p.unresolved-ex-points"; //$NON-NLS-1$
	public static final String P_UNKNOWN_ELEMENT = "compilers.p.unknown-element"; //$NON-NLS-1$
	public static final String P_UNKNOWN_ATTRIBUTE = "compilers.p.unknown-attribute"; //$NON-NLS-1$
	public static final String P_UNKNOWN_CLASS = "compilers.p.unknown-class"; //$NON-NLS-1$
	public static final String P_UNKNOWN_RESOURCE = "compilers.p.unknown-resource"; //$NON-NLS-1$
	public static final String P_UNKNOWN_IDENTIFIER = "compilers.p.unknown-identifier"; //$NON-NLS-1$
	public static final String P_DISCOURAGED_CLASS = "compilers.p.discouraged-class"; //$NON-NLS-1$
	public static final String P_NO_REQUIRED_ATT = "compilers.p.no-required-att"; //$NON-NLS-1$
	public static final String P_NOT_EXTERNALIZED = "compilers.p.not-externalized-att"; //$NON-NLS-1$
	public static final String P_BUILD = "compilers.p.build"; //$NON-NLS-1$
	public static final String P_INCOMPATIBLE_ENV = "compilers.incompatible-environment"; //$NON-NLS-1$
	public static final String P_MISSING_EXPORT_PKGS = "compilers.p.missing-packages"; //$NON-NLS-1$
	public static final String P_DEPRECATED = "compilers.p.deprecated"; //$NON-NLS-1$
	public static final String P_INTERNAL = "compilers.p.internal"; //$NON-NLS-1$

	/**
	 * schema preferences 
	 */
	public static final String S_CREATE_DOCS = "compilers.s.create-docs"; //$NON-NLS-1$
	public static final String S_DOC_FOLDER = "compilers.s.doc-folder"; //$NON-NLS-1$
	public static final String S_OPEN_TAGS = "compilers.s.open-tags"; //$NON-NLS-1$

	/**
	 * feature preferences 
	 */
	public static final String F_UNRESOLVED_PLUGINS = "compilers.f.unresolved-plugins"; //$NON-NLS-1$
	public static final String F_UNRESOLVED_FEATURES = "compilers.f.unresolved-features"; //$NON-NLS-1$

	/**
	 * Returns the value for the requested preference, or 0 if there was a problem getting the preference value
	 * @param project to use as a project specific settings scope, or null
	 * @param flagId the id of the preference to retrieve
	 * @return the value for the given preference id
	 */
	public static int getFlag(IProject project, String flagId) {
		try {
			return Integer.parseInt(getString(project, flagId));
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	/**
	 * Returns the boolean preference denoted by the flag id (preference id)
	 * @param project to use as a project specific settings scope, or null
	 * @param flagId the id of the preference to retrieve
	 * @return the boolean value for the given preference id
	 */
	public static boolean getBoolean(IProject project, String flagId) {
		return Boolean.valueOf(getString(project, flagId)).booleanValue();
	}

	/**
	 * Returns the string preference for the given preference id
	 * @param project to use as a project specific settings scope, or null
	 * @param flagId the id of the preference to retrieve
	 * @return preference value or an empty string, never <code>null</code>
	 */
	public static String getString(IProject project, String flagId) {
		IPreferencesService service = Platform.getPreferencesService();
		IScopeContext[] contexts = project == null ? null : new IScopeContext[] {new ProjectScope(project)};
		return service.getString("org.hyperic.hypclipse", flagId, "", project == null ? null : contexts); //$NON-NLS-1$
	}

	/**
	 * Saves INSTANCE preferences
	 */
	public static void save() {
		try {
			new InstanceScope().getNode("org.hyperic.hypclipse").flush();
		} catch (BackingStoreException e) {
		}
	}
}
