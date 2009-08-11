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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.parts.EditableTablePart;
import org.hyperic.hypclipse.internal.parts.StructuredViewerPart;

/**
 * 
 *
 */
public abstract class TableSection extends StructuredViewerSection {

	protected boolean fHandleDefaultButton = true;
	
	/**
	 * 
	 * @param formPage
	 * @param parent
	 * @param style
	 * @param buttonLabels
	 */
	public TableSection(HQDEFormPage formPage, Composite parent,
			int style, String[] buttonLabels) {
		this(formPage, parent, style, true, buttonLabels);
	}

	/**
	 * 
	 * @param formPage
	 * @param parent
	 * @param style
	 * @param titleBar
	 * @param buttonLabels
	 */
	public TableSection(HQDEFormPage formPage, Composite parent,
			int style, boolean titleBar, String[] buttonLabels) {
		super(formPage, parent, style, titleBar, buttonLabels);
	}

	protected StructuredViewerPart createViewerPart(String[] buttonLabels) {
		return new PartAdapter(buttonLabels);
	}

	protected EditableTablePart getTablePart() {
		return (EditableTablePart) fViewerPart;
	}

	protected void entryModified(Object entry, String value) {
	}

	protected void selectionChanged(IStructuredSelection selection) {
	}

	protected void handleDoubleClick(IStructuredSelection selection) {
	}

	protected void enableButtons() {
	}
	protected boolean createCount() {
		return false;
	}

	
	class PartAdapter extends EditableTablePart {
		private Label fCount;

		public PartAdapter(String[] buttonLabels) {
			super(buttonLabels);
		}

		public void entryModified(Object entry, String value) {
			TableSection.this.entryModified(entry, value);
		}

		public void selectionChanged(IStructuredSelection selection) {
			getManagedForm().fireSelectionChanged(TableSection.this, selection);
			TableSection.this.selectionChanged(selection);
		}

		public void handleDoubleClick(IStructuredSelection selection) {
			TableSection.this.handleDoubleClick(selection);
		}

		public void buttonSelected(Button button, int index) {
			TableSection.this.buttonSelected(index);
			if (fHandleDefaultButton)
				button.getShell().setDefaultButton(null);
		}

		protected void createButtons(Composite parent, FormToolkit toolkit) {
			super.createButtons(parent, toolkit);
			enableButtons();
			if (createCount()) {
				Composite comp = toolkit.createComposite(fButtonContainer);
				comp.setLayout(createButtonsLayout());
				comp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_BOTH));
				fCount = toolkit.createLabel(comp, ""); //$NON-NLS-1$
				fCount.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				fCount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				getTablePart().getTableViewer().getTable().addPaintListener(new PaintListener() {
					public void paintControl(PaintEvent e) {
						updateLabel();
					}
				});
			}
		}

		protected void updateLabel() {
			if (fCount != null && !fCount.isDisposed())
				fCount.setText(NLS.bind("itemCount", Integer.toString(getTableViewer().getTable().getItemCount())));
		}
	}

}
