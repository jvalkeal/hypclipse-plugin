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

package org.hyperic.hypclipse.internal.templates;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public abstract class HQDETemplateSection extends OptionTemplateSection {

//	public static final String KEY_PRODUCT_BRANDING = "productBranding";
//	public static final String KEY_PRODUCT_NAME = "productName";

//	public static final String VALUE_PRODUCT_ID = "product";
//	public static final String VALUE_PRODUCT_NAME = "RCP Product";
//	public static final String VALUE_PERSPECTIVE_NAME = "RCP Perspective";
//	public static final String VALUE_APPLICATION_ID = "application";

	/**
	 * Returns location where this plugin is installed.
	 */
	protected URL getInstallURL() {
		return HQDEPlugin.getDefault().getInstallURL();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.OptionTemplateSection#getTemplateLocation()
	 */
	public URL getTemplateLocation() {
		try {
			URL url = new URL(getInstallURL(), "templates" + "/" + getSectionId());
			return url;
		} catch (MalformedURLException e) { // do nothing
			HQDEPlugin.log(e);
		}
		return null;
	}


	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.ITemplateSection#getNewFiles()
	 */
	public String[] getNewFiles() {
		return new String[0];
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	protected String getFormattedPackageName(String id) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch))
					buffer.append(Character.toLowerCase(ch));
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.')
					buffer.append(ch);
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.templates.AbstractTemplateSection#generateFiles(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void generateFiles(IProgressMonitor monitor) throws CoreException {
		super.generateFiles(monitor);
	}


}
