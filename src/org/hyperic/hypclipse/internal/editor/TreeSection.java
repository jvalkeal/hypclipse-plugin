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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.parts.StructuredViewerPart;
import org.hyperic.hypclipse.internal.parts.TreePart;

public abstract class TreeSection extends StructuredViewerSection {

	protected boolean fHandleDefaultButton = true;

	class PartAdapter extends TreePart {
		public PartAdapter(String[] buttonLabels) {
			super(buttonLabels);
		}

		public void selectionChanged(IStructuredSelection selection) {
			getManagedForm().fireSelectionChanged(TreeSection.this, selection);
			TreeSection.this.selectionChanged(selection);
		}

		public void handleDoubleClick(IStructuredSelection selection) {
			TreeSection.this.handleDoubleClick(selection);
		}

		public void buttonSelected(Button button, int index) {
			TreeSection.this.buttonSelected(index);
			if (fHandleDefaultButton)
				button.getShell().setDefaultButton(null);
		}

		protected void createButtons(Composite parent, FormToolkit toolkit) {
			super.createButtons(parent, toolkit);
			enableButtons();
			if (parent.getData("filtered") != null) { //$NON-NLS-1$
				GridLayout layout = (GridLayout) fButtonContainer.getLayout();
				layout.marginHeight = 28;
			}
		}

		protected TreeViewer createTreeViewer(Composite parent, int style) {
			return TreeSection.this.createTreeViewer(parent, style);
		}

	}

	/**
	 * Constructor for TableSection.
	 * @param formPage
	 */
	public TreeSection(HQDEFormPage formPage, Composite parent, int style, String[] buttonLabels) {
		super(formPage, parent, style, buttonLabels);
	}

	protected StructuredViewerPart createViewerPart(String[] buttonLabels) {
		return new PartAdapter(buttonLabels);
	}

	protected TreePart getTreePart() {
		return (TreePart) fViewerPart;
	}

	protected TreeViewer createTreeViewer(Composite parent, int style) {
		return new TreeViewer(parent, style);
	}

	protected void selectionChanged(IStructuredSelection selection) {
	}

	/**
	 * Expands or collapsed selected node according to its current state
	 * @param selection
	 */
	protected void handleDoubleClick(IStructuredSelection selection) {
		TreeViewer viewer = (TreeViewer) fViewerPart.getViewer();
		boolean expandedState = viewer.getExpandedState(selection.getFirstElement());
		viewer.setExpandedState(selection.getFirstElement(), !expandedState);
	}

	protected void enableButtons() {
	}
}
