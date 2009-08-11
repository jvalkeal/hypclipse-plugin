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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.hyperic.hypclipse.internal.HQDELabelProvider;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.IHQDEConstants;
import org.hyperic.hypclipse.internal.editor.build.ClasspathPage;
import org.hyperic.hypclipse.internal.editor.plugin.HQPluginEnvironmentSection;
import org.hyperic.hypclipse.internal.editor.plugin.PluginExportAction;
import org.hyperic.hypclipse.internal.editor.plugin.StructurePage;
import org.hyperic.hypclipse.internal.util.SharedLabelProvider;

public class OverviewPage extends LaunchShortcutOverviewPage {

	public static final String PAGE_ID = "overview";
	private GeneralInfoSection fInfoSection;
	private PluginExportAction fExportAction;
	
	public OverviewPage(HQDELauncherFormEditor editor) {
		super(editor, PAGE_ID, HQDEMessages.OverviewPage_title);
	}

	
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
//		if (isFragment()) {
//			form.setImage(PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_FRAGMENT_MF_OBJ));
//		} else {
//			form.setImage(PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_PLUGIN_MF_OBJ));
//		}
		form.setText(HQDEMessages.OverviewPage_title);
		fillBody(managedForm, toolkit);

		
	}
	
	private void fillBody(IManagedForm managedForm, FormToolkit toolkit) {
		Composite body = managedForm.getForm().getBody();
		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));
		

		Composite left = toolkit.createComposite(body);
		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		fInfoSection = new GeneralInfoSection(this, left);
		managedForm.addPart(fInfoSection);
		
		managedForm.addPart(new HQPluginEnvironmentSection(this, left));

		Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		createContentSection(managedForm, right, toolkit);
		
		createExportingSection(managedForm, right, toolkit);


	}

	private void createExportingSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit) {
		Section section = createStaticSection(toolkit, parent, HQDEMessages.OverviewPage_DeployingSection_title);
		Composite container = createStaticSectionClient(toolkit, section);
		createClient(container, HQDEMessages.OverviewPage_deploying, toolkit);
		section.setClient(container);
	}
	
	private void createContentSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit) {
		String sectionTitle = HQDEMessages.OverviewPage_ContentSection_title;
		Section section = createStaticSection(toolkit, parent, sectionTitle);

		Composite container = createStaticSectionClient(toolkit, section);

		FormText text = createClient(container, HQDEMessages.OverviewPage_content, toolkit);
		HQDELabelProvider lp = HQDEPlugin.getDefault().getLabelProvider();
		text.setImage("page", lp.get(HQDEPluginImages.DESC_PAGE_OBJ, SharedLabelProvider.F_EDIT));

		section.setClient(container);
	}

	
	protected Composite createStaticSectionClient(FormToolkit toolkit, Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		container.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 1));
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		container.setLayoutData(data);
		return container;
	}

	public void linkActivated(HyperlinkEvent e) {
		String href = (String) e.getHref();

		if (href.equals("export"))
			getExportAction().run();
		else if (href.equals("structure"))
			getEditor().setActivePage(StructurePage.PAGE_ID);
		else if (href.equals("build"))
			getEditor().setActivePage(ClasspathPage.PAGE_ID);
		super.linkActivated(e);
	}

	private PluginExportAction getExportAction() {
		if (fExportAction == null)
			fExportAction = new PluginExportAction((HQDEFormEditor) getEditor());
		return fExportAction;
	}



	protected short getIndent() {
		return 5;
	}

	protected String getHelpResource() {
		return IHQDEConstants.PLUGIN_DOC_ROOT + "help/html/tools/editor/overview_tab.html";
	}


}
