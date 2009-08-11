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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.parts.FormEntry;
import org.hyperic.hypclipse.internal.util.HQDEJavaHelperUI;
import org.hyperic.hypclipse.plugin.IBaseModel;
import org.hyperic.hypclipse.plugin.IModelChangeProvider;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

/**
 * This section contains general plugin information.
 */
public class GeneralInfoSection extends HQDESection {

	// name of the plugin <plugin name=
	private FormEntry fNameEntry;
	
	// name of the plugin <plugin package=
	private FormEntry fPackageEntry;

	// name of the product plugin <plugin class=
	private FormEntry fProductPluginEntry;

	/**
	 * Constructor
	 * 
	 * @param page
	 * @param parent
	 */
	public GeneralInfoSection(HQDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDESection#createClient(org.eclipse.ui.forms.widgets.Section, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText(HQDEMessages.OverviewPage_PluginInfoSection_title);
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription(HQDEMessages.OverviewPage_PluginInfoSection_desc);
		Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientTableWrapLayout(false, 3));
		section.setClient(client);

//		IActionBars actionBars = getPage().getPDEEditor().getEditorSite().getActionBars();
		createNameEntry(client, toolkit, null);
		createPackageEntry(client, toolkit, null);
		createProductPluginEntry(client, toolkit, null);
//		createProviderEntry(client, toolkit, actionBars);
//		if (isBundle() && ((ManifestEditor) getPage().getEditor()).isEquinox())
//			createPlatformFilterEntry(client, toolkit, actionBars);
//		createSpecificControls(client, toolkit, actionBars);
		toolkit.paintBordersFor(client);

		addListeners();
	}
	
	protected IPluginBase getPluginBase() {
		IBaseModel model = getPage().getHQDEEditor().getAggregateModel();
		return ((IPluginModelBase) model).getPluginBase();
	}

	private void createProductPluginEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fProductPluginEntry = new FormEntry(client, toolkit, HQDEMessages.OverviewPage_PluginInfoSection_class, HQDEMessages.OverviewPage_PluginInfoSection_browse, isEditable());
		
		fProductPluginEntry.setFormEntryListener(new FormEntryAdapter(this/*, actionBars*/) {
			public void textValueChanged(FormEntry entry) {
				try {
					getPluginBase().setProductPlugin(entry.getValue());
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
			
			public void linkActivated(HyperlinkEvent e) {
				String value = fProductPluginEntry.getValue();
				IProject project = getPage().getHQDEEditor().getCommonProject();
				HQDEJavaHelperUI.openClass(value, project);
			}

			public void browseButtonSelected(FormEntry entry) {
				doOpenSelectionDialog(entry.getValue());
			}

		});
	}

	private void doOpenSelectionDialog(String className) {
		IResource resource = getPluginBase().getModel().getUnderlyingResource();
		String type = HQDEJavaHelperUI.selectType(resource, IJavaElementSearchConstants.CONSIDER_CLASSES, className, null);
		if (type != null)
			fProductPluginEntry.setValue(type);
	}

	
	private void createNameEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fNameEntry = new FormEntry(client, toolkit, "Name", null, false);
		fNameEntry.setFormEntryListener(new FormEntryAdapter(this/*, actionBars*/) {
			public void textValueChanged(FormEntry entry) {
				try {
					getPluginBase().setName(entry.getValue());
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
		});
		fNameEntry.setEditable(isEditable());
		// Create validator
//		fIdEntryValidator = new TextValidator(getManagedForm(), fIdEntry.getText(), getProject(), true) {
//			protected boolean validateControl() {
//				return validateIdEntry();
//			}
//		};
	}

	private void createPackageEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fPackageEntry = new FormEntry(client, toolkit, "Package", null, false);
		fPackageEntry.setFormEntryListener(new FormEntryAdapter(this/*, actionBars*/) {
			public void textValueChanged(FormEntry entry) {
				try {
					getPluginBase().setPackage(entry.getValue());
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
		});
		fPackageEntry.setEditable(isEditable());
		
	}
	
	
	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
			return;
		}
		refresh();
		if (e.getChangeType() == IModelChangedEvent.CHANGE) {
			Object obj = e.getChangedObjects()[0];
			if (obj instanceof IPluginBase) {
				String property = e.getChangedProperty();
				if (property != null && property.equals(getPage().getHQDEEditor().getTitleProperty()))
					getPage().getHQDEEditor().updateTitle();
			}
		}
	}

	
	public void refresh() {
		IPluginModelBase model = (IPluginModelBase) getPage().getHQDEEditor().getContextManager().getAggregateModel();
		IPluginBase pluginBase = model.getPluginBase();
//		fIdEntry.setValue(pluginBase.getId(), true);
		fNameEntry.setValue(pluginBase.getName(), true);
		fPackageEntry.setValue(pluginBase.getPackage(), true);
		fProductPluginEntry.setValue(pluginBase.getProductPlugin(), true);
//		fVersionEntry.setValue(pluginBase.getVersion(), true);
//		fProviderEntry.setValue(pluginBase.getProviderName(), true);
//		if (fPlatformFilterEntry != null) {
//			IBundle bundle = getBundle();
//			if (bundle != null)
//				fPlatformFilterEntry.setValue(bundle.getHeader(PLATFORM_FILTER), true);
//		}
		getPage().getHQDEEditor().updateTitle();
//		if (fSingleton != null) {
//			IManifestHeader header = getSingletonHeader();
//			fSingleton.setSelection(header instanceof BundleSymbolicNameHeader && ((BundleSymbolicNameHeader) header).isSingleton());
//		}
		super.refresh();

	}

	public void cancelEdit() {
//		fIdEntry.cancelEdit();
		fNameEntry.cancelEdit();
//		fVersionEntry.cancelEdit();
//		fProviderEntry.cancelEdit();
//		if (fPlatformFilterEntry != null)
//			fPlatformFilterEntry.cancelEdit();
		super.cancelEdit();
	}

	public void dispose() {
		removeListeners();
		super.dispose();
	}

	protected void removeListeners() {
		IBaseModel model = getPage().getModel();
		if (model instanceof IModelChangeProvider)
			((IModelChangeProvider) model).removeModelChangedListener(this);
	}

	protected void addListeners() {
		IBaseModel model = getPage().getModel();
		if (model instanceof IModelChangeProvider)
			((IModelChangeProvider) model).addModelChangedListener(this);
	}

}
