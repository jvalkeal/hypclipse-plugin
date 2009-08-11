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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.plugin.IBaseModel;

/**
 * 
 *
 */
public abstract class HQDEFormPage extends FormPage {

	private boolean fNewStyleHeader = true;
	private Control fLastFocusControl;


	private boolean fStale;

	/**
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 */
	public HQDEFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		this.fLastFocusControl = null;
		this.fStale = false;
	}

	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		if (fNewStyleHeader) {
			toolkit.decorateFormHeading(form.getForm());
		}

		IToolBarManager manager = form.getToolBarManager();

		getHQDEEditor().contributeToToolbar(manager);

		final String href = getHelpResource();
		if (href != null) {
			Action helpAction = new Action("help") {
				public void run() {
					BusyIndicator.showWhile(form.getDisplay(), new Runnable() {
						public void run() {
							PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(href);
						}
					});
				}
			};
			helpAction.setToolTipText(HQDEMessages.HQDEFormPage_help);
			helpAction.setImageDescriptor(HQDEPluginImages.DESC_HELP);
			manager.add(helpAction);
		}
		//check to see if our form parts are contributing actions
//		IFormPart[] parts = managedForm.getParts();
//		for (int i = 0; i < parts.length; i++) {
//			if (parts[i] instanceof IAdaptable) {
//				IAdaptable adapter = (IAdaptable) parts[i];
//				IAction[] actions = (IAction[]) adapter.getAdapter(IAction[].class);
//				if (actions != null) {
//					for (int j = 0; j < actions.length; j++) {
//						form.getToolBarManager().add(actions[j]);
//					}
//				}
//			}
//		}
		form.updateToolBar();

	}

	protected String getHelpResource() {
		return null;
	}

	public IBaseModel getModel() {
		return getHQDEEditor().getAggregateModel();
	}

	
	protected void markStale() {
		fStale = true;
	}

	/**
	 * @return true if form is stale, false otherwise
	 */
	protected boolean isStale() {
		return fStale;
	}

	protected void refresh() {
		fStale = false;
	}

	public void setActive(boolean active) {
		super.setActive(active);
		if (active && isStale()) {
			refresh();
		}
	}

	public void dispose() {
		Control c = getPartControl();
		if (c != null && !c.isDisposed()) {
			Menu menu = c.getMenu();
			if (menu != null)
				resetMenu(menu, c);
		}
		super.dispose();
	}

	private void resetMenu(Menu menu, Control c) {
		if (c instanceof Composite) {
			Composite comp = (Composite) c;
			Control[] children = comp.getChildren();
			for (int i = 0; i < children.length; i++) {
				resetMenu(menu, children[i]);
			}
		}
		Menu cmenu = c.getMenu();
		if (cmenu != null && cmenu.equals(menu)) {
			c.setMenu(null);
		}
	}

	
	/**
	 * 
	 * @return
	 */
	public HQDEFormEditor getHQDEEditor() {
		return (HQDEFormEditor) getEditor();
	}

	public void cancelEdit() {
		IFormPart[] parts = getManagedForm().getParts();
		for (int i = 0; i < parts.length; i++) {
			IFormPart part = parts[i];
			if (part instanceof IContextPart)
				((IContextPart) part).cancelEdit();
		}
	}

	public void addLastFocusListeners(Composite composite) {
		Control[] controls = composite.getChildren();
		for (int i = 0; i < controls.length; i++) {
			Control control = controls[i];
			// Add a focus listener if the control is any one of the below types
			// Note that the controls listed below represent all the controls
			// currently in use by all form pages in PDE.  In the future,
			// more controls will have to be added.
			// Could not add super class categories of controls because it 
			// would include things like tool bars that we don't want to track
			// focus for.
			if ((control instanceof Text) || (control instanceof Button) || (control instanceof Combo) || (control instanceof CCombo) || (control instanceof Tree) || (control instanceof Table) || (control instanceof Spinner) || (control instanceof Link) || (control instanceof List) || (control instanceof TabFolder) || (control instanceof CTabFolder) || (control instanceof Hyperlink) || (control instanceof FilteredTree)) {
				addLastFocusListener(control);
			}
			if (control instanceof Composite) {
				// Recursively add focus listeners to this composites children
				addLastFocusListeners((Composite) control);
			}
		}
	}

	private void addLastFocusListener(final Control control) {
		control.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// NO-OP
			}

			public void focusLost(FocusEvent e) {
				fLastFocusControl = control;
			}
		});
	}

	/**
	 * Set the focus on the last control to have focus before a page change
	 * or the editor lost focus.
	 */
	public void updateFormSelection() {
		if ((fLastFocusControl != null) && (fLastFocusControl.isDisposed() == false)) {
			// Set focus on the control
			fLastFocusControl.setFocus();
			// If the control is a Text widget, select its contents
			if (fLastFocusControl instanceof Text) {
				Text text = (Text) fLastFocusControl;
				text.setSelection(0, text.getText().length());
			}
		} else {
			// No focus control set
			// Fallback on managed form selection mechanism by setting the 
			// focus on this page itself.
			// The managed form will set focus on the first managed part.
			// Most likely this will turn out to be a section.
			// In order for this to work properly, we must override the 
			// sections setFocus() method and set focus on a child control
			// (preferrably first) that can practically take focus.
			setFocus();
		}
	}

	
	public void alignSectionHeaders(Section masterSection, Section detailsSection) {
		detailsSection.descriptionVerticalSpacing += masterSection.getTextClientHeightDifference();
	}


}