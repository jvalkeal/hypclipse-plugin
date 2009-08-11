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
package org.hyperic.hypclipse.internal.editor.build;

import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHQDEConstants;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDELauncherFormEditor;
import org.hyperic.hypclipse.internal.editor.LaunchShortcutOverviewPage;

/**
 * Classpath page handles various tasks related to 
 * class resolving. 
 * 
 * Page defines what classes are needed to successfully 
 * compile custom classes. Also page allows modification 
 * of classpath tag in hq-plugin.xml file.
 * 
 */
public class ClasspathPage extends LaunchShortcutOverviewPage {
	
	/** Page id */
	public static final String PAGE_ID = "classpath";
	private BuildContentsSection fBinSection;

	
	private BuildClasspathSection fClasspathSection;

	/**
	 * Default constructor
	 * @param editor Parent editor
	 */
	public ClasspathPage(HQDELauncherFormEditor editor) {
		super(editor, PAGE_ID, HQDEMessages.ClasspathPage_title);
	}

	/**
	 * 
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		
///		form.setImage(PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_BUILD_EXEC));
		form.setText(HQDEMessages.ClasspathPage_title);
		form.getBody().setLayout(FormLayoutFactory.createFormGridLayout(true, 2));

		fBinSection = new BinSection(this, form.getBody());
		fBinSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));

		
		fClasspathSection = new BuildClasspathSection(this, form.getBody());
		
		managedForm.addPart(fBinSection);
		managedForm.addPart(fClasspathSection);
		
	}

	/**
	 * 
	 * @param managedForm
	 * @param toolkit
	 */
	private void fillBody(IManagedForm managedForm, FormToolkit toolkit) {
		
	}

	protected short getIndent() {
		return 5;
	}

	protected String getHelpResource() {
		return IHQDEConstants.PLUGIN_DOC_ROOT + "help/html/tools/editor/classpath_tab.html";
	}


}
