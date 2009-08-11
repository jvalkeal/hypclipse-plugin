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
package org.hyperic.hypclipse.plugin;

/**
 * Listing of constants used in PDE preferences
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPreferenceConstants {

	// Main preference page	
//	public static final String PROP_SHOW_OBJECTS = "Preferences.MainPage.showObjects"; //$NON-NLS-1$
//	public static final String VALUE_USE_IDS = "useIds"; //$NON-NLS-1$
//	public static final String VALUE_USE_NAMES = "useNames"; //$NON-NLS-1$
//	public static final String PROP_AUTO_MANAGE = "Preferences.MainPage.automanageDependencies"; //$NON-NLS-1$
//	public static final String OVERWRITE_BUILD_FILES_ON_EXPORT = "Preferences.MainPage.overwriteBuildFilesOnExport"; //$NON-NLS-1$
	public static final String OVERWRITE_PLUGIN_FILE_ON_EXPORT = "Preferences.MainPage.overwritePluginFileOnExport";

	// Editor Outline
//	public static final String PROP_OUTLINE_SORTING = "PDEMultiPageContentOutline.SortingAction.isChecked"; //$NON-NLS-1$

	// Editor folding
	public static final String EDITOR_FOLDING_ENABLED = "editor.folding";

	// Dependencies view
//	public static final String DEPS_VIEW_SHOW_CALLERS = "DependenciesView.show.callers"; //$NON-NLS-1$
//	public static final String DEPS_VIEW_SHOW_LIST = "DependenciesView.show.list";
//	public static final String DEPS_VIEW_SHOW_STATE = "DependenciesView.show.state";
}
