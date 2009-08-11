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

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;

public class MissingResourcePage extends HQDEFormPage {

	public MissingResourcePage(FormEditor editor) {
		super(editor, "missing", HQDEMessages.MissingResourcePage_missingResource); //$NON-NLS-1$
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		Composite comp = managedForm.getToolkit().createComposite(form);
		comp.setLayout(new GridLayout());
		IPersistableElement persistable = getEditorInput().getPersistable();
		String text;
		if (persistable instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) persistable).getFile();
			text = NLS.bind(HQDEMessages.MissingResourcePage_unableToOpenFull, 
					new String[] {HQDEMessages.MissingResourcePage_unableToOpen, 
								file.getProjectRelativePath().toString(), 
								file.getProject().getName()});
		} else
			text = HQDEMessages.MissingResourcePage_unableToOpen;
		form.setText(text);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
