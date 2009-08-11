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

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.fieldassist.*;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.ide.IDE;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class HQDEJavaHelperUI {

	private static HashMap fDocMap = new HashMap();

	public static String selectType(IResource resource, int scope) {
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		try {
			SelectionDialog dialog = JavaUI.createTypeDialog(HQDEPlugin.getActiveWorkbenchShell(), PlatformUI.getWorkbench().getProgressService(), HQDEJavaHelper.getSearchScope(project), scope, false, ""); //$NON-NLS-1$
			dialog.setTitle("HQDEMessages.ClassAttributeRow_dialogTitle");
			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getResult()[0];
				return type.getFullyQualifiedName('$');
			}
		} catch (JavaModelException e) {
		}
		return null;
	}

	public static String selectType(IResource resource, int scope, String filter, String superTypeName) {
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		try {
			IJavaSearchScope searchScope = null;
			if (superTypeName != null && !superTypeName.equals("java.lang.Object")) {
				IJavaProject javaProject = JavaCore.create(project);
				IType superType = javaProject.findType(superTypeName);
				if (superType != null)
					searchScope = SearchEngine.createHierarchyScope(superType);
			}
			if (searchScope == null)
				searchScope = HQDEJavaHelper.getSearchScope(project);

			SelectionDialog dialog = JavaUI.createTypeDialog(HQDEPlugin.getActiveWorkbenchShell(), PlatformUI.getWorkbench().getProgressService(), searchScope, scope, false, filter);
			dialog.setTitle("HQDEMessages.ClassAttributeRow_dialogTitle");
			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getResult()[0];
				return type.getFullyQualifiedName('$');
			}
		} catch (JavaModelException e) {
		}
		return null;
	}

	
	public static String openClass(String name, IProject project) {
		name = TextUtil.trimNonAlphaChars(name).replace('$', '.');
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				IJavaElement result = null;
				if (name.length() > 0)
					result = javaProject.findType(name);
				if (result != null)
					JavaUI.openInEditor(result);
			}
		} catch (PartInitException e) {
			HQDEPlugin.logException(e);
		} catch (JavaModelException e) {
			Display.getCurrent().beep();
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
		return null;
	}


}
