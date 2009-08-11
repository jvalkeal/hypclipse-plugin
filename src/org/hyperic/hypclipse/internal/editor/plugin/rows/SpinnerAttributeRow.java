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
package org.hyperic.hypclipse.internal.editor.plugin.rows;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.IContextPart;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;

import com.sun.msv.grammar.Expression;

public class SpinnerAttributeRow  extends StructureAttributeRow {

	protected Spinner fSpinner;
	protected Button fDefault;
	
	public SpinnerAttributeRow(IContextPart part, Expression att) {
		super(part, att);
	}

	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		super.createContents(parent, toolkit, span);
		createLabel(parent, toolkit);
		
		fSpinner = new Spinner(parent, SWT.BORDER);
		fSpinner.setLayoutData(createGridData(span));
		fSpinner.setMinimum(1);
		fSpinner.setMaximum(999);
		fSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!blockNotification)
					markDirty();
			}
		});
		fDefault = toolkit.createButton(parent, HQDEMessages.SpinnerAttributeRow_Use_Default, SWT.CHECK);
		fDefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fSpinner.setEnabled(!fDefault.getSelection());
				if (!blockNotification)
					markDirty();
			}
		});
	}
	
	
	protected GridData createGridData(int span) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 20;
		gd.horizontalSpan = span - 2;
		gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT - 1;
		return gd;
	}



	public void commit() {
		if (dirty && input != null) {
			String value = "";
			if(!fDefault.getSelection())
				value = fSpinner.getText();
			try {
				((PluginParentNode)input).setAttribute(getName(), value);
				dirty = false;
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
		}

	}

	public void setFocus() {
		fSpinner.setFocus();
	}

	protected void update() {
		blockNotification = true;
		String val = getValue();
		if(val != null && val.length() > 0) {
			int v = 1;
			try {
				v = Integer.parseInt(val);
			} catch (NumberFormatException e) {
			}
			fSpinner.setSelection(v);
			fDefault.setSelection(false);
			fSpinner.setEnabled(true);
		} else {
			fDefault.setSelection(true);
			fSpinner.setEnabled(false);
		}
		blockNotification = false;
	}

}
