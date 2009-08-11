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
package org.hyperic.hypclipse.internal.editor.plugin;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.HQDESection;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;

public class StructureDetails extends AbstractPluginElementDetails {

	public StructureDetails(HQDESection masterSection) {
		super(masterSection);
	}

	public void createContents(Composite parent) {

	}

	public void selectionChanged(IFormPart part, ISelection selection) {

	}

	public void fireSaveNeeded() {

	}

	public String getContextId() {
		return null;
	}

	public HQDEFormPage getPage() {
		return null;
	}

	public boolean isEditable() {
		return false;
	}

	public void modelChanged(IModelChangedEvent event) {

	}

}
