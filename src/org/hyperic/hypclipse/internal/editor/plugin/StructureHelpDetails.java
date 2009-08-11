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

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.FormEntryAdapter;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.HQDESection;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.parts.ComboPart;
import org.hyperic.hypclipse.internal.parts.FormEntry;
import org.hyperic.hypclipse.internal.text.DocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentTextNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginObjectNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

public class StructureHelpDetails  extends AbstractPluginElementDetails {

	private IHQHelp input;
	private FormEntry name;
	private ComboPart include;
	private ComboPart append;
	private FormEntry fTextBody;


	public StructureHelpDetails(HQDESection masterSection) {
		super(masterSection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#getContextId()
	 */
	public String getContextId() {
		return PluginInputContext.CONTEXT_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#getPage()
	 */
	public HQDEFormPage getPage() {
		return (HQDEFormPage) getManagedForm().getContainer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#isEditable()
	 */
	public boolean isEditable() {
		return getPage().getHQDEEditor().getAggregateModel().isEditable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		parent.setLayout(FormLayoutFactory.createDetailsGridLayout(false, 1));

		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText(HQDEMessages.StructureHelpDetails_title);
		section.setDescription(HQDEMessages.StructureHelpDetails_desc);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(getMasterSection().getSection(), section);

		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 2));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createNameEntryField(toolkit, client);
		createAppendEntryField(toolkit, client);
		createIncludeEntryField(toolkit, client);
		
		createUITextBody(toolkit, client);
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
		IPluginModelBase model = (IPluginModelBase) getPage().getModel();
		model.addModelChangedListener(this);
		markDetailsPart(section);

	}
	
	private void createNameEntryField(FormToolkit toolkit, Composite client) {
		name = new FormEntry(client, toolkit, HQDEMessages.StructureHelpDetails_name, null, false);
		name.setFormEntryListener(new FormEntryAdapter(this) {
			public void textValueChanged(FormEntry entry) {
				if (input != null)
					try {
						input.setName(name.getValue());
					} catch (CoreException e) {
						HQDEPlugin.logException(e);
					}
			}
		});
	}


	private void createIncludeEntryField(FormToolkit toolkit, Composite client) {
		toolkit.createLabel(client, "include:", SWT.NULL);
		include = new ComboPart();
		include.createControl(client, toolkit, SWT.NONE);
		
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL/* : GridData.HORIZONTAL_ALIGN_FILL*/);
		gd.widthHint = 20;
//		gd.horizontalSpan = span - 1;
		gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
		include.getControl().setLayoutData(gd);
//		include.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				HQDEPlugin.logErrorMessage(e.toString());
//			}
//		});
		
		include.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (input != null)
					try {
						input.setInclude(include.getText());
					} catch (CoreException ce) {
						HQDEPlugin.logException(ce);
					}
				
			}
		});

	}
	
	private void createAppendEntryField(FormToolkit toolkit, Composite client) {
		toolkit.createLabel(client, "append:", SWT.NULL);
		append = new ComboPart();
		append.createControl(client, toolkit, SWT.NONE);
		
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL/* : GridData.HORIZONTAL_ALIGN_FILL*/);
		gd.widthHint = 20;
//		gd.horizontalSpan = span - 1;
		gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
		append.getControl().setLayoutData(gd);
//		include.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				HQDEPlugin.logErrorMessage(e.toString());
//			}
//		});
		
		append.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (input != null)
					try {
						input.setAppend(append.getText());
					} catch (CoreException ce) {
						HQDEPlugin.logException(ce);
					}
				
			}
		});

	}

	private void createUITextBody(FormToolkit toolkit, Composite parent) {
		int widget_style = SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;
		fTextBody = new FormEntry(parent, toolkit, null, widget_style);
		
		fTextBody.setFormEntryListener(new FormEntryAdapter(this) {
			public void textValueChanged(FormEntry entry) {
				handleTextBodyValueChanged();
			}
		});
		
		int layout_text_style = GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL;
		GridData data = new GridData(layout_text_style);
		data.horizontalSpan = 2;
		data.heightHint = 300;
		
		data.verticalIndent = 20;
		fTextBody.getText().setLayoutData(data);
	}

	private void handleTextBodyValueChanged() {
		if(input != null) {
			PluginParentNode node = (PluginParentNode)input;
			try {
				node.setText(fTextBody.getValue());
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
//			DocumentElementNode node = (DocumentElementNode)input;
//			if(node != null) {
//				IDocumentTextNode iNode = node.getTextNode();
//				iNode.setText(fTextBody.getValue());
//			}
		}
		
	}



	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IFormPart masterPart, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			input = (IHQHelp) ssel.getFirstElement();
		} else
			input = null;
		update();
	}
	
	private void update() {
		name.setValue(input != null ? input.getName() : null, true);
		
		
		if(input != null) {
			DocumentElementNode node = (DocumentElementNode)input;
			if(node != null) {
				IDocumentTextNode iNode = node.getTextNode();
				fTextBody.setValue(iNode != null ? iNode.getText() : "");				
			}
			
			IPluginBase base = input.getPluginBase();
			ArrayList<PluginObjectNode> result = new ArrayList<PluginObjectNode>();
			((PluginObjectNode)base).queryNodes("help", result);
			
			ArrayList<String> helps = new ArrayList<String>();
			ListIterator<PluginObjectNode> iter = result.listIterator();
			while(iter.hasNext()) {
				String i = iter.next().getXMLAttributeValue("name");
				if(i != null)
					helps.add(i);
			}
			String aHelps[] = helps.toArray(new String[0]);
			
			include.setItems(aHelps);
			append.setItems(aHelps);

			// TODO there may be multiple references separated by comma
			
			if(input.getInclude() != null)
				include.setText(input.getInclude());
			if(input.getAppend() != null)
				append.setText(input.getAppend());
			
		}
		
		updateLabel(true, name, HQDEMessages.StructureHelpDetails_name);
	}

	private void updateLabel(boolean required, FormEntry field, String label) {
		// Get the label
		Control control = field.getLabel();
		// Ensure label is defined
		if ((control == null) || ((control instanceof Label) == false)) {
			return;
		}
		Label labelControl = ((Label) control);
		// If the label is required, add the '*' to indicate that
		if (required) {
			labelControl.setText(label + '*' + ':');
		} else {
			labelControl.setText(label + ':');
		}
		// Force the label's parent composite to relayout because 
		// clippage can occur when updating the text
		labelControl.getParent().layout();
	}

	public void cancelEdit() {
//		id.cancelEdit();
		name.cancelEdit();
		super.cancelEdit();
	}

	public void commit(boolean onSave) {
		//			id.commit();
		name.commit();
		super.commit(onSave);
	}


	public void setFocus() {
		name.getText().setFocus();
	}

	public void dispose() {
		IPluginModelBase model = (IPluginModelBase) getPage().getModel();
		if (model != null)
			model.removeModelChangedListener(this);
		super.dispose();
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.CHANGE) {
			Object obj = e.getChangedObjects()[0];
			if (obj.equals(input))
				refresh();
		}
	}
	

	public void refresh() {
		update();
		super.refresh();
	}


	public void fireSaveNeeded() {
		markDirty();
		HQDEFormPage page = (HQDEFormPage) getManagedForm().getContainer();
		page.getHQDEEditor().fireSaveNeeded(getContextId(), false);
	}

	
	
}
