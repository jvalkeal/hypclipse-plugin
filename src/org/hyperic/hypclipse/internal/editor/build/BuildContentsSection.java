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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.TableSection;
import org.hyperic.hypclipse.internal.elements.DefaultContentProvider;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildPropertiesConstants;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;

public abstract class BuildContentsSection extends TableSection implements IModelChangedListener, IResourceChangeListener, IResourceDeltaVisitor {

	protected CheckboxTreeViewer fTreeViewer;
	private boolean fDoRefresh = false;
	protected IProject fProject;
	protected IBuildModel fBuildModel;
	protected IResource fOriginalResource, fParentResource;
	protected boolean isChecked;

	public class TreeContentProvider extends DefaultContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object parent) {
			if (parent instanceof IProject) {
				try {
					return ((IProject) parent).members();
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			try {
				if (parent instanceof IFolder)
					return ((IFolder) parent).members();
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
			return new Object[0];
		}

		public Object[] getFolderChildren(Object parent) {
			IResource[] members = null;
			try {
				if (!(parent instanceof IFolder))
					return new Object[0];
				members = ((IFolder) parent).members();
				ArrayList<IResource> results = new ArrayList<IResource>();
				for (int i = 0; i < members.length; i++) {
					if ((members[i].getType() == IResource.FOLDER)) {
						results.add(members[i]);
					}
				}
				return results.toArray();
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			if (element != null && element instanceof IResource) {
				return ((IResource) element).getParent();
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			if (element instanceof IFolder)
				return getChildren(element).length > 0;
			return false;
		}
	}

	protected void createViewerPartControl(Composite parent, int style, int span, FormToolkit toolkit) {
		MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager mng) {
				fillContextMenu(mng);
			}
		};
		popupMenuManager.addMenuListener(listener);
		popupMenuManager.setRemoveAllWhenShown(true);
		Control control = fTreeViewer.getControl();
		Menu menu = popupMenuManager.createContextMenu(control);
		control.setMenu(menu);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(getPage().getHQDEEditor().getContributor().getRevertAction());
		getPage().getHQDEEditor().getContributor().contextMenuAboutToShow(manager, false);
	}

	private IBuildModel getBuildModel() {
		InputContext context = getPage().getHQDEEditor().getContextManager().findContext(BuildInputContext.CONTEXT_ID);
		return (IBuildModel) context.getModel();
	}

	public BuildContentsSection(HQDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[0]);
		HQDEPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void createClient(final Section section, FormToolkit toolkit) {
		Composite container = createClientContainer(section, 2, toolkit);
		fBuildModel = getBuildModel();
		if (fBuildModel.getUnderlyingResource() != null)
			fProject = fBuildModel.getUnderlyingResource().getProject();

		fTreeViewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK));
		fTreeViewer.setContentProvider(new TreeContentProvider());
		fTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
		fTreeViewer.setAutoExpandLevel(0);
		fTreeViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(final CheckStateChangedEvent event) {
				final Object element = event.getElement();
				BusyIndicator.showWhile(section.getDisplay(), new Runnable() {

					public void run() {
						if (element instanceof IFile) {
							IFile file = (IFile) event.getElement();
							handleCheckStateChanged(file, event.getChecked());
						} else if (element instanceof IFolder) {
							IFolder folder = (IFolder) event.getElement();
							handleCheckStateChanged(folder, event.getChecked());
						}
					}
				});
			}
		});
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.widthHint = 100;
		fTreeViewer.getTree().setLayoutData(gd);
		initialize();
		toolkit.paintBordersFor(container);
		createViewerPartControl(container, SWT.FULL_SELECTION, 2, toolkit);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setClient(container);
	}

	public void enableSection(boolean enable) {
		fTreeViewer.getTree().setEnabled(enable);
	}

	protected void handleCheckStateChanged(IResource resource, boolean checked) {
		fOriginalResource = resource;
		isChecked = checked;
		boolean wasTopParentChecked = fTreeViewer.getChecked(fOriginalResource.getParent());
		if (!isChecked) {
			resource = handleAllUnselected(resource, resource.getName());
		}
		fParentResource = resource;
		handleBuildCheckStateChange(wasTopParentChecked);
	}

	protected IResource handleAllUnselected(IResource resource, String name) {
		IResource parent = resource.getParent();
		if (parent == resource.getProject()) {
			return resource;
		}
		try {
			boolean uncheck = true;
			IResource[] members = ((IFolder) parent).members();
			for (int i = 0; i < members.length; i++) {
				if (fTreeViewer.getChecked(members[i]) && !members[i].getName().equals(name))
					uncheck = false;
			}
			if (uncheck) {
				return handleAllUnselected(parent, parent.getName());
			}
			return resource;
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
			return null;
		}
	}

	protected void setChildrenGrayed(IResource folder, boolean isGray) {
		fTreeViewer.setGrayed(folder, isGray);
		if (((TreeContentProvider) fTreeViewer.getContentProvider()).hasChildren(folder)) {
			Object[] members = ((TreeContentProvider) fTreeViewer.getContentProvider()).getFolderChildren(folder);
			for (int i = 0; i < members.length; i++) {
				setChildrenGrayed((IFolder) members[i], isGray);
			}
		}
	}

	protected void setParentsChecked(IResource resource) {
		if (resource.getParent() != resource.getProject()) {
			fTreeViewer.setChecked(resource.getParent(), true);
			setParentsChecked(resource.getParent());
		}
	}

	/**
	 * removes all child resources of the specified folder from build entries
	 * 
	 * @param folder -
	 *            current folder being modified in tree
	 * 
	 * note: does not remove folder itself
	 */
	protected abstract void deleteFolderChildrenFromEntries(IFolder folder);

	protected void initializeCheckState() {
		uncheckAll();
	}

	protected void initializeCheckState(final IBuildEntry includes, final IBuildEntry excludes) {
		fTreeViewer.getTree().getDisplay().asyncExec(new Runnable() {

			public void run() {
				// found slight improvements using Display.getCurrent() instead of fTreeViewer.getTree().getDisplay()
				BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

					public void run() {
						if (fTreeViewer.getTree().isDisposed())
							return;
						Vector fileExt = new Vector();
						String[] inclTokens, exclTokens = new String[0];
						if (fProject == null || includes == null)
							return;
						inclTokens = includes.getTokens();
						if (excludes != null)
							exclTokens = excludes.getTokens();
						Set temp = new TreeSet();
						for (int i = 0; i < inclTokens.length; i++)
							temp.add(inclTokens[i]);
						for (int i = 0; i < exclTokens.length; i++)
							temp.add(exclTokens[i]);
						Iterator iter = temp.iterator();
						while (iter.hasNext()) {
							String resource = iter.next().toString();
							boolean isIncluded = includes.contains(resource);
							if (resource.equals(".") || resource.equals("./") || resource.equals(".\\")) {
								// ignore - should be root directory
							} else if (resource.lastIndexOf(IPath.SEPARATOR) == resource.length() - 1) {
								IFolder folder = fProject.getFolder(resource);
								if (!folder.exists())
									continue;
								fTreeViewer.setSubtreeChecked(folder, isIncluded);
								fTreeViewer.setParentsGrayed(folder, true);
								if (isIncluded) {
									setParentsChecked(folder);
									fTreeViewer.setGrayed(folder, false);
								}
							} else if (resource.startsWith("*.")) {
								if (isIncluded)
									fileExt.add(resource.substring(2));
							} else {
								IFile file = fProject.getFile(resource);
								if (!file.exists())
									continue;
								fTreeViewer.setChecked(file, isIncluded);
								fTreeViewer.setParentsGrayed(file, true);
								if (isIncluded) {
									fTreeViewer.setGrayed(file, false);
									setParentsChecked(file);
								}
							}
						}
						if (fileExt.size() == 0)
							return;
						try {
							IResource[] members = fProject.members();
							for (int i = 0; i < members.length; i++) {
								if (!(members[i] instanceof IFolder) && (fileExt.contains(members[i].getFileExtension()))) {
									fTreeViewer.setChecked(members[i], includes.contains("*." + members[i].getFileExtension()));
								}
							}
						} catch (CoreException e) {
							HQDEPlugin.logException(e);
						}
					}
				});
			}
		});
	}

	protected abstract void handleBuildCheckStateChange(boolean wasTopParentChecked);

	protected void handleCheck(IBuildEntry includes, IBuildEntry excludes, String resourceName, IResource resource, boolean wasTopParentChecked, String PROPERTY_INCLUDES) {

		try {
			if (includes == null) {
				includes = fBuildModel.getFactory().createEntry(PROPERTY_INCLUDES);
				IBuild build = fBuildModel.getBuild();
				build.add(includes);
			}
			if ((!wasTopParentChecked && !includes.contains(resourceName)) || isValidIncludeEntry(includes, excludes, resource, resourceName)) {
				includes.addToken(resourceName);
			}
			if (excludes != null && excludes.contains(resourceName))
				excludes.removeToken(resourceName);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	protected boolean isValidIncludeEntry(IBuildEntry includes, IBuildEntry excludes, IResource resource, String resourceName) {
		if (excludes == null)
			return true;
		IPath resPath = resource.getProjectRelativePath();
		while (resPath.segmentCount() > 1) {
			resPath = resPath.removeLastSegments(1);
			if (includes.contains(resPath.toString() + IPath.SEPARATOR))
				return false;
			else if (excludes != null && excludes.contains(resPath.toString() + IPath.SEPARATOR))
				return true;
		}
		return !excludes.contains(resourceName);
	}

	protected void handleUncheck(IBuildEntry includes, IBuildEntry excludes, String resourceName, IResource resource, String PROPERTY_EXCLUDES) {

		try {
			if (fTreeViewer.getChecked(resource.getParent())) {
				if (excludes == null) {
					excludes = fBuildModel.getFactory().createEntry(PROPERTY_EXCLUDES);
					IBuild build = fBuildModel.getBuild();
					build.add(excludes);
				}
				if (!excludes.contains(resourceName) && (includes != null ? !includes.contains(resourceName) : true))
					excludes.addToken(resourceName);
			}
			if (includes != null) {
				if (includes.contains(resourceName))
					includes.removeToken(resourceName);
				if (includes.contains("*." + resource.getFileExtension())) {
					IResource[] members = fProject.members();
					for (int i = 0; i < members.length; i++) {
						if (!(members[i] instanceof IFolder) && !members[i].getName().equals(resource.getName()) && (resource.getFileExtension().equals(members[i].getFileExtension()))) {
							includes.addToken(members[i].getName());
						}
						IBuildEntry[] libraries = BuildUtil.getBuildLibraries(fBuildModel.getBuild().getBuildEntries());
						if (resource.getFileExtension().equals("jar")
								&& libraries.length != 0) {
							for (int j = 0; j < libraries.length; j++) {
								String libName = libraries[j].getName().substring(7);
								IPath path = fProject.getFile(libName).getProjectRelativePath();
								if (path.segmentCount() == 1 && !includes.contains(libName) && !libName.equals(resource.getName()))
									includes.addToken(libName);
							}
						}
					}
					includes.removeToken("*." + resource.getFileExtension());
				}
			}
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	protected String getResourceFolderName(String resourceName) {
		return resourceName + IPath.SEPARATOR;
	}

	/**
	 * @param resource -
	 *            file/folder being modified in tree
	 * @param resourceName -
	 *            name file/folder
	 * @return relative path of folder if resource is folder, otherwise, return
	 *         resourceName
	 */
	protected String handleResourceFolder(IResource resource, String resourceName) {
		if (resource instanceof IFolder) {
			deleteFolderChildrenFromEntries((IFolder) resource);
			return getResourceFolderName(resourceName);
		}
		return resourceName;
	}

	public void initialize() {
		if (fTreeViewer.getInput() == null) {
			fTreeViewer.setUseHashlookup(true);
			fTreeViewer.setInput(fProject);
		}
		fBuildModel.addModelChangedListener(this);
	}

	public void dispose() {
		fBuildModel.removeModelChangedListener(this);
		HQDEPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	protected void deleteEmptyEntries() {
		IBuild build = fBuildModel.getBuild();
		IBuildEntry[] entries = {build.getEntry(IBuildPropertiesConstants.PROPERTY_BIN_EXCLUDES), build.getEntry(IBuildPropertiesConstants.PROPERTY_BIN_INCLUDES), build.getEntry(IBuildPropertiesConstants.PROPERTY_SRC_EXCLUDES), build.getEntry(IBuildPropertiesConstants.PROPERTY_SRC_INCLUDES)};
		try {
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] != null && entries[i].getTokens().length == 0)
					build.remove(entries[i]);
			}
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	public CheckboxTreeViewer getTreeViewer() {
		return fTreeViewer;
	}

	protected ISelection getViewerSelection() {
		return getTreeViewer().getSelection();
	}

	public void refresh() {
		initializeCheckState();
		super.refresh();
	}

	public void uncheckAll() {
		fTreeViewer.setCheckedElements(new Object[0]);
	}

	protected void removeChildren(IBuildEntry entry, String parentFolder) {
		try {
			if (entry != null) {
				String[] tokens = entry.getTokens();
				for (int i = 0; i < tokens.length; i++) {
					if (tokens[i].indexOf(IPath.SEPARATOR) != -1 && tokens[i].startsWith(parentFolder) && !tokens[i].equals(parentFolder)) {
						entry.removeToken(tokens[i]);
					}
				}
			}
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (fTreeViewer.getControl().isDisposed())
			return;
		fDoRefresh = false;
		IResourceDelta delta = event.getDelta();
		try {
			if (delta != null)
				delta.accept(this);
			if (fDoRefresh) {
				asyncRefresh();
				fDoRefresh = false;
			}
		} catch (CoreException e) {
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		IProject project = fBuildModel.getUnderlyingResource().getProject();

		if ((resource instanceof IFile || resource instanceof IFolder) && resource.getProject().equals(project)) {
			if (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.REMOVED) {
				fDoRefresh = true;
				return false;
			}
		} else if (resource instanceof IProject && ((IProject) resource).equals(project)) {
			return delta.getKind() != IResourceDelta.REMOVED;
		}
		return true;
	}

	private void asyncRefresh() {
		Control control = fTreeViewer.getControl();
		if (!control.isDisposed()) {
			control.getDisplay().asyncExec(new Runnable() {

				public void run() {
					if (!fTreeViewer.getControl().isDisposed()) {
						fTreeViewer.refresh(true);
						initializeCheckState();
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.editor.TableSection#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	protected void selectionChanged(IStructuredSelection selection) {
		getPage().getHQDEEditor().setSelection(selection);

	}

	public void modelChanged(IModelChangedEvent event) {

		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
		}
		Object changeObject = event.getChangedObjects()[0];

		if (!(changeObject instanceof IBuildEntry && (((IBuildEntry) changeObject).getName().equals(IBuildPropertiesConstants.PROPERTY_BIN_EXCLUDES) || ((IBuildEntry) changeObject).getName().equals(IBuildPropertiesConstants.PROPERTY_BIN_INCLUDES) || ((IBuildEntry) changeObject).getName().equals(IBuildPropertiesConstants.PROPERTY_SRC_EXCLUDES) || ((IBuildEntry) changeObject).getName().equals(IBuildPropertiesConstants.PROPERTY_SRC_INCLUDES))))
			return;

		if ((fParentResource == null && fOriginalResource != null) || (fOriginalResource == null && fParentResource != null)) {
			initializeCheckState();
			return;
		}
		if ((fParentResource == null && fOriginalResource == null) || (event.getChangedProperty() != null && event.getChangedProperty().equals(IBuildPropertiesConstants.PROPERTY_BIN_INCLUDES))) {

			return;
		}

		fTreeViewer.setChecked(fParentResource, isChecked);
		fTreeViewer.setGrayed(fOriginalResource, false);
		fTreeViewer.setParentsGrayed(fParentResource, true);
		setParentsChecked(fParentResource);
		fTreeViewer.setGrayed(fParentResource, false);
		if (fParentResource instanceof IFolder) {
			fTreeViewer.setSubtreeChecked(fParentResource, isChecked);
			setChildrenGrayed(fParentResource, false);
		}
		while (!fOriginalResource.equals(fParentResource)) {
			fTreeViewer.setChecked(fOriginalResource, isChecked);
			fOriginalResource = fOriginalResource.getParent();
		}
		fParentResource = null;
		fOriginalResource = null;
	}
}
