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

import java.util.ArrayList;

import org.hyperic.hypclipse.internal.templates.master.ProductPluginTemplate;
import org.hyperic.hypclipse.plugin.IPluginFieldData;
import org.hyperic.hypclipse.plugin.ITemplateSection;

/**
 * This wizard should be used as a base class for wizards that generate plug-in
 * content using a closed set of templates. These wizards are loaded during new
 * plug-in or fragment creation and are used to provide initial content (Java
 * classes, directories/files and extensions).
 * <p>
 * The list of templates is fixed. It must be known in advance so that the
 * required wizard pages can be created. Upon finish, the template sections are
 * executed in the order of creation.
 */
public abstract class NewPluginTemplateWizard extends AbstractNewPluginTemplateWizard {
	
	private ITemplateSection[] sections;

	/**
	 * Creates a new template wizard.
	 */
	public NewPluginTemplateWizard() {
	}

	/**
	 * Subclasses are required to implement this method by creating templates
	 * that will appear in this wizard.
	 * 
	 * @return an array of template sections that will appear in this wizard.
	 */
	public abstract ITemplateSection[] createTemplateSections();

	public void initTemplates() {
		sections = createTemplateSectionsWithDefault();
	}
	
	/**
	 * Utility method to wrap template creation. This method can
	 * be used to add default templates to the execution
	 * framework.
	 * @return List of templates
	 */
	private ITemplateSection[] createTemplateSectionsWithDefault() {
		ArrayList<ITemplateSection> allsections = new ArrayList<ITemplateSection>();
		ITemplateSection[] customs = createTemplateSections();
		if (customs != null) {
			for (int i = 0; i < customs.length; i++) {
				allsections.add(customs[i]);
			}
		}
		
		// check if user wants to use product plugin class
		if(((IPluginFieldData)getData()).doGenerateClass()) {
			allsections.add(new ProductPluginTemplate());
		}

		return allsections.toArray(new ITemplateSection[0]);
	}
	
	/**
	 * Returns templates that appear in this section.
	 * 
	 * @return an array of templates
	 */
	public final ITemplateSection[] getTemplateSections() {
		return sections;
	}

	/**
	 * Implemented by asking templates in this wizard to contribute pages.
	 *  
	 */
	protected final void addAdditionalPages() {
		// add template pages
		for (int i = 0; i < sections.length; i++) {
			sections[i].addPages(this);
		}
	}
}
