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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.hyperic.hypclipse.internal.plugin.PluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PluginElement extends PluginParent implements IPluginElement {

	public static final String ATTRIBUTE_SHIFT = "      ";

	public static final String ELEMENT_SHIFT = "   ";

//	private transient ISchemaElement fElementInfo;

	protected String fText;

	protected Hashtable<String, IPluginAttribute> fAttributes;

	private IConfigurationElement fElement = null;

	public PluginElement() {
	}

	public PluginElement(IConfigurationElement element) {
		fElement = element;
	}

//	PluginElement(PluginElement element) {
//		setModel(element.getModel());
//		setParent(element.getParent());
//		fName = element.getName();
//		IPluginAttribute[] atts = element.getAttributes();
//		for (int i = 0; i < atts.length; i++) {
//			PluginAttribute att = (PluginAttribute) atts[i];
//			getAttributeMap().put(att.getName(), att.clone());
//		}
//		fText = element.getText();
////		fElementInfo = (ISchemaElement) element.getElementInfo();
//		fElement = element.fElement;
//	}

//	public boolean equals(Object obj) {
//		if (obj == this)
//			return true;
//		if (obj == null)
//			return false;
//		if (obj instanceof IPluginElement) {
//			IPluginElement target = (IPluginElement) obj;
//			if (target.getModel().equals(getModel()))
//				return false;
//			if (target.getAttributeCount() != getAttributeCount())
//				return false;
//			IPluginAttribute tatts[] = target.getAttributes();
//			for (int i = 0; i < tatts.length; i++) {
//				IPluginAttribute tatt = tatts[i];
//				IPluginAttribute att = (IPluginAttribute) getAttributeMap().get(tatt.getName());
//				if (att == null || att.equals(tatt) == false)
//					return false;
//			}
//			return super.equals(obj);
//		}
//		return false;
//	}

//	public IPluginElement createCopy() {
//		return new PluginElement(this);
//	}

	public IPluginAttribute getAttribute(String name) {
		return (IPluginAttribute) getAttributeMap().get(name);
	}

	public IPluginAttribute[] getAttributes() {
		Collection<IPluginAttribute> values = getAttributeMap().values();
		IPluginAttribute[] result = new IPluginAttribute[values.size()];
		return (IPluginAttribute[]) values.toArray(result);
	}

	public int getAttributeCount() {
		// if attributes are initialized, don't load the entire map to find the # of elements
		if (fAttributes == null && fElement != null)
			return fElement.getAttributeNames().length;
		return getAttributeMap().size();
	}

//	public Object getElementInfo() {
//		if (fElementInfo != null) {
//			ISchema schema = fElementInfo.getSchema();
//			if (schema.isDisposed()) {
//				fElementInfo = null;
//			}
//		}
//		if (fElementInfo == null) {
//			IPluginObject parent = getParent();
//			while (parent != null && !(parent instanceof IPluginExtension)) {
//				parent = parent.getParent();
//			}
//			if (parent != null) {
//				PluginExtension extension = (PluginExtension) parent;
//				ISchema schema = (ISchema) extension.getSchema();
//				if (schema != null) {
//					fElementInfo = schema.findElement(getName());
//				}
//			}
//		}
//		return fElementInfo;
//	}

	public String getText() {
		if (fText == null && fElement != null)
			fText = fElement.getValue();
		return fText;
	}

	void load(Node node) {
		fName = node.getNodeName();
		if (fAttributes == null)
			fAttributes = new Hashtable();
		NamedNodeMap attributes = node.getAttributes();
		// TODO implement
//		for (int i = 0; i < attributes.getLength(); i++) {
//			Node attribute = attributes.item(i);
			IPluginAttribute att = getModel().getFactory().createAttribute(this);
//			((PluginAttribute) att).load(attribute);
//			((PluginAttribute) att).setInTheModel(true);
//			this.fAttributes.put(attribute.getNodeName(), att);
//		}

		if (fChildren == null)
			fChildren = new ArrayList();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				PluginElement childElement = new PluginElement();
				childElement.setModel(getModel());
				childElement.setInTheModel(true);
				this.fChildren.add(childElement);
				childElement.setParent(this);
				childElement.load(child);
			} else if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue() != null) {
				String text = child.getNodeValue();
				text = text.trim();
				if (isNotEmpty(text))
					this.fText = text;
			}
		}
	}

	public void removeAttribute(String name) throws CoreException {
		ensureModelEditable();
		PluginAttribute att = (PluginAttribute) getAttributeMap().remove(name);
		String oldValue = att.getValue();
		if (att != null) {
			att.setInTheModel(false);
		}
		firePropertyChanged(P_ATTRIBUTE, oldValue, null);
	}

	public void setAttribute(String name, String value) throws CoreException {
		ensureModelEditable();
		if (value == null) {
			removeAttribute(name);
			return;
		}
		IPluginAttribute attribute = getAttribute(name);
		if (attribute == null) {
			attribute = getModel().getFactory().createAttribute(this);
			attribute.setName(name);
			getAttributeMap().put(name, attribute);
			((PluginAttribute) attribute).setInTheModel(true);
		}
		attribute.setValue(value);
	}

//	public void setElementInfo(ISchemaElement newElementInfo) {
//		fElementInfo = newElementInfo;
//		if (fElementInfo == null) {
//			for (Enumeration atts = getAttributeMap().elements(); atts.hasMoreElements();) {
//				PluginAttribute att = (PluginAttribute) atts.nextElement();
//				att.setAttributeInfo(null);
//			}
//		}
//	}

	public void setText(String newText) throws CoreException {
		ensureModelEditable();
		String oldValue = fText;
		fText = newText;
		firePropertyChanged(P_TEXT, oldValue, fText);

	}

	public void write(String indent, PrintWriter writer) {
		writer.print(indent);
		writer.print("<" + getName());
		String newIndent = indent + ATTRIBUTE_SHIFT;
		if (getAttributeMap().isEmpty() == false) {
			writer.println();
			for (Iterator iter = getAttributeMap().values().iterator(); iter.hasNext();) {
				IPluginAttribute attribute = (IPluginAttribute) iter.next();
				attribute.write(newIndent, writer);
				if (iter.hasNext())
					writer.println();
			}
		}
		writer.println(">"); //$NON-NLS-1$
		newIndent = indent + ELEMENT_SHIFT;
		IPluginObject[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			IPluginElement element = (IPluginElement) children[i];
			element.write(newIndent, writer);
		}
		if (getText() != null) {
			//TODO implement later
//			writer.println(newIndent + getWritableString(getText()));
		}
		writer.println(indent + "</" + getName() + ">");
	}

	protected Hashtable<String, IPluginAttribute> getAttributeMap() {
		if (fAttributes == null) {
			fAttributes = new Hashtable<String, IPluginAttribute>();
			if (fElement != null) {
				String[] names = fElement.getAttributeNames();
				for (int i = 0; i < names.length; i++) {
					IPluginAttribute attr = createAttribute(names[i], fElement.getAttribute(names[i]));
					if (attr != null)
						fAttributes.put(names[i], attr);
				}
			}
		}
		return fAttributes;
	}

	private IPluginAttribute createAttribute(String name, String value) {
		if (name == null || value == null)
			return null;
		try {
			IPluginAttribute attr = getPluginModel().getPluginFactory().createAttribute(this);
//			if (attr instanceof PluginAttribute)
//				((PluginAttribute) attr).load(name, value);
//			else {
				attr.setName(name);
				attr.setValue(value);
//			}
			return attr;
		} catch (CoreException e) {
		}
		return null;
	}

	protected ArrayList getChildrenList() {
		if (fChildren == null) {
			fChildren = new ArrayList();
			if (fElement != null) {
				IConfigurationElement[] elements = fElement.getChildren();
				for (int i = 0; i < elements.length; i++) {
					PluginElement element = new PluginElement(elements[i]);
					element.setModel(getModel());
					element.setParent(this);
					fChildren.add(element);
				}
			}
		}
		return fChildren;
	}

	public String getName() {
		if (fName == null && fElement != null) {
			fName = fElement.getName();
		}
		return fName;
	}

	public IPluginElement createCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPluginModelBase getPluginModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
