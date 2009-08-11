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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.editor.IContextPart;

import com.sun.msv.grammar.Expression;

public class BooleanAttributeRow extends ChoiceAttributeRow {
	/**
	 * @param att
	 */
	public BooleanAttributeRow(IContextPart part, Expression att) {
		super(part, att);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ExtensionElementEditor#createContents(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit, int)
	 */
	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		super.createContents(parent, toolkit, span);
//		if (getUse() != ISchemaAttribute.REQUIRED)
//			combo.add(""); //$NON-NLS-1$
		combo.add("true"); //$NON-NLS-1$
		combo.add("false"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.plugin.rows.ChoiceAttributeRow#isValid(java.lang.String)
	 */
	protected boolean isValid(String value) {
//		if (getUse() == ISchemaAttribute.REQUIRED)
//			return (value.equals("true") || value.equals("false")); //$NON-NLS-1$ //$NON-NLS-2$
		return (value.equals("true") || value.equals("false") || value.equals("")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.plugin.rows.ChoiceAttributeRow#getValidValue()
	 */
	protected String getValidValue() {
		return "true"; //$NON-NLS-1$
	}
}
