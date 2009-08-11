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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hyperic.hypclipse.internal.HQDELabelProvider;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.HQDEPluginEditor;
import org.hyperic.hypclipse.internal.editor.TreeSection;
import org.hyperic.hypclipse.internal.elements.DefaultContentProvider;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.parts.TreePart;
import org.hyperic.hypclipse.internal.schema.ElementCollector;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.text.DocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginParentNode;
import org.hyperic.hypclipse.internal.util.SWTUtil;
import org.hyperic.hypclipse.internal.wizards.structure.NewStructureWizard;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;

public class StructureSection extends TreeSection implements IModelChangedListener, IPropertyChangeListener {

	private static final int BUTTON_MOVE_DOWN = 4;
	private static final int BUTTON_MOVE_UP = 3;
	private static final int BUTTON_EDIT = 2;
	private static final int BUTTON_REMOVE = 1;
	private static final int BUTTON_ADD = 0;
	
	private TreeViewer fStructureTree;
	private Hashtable fEditorWizards;


	public StructureSection(HQDEFormPage formPage, Composite parent) {
		super(formPage, parent, Section.DESCRIPTION,new String[] {
				HQDEMessages.StructureSection_add,
				HQDEMessages.StructureSection_remove,
				HQDEMessages.StructureSection_edit,
				HQDEMessages.StructureSection_up,
				HQDEMessages.StructureSection_down
		});
	}

	public void initializeImages() {
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void createClient(Section section, FormToolkit toolkit) {
		initializeImages();
		Composite container = createClientContainer(section, 2, toolkit);
		TreePart treePart = getTreePart();
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		fStructureTree = treePart.getTreeViewer();
		fStructureTree.setContentProvider(new StructureContentProvider());
		fStructureTree.setLabelProvider(new StructureLabelProvider());
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription(HQDEMessages.StructureSection_StructureTree_desc);
		section.setText(HQDEMessages.StructureSection_StructureTree_title);
		initialize((IPluginModelBase) getPage().getModel());
		
//		createSectionToolbar(section, toolkit);
		// Create the adapted listener for the filter entry field
//		fFilteredTree.createUIListenerEntryFilter(this);
//		Text filterText = fFilteredTree.getFilterControl();
//		if (filterText != null) {
//			filterText.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent e) {
//					StructuredViewer viewer = getStructuredViewerPart().getViewer();
//					IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
//					updateButtons(ssel.size() != 1 ? null : ssel);
//				}
//			});
//		}
		
	}

	protected void selectionChanged(IStructuredSelection selection) {
		getPage().getHQDEEditor().setSelection(selection);
		updateButtons(selection);
//		getTreePart().getButton(BUTTON_EDIT).setVisible(isSelectionEditable(selection));
		getTreePart().getButton(BUTTON_EDIT).setVisible(false);
	}

	private void updateButtons(Object item) {
		boolean addEnabled = true;
		boolean removeEnabled = false;
		boolean upEnabled = false;
		boolean downEnabled = false;

		if (item != null) {
			removeEnabled = true;
		}
		
		if (item instanceof IStructuredSelection) {
			if (((IStructuredSelection) item).size() == 1) {
				Object selected = ((IStructuredSelection) item).getFirstElement();
				if (selected instanceof IPluginElement) {
					IPluginElement element = (IPluginElement) selected;
					if(element.getParent() instanceof PluginNode) {
						PluginNode node = (PluginNode)element.getParent();
						DocumentElementNode parent = (DocumentElementNode)element.getParent(); 
						IDocumentElementNode[]children =  parent.getChildNodes();
						int index = parent.indexOf((PluginElementNode)element);
						if (index > 0)
							upEnabled = true;
						if (index < parent.getChildCount() - 1)
							downEnabled = true;						
					
					} else {
						IPluginParent parent = (IPluginParent) element.getParent();
						// check up
						int index = parent.getIndexOf(element);
						if (index > 0)
							upEnabled = true;
						if (index < parent.getChildCount() - 1)
							downEnabled = true;						
					}

				} else if (selected instanceof IPluginParent) {
					IPluginParent element = (IPluginParent)selected;
					if(element.getParent() instanceof PluginNode) {
						DocumentElementNode parent = (DocumentElementNode)element.getParent(); 
						int index = parent.indexOf((PluginParentNode)element);
						if (index > 0)
							upEnabled = true;
						if (index < parent.getChildCount() - 1)
							downEnabled = true;						
					}
					
				}

			} 

		}

		
		getTreePart().setButtonEnabled(BUTTON_ADD, addEnabled);
		getTreePart().setButtonEnabled(BUTTON_REMOVE, removeEnabled);
		getTreePart().setButtonEnabled(BUTTON_MOVE_UP, upEnabled);
		getTreePart().setButtonEnabled(BUTTON_MOVE_DOWN, downEnabled);

	}

	protected void buttonSelected(int index) {
		switch (index) {
			case BUTTON_ADD :
				handleNew();
				break;
			case BUTTON_REMOVE :
				handleDelete();
				break;
			case BUTTON_EDIT :
//				handleEdit();
				break;
			case BUTTON_MOVE_UP :
				handleMove(true);
				break;
			case BUTTON_MOVE_DOWN :
				handleMove(false);
				break;
		}
	}

	public void dispose() {
		// Explicitly call the dispose method on the extensions tree
//		if (fFilteredTree != null) {
//			fFilteredTree.dispose();
//		}
		fEditorWizards = null;
		IPluginModelBase model = (IPluginModelBase) getPage().getHQDEEditor().getAggregateModel();
		if (model != null)
			model.removeModelChangedListener(this);
		super.dispose();
	}

	public boolean setFormInput(Object object) {
		if (object instanceof IHQServer || object instanceof IPluginElement) {
			fStructureTree.setSelection(new StructuredSelection(object), true);
			return true;
		}
		return false;
	}

	protected void fillContextMenu(IMenuManager manager) {
		ISelection selection = fStructureTree.getSelection();
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			Object object = ssel.getFirstElement();
			if (object instanceof IPluginParent) {
				IPluginParent parent = (IPluginParent) object;
				fillContextMenu(getPage(), parent, manager);
				manager.add(new Separator());
			}
			
		} else if (ssel.size() > 1) {
			
		}
		Action delAction = new Action() {
			public void run() {
				handleDelete();
			}
		};
		delAction.setText(HQDEMessages.StructureSection_remove);
		manager.add(delAction);
		manager.add(new Separator());
//		getPage().getHQDEEditor().getContributor().contextMenuAboutToShow(manager, false);

	}

	IMenuManager fillContextMenu(HQDEFormPage page, final IPluginParent parent, IMenuManager manager) {
		ArrayList<ElementExp> sElements = new ArrayList<ElementExp>();
		if(parent instanceof ISchemaElement) {
			ExpressionPool pool = new ExpressionPool();
			ElementCollector coll = new ElementCollector();
			ElementExp e = ((ISchemaElement)parent).getElementExp();
			Expression eExp = e.contentModel.getExpandedExp(pool);
			coll.collect(eExp, sElements);	
		}
		MenuManager newMenu = new MenuManager(HQDEMessages.StructureSection_new);
		ListIterator<ElementExp> iter = sElements.listIterator();
		HQDELabelProvider provider = HQDEPlugin.getDefault().getLabelProvider();
		while(iter.hasNext()) {
			ElementExp exp = iter.next();
			NewStructureAction action = new NewStructureAction(parent,exp);
			String name = exp.getNameClass().toString();
			action.setText(name);
			action.setImageDescriptor(provider.getStructureImageDescriptor(name));
			newMenu.add(action);
		}
		manager.add(newMenu);
		return manager;	
	}

	
	public void initialize(IPluginModelBase model) {
		fStructureTree.setInput(model.getPluginBase());
//		selectFirstExtension();
		boolean editable = model.isEditable();
		TreePart treePart = getTreePart();
		treePart.setButtonEnabled(BUTTON_ADD, editable);
		treePart.setButtonEnabled(BUTTON_REMOVE, false);
		treePart.setButtonEnabled(BUTTON_EDIT, false);
		treePart.setButtonEnabled(BUTTON_MOVE_UP, false);
		treePart.setButtonEnabled(BUTTON_MOVE_DOWN, false);
		model.addModelChangedListener(this);
	}
	
	public void modelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
			return;
		}
		// TODO very ugly method... clean!
		Object changeObject = event.getChangedObjects()[0];

		if (changeObject instanceof IPluginBase) {
			if (event.getChangedProperty().equals(IPluginParent.P_SIBLING_ORDER)) {
//				IPluginObject pobj = (IPluginObject) changeObject;
				IStructuredSelection sel = (IStructuredSelection) fStructureTree.getSelection();
				IPluginObject child = (IPluginObject) sel.getFirstElement();
				fStructureTree.refresh(child.getParent());
				fStructureTree.setSelection(new StructuredSelection(child));
			}
		}
		
		boolean customIHQ = false;
		if (
			changeObject instanceof IHQServer ||
			changeObject instanceof IHQClasspath ||
			changeObject instanceof IHQMetrics ||
			changeObject instanceof IHQHelp ||
			changeObject instanceof IHQScript ||
			changeObject instanceof IHQPlatform
			)
			customIHQ = true;
			
		
		if (customIHQ || (changeObject instanceof IPluginElement/* && ((IPluginElement) changeObject).getParent() instanceof IPluginParent*/)) {
			IPluginObject pobj = (IPluginObject) changeObject;
//			IPluginObject parent = changeObject instanceof IPluginExtension ? ((IPluginModelBase) getPage().getModel()).getPluginBase() : pobj.getParent();
			IPluginObject parent = customIHQ ? ((IPluginModelBase) getPage().getModel()).getPluginBase() : pobj.getParent();
//			IPluginObject parent = pobj.getParent();
			if (event.getChangeType() == IModelChangedEvent.INSERT) {
				fStructureTree.refresh(parent);
				fStructureTree.setSelection(new StructuredSelection(changeObject), true);
				fStructureTree.getTree().setFocus();
			} else if (event.getChangeType() == IModelChangedEvent.REMOVE) {
				fStructureTree.remove(pobj);
			} else {
				if (event.getChangedProperty().equals(IPluginParent.P_SIBLING_ORDER)) {
					IStructuredSelection sel = (IStructuredSelection) fStructureTree.getSelection();
					IPluginObject child = (IPluginObject) sel.getFirstElement();
					fStructureTree.refresh(child.getParent());
					fStructureTree.setSelection(new StructuredSelection(child));
				} else {
					fStructureTree.update(changeObject, null);
				}
			}
		}
	}

	
	protected void selectStructureElement(ISelection selection) {
		fStructureTree.setSelection(selection, true);
	}

	private void selectFirstExtension() {
		Tree tree = fStructureTree.getTree();
		TreeItem[] items = tree.getItems();
		if (items.length == 0)
			return;
		TreeItem firstItem = items[0];
		Object obj = firstItem.getData();
		fStructureTree.setSelection(new StructuredSelection(obj));
	}

	
	public void refresh() {
		IPluginModelBase model = (IPluginModelBase) getPage().getModel();
		fStructureTree.setInput(model.getPluginBase());
		selectFirstExtension();
		getManagedForm().fireSelectionChanged(StructureSection.this, fStructureTree.getSelection());
		super.refresh();
	}

	private Image resolveObjectImage(Object obj) {
		HQDELabelProvider provider = HQDEPlugin.getDefault().getLabelProvider();
		return provider.resolveObjectImage(obj);
	}

	private void handleNew() {
		final IProject project = getPage().getHQDEEditor().getCommonProject();
		BusyIndicator.showWhile(fStructureTree.getTree().getDisplay(), new Runnable() {
			public void run() {
//				((HQDEPluginEditor) getPage().getEditor()).ensurePluginContextPresence();
				NewStructureWizard wizard = new NewStructureWizard(project, (IPluginModelBase) getPage().getModel(), (HQDEPluginEditor) getPage().getHQDEEditor()) {
					public boolean performFinish() {
						return super.performFinish();
					}
				};
				WizardDialog dialog = new WizardDialog(HQDEPlugin.getActiveWorkbenchShell(), wizard);
				dialog.create();
				SWTUtil.setDialogSize(dialog, 500, 500);
				dialog.open();
			}
		});
	}

	
	private void handleDelete() {
		IStructuredSelection sel = (IStructuredSelection) fStructureTree.getSelection();
		if (sel.isEmpty())
			return;
		for (Iterator iter = sel.iterator(); iter.hasNext();) {
			IPluginObject object = (IPluginObject) iter.next();
			try {
				IStructuredSelection newSelection = null;
				// TODO ugly - deletion should be more general...
				if (object instanceof IPluginElement) {
					IPluginElement ee = (IPluginElement) object;
					if(ee.getParent() instanceof PluginNode) {
						PluginBaseNode parent = (PluginBaseNode)ee.getParent();
						int index = getNewSelectionIndex(parent.indexOf((IDocumentElementNode)ee), parent.getChildCount());
						newSelection = index == -1 ? new StructuredSelection(parent) : new StructuredSelection(parent.getChildNodes()[index]);
						parent.remove(ee);
					} else {
						IPluginParent parent = (IPluginParent) ee.getParent();
						int index = getNewSelectionIndex(parent.getIndexOf(ee), parent.getChildCount());
						newSelection = index == -1 ? new StructuredSelection(parent) : new StructuredSelection(parent.getChildren()[index]);
						parent.remove(ee);
					}
				} else {
					if (object instanceof IPluginParent) {
						IPluginParent ee = (IPluginParent)object;
						if(ee.getParent() instanceof PluginNode) {
							PluginBaseNode parent = (PluginBaseNode)ee.getParent();
							int index = getNewSelectionIndex(parent.indexOf((IDocumentElementNode)ee), parent.getChildCount());
							newSelection = index == -1 ? new StructuredSelection(parent) : new StructuredSelection(parent.getChildNodes()[index]);
							parent.remove(ee);							
						}
						
					}
				}
				if (newSelection != null)
					fStructureTree.setSelection(newSelection);

			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}

		}

	}

	private void handleMove(boolean up) {
		// TODO very ugly method... clean!
		IStructuredSelection sel = (IStructuredSelection) fStructureTree.getSelection();
		IPluginObject object = (IPluginObject) sel.getFirstElement();
		if (object instanceof IPluginElement) {
			IPluginElement element = (IPluginElement)object;
			if(element.getParent() instanceof PluginNode) {
				PluginNode node = (PluginNode)element.getParent();
				DocumentElementNode parent = (DocumentElementNode)element.getParent(); 
				IDocumentElementNode[]children =  parent.getChildNodes();
				int index = parent.indexOf((PluginElementNode)element);
				int newIndex = up ? index - 1 : index + 1;
				IDocumentElementNode child2 = children[newIndex];
				
				try {
					node.swap((IPluginParent)element, (IPluginParent)child2);
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
				
			} else {
				IPluginParent parent = (IPluginParent) object.getParent();
				IPluginObject[] children = parent.getChildren();
				int index = parent.getIndexOf(object);
				int newIndex = up ? index - 1 : index + 1;
				IPluginObject child2 = children[newIndex];
				try {
					parent.swap(object, child2);
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
			}
		} else if (object instanceof IPluginParent) {
			IPluginParent element = (IPluginParent)object;
			if(element.getParent() instanceof PluginNode) {
				PluginNode node = (PluginNode)element.getParent();
				DocumentElementNode parent = (DocumentElementNode)element.getParent(); 
				IDocumentElementNode[]children =  parent.getChildNodes();
				int index = parent.indexOf((PluginParentNode)element);
				int newIndex = up ? index - 1 : index + 1;
				IDocumentElementNode child2 = children[newIndex];
				
				try {
					node.swap((IPluginParent)element, (IPluginParent)child2);
				} catch (CoreException e) {
					HQDEPlugin.logException(e);
				}
				
			}
		}
		
	}

	
	
	/**
	 * 
	 *
	 */
	class StructureContentProvider extends DefaultContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parent) {
			Object[] children = null;
			
			if(parent instanceof IHQServer) {
				children = ((IHQServer)parent).getChildren();
			} else if (parent instanceof IHQClasspath) {
				children = ((IHQClasspath)parent).getChildren();
			} else if (parent instanceof IHQMetrics) {
				children = ((IHQMetrics)parent).getChildren();
			} else if (parent instanceof IHQHelp) {
				children = ((IHQHelp)parent).getChildren();
			} else if (parent instanceof IHQPlatform) {
				children = ((IHQPlatform)parent).getChildren();
			} else if (parent instanceof IPluginElement) {
				children = ((IPluginElement) parent).getChildren();
			}
			
			if (children == null)
				children = new Object[0];
			return children;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;		
		}

		public Object[] getElements(Object inputElement) {
			

			if(inputElement instanceof PluginNode) {
				PluginNode n = (PluginNode)inputElement;
				IDocumentElementNode[] children = n.getChildNodes();
				return children;
			}
			
//			if(inputElement instanceof IPluginBase) {
//				ArrayList<Object> elements = new ArrayList<Object>();
//
//				IHQMetrics[] metrics = ((IPluginBase)inputElement).getMetrics();
//				for (int i = 0; i < metrics.length; i++) {
//					elements.add(metrics[i]);
//				}
//
//				IHQServer[] servers = ((IPluginBase)inputElement).getServers();
//				for (int i = 0; i < servers.length; i++) {
//					elements.add(servers[i]);
//				}
//				return elements.toArray();
//			} 
			return null;
		}
		
	}

	/**
	 * 
	 *
	 */
	class StructureLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if(element instanceof IHQServer) {
				IHQServer s = (IHQServer)element;
				String v = s.getVersion();
				return s.getName() + (v != null ? " " + v : "") + " (server)";
			} else if(element instanceof IHQClasspath){
				return "(classpath)";
			} else if(element instanceof IHQHelp){
				IHQHelp m = (IHQHelp)element;
				return m.getName() + " (help)";
			} else if(element instanceof IHQScript){
				IHQScript m = (IHQScript)element;
				return m.getName() + " (script)";
			} else if(element instanceof IHQMetrics){
				IHQMetrics m = (IHQMetrics)element;
				String name = m.getName();
				String include = m.getInclude();
				return ((name != null && name.length() > 0) ? name : include) + " (metrics)";
			} else if(element instanceof IHQPlatform){
				IHQPlatform s = (IHQPlatform)element;
				return s.getName() + " (platform)";
			} else if(element instanceof PluginElementNode){
				PluginElementNode node = (PluginElementNode)element;
				IPluginAttribute att = node.getAttribute("name");
				String fName = node.getName();
				if(att != null) {
					return att.getValue() + " (" + fName + ")";											
				} else {
					if(fName.equals("plugin")) {
						IPluginAttribute tAtt = node.getAttribute("type");
						String fType = tAtt != null ? tAtt.getValue() : "";
						return fType + " (" + fName + ")";
					} else if (fName.equals("metrics")){
						String sName = "";
						IPluginAttribute tAttName = node.getAttribute("name");
						if(tAttName != null)
							sName = tAttName.getValue();
						else {
							IPluginAttribute tAttInclude = node.getAttribute("include");
							if(tAttInclude != null)
								sName = "#"+tAttInclude.getValue();
						}
						return sName + " (" + fName + ")";
					} else {
						return "("+((PluginElementNode)element).getName()+")";
					}
				}
			} else {
				return element.toString();
			}
		}

		public Image getImage(Object obj) {
			return resolveObjectImage(obj);
		}

	}

}
