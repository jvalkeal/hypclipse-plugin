package org.hyperic.hypclipse.internal.editor.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.editor.FormLayoutFactory;
import org.hyperic.hypclipse.internal.editor.HQDEFormPage;
import org.hyperic.hypclipse.internal.editor.TableSection;
import org.hyperic.hypclipse.internal.editor.build.BuildInputContext;
import org.hyperic.hypclipse.internal.elements.DefaultTableProvider;
import org.hyperic.hypclipse.internal.parts.EditableTablePart;
import org.hyperic.hypclipse.internal.parts.TablePart;
import org.hyperic.hypclipse.internal.preferences.HQEnvInfo;
import org.hyperic.hypclipse.internal.preferences.PreferencesManager;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildPropertiesConstants;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;

/**
 * The Class HQPluginEnvironmentSection.
 */
public class HQPluginEnvironmentSection extends TableSection implements IModelChangedListener {

	private TableViewer fHQEnvTable;
	private Action fAddAction;


	/**
	 * Instantiates a new hQ plugin environment section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public HQPluginEnvironmentSection(HQDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[] {HQDEMessages.HQPluginEnvironmentSection_modify});
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDESection#createClient(org.eclipse.ui.forms.widgets.Section, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText(HQDEMessages.HQPluginEnvironmentSection_title);
		section.setDescription(HQDEMessages.HQPluginEnvironmentSection_pluginDesc);

		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));

		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		Composite container = createClientContainer(section, 2, toolkit);
		EditableTablePart tablePart = getTablePart();
		tablePart.setEditable(isEditable());
		tablePart.setMinimumSize(100, 20);

		createViewerPartControl(container, SWT.FULL_SELECTION | SWT.MULTI, 2, toolkit);
		fHQEnvTable = tablePart.getTableViewer();
		fHQEnvTable.setContentProvider(new ContentProvider());
		fHQEnvTable.setLabelProvider(new EELabelProvider());

		Hyperlink link = toolkit.createHyperlink(container, HQDEMessages.BuildExecutionEnvironmentSection_configure, SWT.NONE);
		link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		link.addHyperlinkListener(new IHyperlinkListener() {
			public void linkEntered(HyperlinkEvent e) {
			}

			public void linkExited(HyperlinkEvent e) {
			}

			public void linkActivated(HyperlinkEvent e) {
				PreferencesUtil.createPreferenceDialogOn(HQDEPlugin.getActiveWorkbenchShell(), "org.eclipse.jdt.debug.ui.jreProfiles",
						new String[] {"org.eclipse.jdt.debug.ui.jreProfiles"}, null).open(); 
			}
		});
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		link.setLayoutData(gd);

		final IProject project = getPage().getHQDEEditor().getCommonProject();
		try {
			if (project != null && project.hasNature(JavaCore.NATURE_ID)) {
				link = toolkit.createHyperlink(container, HQDEMessages.ExecutionEnvironmentSection_updateClasspath, SWT.NONE);
				link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
				link.addHyperlinkListener(new IHyperlinkListener() {
					public void linkEntered(HyperlinkEvent e) {
					}

					public void linkExited(HyperlinkEvent e) {
					}

					public void linkActivated(HyperlinkEvent e) {
//						try {
//							getPage().getEditor().doSave(null);
//							IPluginModelBase model = getPage().getHQDEEditor().get//PluginRegistry.findModel(project);
//							if (model != null) {
//								ClasspathComputer.setClasspath(project, model);
//								if (HQDEPlugin.getWorkspace().isAutoBuilding()) {
//									doFullBuild(project);
//								}
//							}
//						} catch (CoreException e1) {
//						}
					}
				});
				gd = new GridData();
				gd.horizontalSpan = 2;
				link.setLayoutData(gd);
			}
		} catch (CoreException e1) {
		}

		makeActions();

		
		IBuildModel model = getBuildModel();
		if (model != null) {
			fHQEnvTable.setInput(model);
			model.addModelChangedListener(this);
		}
		
		
		
		toolkit.paintBordersFor(container);
		section.setClient(container);
	}

	public void dispose() {
//		IBundleModel model = getBundleModel();
//		if (model != null)
//			model.removeModelChangedListener(this);
	}

	public void refresh() {
		fHQEnvTable.refresh();
		updateButtons();
	}

	protected void buttonSelected(int index) {
		switch (index) {
			case 0 :
				handleAdd();
				break;
		}
	}

	private void makeActions() {
		fAddAction = new Action("HQDEMessages.RequiredExecutionEnvironmentSection_add") {
			public void run() {
				handleAdd();
			}
		};
		fAddAction.setEnabled(isEditable());

	}

	private void updateButtons() {
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(0, isEditable());
	}




	private void handleAdd() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(HQDEPlugin.getActiveWorkbenchShell(), HQDEPlugin.getDefault().getLabelProvider());
		dialog.setElements(getEnvironments());
		dialog.setAllowDuplicates(false);
		dialog.setMultipleSelection(false);
		dialog.setTitle(HQDEMessages.HQPluginEnvironmentSection_dialog_title);
		dialog.setMessage(HQDEMessages.HQPluginEnvironmentSection_dialogMessage);
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), "IHelpContextIds.EXECUTION_ENVIRONMENT_SELECTION");
		if (dialog.open() == Window.OK) {
			modifyHQPluginEnvironment(dialog.getResult());
		}
	}


	private void modifyHQPluginEnvironment(Object[] result) {

		try {
			IBuildModel model = getBuildModel();
			IBuild build = model.getBuild();
			IBuildEntry entry = model.getBuild().getEntry(IBuildPropertiesConstants.PROPERTY_HQ_ENV_NAME);
			build.remove(entry);
			entry = model.getFactory().createEntry(IBuildPropertiesConstants.PROPERTY_HQ_ENV_NAME);
			entry.addToken(((HQEnvInfo)result[0]).getName());
			build.add(entry);
		} catch (CoreException e) {
		}
		
	}

	private Object[] getEnvironments() {
		
		PreferencesManager manager = PreferencesManager.getInstance();
		HQEnvInfo envs[] = manager.getEnvironments();
		return envs;
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
		}
		
		fHQEnvTable.refresh();
//		else if (e.getChangeType() == IModelChangedEvent.REMOVE) {
//			Object[] objects = e.getChangedObjects();
//			for (int i = 0; i < objects.length; i++) {
//				Table table = fHQEnvTable.getTable();
////				if (objects[i] instanceof ExecutionEnvironment) {
////					int index = table.getSelectionIndex();
////					fHQEnvTable.remove(objects[i]);
////					if (canSelect()) {
////						table.setSelection(index < table.getItemCount() ? index : table.getItemCount() - 1);
////					}
////				}
//			}
//			updateButtons();
//		} else if (e.getChangeType() == IModelChangedEvent.INSERT) {
//			Object[] objects = e.getChangedObjects();
//			if (objects.length > 0) {
//				fHQEnvTable.refresh();
//				fHQEnvTable.setSelection(new StructuredSelection(objects[objects.length - 1]));
//			}
//			updateButtons();
//		} else if (Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT.equals(e.getChangedProperty())) {
//			refresh();
//			// Bug 171896
//			// Since the model sends a CHANGE event instead of
//			// an INSERT event on the very first addition to the empty table
//			// Selection should fire here to take this first insertion into account
//			Object lastElement = fHQEnvTable.getElementAt(fHQEnvTable.getTable().getItemCount() - 1);
//			if (lastElement != null) {
//				fHQEnvTable.setSelection(new StructuredSelection(lastElement));
//			}
//		}
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


	public boolean doGlobalAction(String actionId) {
		if (!isEditable()) {
			return false;
		}


		return false;
	}


	protected void selectionChanged(IStructuredSelection selection) {
		getPage().getHQDEEditor().setSelection(selection);
		if (getPage().getModel().isEditable())
			updateButtons();
	}


	private void doFullBuild(final IProject project) {
		Job buildJob = new Job("HQDEMessages.CompilersConfigurationBlock_building") {
			public boolean belongsTo(Object family) {
				return ResourcesPlugin.FAMILY_MANUAL_BUILD == family;
			}

			protected IStatus run(IProgressMonitor monitor) {
				try {
					project.build(IncrementalProjectBuilder.FULL_BUILD, JavaCore.BUILDER_ID, null, monitor);
				} catch (CoreException e) {
				}
				return Status.OK_STATUS;
			}
		};
		buildJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
		buildJob.schedule();
	}

	/**
	 * The Class EELabelProvider.
	 */
	class EELabelProvider extends LabelProvider {

		private Image fImage;

		public EELabelProvider() {
			fImage = HQDEPluginImages.DESC_JAVA_LIB_OBJ.createImage();
		}

		public Image getImage(Object element) {
			return fImage;
		}

		public String getText(Object element) {
			if(element instanceof String) {
				return (String)element;
			}
			return "Unknown";
		}

		public void dispose() {
			if (fImage != null)
				fImage.dispose();
			super.dispose();
		}
	}

	/**
	 * The Class ContentProvider.
	 */
	class ContentProvider extends DefaultTableProvider {
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof IBuildModel) {
				IBuildModel model = (IBuildModel)inputElement;
				IBuildEntry entry = model.getBuild().getEntry(IBuildPropertiesConstants.PROPERTY_HQ_ENV_NAME);
				if(entry != null)
					return entry.getTokens();
			}
			return new String[0];
		}
	}


}
