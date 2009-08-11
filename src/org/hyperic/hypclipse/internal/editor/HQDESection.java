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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;

/**
 * 
 *
 */
public abstract class HQDESection extends SectionPart implements IContextPart {

	private HQDEFormPage fPage;
	
	public HQDESection(HQDEFormPage page, Composite parent, int style) {
		this(page, parent, style, true);
	}

	public HQDESection(HQDEFormPage page, Composite parent, int style, boolean titleBar) {
		super(parent, page.getManagedForm().getToolkit(), titleBar ? (ExpandableComposite.TITLE_BAR | style) : style);
		fPage = page;
		initialize(page.getManagedForm());
		getSection().clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		getSection().setData("part", this);
	}

	protected abstract void createClient(Section section, FormToolkit toolkit);

	public HQDEFormPage getPage() {
		return fPage;
	}
	
	protected IProject getProject() {
		return fPage.getHQDEEditor().getCommonProject();
	}
	
	public boolean doGlobalAction(String actionId) {
		return false;
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED)
			markStale();
	}

	public String getContextId() {
		return null;
	}

	public void fireSaveNeeded() {
		markDirty();
		if (getContextId() != null)
			getPage().getHQDEEditor().fireSaveNeeded(getContextId(), false);
	}

	public boolean isEditable() {
		// getAggregateModel() can (though never should) return null
//		IBaseModel model = getPage().getPDEEditor().getAggregateModel();
//		return model == null ? false : model.isEditable();
		return true;
	}

	public boolean canCopy(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	public boolean canCut(ISelection selection) {
		// Sub-classes to override
		return false;
	}

	public boolean canPaste(Clipboard clipboard) {
		return false;
	}

	public void cancelEdit() {
		super.refresh();
	}

}
