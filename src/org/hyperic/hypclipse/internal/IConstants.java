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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Constants;

public interface IConstants {
	
	public final static String SCHEMA_HQ_RELAX = "schema/hq-plugin.rng";
	
	// TODO move to API - IModelProviderEvent
	int ENVIRONMENT_CHANGED = 0x10;

	// Target Platform
	String PLATFORM_PATH = "platform_path"; //$NON-NLS-1$
	String SAVED_PLATFORM = "saved_platform"; //$NON-NLS-1$
	String TARGET_MODE = "target_mode"; //$NON-NLS-1$
	String VALUE_USE_THIS = "useThis"; //$NON-NLS-1$
	String VALUE_USE_OTHER = "useOther"; //$NON-NLS-1$
	String CHECKED_PLUGINS = "checkedPlugins"; //$NON-NLS-1$
	String VALUE_SAVED_NONE = "[savedNone]"; //$NON-NLS-1$
	String VALUE_SAVED_ALL = "[savedAll]"; //$NON-NLS-1$
	String VALUE_SAVED_SOME = "savedSome"; //$NON-NLS-1$
	String P_SOURCE_LOCATIONS = "source_locations"; //$NON-NLS-1$
	String P_EXT_LOCATIONS = "ext_locations"; //$NON-NLS-1$
	String PROGRAM_ARGS = "program_args"; //$NON-NLS-1$
	String VM_ARGS = "vm_args"; //$NON-NLS-1$
	String VM_LAUNCHER_INI = "vm_launcher_ini"; //$NON-NLS-1$
	String IMPLICIT_DEPENDENCIES = "implicit_dependencies"; //$NON-NLS-1$
	String GROUP_PLUGINS_VIEW = "group_plugins"; //$NON-NLS-1$
	String ADDITIONAL_LOCATIONS = "additional_locations"; //$NON-NLS-1$
	String TARGET_PLATFORM_REALIZATION = "target_platform_realization"; //$NON-NLS-1$

	// Target Environment
	String OS = "org.eclipse.pde.ui.os"; //$NON-NLS-1$
	String WS = "org.eclipse.pde.ui.ws"; //$NON-NLS-1$
	String NL = "org.eclipse.pde.ui.nl"; //$NON-NLS-1$
	String ARCH = "org.eclipse.pde.ui.arch"; //$NON-NLS-1$

	String OS_EXTRA = "org.eclipse.pde.os.extra"; //$NON-NLS-1$
	String WS_EXTRA = "org.eclipse.pde.ws.extra"; //$NON-NLS-1$
	String NL_EXTRA = "org.eclipse.pde.nl.extra"; //$NON-NLS-1$
	String ARCH_EXTRA = "org.eclipse.pde.arch.extra"; //$NON-NLS-1$

	// Target JRE
	String TARGET_JRE = "targetJRE"; //$NON-NLS-1$

	/** Constant for the string <code>extension</code> */
	public final static String EXTENSION_NAME = "extension"; //$NON-NLS-1$	

	/** Constant for the string <code>plugin.xml</code> */
	public final static String PLUGIN_FILENAME_DESCRIPTOR = "plugin.xml"; //$NON-NLS-1$

	/** Constant for the string <code>feature.xml</code> */
	public final static String FEATURE_FILENAME_DESCRIPTOR = "feature.xml"; //$NON-NLS-1$

	/** Constant for the string <code>fragment.xml</code> */
	public final static String FRAGMENT_FILENAME_DESCRIPTOR = "fragment.xml"; //$NON-NLS-1$

	/** Constant for the string <code>META-INF/MANIFEST.MF</code> */
	public final static String BUNDLE_FILENAME_DESCRIPTOR = "META-INF/MANIFEST.MF"; //$NON-NLS-1$

	/** Constant for the string <code>build.properties</code> */
	public final static String BUILD_FILENAME_DESCRIPTOR = "hqbuild.properties"; //$NON-NLS-1$

	public final static String TARGET30 = "3.0"; //$NON-NLS-1$
	public final static String TARGET31 = "3.1"; //$NON-NLS-1$
	public final static String TARGET32 = "3.2"; //$NON-NLS-1$
	public final static String TARGET33 = "3.3"; //$NON-NLS-1$
	public final static String TARGET34 = "3.4"; //$NON-NLS-1$

	public final static String EQUINOX = "Equinox"; //$NON-NLS-1$

	// project preferences
	public static final String SELFHOSTING_BIN_EXCLUDES = "selfhosting.binExcludes"; //$NON-NLS-1$
	public static final String EQUINOX_PROPERTY = "pluginProject.equinox"; //$NON-NLS-1$
	public static final String EXTENSIONS_PROPERTY = "pluginProject.extensions"; //$NON-NLS-1$
	public static final String RESOLVE_WITH_REQUIRE_BUNDLE = "resolve.requirebundle"; //$NON-NLS-1$
	public static final String TARGET_PROFILE = "target.profile"; //$NON-NLS-1$

	// for backwards compatibility with Eclipse 3.0 bundle manifest files
	public final static String PROVIDE_PACKAGE = "Provide-Package"; //$NON-NLS-1$
	public final static String REPROVIDE_ATTRIBUTE = "reprovide"; //$NON-NLS-1$
	public final static String OPTIONAL_ATTRIBUTE = "optional"; //$NON-NLS-1$
	public final static String REQUIRE_PACKAGES_ATTRIBUTE = "require-packages"; //$NON-NLS-1$
	public final static String SINGLETON_ATTRIBUTE = "singleton"; //$NON-NLS-1$
	public final static String PACKAGE_SPECIFICATION_VERSION = "specification-version"; //$NON-NLS-1$
	public static final String IMPORT_SERVICE = "Import-Service"; //$NON-NLS-1$
	public static final String EXPORT_SERVICE = "Export-Service"; //$NON-NLS-1$

	// Equinox-specific headers
	public final static String EXTENSIBLE_API = "Eclipse-ExtensibleAPI"; //$NON-NLS-1$
	public final static String PATCH_FRAGMENT = "Eclipse-PatchFragment"; //$NON-NLS-1$
	public final static String PLUGIN_CLASS = "Plugin-Class"; //$NON-NLS-1$
	public final static String ECLIPSE_AUTOSTART = "Eclipse-AutoStart"; //$NON-NLS-1$
	public final static String ECLIPSE_LAZYSTART = "Eclipse-LazyStart"; //$NON-NLS-1$
	public final static String ECLIPSE_JREBUNDLE = "Eclipse-JREBundle"; //$NON-NLS-1$
	public static final String ECLIPSE_BUDDY_POLICY = "Eclipse-BuddyPolicy"; //$NON-NLS-1$
	public static final String ECLIPSE_REGISTER_BUDDY = "Eclipse-RegisterBuddy"; //$NON-NLS-1$
	public static final String ECLIPSE_GENERIC_CAPABILITY = "Eclipse-GenericCapabilty"; //$NON-NLS-1$
	public static final String ECLIPSE_GENERIC_REQUIRED = "Eclipse-GenericRequire"; //$NON-NLS-1$
	public static final String PLATFORM_FILTER = "Eclipse-PlatformFilter"; //$NON-NLS-1$
	public static final String ECLIPSE_SOURCE_BUNDLE = "Eclipse-SourceBundle"; //$NON-NLS-1$
	public static final String ECLIPSE_SYSTEM_BUNDLE = "Eclipse-SystemBundle"; //$NON-NLS-1$

	// Equinox-specific system properties
	public static final String OSGI_SYSTEM_BUNDLE = "osgi.system.bundle"; //$NON-NLS-1$
	public static final String OSGI_OS = "osgi.os"; //$NON-NLS-1$
	public static final String OSGI_WS = "osgi.ws"; //$NON-NLS-1$
	public static final String OSGI_NL = "osgi.nl"; //$NON-NLS-1$
	public static final String OSGI_ARCH = "osgi.arch"; //$NON-NLS-1$
	public static final String OSGI_RESOLVE_OPTIONAL = "osgi.resolveOptional"; //$NON-NLS-1$
	public static final String OSGI_RESOLVER_MODE = "osgi.resolverMode"; //$NON-NLS-1$

	// Equinox-specific directives
	public static final String INTERNAL_DIRECTIVE = "x-internal"; //$NON-NLS-1$
	public static final String FRIENDS_DIRECTIVE = "x-friends"; //$NON-NLS-1$

	public static final String[] TRANSLATABLE_HEADERS = new String[] {Constants.BUNDLE_VENDOR, Constants.BUNDLE_NAME, Constants.BUNDLE_DESCRIPTION, Constants.BUNDLE_COPYRIGHT, Constants.BUNDLE_CATEGORY};

	// EASTER EGG
	public static final String[] EE_TOKENS = new String[] {"wassim", "zx", "bbauman", "cherie", "jlb" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	};

	// Common folder names
	public static String MANIFEST_FOLDER_NAME = "META-INF/"; //$NON-NLS-1$
	public static String OSGI_INF_FOLDER_NAME = "OSGI-INF/"; //$NON-NLS-1$

	// Common paths
	public static IPath MANIFEST_PATH = new Path(BUNDLE_FILENAME_DESCRIPTOR);
	public static IPath PLUGIN_PATH = new Path(PLUGIN_FILENAME_DESCRIPTOR);
	public static IPath FRAGMENT_PATH = new Path(FRAGMENT_FILENAME_DESCRIPTOR);
	public static IPath FEATURE_PATH = new Path(FEATURE_FILENAME_DESCRIPTOR);
	public static IPath BUILD_PROPERTIES_PATH = new Path(BUILD_FILENAME_DESCRIPTOR);
	public static IPath OSGI_INF_PATH = new Path(OSGI_INF_FOLDER_NAME);

}
