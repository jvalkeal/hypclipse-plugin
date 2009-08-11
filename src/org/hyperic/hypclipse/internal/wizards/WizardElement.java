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
package org.hyperic.hypclipse.internal.wizards;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPluginContribution;
import org.hyperic.hypclipse.internal.elements.NamedElement;
import org.osgi.framework.Bundle;

public class WizardElement extends NamedElement implements IPluginContribution {
	public static final String ATT_NAME = "name";

	public static final String TAG_DESCRIPTION = "description";

	public static final String ATT_ICON = "icon";

	public static final String ATT_ID = "id";

	public static final String ATT_CLASS = "class";

	public static final String ATT_TEMPLATE = "template";

	public static final String ATT_POINT = "point";

	public static final String ATT_DEFAULT = "default";

	private String description;

	private IConfigurationElement configurationElement;

	private IConfigurationElement template;

	public WizardElement(IConfigurationElement config) {
		super(config.getAttribute(ATT_NAME));
		this.configurationElement = config;
	}

	public Object createExecutableExtension() throws CoreException {
		return configurationElement.createExecutableExtension(ATT_CLASS);
	}

	public IConfigurationElement getConfigurationElement() {
		return configurationElement;
	}

	public String getDescription() {
		if (description == null) {
			IConfigurationElement[] children = configurationElement.getChildren(TAG_DESCRIPTION);
			if (children.length > 0) {
				description = expandDescription(children[0].getValue());
			}
		}
		return description;
	}

	/**
	 * We allow replacement variables in description values as well. This is to
	 * allow extension template descriptin reuse in project template wizards.
	 * Tokens in form '%token%' will be evaluated against the contributing
	 * plug-in's resource bundle. As before, to have '%' in the description, one
	 * need to add '%%'.
	 */
	private String expandDescription(String source) {
		if (source == null || source.length() == 0)
			return source;
		if (source.indexOf('%') == -1)
			return source;

		Bundle bundle = Platform.getBundle(configurationElement.getNamespaceIdentifier());
		if (bundle == null)
			return source;

		ResourceBundle resourceBundle = Platform.getResourceBundle(bundle);
		if (resourceBundle == null)
			return source;
		StringBuffer buf = new StringBuffer();
		boolean keyMode = false;
		int keyStartIndex = -1;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '%') {
				char c2 = source.charAt(i + 1);
				if (c2 == '%') {
					i++;
					buf.append('%');
					continue;
				}
				if (keyMode) {
					keyMode = false;
					String key = source.substring(keyStartIndex, i);
					String value = key;
					try {
						value = resourceBundle.getString(key);
					} catch (MissingResourceException e) {
					}
					buf.append(value);
				} else {
					keyStartIndex = i + 1;
					keyMode = true;
				}
			} else if (!keyMode) {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public String getID() {
		return configurationElement.getAttribute(ATT_ID);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getTemplateId() {
		return configurationElement.getAttribute(ATT_TEMPLATE);
	}

	public boolean isTemplate() {
		return getTemplateId() != null;
	}

	public IConfigurationElement getTemplateElement() {
		if (template == null)
			template = findTemplateElement();
		return template;
	}

	private IConfigurationElement findTemplateElement() {
		String templateId = getTemplateId();
		if (templateId == null)
			return null;
		IConfigurationElement[] templates = Platform.getExtensionRegistry().getConfigurationElementsFor("org.hyperic.hypclipse.templates");
		for (int i = 0; i < templates.length; i++) {
			IConfigurationElement template = templates[i];
			String id = template.getAttribute("id");
			if (id != null && id.equals(templateId))
				return template;
		}
		return null;
	}

	public String getContributingId() {
		IConfigurationElement tel = getTemplateElement();
		return (tel == null) ? null : tel.getAttribute("contributingId");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPluginContribution#getLocalId()
	 */
	public String getLocalId() {
		return getID();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPluginContribution#getPluginId()
	 */
	public String getPluginId() {
		return null;
	}
}
