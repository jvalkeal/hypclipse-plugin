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

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.IHQDEConstants;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.HQDELauncherFormEditor;
import org.hyperic.hypclipse.internal.editor.HQDEMasterDetailsBlock;
import org.hyperic.hypclipse.internal.editor.HQDESection;
import org.hyperic.hypclipse.internal.editor.LaunchShortcutOverviewPage;
import org.hyperic.hypclipse.internal.hqmodel.HQServerNode;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentRange;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IPluginElement;

public class StructurePage extends LaunchShortcutOverviewPage {

	public static final String PAGE_ID = "structure";
	private StructureBlock fBlock;
	private StructureSection fSection;
	
	public StructurePage(HQDELauncherFormEditor editor) {
		super(editor, PAGE_ID, HQDEMessages.StructurePage_title);
		fBlock = new StructureBlock();
	}
	
	
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(HQDEMessages.StructurePage_title);
//		form.setImage(PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_EXTENSIONS_OBJ));
		fBlock.createContent(managedForm);
		//refire selection
//		fSection.fireSelection();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), "IHelpContextIds.EDITOR_PLUGIN_STRUCTURE");
		super.createFormContent(managedForm);
	}

	public void updateFormSelection() {
		super.updateFormSelection();
		IFormPage page = getHQDEEditor().findPage(PluginInputContext.CONTEXT_ID);
		if (page instanceof PluginSourcePage) {
			ISourceViewer viewer = ((PluginSourcePage) page).getViewer();
			if (viewer == null)
				return;
			StyledText text = viewer.getTextWidget();
			if (text == null)
				return;
			int offset = text.getCaretOffset();
			if (offset < 0)
				return;

			IDocumentRange range = ((PluginSourcePage) page).getRangeElement(offset, true);
			if (range instanceof IDocumentAttributeNode)
				range = ((IDocumentAttributeNode) range).getEnclosingElement();
			else if (range instanceof IDocumentTextNode)
				range = ((IDocumentTextNode) range).getEnclosingElement();
			
			if (
					(range instanceof IHQServer) || 
					(range instanceof IHQClasspath) || 
					(range instanceof IHQMetrics) || 
					(range instanceof IHQHelp) || 
					(range instanceof IHQScript) || 
					(range instanceof IHQPlatform) || 
					(range instanceof IPluginElement)) {
				fSection.selectStructureElement(new StructuredSelection(range));
			}
			
		}
	}

	/**
	 *
	 */
	public class StructureBlock extends HQDEMasterDetailsBlock implements IDetailsPageProvider {

		private StructureElementBodyTextDetails fBodyTextDetails;

		public StructureBlock() {
			super(StructurePage.this);
		}

		protected HQDESection createMasterSection(IManagedForm managedForm, Composite parent) {
			fSection = new StructureSection(getPage(), parent);
			return fSection;
		}

		protected void registerPages(DetailsPart detailsPart) {
			detailsPart.setPageLimit(10);
			// TODO is this pre registering working?
//			detailsPart.registerPage(HQServerNode.class, new StructureServerDetails(fSection));

			fBodyTextDetails = new StructureElementBodyTextDetails(fSection);
			detailsPart.registerPage(StructureElementBodyTextDetails.class, fBodyTextDetails);
			detailsPart.setPageProvider(this);

		}

		public IDetailsPage getPage(Object key) {

			if(key instanceof PluginElementNode) {
				return new StructureElementDetails(fSection, (PluginElementNode)key);				
			} else if(key instanceof IHQHelp){
				return new StructureHelpDetails(fSection);
			} else if(key instanceof IHQScript){
				return new StructureScriptDetails(fSection);
			} else if(key instanceof IHQServer){
				return new StructureServerDetails(fSection, (PluginParentNode)key);
			} else if(key instanceof IHQPlatform){
				return new StructurePlatformDetails(fSection, (PluginParentNode)key);
			} else if(key instanceof IHQMetrics){
				return new StructureMetricsDetails(fSection, (PluginParentNode)key);
			} else {
				return null;
			}
		}

		public Object getPageKey(Object object) {
			if(object instanceof IHQServer)
				return object;
			if(object instanceof IHQPlatform)
				return object;
			else if(object instanceof IHQHelp)
				return object;
			else if(object instanceof IHQScript)
				return object;
			else if(object instanceof IHQMetrics)
				return object;
			else if(object instanceof PluginElementNode)
				return object;
			return object.getClass();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.LaunchShortcutOverviewPage#getIndent()
	 */
	protected short getIndent() {
		return 5;
	}

	protected String getHelpResource() {
		return IHQDEConstants.PLUGIN_DOC_ROOT + "help/html/tools/editor/structure_tab.html";
	}

}
