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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class HQDEMultiPageContentOutline 
	extends Page 
	implements 	IContentOutlinePage, 
				ISelectionProvider, 
				ISelectionChangedListener 
				/*IPreferenceConstants*/ {
	// TODO above IPreferenceConstants disabled
	private PageBook pagebook;
	private ISelection selection;
	private ArrayList<ISelectionChangedListener> listeners;
	private ISortableContentOutlinePage currentPage;
	private ISortableContentOutlinePage emptyPage;
	private IActionBars actionBars;
	private boolean sortingOn;
	private HQDEFormEditor editor;

	public HQDEMultiPageContentOutline(HQDEFormEditor editor) {
		this.editor = editor;
		listeners = new ArrayList<ISelectionChangedListener>();
		sortingOn = HQDEPlugin.getDefault().getPreferenceStore().getBoolean("PDEMultiPageContentOutline.SortingAction.isChecked"); //$NON-NLS-1$

	}

	public void addFocusListener(FocusListener listener) {
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	public void createControl(Composite parent) {
		pagebook = new PageBook(parent, SWT.NONE);
	}

	public void dispose() {
		if (pagebook != null && !pagebook.isDisposed())
			pagebook.dispose();
		if (emptyPage != null) {
			emptyPage.dispose();
			emptyPage = null;
		}
		pagebook = null;
		listeners = null;
	}

	public boolean isDisposed() {
		return listeners == null;
	}

	public Control getControl() {
		return pagebook;
	}

	public PageBook getPagebook() {
		return pagebook;
	}

	public ISelection getSelection() {
		return selection;
	}

	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
	}

	public void removeFocusListener(FocusListener listener) {
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		setSelection(event.getSelection());
	}

	public void setActionBars(IActionBars actionBars) {
		this.actionBars = actionBars;
		registerToolbarActions(actionBars);
		if (currentPage != null)
			setPageActive(currentPage);

	}

	public IActionBars getActionBars() {
		return actionBars;
	}

	public void setFocus() {
		if (currentPage != null)
			currentPage.setFocus();
	}

	private ISortableContentOutlinePage getEmptyPage() {
		if (emptyPage == null)
			emptyPage = new EmptyOutlinePage();
		return emptyPage;
	}

	public void setPageActive(ISortableContentOutlinePage page) {
		if (page == null) {
			page = getEmptyPage();
		}
		if (currentPage != null) {
			currentPage.removeSelectionChangedListener(this);
		}
		//page.init(getSite());
		page.sort(sortingOn);
		page.addSelectionChangedListener(this);
		this.currentPage = page;
		if (pagebook == null) {
			// still not being made
			return;
		}
		Control control = page.getControl();
		if (control == null || control.isDisposed()) {
			// first time
			page.createControl(pagebook);
			page.setActionBars(getActionBars());
			control = page.getControl();
		}
		pagebook.showPage(control);
		this.currentPage = page;
	}

	/**
	 * Set the selection.
	 */
	public void setSelection(ISelection selection) {
		this.selection = selection;
		if (listeners == null)
			return;
		SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
		for (int i = 0; i < listeners.size(); i++) {
			((ISelectionChangedListener) listeners.get(i)).selectionChanged(e);
		}
	}

	private void registerToolbarActions(IActionBars actionBars) {

		// TODO toolbar removed
//		IToolBarManager toolBarManager = actionBars.getToolBarManager();
//		if (toolBarManager != null) {
//			toolBarManager.add(new ToggleLinkWithEditorAction(editor));
//			toolBarManager.add(new SortingAction());
//		}
	}

	class SortingAction extends Action {

		public SortingAction() {
			super();
			// TODO add help
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IHelpContextIds.OUTLINE_SORT_ACTION);
			setText(HQDEMessages.HQDEMultiPageContentOutline_SortingAction_label);
			// TODO add images
//			setImageDescriptor(PDEPluginImages.DESC_ALPHAB_SORT_CO);
//			setDisabledImageDescriptor(PDEPluginImages.DESC_ALPHAB_SORT_CO_DISABLED);
			setToolTipText(HQDEMessages.HQDEMultiPageContentOutline_SortingAction_tooltip);
			setDescription(HQDEMessages.HQDEMultiPageContentOutline_SortingAction_description);
			setChecked(sortingOn);
		}

		public void run() {
			setChecked(isChecked());
			valueChanged(isChecked());
		}

		private void valueChanged(final boolean on) {
			sortingOn = on;
			if (currentPage != null)
				currentPage.sort(on);
			HQDEPlugin.getDefault().getPreferenceStore().setValue("PDEMultiPageContentOutline.SortingAction.isChecked", on); //$NON-NLS-1$
		}

	}
}
