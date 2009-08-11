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
package org.hyperic.hypclipse.internal.text;

import java.net.URL;
import org.eclipse.jface.text.*;
import org.hyperic.hypclipse.internal.editor.HQDESourcePage;
import org.hyperic.hypclipse.plugin.IPluginObject;

public class PluginXMLTextHover extends HQDETextHover {

	private HQDESourcePage fSourcePage;

	public PluginXMLTextHover(HQDESourcePage sourcePage) {
		fSourcePage = sourcePage;
	}

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		int offset = hoverRegion.getOffset();
		IDocumentRange range = fSourcePage.getRangeElement(offset, true);
//		if (range instanceof IDocumentTextNode)
//			return checkTranslatedValue((IDocumentTextNode) range);
		if (!(range instanceof IPluginObject))
			return null;

//		ISchema schema = getExtensionSchema((IPluginObject) range);
//		if (schema != null) {
//			ISchemaObject sObj = getSchemaObject(schema, (IPluginObject) range);
//			if (sObj == null) {
//				return null;
//			} else if (range instanceof IPluginAttribute && sObj instanceof ISchemaElement) {
//				IDocumentAttributeNode da = (IDocumentAttributeNode) range;
//				if (da.getNameOffset() <= offset && offset <= da.getNameOffset() + da.getNameLength() - 1)
//					// inside name
//					return getAttributeText((IPluginAttribute) range, (ISchemaElement) sObj);
//				else if (da.getValueOffset() <= offset && offset <= da.getValueOffset() + da.getValueLength() - 1)
//					// inside value
//					return getAttributeValueText((IPluginAttribute) range, (ISchemaElement) sObj);
//			} else if (range instanceof IPluginElement) {
//				IDocumentElementNode dn = (IDocumentElementNode) range;
//				int dnOff = dn.getOffset();
//				int dnLen = dn.getLength();
//				String dnName = dn.getXMLTagName();
//				if (dnOff + 1 <= offset && offset <= dnOff + dnName.length())
//					// inside opening tag
//					return getElementText((ISchemaElement) sObj);
//				try {
//					String nt = textViewer.getDocument().get(dnOff, dnLen);
//					if (nt.endsWith("</" + dnName + '>')) { //$NON-NLS-1$
//						offset = offset - dnOff;
//						if (nt.length() - dnName.length() - 1 <= offset && offset <= nt.length() - 2)
//							// inside closing tag
//							return getElementText((ISchemaElement) sObj);
//					}
//				} catch (BadLocationException e) {
//				}
//			}
//		} else if (range instanceof IDocumentAttributeNode && ((IDocumentAttributeNode) range).getEnclosingElement() instanceof IPluginExtensionPoint)
//			return getExtensionPointHoverInfo((IPluginObject) range, offset);

		return null;
	}

	private String getExtensionPointHoverInfo(IPluginObject object, int offset) {
		IDocumentAttributeNode da = (IDocumentAttributeNode) object;
		if (da.getValueOffset() <= offset && offset <= da.getValueOffset() + da.getValueLength() - 1) {
			String value = da.getAttributeValue();
			if (da.getAttributeName().equals(IPluginObject.P_NAME) && value.startsWith("%")) //$NON-NLS-1$
				return object.getResourceString(value);
		}
		return null;

	}

//	private ISchema getExtensionSchema(IPluginObject object) {
//		IPluginObject extension = object;
//		if (object instanceof IDocumentAttributeNode)
//			extension = (IPluginObject) ((IDocumentAttributeNode) object).getEnclosingElement();
//		while (extension != null && !(extension instanceof IPluginExtension))
//			extension = extension.getParent();
//
//		if (extension == null)
//			// started off outside of an extension element
//			return null;
//
//		String point = ((IPluginExtension) extension).getPoint();
//		return PDECore.getDefault().getSchemaRegistry().getSchema(point);
//	}
//
//	private ISchemaObject getSchemaObject(ISchema schema, IPluginObject object) {
//		if (object instanceof IPluginElement)
//			return schema.findElement(((IPluginElement) object).getName());
//		if (object instanceof IPluginExtension)
//			return schema.findElement("extension"); //$NON-NLS-1$
//		if (object instanceof IDocumentAttributeNode)
//			return schema.findElement(((IDocumentAttributeNode) object).getEnclosingElement().getXMLTagName());
//		return null;
//	}
//
//	private String getAttributeText(IPluginAttribute attrib, ISchemaElement sEle) {
//		ISchemaAttribute sAtt = sEle.getAttribute(attrib.getName());
//		if (sAtt == null)
//			return null;
//		return sAtt.getDescription();
//	}
//
//	private String getAttributeValueText(IPluginAttribute attrib, ISchemaElement sEle) {
//		if (sEle.getName().equals("extension") && //$NON-NLS-1$
//				attrib.getName().equals(IPluginExtension.P_POINT))
//			return getSchemaDescription(attrib, sEle);
//		ISchemaAttribute sAtt = sEle.getAttribute(attrib.getName());
//		if (sAtt == null)
//			return null;
//
//		String value = attrib.getValue();
//		if (sAtt.isTranslatable() && value.startsWith("%")) //$NON-NLS-1$
//			return attrib.getResourceString(value);
//		return null;
//	}
//
//	private String getSchemaDescription(IPluginAttribute attr, ISchemaElement sEle) {
//		String description = XMLComponentRegistry.Instance().getDescription(attr.getValue(), XMLComponentRegistry.F_SCHEMA_COMPONENT);
//
//		if (description == null) {
//			URL url = sEle.getSchema().getURL();
//			SchemaAnnotationHandler handler = new SchemaAnnotationHandler();
//			SchemaUtil.parseURL(url, handler);
//			description = handler.getDescription();
//			XMLComponentRegistry.Instance().putDescription(attr.getValue(), description, XMLComponentRegistry.F_SCHEMA_COMPONENT);
//		}
//		return description;
//	}
//
//	private String getElementText(ISchemaElement sEle) {
//		if (sEle == null) {
//			return null;
//		}
//		return sEle.getDescription();
//	}
//
//	private String checkTranslatedValue(IDocumentTextNode node) {
//		String value = node.getText();
//		if (value.startsWith("%")) //$NON-NLS-1$
//			return ((IPluginObject) node.getEnclosingElement()).getResourceString(value);
//
//		return null;
//	}
}
