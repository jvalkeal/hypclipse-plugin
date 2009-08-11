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
package org.hyperic.hypclipse.internal.text.plugin;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.hyperic.hypclipse.internal.ModelChangedEvent;
import org.hyperic.hypclipse.internal.text.DocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentAttributeNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentRange;
import org.hyperic.hypclipse.internal.util.HQDEXMLHelper;
import org.hyperic.hypclipse.plugin.IEditingModel;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IModelChangeProvider;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.ISharedPluginModel;

public class PluginObjectNode extends DocumentElementNode implements IPluginObject/*, IWritableDelimiter*/ {

	private transient boolean fInTheModel;
	private transient ISharedPluginModel fModel;

	private static final long serialVersionUID = 1L;
	protected String fName;

	/**
	 * 
	 */
	public PluginObjectNode() {
		super();
	}
	
	public void connectAttributeSchemas() {
		
	}

	/*
	 * 
	 */
	public ISharedPluginModel getModel() {
		return fModel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getPluginModel()
	 */
	public IPluginModelBase getPluginModel() {
		return (IPluginModelBase) fModel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getName()
	 */
	public String getName() {
		return fName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#isInTheModel()
	 */
	public boolean isInTheModel() {
		return fInTheModel;
	}

	public String getTranslatedName() {
		return getResourceString(getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getParent()
	 */
	public IPluginObject getParent() {
		return (IPluginObject) getParentNode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getPluginBase()
	 */
	public IPluginBase getPluginBase() {
		return fModel != null ? ((IPluginModelBase) fModel).getPluginBase() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#getResourceString(java.lang.String)
	 */
	public String getResourceString(String key) {
		return fModel != null ? fModel.getResourceString(key) : key;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#setName(java.lang.String)
	 */
	public void setName(String name) throws CoreException {
		fName = name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#isValid()
	 */
	public boolean isValid() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IWritable#write(java.lang.String, java.io.PrintWriter)
	 */
	public void write(String indent, PrintWriter writer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginObject#setInTheModel(boolean)
	 */
	public void setInTheModel(boolean inModel) {
		fInTheModel = inModel;
	}

	public void setModel(ISharedPluginModel model) {
		fModel = model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#setXMLAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setXMLAttribute(String name, String value) {
		// Overrided by necessity - dealing with different objects
		String oldValue = getXMLAttributeValue(name);
		if (oldValue != null && oldValue.equals(value))
			return false;
		PluginNodeAttribute attr = (PluginNodeAttribute) getNodeAttributesMap().get(name);
		try {
			if (value == null)
				value = "";
			if (attr == null) {
				attr = new PluginNodeAttribute();
				attr.setName(name);
				attr.setEnclosingElement(this);
				attr.setModel(getModel());
				getNodeAttributesMap().put(name, attr);
			}
			attr.setValue(value == null ? "" : value);
		} catch (CoreException e) {
		}
		if (fInTheModel)
			firePropertyChanged(attr.getEnclosingElement(), attr.getAttributeName(), oldValue, value);
		return true;
	}

	protected void firePropertyChanged(IDocumentRange node, String property, Object oldValue, Object newValue) {
		if (fModel.isEditable()) {
			fModel.fireModelObjectChanged(node, property, oldValue, newValue);
		}
	}

	protected void fireStructureChanged(IPluginObject child, int changeType) {
		IModel model = getModel();
		if (model.isEditable() && model instanceof IModelChangeProvider) {
			IModelChangedEvent e = new ModelChangedEvent(fModel, changeType, new Object[] {child}, null);
			fireModelChanged(e);
		}
	}

	protected void fireStructureChanged(IPluginObject[] children, int changeType) {
		IModel model = getModel();
		if (model.isEditable() && model instanceof IModelChangeProvider) {
			IModelChangedEvent e = new ModelChangedEvent(fModel, changeType, children, null);
			fireModelChanged(e);
		}
	}

	private void fireModelChanged(IModelChangedEvent e) {
		IModel model = getModel();
		if (model.isEditable() && model instanceof IModelChangeProvider) {
			IModelChangeProvider provider = (IModelChangeProvider) model;
			provider.fireModelChanged(e);
		}
	}

	public String getWritableString(String source) {
		return HQDEXMLHelper.getWritableString(source);
	}

	protected void appendAttribute(StringBuffer buffer, String attrName) {
		appendAttribute(buffer, attrName, ""); //$NON-NLS-1$
	}

	protected void appendAttribute(StringBuffer buffer, String attrName, String defaultValue) {
		IDocumentAttributeNode attr = getDocumentAttribute(attrName);
		if (attr != null) {
			String value = attr.getAttributeValue();
			if (value != null && value.trim().length() > 0 && !value.equals(defaultValue))
				buffer.append(" " + attr.write()); //$NON-NLS-1$
		}
	}

	/**
	 * @return
	 */
	public String getLineDelimiter() {
		IModel model = getModel();
		IDocument document = ((IEditingModel) model).getDocument();
		return TextUtilities.getDefaultLineDelimiter(document);
	}

	public void addChildNode(IDocumentElementNode child, int position) {
		super.addChildNode(child, position);
		((IPluginObject) child).setInTheModel(true);
	}

	public String toString() {
		return write(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#reconnect(org.hyperic.hypclipse.internal.text.IDocumentElementNode, org.hyperic.hypclipse.plugin.IModel)
	 */
	public void reconnect(IDocumentElementNode parent, IModel model) {
		super.reconnect(parent, model);
		// Transient field:  In The Model
		// Value set to true when added to the parent; however, serialized
		// children's value remains unchanged.  Since, reconnect and add calls
		// are made so close together, set value to true for parent and all
		// children
		fInTheModel = true;
		// Transient field:  Model
		if (model instanceof ISharedPluginModel) {
			fModel = (ISharedPluginModel) model;
		}
	}

	/*
	 * 
	 */
	public void writeDelimeter(PrintWriter writer) {
		// NO-OP
		// Child classes to override
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#getXMLAttributeValue(java.lang.String)
	 */
	public String getXMLAttributeValue(String name) {
		// Overrided by necessity - dealing with different objects
		PluginNodeAttribute attr = (PluginNodeAttribute) getNodeAttributesMap().get(name);
		return attr == null ? null : attr.getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#write(boolean)
	 */
	public String write(boolean indent) {
		// Used by text edit operations
		// Subclasses to override
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#writeShallow(boolean)
	 */
	public String writeShallow(boolean terminate) {
		// Used by text edit operations
		// Subclasses to override
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.text.DocumentElementNode#getFileEncoding()
	 */
	protected String getFileEncoding() {
		if ((fModel != null) && (fModel instanceof IEditingModel)) {
			return ((IEditingModel) fModel).getCharset();
		}
		return super.getFileEncoding();
	}
	
	public void queryNodes(String name, ArrayList<PluginObjectNode> result) {
		if(name.equals(getXMLTagName()))
			result.add(this);
		IDocumentElementNode[] nodes = getChildNodes();
		for (int i = 0; i < nodes.length; i++) {
			PluginObjectNode n = (PluginObjectNode)nodes[i];
			n.queryNodes(name, result);
		}
	}

}
