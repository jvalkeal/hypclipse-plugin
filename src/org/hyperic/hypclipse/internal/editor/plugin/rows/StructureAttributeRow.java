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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.hyperic.hypclipse.internal.editor.IContextPart;
import org.hyperic.hypclipse.internal.schema.AnnotationExp;
import org.hyperic.hypclipse.internal.schema.ISchemaAttribute;
import org.hyperic.hypclipse.internal.text.HQDETextHover;
import org.hyperic.hypclipse.internal.text.IControlHoverContentProvider;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;

public abstract class StructureAttributeRow implements IControlHoverContentProvider {
	protected IContextPart part;
	protected Object att;
	protected IPluginParent input;
	protected boolean blockNotification;
	protected boolean dirty;
	protected IInformationControl fIC;
	
	private Label label;

	public StructureAttributeRow(IContextPart part, Expression att) {
		this.part = part;
		this.att = att;
	}

	public StructureAttributeRow(IContextPart part, ISchemaAttribute att) {
		this.part = part;
		this.att = att;
	}


	public StructureAttributeRow(IContextPart part, IPluginAttribute att) {
		this.part = part;
		this.att = att;
	}

	public ISchemaAttribute getAttribute() {
		return (att instanceof ISchemaAttribute) ? (ISchemaAttribute) att : null;
	}


	public String getName() {
		if (att instanceof AttributeExp)
			return ((AttributeExp) att).getNameClass().toString();
		if (att instanceof AnnotationExp)
			return ((AttributeExp)(((AnnotationExp)att).exp)).getNameClass().toString();
		return null;
	}


	protected String getDescription() {
		if(att instanceof AnnotationExp)
			return ((AnnotationExp)att).annotation;
		
		return "not found";
	}

	protected String getValue() {
		String value = "";
		if (input != null) {
			IPluginAttribute patt = ((PluginParentNode)input).getAttribute(getName());
			if (patt != null)
				value = patt.getValue();
		}
		return value;
	}

	protected String getPropertyLabel() {
		String label = getName();
		if (input != null) {
			String[] optional = ((PluginParentNode)input).getOptionalAttibutes();
			if (isOptional(optional, label))
				label += ":";
			else
				label += "*:";			
		} else {
			label += ":  ";			
		}
		return label;
	}
	
	private void updateLabel() {
		label.setText(getPropertyLabel());
	}
	
	private boolean isOptional(String[] opts, String name) {
		for (int i = 0; i < opts.length; i++) {
			if(opts[i].equals(name))
				return true;
		}
		return false;
	}

	protected void createLabel(Composite parent, FormToolkit toolkit) {
		label = toolkit.createLabel(parent, getPropertyLabel(), SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		HQDETextHover.addHoverListenerToControl(fIC, label, this);
	}

	/**
	 * @param control
	 */
	protected void createTextHover(Control control) {
		fIC = HQDETextHover.getInformationControlCreator().createInformationControl(control.getShell());
		fIC.setSizeConstraints(300, 600);
	}

	public String getHoverContent(Control c) {
		if (c instanceof Label || c instanceof Hyperlink)
			return getDescription();
		return null;
	}

	/**
	 * @param parent
	 * @param toolkit
	 * @param span
	 */
	public void createContents(Composite parent, FormToolkit toolkit, int span) {
		createTextHover(parent);
	}

	protected abstract void update();

	public abstract void commit();

	public abstract void setFocus();

	public boolean isDirty() {
		return dirty;
	}

	protected void markDirty() {
		dirty = true;
		part.fireSaveNeeded();
	}

	public void dispose() {
		if (fIC != null)
			fIC.dispose();
	}

	public void setInput(IPluginParent input) {
		this.input = input;
		update();
		updateLabel();
	}

	protected IProject getProject() {
		return part.getPage().getHQDEEditor().getCommonProject();
	}
}
