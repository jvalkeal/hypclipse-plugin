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

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.context.InputContext;

/**
 * 
 *
 */
public abstract class MultiSourceEditor extends HQDEFormEditor {

	/**
	 * 
	 * @param contextId
	 */
	protected void addSourcePage(String contextId) {
		InputContext context = fInputContextManager.findContext(contextId);
		if (context == null)
			return;
		HQDESourcePage sourcePage;
		// Don't duplicate
		if (findPage(contextId) != null)
			return;
		sourcePage = createSourcePage(this, contextId, context.getInput().getName(), context.getId());
		sourcePage.setInputContext(context);
		try {
			addPage(sourcePage, context.getInput());
		} catch (PartInitException e) {
			HQDEPlugin.logException(e);
		}
	}

	/**
	 * 
	 * @param pageId
	 */
	protected void removePage(String pageId) {
		IFormPage page = findPage(pageId);
		if (page == null)
			return;
		if (page.isDirty()) {
			// need to ask the user about this
		} else {
			removePage(page.getIndex());
			if (!page.isEditor())
				page.dispose();
		}
	}

	/**
	 * 
	 * @param editor
	 * @param title
	 * @param name
	 * @param contextId
	 * @return
	 */
	protected HQDESourcePage createSourcePage(HQDEFormEditor editor, String title, String name, String contextId) {
		return new GenericSourcePage(editor, title, name);
	}
}
