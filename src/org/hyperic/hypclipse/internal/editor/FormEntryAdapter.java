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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.hyperic.hypclipse.internal.parts.FormEntry;
import org.hyperic.hypclipse.internal.parts.IFormEntryListener;

public class FormEntryAdapter implements IFormEntryListener {
	private IContextPart contextPart;
	protected IActionBars actionBars;

	public FormEntryAdapter(IContextPart contextPart) {
		this(contextPart, null);
	}

	public FormEntryAdapter(IContextPart contextPart, IActionBars actionBars) {
		this.contextPart = contextPart;
		this.actionBars = actionBars;
	}

	// TODO no actions yet
	public void focusGained(FormEntry entry) {
		ITextSelection selection = new TextSelection(1, 1);
		//contextPart.getPage().getHQDEEditor().getContributor().updateSelectableActions(selection);
	}

	public void textDirty(FormEntry entry) {
		contextPart.fireSaveNeeded();
	}

	public void textValueChanged(FormEntry entry) {
	}

	public void browseButtonSelected(FormEntry entry) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkEntered(HyperlinkEvent e) {
		if (actionBars == null)
			return;
		IStatusLineManager mng = actionBars.getStatusLineManager();
		mng.setMessage(e.getLabel());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkExited(HyperlinkEvent e) {
		if (actionBars == null)
			return;
		IStatusLineManager mng = actionBars.getStatusLineManager();
		mng.setMessage(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent e) {
	}

	// TODO no actions yet
	public void selectionChanged(FormEntry entry) {
		ITextSelection selection = new TextSelection(1, 1);
		//contextPart.getPage().getHQDEEditor().getContributor().updateSelectableActions(selection);
	}
}
