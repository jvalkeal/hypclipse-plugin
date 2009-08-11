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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;

public class EmptyOutlinePage implements ISortableContentOutlinePage {
	private Composite control;

	/**
	 * 
	 */
	public EmptyOutlinePage() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		control = new Composite(parent, SWT.NULL);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return control;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
	 */
	public void setActionBars(IActionBars actionBars) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return new ISelection() {
			public boolean isEmpty() {
				return true;
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.ISortableContentOutlinePage#sort(boolean)
	 */
	public void sort(boolean sorting) {
	}
}
