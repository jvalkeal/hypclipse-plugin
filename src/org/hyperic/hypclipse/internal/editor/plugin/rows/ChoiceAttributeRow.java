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

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.IContextPart;
import org.hyperic.hypclipse.internal.parts.ComboPart;
import org.hyperic.hypclipse.internal.schema.AttributeChoiceCollector;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;


import com.sun.msv.grammar.Expression;

public class ChoiceAttributeRow extends StructureAttributeRow {
	protected ComboPart combo;

	/**
	 * @param att
	 */

	public ChoiceAttributeRow(IContextPart part, Expression att) {
		super(part, att);
	}

	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		super.createContents(parent, toolkit, span);
		createLabel(parent, toolkit);
		combo = new ComboPart();
		combo.createControl(parent, toolkit, SWT.READ_ONLY);
		
		AttributeChoiceCollector col1 = new AttributeChoiceCollector();
		ArrayList<String> res = new ArrayList<String>();
		col1.collect((Expression)att, res);
		ListIterator<String> iter = res.listIterator();
		combo.add("");
		while(iter.hasNext()) {
			combo.add(iter.next());
		}

		
//		ISchemaSimpleType type = getAttribute().getType();
//		ISchemaRestriction restriction = type.getRestriction();
//		if (restriction != null) {
//			Object rchildren[] = restriction.getChildren();
//			if (getUse() != ISchemaAttribute.REQUIRED)
//				combo.add(""); //$NON-NLS-1$
//			for (int i = 0; i < rchildren.length; i++) {
//				Object rchild = rchildren[i];
//				if (rchild instanceof ISchemaEnumeration)
//					combo.add(((ISchemaEnumeration) rchild).getName());
//			}
//		}
		GridData gd = new GridData(span == 2 ? GridData.FILL_HORIZONTAL : GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 20;
		gd.horizontalSpan = span - 1;
		gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
		combo.getControl().setLayoutData(gd);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!blockNotification)
					markDirty();
			}
		});
		combo.getControl().setEnabled(part.isEditable());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ExtensionElementEditor#update(org.eclipse.pde.internal.ui.neweditor.plugin.DummyExtensionElement)
	 */
	protected void update() {
		blockNotification = true;
		String value = getValue();
		if (value != null && isValid(value))
			combo.setText(value);
//		else if (getUse() == ISchemaAttribute.REQUIRED)
//			combo.setText(getValidValue());
		else
			combo.setText(""); //$NON-NLS-1$
		blockNotification = false;
		dirty = false;
	}

	protected String getValidValue() {
//		ISchemaAttribute attInfo = getAttribute();
//		if (attInfo.getType().getRestriction() != null)
//			return attInfo.getType().getRestriction().getChildren()[0].toString();
		return ""; //$NON-NLS-1$
	}

	protected boolean isValid(String value) {
//		if (getAttribute().getUse() != ISchemaAttribute.REQUIRED && value.equals("")) //$NON-NLS-1$
//			return true;
//
//		ISchemaRestriction restriction = getAttribute().getType().getRestriction();
//		if (restriction == null)
//			return true;
//		Object[] children = restriction.getChildren();
//		for (int i = 0; i < children.length; i++) {
//			Object rchild = children[i];
//			if (rchild instanceof ISchemaEnumeration && ((ISchemaEnumeration) rchild).getName().equals(value))
//				return true;
//		}
//		return false;
		return true;
	}

	public void commit() {
		if (dirty && input != null) {
			try {
				String selection = combo.getSelection();
				if (selection.length() == 0)
					selection = null;
				((PluginParentNode)input).setAttribute(getName(), selection);
				dirty = false;
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
		}
	}

	public void setFocus() {
		combo.getControl().setFocus();
	}
}
