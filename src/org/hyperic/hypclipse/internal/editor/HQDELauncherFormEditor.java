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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class HQDELauncherFormEditor extends MultiSourceEditor {

	Action[][] fActions = null;

	protected abstract ILauncherFormPageHelper getLauncherHelper();

	protected void contributeLaunchersToToolbar(IToolBarManager manager) {

		// this should never be null (no point in using this class if you don't provide an ILauncherFormPageHelper)
		// but we'll guard against it anyway
		if (getLauncherHelper() != null) {
//
//			Action[][] actions = getActions();
//			if (actions[RUN_LAUNCHER_INDEX].length > 0) {
//				Action runAction = new ActionMenu(actions[RUN_LAUNCHER_INDEX]);
//				manager.add(runAction);
//			}
//
//			if (actions[DEBUG_LAUNCHER_INDEX].length > 0) {
//				Action runAction = new ActionMenu(actions[DEBUG_LAUNCHER_INDEX]);
//				manager.add(runAction);
//			}
//
//			if (actions[PROFILE_LAUNCHER_INDEX].length > 0) {
//				Action runAction = new ActionMenu(actions[PROFILE_LAUNCHER_INDEX]);
//				manager.add(runAction);
//			}
		}
	}

	private Action[][] getActions() {
		if (fActions == null) {
			fActions = new Action[3][];
//			IConfigurationElement[][] elements = getLaunchers(getLauncherHelper().isOSGi());
//			fActions[RUN_LAUNCHER_INDEX] = getLauncherActions(elements[RUN_LAUNCHER_INDEX]);
//			fActions[DEBUG_LAUNCHER_INDEX] = getLauncherActions(elements[DEBUG_LAUNCHER_INDEX]);
//			fActions[PROFILE_LAUNCHER_INDEX] = getLauncherActions(elements[PROFILE_LAUNCHER_INDEX]);
		}
		return fActions;
	}

	private Action[] getLauncherActions(IConfigurationElement[] elements) {
		Action[] result = new Action[elements.length];
		for (int i = 0; i < elements.length; i++) {
			String label = elements[i].getAttribute("label"); //$NON-NLS-1$
			final String thisLaunchShortcut = getLaunchString(elements[i]);
			Action thisAction = new Action(label) { //$NON-NLS-1$
				public void run() {
					doSave(null);
//					launch(thisLaunchShortcut, getPreLaunchRunnable(), getLauncherHelper().getLaunchObject());
				}
			};
			thisAction.setToolTipText(label);
			thisAction.setImageDescriptor(getImageDescriptor(elements[i]));
			result[i] = thisAction;
		}
		return result;
	}

	protected Runnable getPreLaunchRunnable() {
		return new Runnable() {
			public void run() {
//				getLauncherHelper().preLaunch();
			}
		};
	}

	public String getLaunchString(IConfigurationElement e) {
		StringBuffer sb = new StringBuffer("launchShortcut."); //$NON-NLS-1$
		sb.append(e.getAttribute("mode")); //$NON-NLS-1$
		sb.append("."); //$NON-NLS-1$
		sb.append(e.getAttribute("id")); //$NON-NLS-1$
		return sb.toString();
	}

	private ImageDescriptor getImageDescriptor(IConfigurationElement element) {
//		String mode = element.getAttribute("mode"); //$NON-NLS-1$
//		if (mode == null)
//			return null;
//		else if (mode.equals(ILaunchManager.RUN_MODE))
//			return PDEPluginImages.DESC_RUN_EXC;
//		else if (mode.equals(ILaunchManager.DEBUG_MODE))
//			return PDEPluginImages.DESC_DEBUG_EXC;
//		else if (mode.equals(ILaunchManager.PROFILE_MODE))
//			return PDEPluginImages.DESC_PROFILE_EXC;
		return null;
	}

	public void launch(String launchShortcut, Runnable preLaunch, Object launchObject) {
		if (launchShortcut.startsWith("launchShortcut.")) { //$NON-NLS-1$
			launchShortcut = launchShortcut.substring(15);
			int index = launchShortcut.indexOf('.');
			if (index < 0)
				return; // error.  Format of launchShortcut should be launchShortcut.<mode>.<launchShortcutId>
			String mode = launchShortcut.substring(0, index);
			String id = launchShortcut.substring(index + 1);
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor("org.eclipse.debug.ui.launchShortcuts"); //$NON-NLS-1$
			for (int i = 0; i < elements.length; i++) {
//				if (id.equals(elements[i].getAttribute("id"))) //$NON-NLS-1$
//					try {
//						ILaunchShortcut shortcut = (ILaunchShortcut) elements[i].createExecutableExtension("class"); //$NON-NLS-1$
//						preLaunch.run();
//						shortcut.launch(new StructuredSelection(launchObject), mode);
//					} catch (CoreException e1) {
//					}
			}
		}
	}

	protected static final int RUN_LAUNCHER_INDEX = 0;
	protected static final int DEBUG_LAUNCHER_INDEX = 1;
	protected static final int PROFILE_LAUNCHER_INDEX = 2;

	protected IConfigurationElement[][] getLaunchers(boolean osgi) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor("org.eclipse.pde.ui.launchShortcuts"); //$NON-NLS-1$
		// validate elements
		ArrayList runList = new ArrayList();
		ArrayList debugList = new ArrayList();
		ArrayList profileList = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			String mode = elements[i].getAttribute("mode"); //$NON-NLS-1$
			if (mode != null && elements[i].getAttribute("label") != null && elements[i].getAttribute("id") != null && //$NON-NLS-1$ //$NON-NLS-2$
					osgi == "true".equals(elements[i].getAttribute("osgi"))) { //$NON-NLS-1$ //$NON-NLS-2$
//				if (mode.equals(ILaunchManager.RUN_MODE))
//					runList.add(elements[i]);
//				else if (mode.equals(ILaunchManager.DEBUG_MODE))
//					debugList.add(elements[i]);
//				else if (mode.equals(ILaunchManager.PROFILE_MODE))
//					profileList.add(elements[i]);
			}
		}

		// sort elements based on criteria specified in bug 172703
		IConfigurationElement[] runElements = (IConfigurationElement[]) runList.toArray(new IConfigurationElement[runList.size()]);
		IConfigurationElement[] debugElements = (IConfigurationElement[]) debugList.toArray(new IConfigurationElement[debugList.size()]);
		IConfigurationElement[] profileElements = (IConfigurationElement[]) profileList.toArray(new IConfigurationElement[profileList.size()]);
		Comparator comparator = new Comparator() {
			public int compare(Object arg0, Object arg1) {
				String label1 = ((IConfigurationElement) arg0).getAttribute("label"); //$NON-NLS-1$
				String label2 = ((IConfigurationElement) arg1).getAttribute("label"); //$NON-NLS-1$
				return label1.compareTo(label2);
			}
		};
		Arrays.sort(runElements, comparator);
		Arrays.sort(debugElements, comparator);
		Arrays.sort(profileElements, comparator);
		return new IConfigurationElement[][] {runElements, debugElements, profileElements};
	}
}
