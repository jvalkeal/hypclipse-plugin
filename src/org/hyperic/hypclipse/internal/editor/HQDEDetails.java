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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class HQDEDetails extends AbstractFormPart implements IDetailsPage, IContextPart {

	public HQDEDetails() {
	}

	public boolean canPaste(Clipboard clipboard) {
		return true;
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCopy(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCut(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	public boolean doGlobalAction(String actionId) {
		return false;
	}

	protected void markDetailsPart(Control control) {
		control.setData("part", this);
	}

	protected void createSpacer(FormToolkit toolkit, Composite parent, int span) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	public void cancelEdit() {
		super.refresh();
	}
}
