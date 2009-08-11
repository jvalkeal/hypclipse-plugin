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
package org.hyperic.hypclipse.internal.editor.build;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.TableSection;
import org.hyperic.hypclipse.internal.elements.DefaultContentProvider;
import org.hyperic.hypclipse.internal.parts.EditableTablePart;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildPropertiesConstants;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;

public class BuildClasspathSection extends TableSection implements IModelChangedListener {

	private TableViewer fTableViewer;
	private boolean fEnabled = true;

	/**
	 * Implementation of a <code>ISelectionValidator</code> to validate the
	 * type of an element.
	 * Empty selections are not accepted.
	 */
	class ElementSelectionValidator implements ISelectionStatusValidator {

		private Class[] fAcceptedTypes;
		private boolean fAllowMultipleSelection;

		/**
		 * @param acceptedTypes The types accepted by the validator
		 * @param allowMultipleSelection If set to <code>true</code>, the validator
		 * allows multiple selection.
		 */
		public ElementSelectionValidator(Class[] acceptedTypes, boolean allowMultipleSelection) {
			Assert.isNotNull(acceptedTypes);
			fAcceptedTypes = acceptedTypes;
			fAllowMultipleSelection = allowMultipleSelection;
		}

		/*
		 * @see org.eclipse.ui.dialogs.ISelectionValidator#isValid(java.lang.Object)
		 */
		public IStatus validate(Object[] elements) {
			if (isValid(elements)) {
				return new Status(IStatus.OK, HQDEPlugin.getPluginId(), IStatus.OK, "", //$NON-NLS-1$
						null);
			}
			return new Status(IStatus.ERROR, HQDEPlugin.getPluginId(), IStatus.ERROR, "", //$NON-NLS-1$
					null);
		}

		private boolean isOfAcceptedType(Object o) {
			for (int i = 0; i < fAcceptedTypes.length; i++) {
				if (fAcceptedTypes[i].isInstance(o)) {
					return true;
				}
			}
			return false;
		}

		private boolean isValid(Object[] selection) {
			if (selection.length == 0) {
				return false;
			}

			if (!fAllowMultipleSelection && selection.length != 1) {
				return false;
			}

			for (int i = 0; i < selection.length; i++) {
				Object o = selection[i];
				if (!isOfAcceptedType(o)) {
					return false;
				}
			}
			return true;
		}
	}

	class TableContentProvider extends DefaultContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IBuildModel) {
				IBuild build = ((IBuildModel) parent).getBuild();
				IBuildEntry entry = build.getEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
				if (entry != null) {
					return entry.getTokens();
				}
			}
			return new Object[0];
		}
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return obj.toString();
		}

		public Image getColumnImage(Object obj, int index) {
			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}
	}

	public BuildClasspathSection(HQDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TWISTIE, new String[] {HQDEMessages.ClasspathPage_ClasspathSection_add, HQDEMessages.ClasspathPage_ClasspathSection_remove, null, null});
		//super(page, parent, Section.DESCRIPTION , new String[] {HQDEMessages.ClasspathPage_ClasspathSection_add, HQDEMessages.ClasspathPage_ClasspathSection_remove, null, null});
		getSection().setText(HQDEMessages.ClasspathPage_ClasspathSection_title);
		getSection().setDescription(HQDEMessages.ClasspathPage_ClasspathSection_desc);
		initialize();

	}

	private IBuildModel getBuildModel() {
		InputContext context = 
			getPage().
			getHQDEEditor().
			getContextManager().
			findContext(BuildInputContext.CONTEXT_ID);
		if (context == null)
			return null;
		return (IBuildModel) context.getModel();
	}

	public void initialize() {
		getBuildModel().addModelChangedListener(this);
		IBuildEntry entry = getBuildModel().getBuild().getEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
		getSection().setExpanded(entry != null && entry.getTokens().length > 0);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.FULL_SELECTION, 2, toolkit);

		EditableTablePart tablePart = getTablePart();
		tablePart.setEditable(true);
		fTableViewer = tablePart.getTableViewer();

		fTableViewer.setContentProvider(new TableContentProvider());
		fTableViewer.setLabelProvider(new TableLabelProvider());
		fTableViewer.setInput(getBuildModel());

		toolkit.paintBordersFor(container);
		enableSection(true);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		section.setLayoutData(data);
		data.horizontalSpan = 2;
		section.setClient(container);
	}
// TODO menus disabled
//	protected void fillContextMenu(IMenuManager manager) {
//		ISelection selection = fTableViewer.getSelection();
//
//		// add NEW action
//		Action action = new Action(HQDEMessages.ClasspathPage_ClasspathSection_add) {
//			public void run() {
//				handleNew();
//			}
//		};
//		action.setEnabled(fEnabled);
//		manager.add(action);
//
//		manager.add(new Separator());
//
//		// add DELETE action
//		action = new Action(HQDEMessages.ClasspathPage_ClasspathSection_remove) {
//			public void run() {
//				handleDelete();
//			}
//		};
//		action.setEnabled(!selection.isEmpty() && fEnabled);
//		manager.add(action);
//
//		getPage().getHQDEEditor().getContributor().contextMenuAboutToShow(manager, false);
//	}

	public void dispose() {
		IBuildModel model = getBuildModel();
		if (model != null)
			model.removeModelChangedListener(this);
		super.dispose();
	}

	public void refresh() {
		fTableViewer.refresh();
	}

	public boolean doGlobalAction(String actionId) {
		if (actionId.equals(ActionFactory.DELETE.getId())) {
			if (fEnabled) {
				handleDelete();
			}
			return true;
		}
		return false;
	}

	public void enableSection(boolean enable) {
		fEnabled = enable;
		EditableTablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(1, enable && !fTableViewer.getSelection().isEmpty());
		tablePart.setButtonEnabled(0, enable);
	}

	protected void selectionChanged(IStructuredSelection selection) {
		getPage().getHQDEEditor().setSelection(selection);
		getTablePart().setButtonEnabled(1, selection != null && selection.size() > 0 && fEnabled);
	}

	private void handleDelete() {
		Object selection = ((IStructuredSelection) fTableViewer.getSelection()).getFirstElement();
		if (selection != null && selection instanceof String) {
			IBuild build = getBuildModel().getBuild();
			IBuildEntry entry = build.getEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
			if (entry != null) {
				try {
					entry.removeToken(selection.toString());

					String[] tokens = entry.getTokens();
					if (tokens.length == 0)
						build.remove(entry);

				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
		}
	}

	private void initializeDialogSettings(ElementTreeSelectionDialog dialog) {
		Class[] acceptedClasses = new Class[] {IFile.class};
		dialog.setValidator(new ElementSelectionValidator(acceptedClasses, true));
		dialog.setTitle(HQDEMessages.ClasspathPage_ClasspathSection_jarsTitle);
		dialog.setMessage(HQDEMessages.ClasspathPage_ClasspathSection_jarsDesc);
		// TODO add filters
//		dialog.addFilter(new JARFileFilter());
//		dialog.addFilter(new ViewerFilter() {
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				if (element instanceof IProject) {
//					try {
//						return ((IProject) element).hasNature(PDE.PLUGIN_NATURE);
//					} catch (CoreException e) {
//					}
//					return false;
//				} else if (element instanceof IResource) {
//					IBuildModel model = getBuildModel();
//					IBuildEntry entry = model.getBuild().getEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
//					if (entry != null)
//						return !entry.contains(getRelativePathTokenName((IResource) element));
//				}
//				return true;
//			}
//		});
		dialog.setInput(HQDEPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		//dialog.setInitialSelection(getBuildModel().getUnderlyingResource().getProject());

	}

	private void handleNew() {
//		ElementTreeSelectionDialog dialog = 
//			new ElementTreeSelectionDialog(
//					getSection().getShell(),
//					new WorkbenchLabelProvider(),
//					new WorkbenchContentProvider());
//		
//		initializeDialogSettings(dialog);
//		if (dialog.open() == Window.OK) {
//			Object[] elements = dialog.getResult();
//			for (int i = 0; i < elements.length; i++) {
//				IResource elem = (IResource) elements[i];
//				String tokenName = getRelativePathTokenName(elem);
//				if (tokenName == null)
//					continue;
//				addClasspathToken(tokenName);
//			}
//		}
		FileDialog dialog = new FileDialog(getSection().getShell());
		dialog.setText("Select jar");
		String filePath = dialog.open();
		if(filePath != null) {
			addClasspathToken(filePath);
		}
	}

	private void addClasspathToken(String tokenName) {
		IBuildModel model = getBuildModel();
		IBuildEntry entry = model.getBuild().getEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
		try {
			if (entry == null) {
				entry = model.getFactory().createEntry(IBuildPropertiesConstants.PROPERTY_JAR_EXTRA_CLASSPATH);
				model.getBuild().add(entry);
			}
			if (!entry.contains(tokenName))
				entry.addToken(tokenName);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	private String getRelativePathTokenName(IResource elem) {
		IProject thisProject = getBuildModel().getUnderlyingResource().getProject();
		IProject elemProject = elem.getProject();
		String projectRelative = elem.getProjectRelativePath().toString();
		return elem.getLocation().toOSString();
//		if (thisProject == elemProject)
//			return projectRelative;

//		IPluginModelBase model = PluginRegistry.findModel(elemProject);
//		if (model != null) {
//			// TODO ok fix this method
//			return "platform:/plugin/" + model.getPluginBase().getId() + '/' + projectRelative; //$NON-NLS-1$
//		}
//		return null;
	}

	protected void buttonSelected(int index) {
		switch (index) {
			case 0 :
				handleNew();
				break;
			case 1 :
				handleDelete();
				break;
			default :
				break;
		}
	}

	public void modelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED)
			markStale();
		else if (event.getChangeType() == IModelChangedEvent.CHANGE) {
			Object changeObject = event.getChangedObjects()[0];

			if (changeObject instanceof IBuildEntry && ((IBuildEntry) changeObject).getName().equals(IBuildEntry.JARS_EXTRA_CLASSPATH)) {
				Table table = fTableViewer.getTable();
				int index = table.getSelectionIndex();
				fTableViewer.refresh();
				int count = table.getItemCount();
				if (index == -1 || index >= count || event.getOldValue() == null)
					index = count - 1;
				if (count == 0)
					fTableViewer.setSelection(null);
				else
					fTableViewer.setSelection(new StructuredSelection(table.getItem(index).getData()));
				table.setFocus();
			}
		}
	}
}
