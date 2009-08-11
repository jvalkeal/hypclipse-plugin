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
package org.hyperic.hypclipse.internal.editor;

/**
 * This is an interface used only by subclasses of PDELauncherFormPage.
 * It's purpose is to allow code reuse between direct subclasses of
 * HQDELauncherFormPage and subclasses of LaunchShortcutOverviewPage.
 */

public interface ILauncherFormPageHelper {
	public void preLaunch();

	public Object getLaunchObject();

//	public boolean isOSGi();
}