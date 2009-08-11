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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.IContextPart;
import org.hyperic.hypclipse.internal.schema.ISchemaAttribute;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IPluginAttribute;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;

public class TextAttributeRow extends StructureAttributeRow {
	protected Text text;

	public TextAttributeRow(IContextPart part, Expression att) {
		super(part, att);
	}

	public TextAttributeRow(IContextPart part, ISchemaAttribute att) {
		super(part, att);
	}

	public TextAttributeRow(IContextPart part, IPluginAttribute att) {
		super(part, att);
	}

	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		super.createContents(parent, toolkit, span);
		createLabel(parent, toolkit);
		text = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
		text.setLayoutData(createGridData(span));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!blockNotification)
					markDirty();
//				PDETextHover.updateHover(fIC, getHoverContent(text));
			}
		});
		text.setEditable(part.isEditable());
//		PDETextHover.addHoverListenerToControl(fIC, text, this);
		// Create a focus listener to update selectable actions
		createUITextFocusListener();
	}

	/**
	 * 
	 */
	private void createUITextFocusListener() {
		// Required to enable Ctrl-V paste operations
		text.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				ITextSelection selection = new TextSelection(1, 1);
//				part.getPage().getHQDEEditor().getContributor().updateSelectableActions(selection);
			}
		});
	}

	protected GridData createGridData(int span) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 20;
		gd.horizontalSpan = span - 1;
		gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
		return gd;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.plugin.rows.StructureAttributeRow#update()
	 */
	protected void update() {
		blockNotification = true;
		text.setText(getValue());
		blockNotification = false;
	}

	public void commit() {
		if (dirty && input != null) {
			String value = text.getText();
			try {
				((PluginParentNode)input).setAttribute(getName(), value);
				dirty = false;
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
		}
	}

	public void setFocus() {
		text.setFocus();
	}
}
