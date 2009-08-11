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
package org.hyperic.hypclipse.internal.editor.plugin;

import org.hyperic.hypclipse.internal.editor.HQDELauncherFormEditor;
import org.hyperic.hypclipse.internal.editor.HQDEPluginEditor;
import org.hyperic.hypclipse.internal.editor.ILauncherFormPageHelper;


public class PluginLauncherFormPageHelper implements ILauncherFormPageHelper {
	HQDELauncherFormEditor fEditor;

	public PluginLauncherFormPageHelper(HQDELauncherFormEditor editor) {
		fEditor = editor;
	}

	public Object getLaunchObject() {
		return fEditor.getCommonProject();
	}

//	public boolean isOSGi() {
//		return !((HQDEPluginEditor) fEditor).showExtensionTabs();
//	}

	public void preLaunch() {
	}
}
