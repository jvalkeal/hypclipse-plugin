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
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDEDetails;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.HQDESection;
import org.hyperic.hypclipse.internal.editor.plugin.rows.BooleanAttributeRow;
import org.hyperic.hypclipse.internal.editor.plugin.rows.ChoiceAttributeRow;
import org.hyperic.hypclipse.internal.editor.plugin.rows.SpinnerAttributeRow;
import org.hyperic.hypclipse.internal.editor.plugin.rows.StructureAttributeRow;
import org.hyperic.hypclipse.internal.editor.plugin.rows.TextAttributeRow;
import org.hyperic.hypclipse.internal.schema.AnnotationExp;
import org.hyperic.hypclipse.internal.schema.ElementAttributeCollector;
import org.hyperic.hypclipse.internal.schema.NodeSchemaReconnector;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.msv.datatype.xsd.PositiveIntegerType;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceExp;

/**
 * The Class AbstractElementDetails.
 */
public abstract class AbstractElementDetails extends HQDEDetails {

	/** The master section. */
	private HQDESection fMasterSection;
	
	/** The section. */
	private Section section;
	
	/** The rows. */
	private ArrayList<StructureAttributeRow> rows;
	
	/** The structure element. */
	private PluginParentNode structureElement;
	
	/** The input. */
	private IPluginParent input;
	
	/**
	 * Instantiates a new abstract element details.
	 * 
	 * @param masterSection the master section
	 */
	public AbstractElementDetails(HQDESection masterSection, PluginParentNode structureElement) {
		fMasterSection = masterSection;
		this.structureElement = structureElement;
		rows = new ArrayList<StructureAttributeRow>();
	}

	/**
	 * Gets the master section.
	 * 
	 * @return the master section
	 */
	public HQDESection getMasterSection() {
		return fMasterSection;
	}
	
	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#getContextId()
	 */
	public String getContextId() {
		return PluginInputContext.CONTEXT_ID;
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#fireSaveNeeded()
	 */
	public void fireSaveNeeded() {
		markDirty();
		getPage().getHQDEEditor().fireSaveNeeded(getContextId(), false);
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#getPage()
	 */
	public HQDEFormPage getPage() {
		return (HQDEFormPage) getManagedForm().getContainer();
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.IContextPart#isEditable()
	 */
	public boolean isEditable() {
		return getPage().getHQDEEditor().getAggregateModel().isEditable();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		parent.setLayout(FormLayoutFactory.createDetailsGridLayout(false, 1));
		FormToolkit toolkit = getManagedForm().getToolkit();
		section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText(HQDEMessages.StructureElementDetails_title);
		section.setDescription("");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));

		
		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(getMasterSection().getSection(), section);

		Composite client = toolkit.createComposite(section);
		int span = 2;
		GridLayout glayout = FormLayoutFactory.createSectionClientGridLayout(false, span);
		client.setLayout(glayout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		ExpressionPool pool = new ExpressionPool();
		if(structureElement != null) {
			
			ElementExp exp = structureElement.getElementExp();
			
			// TODO should we fix node/schema association here
			// there might be better place to fix this.
			// this is correct place, if schema is not needed until at this point
			if(exp == null) {
				NodeSchemaReconnector schemaConnector = new NodeSchemaReconnector();
				if(structureElement.getParent() instanceof PluginParentNode) {
					schemaConnector.connect((PluginParentNode)structureElement.getParent());
				} else if(structureElement.getParent() instanceof PluginBaseNode) {
					schemaConnector.connect(
						(PluginBaseNode)structureElement.getParent(),
						(PluginParentNode)structureElement
					);
				}
				exp = structureElement.getElementExp();					
			}
			
//			Expression eExp = exp.contentModel.getExpandedExp(pool);
			Collection<Expression> result = new ArrayList<Expression>();
			ElementAttributeCollector coll = new ElementAttributeCollector();
			coll.collect(exp.contentModel, result);
			
			Iterator<Expression> iter = result.iterator();
			while(iter.hasNext()) {
				Expression ae1 = iter.next();
				rows.add(createAttributeRow(ae1, client, toolkit, span));
			}
		
			

		}
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
		// Dynamically add focus listeners to all the section client's 
		// children in order to track the last focus control
		getPage().addLastFocusListeners(client);

		IPluginModelBase model = (IPluginModelBase) getPage().getModel();
		model.addModelChangedListener(this);
		markDetailsPart(section);
	}

	
	/**
	 * Creates the attribute row.
	 * 
	 * @param att the att
	 * @param parent the parent
	 * @param toolkit the toolkit
	 * @param span the span
	 * 
	 * @return the structure attribute row
	 */
	private StructureAttributeRow createAttributeRow(Expression att, Composite parent, FormToolkit toolkit, int span) {
		StructureAttributeRow row = null;
		
		// find AttributeExp if encapsulated inside annonation
		AttributeExp aExp = reduceAttributeAnnonation(att);
		
		// find matching expression type.
		// it may be behind reference.
		Expression mExp;
		if(aExp.exp instanceof ReferenceExp)
			mExp =  reduceAnnonation(((ReferenceExp)aExp.exp).exp);
		else
			mExp = aExp.exp;
		
		// get data type from schema
		if(mExp instanceof ChoiceExp) {
			row = new ChoiceAttributeRow(this,att);
		} else if (mExp instanceof DataExp){
			DataExp dExp = (DataExp)mExp;
			if(dExp.dt instanceof BooleanType)
				row = new BooleanAttributeRow(this,att);
			else if(dExp.dt instanceof PositiveIntegerType)
				row = new SpinnerAttributeRow(this,att);
		}

		// we don't have any special data type,
		// fall back to text row
		if(row == null)
			row = new TextAttributeRow(this, att);							
		
		row.createContents(parent, toolkit, span);
		return row;
	}

	/**
	 * Reduce attribute annonation.
	 * 
	 * @param exp the exp
	 * 
	 * @return the attribute exp
	 */
	private AttributeExp reduceAttributeAnnonation(Expression exp) {
		if(exp instanceof AnnotationExp )
			return (AttributeExp)((AnnotationExp)exp).exp;
		else 
			return (AttributeExp)exp;
	}

	/**
	 * Reduce annonation.
	 * 
	 * @param exp the exp
	 * 
	 * @return the expression
	 */
	private Expression reduceAnnonation(Expression exp) {
		if(exp instanceof AnnotationExp )
			return ((AnnotationExp)exp).exp;
		else 
			return exp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IFormPart masterPart, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			input = (IPluginParent) ssel.getFirstElement();
		} else
			input = null;
		update();
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IModelChangedListener#modelChanged(org.hyperic.hypclipse.plugin.IModelChangedEvent)
	 */
	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.CHANGE) {
			Object obj = e.getChangedObjects()[0];
			if (obj.equals(input)) {
				// do smart update (update only the row whose property changed
				String property = e.getChangedProperty();
				if (property != null) {
					for (int i = 0; i < rows.size(); i++) {
						StructureAttributeRow row = rows.get(i);
//						ISchemaAttribute attribute = row.getAttribute();
//						if (attribute == null) {
//							continue;
//						}
//						String name = attribute.getName();
//						if (name == null) {
//							continue;
//						}
//						if (name.equals(property)) {
//							row.setInput(input);
//						}
					}
				} else
					refresh();
			}
		}
	}
	


	/**
	 * Update.
	 */
	private void update() {
		updateDescription();
//		if (structureElement == null)
//			updateRows();
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).setInput(input);
		}
	}

	/**
	 * Update rows.
	 */
	private void updateRows() {
		if (input == null)
			return;
		IPluginAttribute[] atts = (IPluginAttribute[])((PluginParentNode)input).getNodeAttributesMap().values().toArray(new IPluginAttribute[((PluginParentNode)input).getNodeAttributesMap().size()]);
//		return (IPluginAttribute[]) getNodeAttributesMap().values().toArray(new IPluginAttribute[getNodeAttributesMap().size()]);
		FormToolkit toolkit = getManagedForm().getToolkit();
		boolean rowsAdded = false;
//		for (int i = 0; i < atts.length; i++) {
//			if (!hasAttribute(atts[i].getName())) {
//				rows.add(createAttributeRow(atts[i], (Composite) section.getClient(), toolkit, 2));
//				rowsAdded = true;
//			}
//		}
		if (rowsAdded) {
			((Composite) section.getClient()).layout(true);
			section.layout(true);
			section.getParent().layout(true);
			reflow();
		}
	}
	
	private StructureAttributeRow createAttributeRow(IPluginAttribute att, Composite parent, FormToolkit toolkit, int span) {
		StructureAttributeRow row;
		row = new TextAttributeRow(this, att);
		row.createContents(parent, toolkit, span);
		return row;
	}


	private void reflow() {
		Composite parent = section.getParent();
		while (parent != null) {
			if (parent instanceof SharedScrolledComposite) {
				((SharedScrolledComposite) parent).reflow(true);
				return;
			}
			parent = parent.getParent();
		}
	}

	private boolean hasAttribute(String attName) {
		for (int i = 0; i < rows.size(); i++) {
			StructureAttributeRow row = rows.get(i);
			if (row.getName().equals(attName))
				return true;
		}
		return false;
	}

	private void updateDescription() {
		if (input != null) {
			// need to check this from used rows.
			// we can't check attribute count, since those
			// doesn't exist if attributes are empty. 
			if(rows.size() < 1) {
				section.setDescription(HQDEMessages.StructureElementDetails_descNoAttributes);
			} else {
				String iname = ((PluginParentNode)input).getXMLTagName();
				section.setDescription(NLS.bind(HQDEMessages.StructureElementDetails_setDesc, iname));
			}
		} else {
			// no structure = no description
			section.setDescription("");
		}
		section.layout();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#commit(boolean)
	 */
	public void commit(boolean onSave) {
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).commit();
		}
		super.commit(onSave);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#setFocus()
	 */
	public void setFocus() {
		if (rows.size() > 0)
			rows.get(0).setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	public void dispose() {
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).dispose();
		}
		IPluginModelBase model = (IPluginModelBase) getPage().getModel();
		if (model != null)
			model.removeModelChangedListener(this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
	 */
	public void refresh() {
		update();
		super.refresh();
	}


}
