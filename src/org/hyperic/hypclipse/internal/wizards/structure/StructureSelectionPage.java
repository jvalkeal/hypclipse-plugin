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
package org.hyperic.hypclipse.internal.wizards.structure;

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.hyperic.hypclipse.internal.HQDELabelProvider;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.IHelpContextIds;
import org.hyperic.hypclipse.internal.elements.DefaultContentProvider;
import org.hyperic.hypclipse.internal.elements.ElementLabelProvider;
import org.hyperic.hypclipse.internal.hqmodel.HQClasspathNode;
import org.hyperic.hypclipse.internal.hqmodel.HQHelpNode;
import org.hyperic.hypclipse.internal.hqmodel.HQMetricsNode;
import org.hyperic.hypclipse.internal.hqmodel.HQPlatformNode;
import org.hyperic.hypclipse.internal.hqmodel.HQScriptNode;
import org.hyperic.hypclipse.internal.hqmodel.HQServerNode;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.schema.AnnoPrinter;
import org.hyperic.hypclipse.internal.schema.AnnotationExp;
import org.hyperic.hypclipse.internal.schema.ElementCollector;
import org.hyperic.hypclipse.internal.schema.ElementPathCollector;
import org.hyperic.hypclipse.internal.schema.ISchemaElement;
import org.hyperic.hypclipse.internal.text.HTMLPrinter;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginElementNode;
import org.hyperic.hypclipse.internal.util.HQDEHTMLHelper;
import org.hyperic.hypclipse.internal.util.TextUtil;
import org.hyperic.hypclipse.internal.wizards.BaseWizardSelectionPage;
import org.hyperic.hypclipse.internal.wizards.WizardCollectionElement;
import org.hyperic.hypclipse.internal.wizards.WizardElement;
import org.hyperic.hypclipse.internal.wizards.WizardNode;
import org.hyperic.hypclipse.plugin.IBasePluginWizard;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;
import org.hyperic.hypclipse.plugin.IStructureWizard;
import org.hyperic.hypclipse.plugin.ITemplateSection;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;

/**
 * Structure selection page contains methods to add new content
 * to content model and its underlying plugin descriptor. 
 *
 */
public class StructureSelectionPage extends BaseWizardSelectionPage {

	/** List of structure types */
	private TableViewer fStructureListViewer;
	
	/** List of available templates */
	private TableViewer fTemplateViewer;

	/** Handle to model */
	private IPluginModelBase fModel;

	private WizardCollectionElement fTemplateCollection;
	private WizardCollectionElement fWizardCollection;
	private Label fTemplateLabel;
//	private StructureTreeSelectionPage fWizardsPage;

	private NewStructureWizard fWizard;
	private IProject fProject;
	private Text fStructureDescription;
	private Link fDescLink;

	private Browser fStructureDescBrowser;

	private StructureItem fCurrentStructureItem;
	
	private Image fGenericElementImage;
	private Image fServerImage;
	private Image fMetricImage;
	private Image fMetricsImage;
	private Image fServiceImage;
	private Image fHelpImage;
	private Image fPluginImage;
	private Image fConfigImage;
	private Image fOptionImage;
	private Image fScanImage;
	private Image fIncludeImage;
	private Image fPropertyImage;
	private Image fFilterImage;
	private Image fPropertiesImage;
	private Image fActionsImage;
	private Image fClasspathImage;
	private Image fPlatformImage;
	private Image fScriptImage;
	
	private boolean templateSelected;

	
	/**
	 * 
	 * @param project
	 * @param model
	 * @param element
	 * @param templates
	 * @param wizard
	 */
	public StructureSelectionPage(IProject project, IPluginModelBase model, WizardCollectionElement element, WizardCollectionElement templates, NewStructureWizard wizard) {
		super("pointSelectionPage", HQDEMessages.NewStructureWizard_StructureSelectionPage_title); 
		this.fModel = model;
		this.fWizardCollection = element;
		this.fTemplateCollection = templates;
		this.fWizard = wizard;
		this.fProject = project;
		this.templateSelected = true;
//		fWildCardFilter = new WildcardFilter();
//		fAvailableImports = PluginSelectionDialog.getExistingImports(model, true);
		setTitle(HQDEMessages.NewStructureWizard_StructureSelectionPage_title);
		setDescription(HQDEMessages.NewStructureWizard_StructureSelectionPage_desc);
		initializeImages();
	}

	public void initializeImages() {
		HQDELabelProvider provider = HQDEPlugin.getDefault().getLabelProvider();
//		fExtensionImage = provider.get(PDEPluginImages.DESC_EXTENSION_OBJ);
		fGenericElementImage = provider.get(HQDEPluginImages.DESC_GENERIC_XML_OBJ);
		fMetricImage = provider.get(HQDEPluginImages.DESC_TREE_METRIC);
		fMetricsImage = provider.get(HQDEPluginImages.DESC_TREE_METRICS);
		fServerImage = provider.get(HQDEPluginImages.DESC_TREE_SERVER);
		fServiceImage = provider.get(HQDEPluginImages.DESC_TREE_SERVICE);
		fHelpImage = provider.get(HQDEPluginImages.DESC_TREE_HELP);
		fPluginImage = provider.get(HQDEPluginImages.DESC_TREE_PLUGIN);
		fConfigImage = provider.get(HQDEPluginImages.DESC_TREE_CONFIG);
		fOptionImage = provider.get(HQDEPluginImages.DESC_TREE_OPTION);
		fScanImage = provider.get(HQDEPluginImages.DESC_TREE_SCAN);
		fIncludeImage = provider.get(HQDEPluginImages.DESC_TREE_INCLUDE);
		fPropertyImage = provider.get(HQDEPluginImages.DESC_TREE_PROPERTY);
		fFilterImage = provider.get(HQDEPluginImages.DESC_TREE_FILTER);
		fPropertiesImage = provider.get(HQDEPluginImages.DESC_TREE_PROPERTIES);
		fActionsImage = provider.get(HQDEPluginImages.DESC_TREE_ACTIONS);
		fClasspathImage = provider.get(HQDEPluginImages.DESC_TREE_CLASSPATH);
		fPlatformImage = provider.get(HQDEPluginImages.DESC_TREE_PLATFORM);
		fScriptImage = provider.get(HQDEPluginImages.DESC_TREE_SCRIPT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// tab folder
		final TabFolder tabFolder = new TabFolder(parent, SWT.FLAT);
		
		TabItem firstTab = new TabItem(tabFolder, SWT.NULL);
		firstTab.setText(HQDEMessages.StructureSelectionPage_tab1);
		
		// disable second tab for now!
		// we may not need it
//		TabItem secondTab = new TabItem(tabFolder, SWT.NULL);
//		secondTab.setText(HQDEMessages.StructureSelectionPage_tab2);
//		secondTab.setControl(createWizardsPage(tabFolder));
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTabSelection(tabFolder.getSelectionIndex());
			}
		});
		
		// top level group
		Composite outerContainer = new Composite(tabFolder, SWT.NONE);
		firstTab.setControl(outerContainer);
		GridLayout layout = new GridLayout();
		outerContainer.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		outerContainer.setLayoutData(gd);

		Composite pointContainer = new Composite(outerContainer, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		pointContainer.setLayout(layout);
		gd = new GridData(GridData.FILL_BOTH);
		pointContainer.setLayoutData(gd);


		fStructureListViewer = new TableViewer(pointContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fStructureListViewer.setContentProvider(new StructureContentProvider());
		fStructureListViewer.setLabelProvider(new StructureLabelProvider());
		fStructureListViewer.addSelectionChangedListener(this);
		fStructureListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (canFinish()) {
					fWizard.performFinish();
					fWizard.getShell().close();
					fWizard.dispose();
					fWizard.setContainer(null);
				}
			}
		});
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 150;
		fStructureListViewer.getTable().setLayoutData(gd);

		Composite templateComposite = new Composite(outerContainer, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 0;
		templateComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		templateComposite.setLayoutData(gd);


		Control c = null;
		Composite comp = new Composite(templateComposite, SWT.BORDER);
		layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		try {
			c = fStructureDescBrowser = new Browser(comp, SWT.NONE);
		} catch (SWTError e) {
		}
		if (c == null)
			c = fStructureDescription = new Text(comp, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);

		setStructureDescriptionText(HQDEMessages.NewStructureWizard_StructureSelectionPage_extStructureDescription);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 80;
		c.setLayoutData(gd);

		fTemplateLabel = new Label(templateComposite, SWT.NONE | SWT.WRAP);
		fTemplateLabel.setText(HQDEMessages.NewStructureWizard_StructureSelectionPage_contributedTemplates_title);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fTemplateLabel.setLayoutData(gd);

		SashForm templateSashForm = new SashForm(templateComposite, SWT.HORIZONTAL);
		templateSashForm.setLayout(new GridLayout());
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 80;
		templateSashForm.setLayoutData(gd);

		Composite wizardComposite = new Composite(templateSashForm, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		wizardComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		wizardComposite.setLayoutData(gd);
		fTemplateViewer = new TableViewer(wizardComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fTemplateViewer.setContentProvider(new TemplateContentProvider());
		fTemplateViewer.setLabelProvider(ElementLabelProvider.INSTANCE);
//		fTemplateViewer.setComparator(ListUtil.NAME_COMPARATOR);
		fTemplateViewer.addSelectionChangedListener(this);
		gd = new GridData(GridData.FILL_BOTH);

		fTemplateViewer.getTable().setLayoutData(gd);
		TableItem[] selection = fStructureListViewer.getTable().getSelection();
		if (selection != null && selection.length > 0)
			fTemplateViewer.setInput(selection[0]);
		fTemplateViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (canFlipToNextPage()) {
					advanceToNextPage();
				}
			}
		});

		Composite descriptionComposite = new Composite(templateSashForm, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		descriptionComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		descriptionComposite.setLayoutData(gd);
		createDescriptionIn(descriptionComposite);

		initialize();
		setControl(tabFolder);
		Dialog.applyDialogFont(outerContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(outerContainer, IHelpContextIds.STRUCTURE_SELECTION_PAGE);
	}

	/**
	 * Creates a page where wizard(template) can be selected. In this
	 * page new structure is created using templates.
	 * 
	 * @param parent
	 * @return
	 */
//	private Control createWizardsPage(Composite parent) {
//		fWizardsPage = new StructureTreeSelectionPage(fWizardCollection, null, "HQDEMessages.PointSelectionPage_categories");
//		fWizardsPage.createControl(parent);
//		fWizardsPage.setWizard(fWizard);
//		fWizardsPage.getSelectionProvider().addSelectionChangedListener(this);
//		fWizardsPage.init(fProject, fModel.getPluginBase());
//		return fWizardsPage.getControl();
//	}

	public void advanceToNextPage() {
		getContainer().showPage(getNextPage());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardSelectionPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage() {
		return getNextPage() != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean canFinish() {
		if (fTemplateViewer != null) {
			ISelection selection = fTemplateViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				if (!ssel.isEmpty() && templateSelected)
					return false;
			}
		}
		if (fStructureListViewer != null) {
			ISelection selection = fStructureListViewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				if (ssel.isEmpty() == false)
					return true;
			}
		}
		return false;
	}

	public void dispose() {
//		fWizardsPage.dispose();
		super.dispose();
	}

	public boolean finish() {
		
		// if there are no wizard selected, just create
		// new structure based on selected structure row.

		try {
			IPluginModelFactory factory = fModel.getPluginFactory();
			String name = fCurrentStructureItem.exp.getNameClass().toString();
			
			// check if we are creating root element or new sub
			// element under existing element
			if(fCurrentStructureItem.type == StructureItem.TYPE_OTHER) {
				// What is selected. This is the parent, unless we are creating root element.
				ISelection selection = fWizard.getEditor().getSelection();
				IStructuredSelection sSelection = (IStructuredSelection)selection;
				IPluginParent parent = (IPluginParent)sSelection.getFirstElement();
				
				
				IPluginElement element = factory.createElement(parent);
				((PluginElementNode)element).setElementExp(fCurrentStructureItem.exp);
				((PluginElementNode) element).setParentNode((IDocumentElementNode) parent);
				element.setName(name);
				parent.add(element);				
			} else {
				// get base node
				IPluginBase base = fModel.getPluginBase();
				if(name.equals("metrics")) {
					IHQMetrics node = factory.createMetrics();
					((HQMetricsNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else if(name.equals("server")) {
					IHQServer node = factory.createServer();
					((HQServerNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else if(name.equals("classpath")) {
					IHQClasspath node = factory.createClasspath();
					((HQClasspathNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else if(name.equals("help")) {
					IHQHelp node = factory.createHelp();
					((HQHelpNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else if(name.equals("script")) {
					IHQScript node = factory.createScript();
					((HQScriptNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else if(name.equals("platform")) {
					IHQPlatform node = factory.createPlatform();
					((HQPlatformNode)node).setElementExp(fCurrentStructureItem.exp);
					base.add(node);
				} else {
					IPluginElement element = factory.createElement(base);
					((PluginElementNode)element).setElementExp(fCurrentStructureItem.exp);
					((PluginElementNode) element).setParentNode((IDocumentElementNode) base);
					element.setName(name);
					base.add(element);
					
				}
			}
			
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
		
		return true;
	}


	/**
	 * 
	 */
	protected void initialize() {
		ISelection selection = fWizard.getEditor().getSelection();
		IStructuredSelection sSelection = (IStructuredSelection)selection;
		Object obj = sSelection.getFirstElement();
		// prevent giving null to table, otherwise 
		// provider method wont get called.
		fStructureListViewer.setInput(obj != null ? obj : new Object());			
		fStructureListViewer.getTable().setFocus();		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel != null && !ssel.isEmpty()) {
				Object element = ssel.getFirstElement();
				if (element instanceof WizardElement)
					handleTemplateSelection((WizardElement) element);
				else if (element instanceof StructureItem)
					handleStructureSelection((StructureItem) element);
			} else {

				// checking if null event is coming from template selection
				// list. If this is the case, don't try to set texts, 
				// it will just go wrong since selection is null.
				boolean set = true;
				if(event.getSelectionProvider() instanceof TableViewer) {
					TableViewer viewer = (TableViewer) event.getSelectionProvider();
					if(viewer.getContentProvider() instanceof TemplateContentProvider) {
						set = false;
					}
				}
				
				if(set) {
					setDescription("");
					setDescriptionText("");
					fTemplateLabel.setText(HQDEMessages.NewStructureWizard_StructureSelectionPage_contributedTemplates_title);
					setStructureDescriptionText(HQDEMessages.StructureSelectionPage_noDescAvailable);					
				}
				setSelectedNode(null);
				setPageComplete(false);
			}
			getContainer().updateButtons();
		}
	}

	/**
	 * Handles selection if template is selected.
	 * @param element
	 */
	private void handleTemplateSelection(WizardElement element) {
		templateSelected = true;
		setSelectedNode(createWizardNode(element));
		setDescriptionText(element.getDescription());
		setDescription(NLS.bind(HQDEMessages.NewStructureWizard_StructureSelectionPage_templateDescription, element.getLabel()));
		setPageComplete(false);
	}
	
	/**
	 * Handles selection if structure is selected.
	 * @param element
	 */
	private void handleStructureSelection(StructureItem element) {
		fCurrentStructureItem = element;
		templateSelected = false;
		
		String name = "";
		
		if(element.exp.contentModel instanceof AnnotationExp) {
			setStructureDescriptionText(((AnnotationExp)element.exp.contentModel).annotation);
		} else {
			StringBuffer desc = new StringBuffer();
			String desc1 = AnnoPrinter.printMaster(element.exp.contentModel);
			String currname = element.exp.getNameClass().toString();
			desc.append("<b>Info for " + currname + " tag:" + "</b></br>");
			desc.append(desc1);
			name = currname;
			ISelection selection = fWizard.getEditor().getSelection();
			IStructuredSelection sSelection = (IStructuredSelection)selection;
			Object obj = sSelection.getFirstElement();
			if(obj != null) {
				ElementExp e = ((ISchemaElement)obj).getElementExp();

		        String desc2 = AnnoPrinter.printSub(
		                e.contentModel,currname).trim();
		        if(desc2 != null && desc2.length() > 0) {
			        desc.append("</br></br>");
					desc.append("<b>Info for parent location:</b></br>");
			        desc.append(desc2);		        	
		        }
			}
	
			setStructureDescriptionText(desc.toString());
		}

		fTemplateViewer.setInput(element);
		
		//clear template description
		setDescriptionText("");
		setDescription(HQDEMessages.NewStructureWizard_StructureSelectionPage_desc);
		fTemplateLabel.setText(NLS.bind(HQDEMessages.NewStructureWizard_StructureSelectionPage_contributedTemplates_label, name));
		setSelectedNode(null);
		setPageComplete(true);

	}


	private void updateTabSelection(int index) {
		if (index == 0) {
			// extension point page
			ISelection selection = fTemplateViewer.getSelection();
			if (selection.isEmpty() == false)
				selectionChanged(new SelectionChangedEvent(fTemplateViewer, selection));
			else
				selectionChanged(new SelectionChangedEvent(fStructureListViewer, fStructureListViewer.getSelection()));
//			fFilterText.setFocus();
		} else {
			// wizard page
//			ISelectionProvider provider = fWizardsPage.getSelectionProvider();
//			selectionChanged(new SelectionChangedEvent(provider, provider.getSelection()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.BaseWizardSelectionPage#createWizardNode(org.hyperic.hypclipse.internal.wizards.WizardElement)
	 */
	protected IWizardNode createWizardNode(WizardElement element) {
		return new WizardNode(this, element) {
			public IBasePluginWizard createWizard() throws CoreException {
				IStructureWizard wizard = createWizard(wizardElement);
				if (wizard == null)
					throw new CoreException(new Status(IStatus.ERROR, wizardElement.getConfigurationElement().getNamespaceIdentifier(), HQDEMessages.StructureSelectionPage_cannotFindTemplate));
				wizard.init(fProject, fModel, fCurrentStructureItem.parent);
				return wizard;
			}

			protected IStructureWizard createWizard(WizardElement element) throws CoreException {
				if (element.isTemplate()) {
					IConfigurationElement template = element.getTemplateElement();
					if (template == null)
						return null;
					ITemplateSection section = (ITemplateSection) template.createExecutableExtension("class");
					return new NewStructureTemplateWizard(section);
				}
				return (IStructureWizard) element.createExecutableExtension();
			}
		};
	}

	/**
	 * 
	 */
	public void checkModel() {
//		IWizardNode node = getSelectedNode();
//		if (node == null)
//			return;
//		IWizard wizard = node.getWizard();
//		if (wizard instanceof NewExtensionTemplateWizard) {
//			if (((NewExtensionTemplateWizard) wizard).updatedDependencies()) {
//				if (MessageDialog.openQuestion(getShell(), PDEUIMessages.PointSelectionPage_newDepFound, PDEUIMessages.PointSelectionPage_newDepMessage)) {
//					fWizard.getEditor().doSave(new NullProgressMonitor());
//				}
//			}
//		}
	}

	public void setVisible(boolean visible) {
//		if (visible)
//			fFilterText.setFocus();
		super.setVisible(visible);
	}

	private void setStructureDescriptionText(String text) {
		if (fStructureDescBrowser != null) {
			StringBuffer desc = new StringBuffer();
			HTMLPrinter.insertPageProlog(desc, 0, new RGB(255, 255, 255), TextUtil.getJavaDocStyleSheerURL());
			desc.append(text);
			HTMLPrinter.addPageEpilog(desc);
			fStructureDescBrowser.setText(desc.toString());
		} else {
			fStructureDescription.setText(HQDEHTMLHelper.stripTags(text));
		}
	}

	class StructureItem {
		public final static int TYPE_GLOBAL = 1;
		public final static int TYPE_OTHER = 2;
		ElementExp exp;
		int type;
		IPluginObject parent;
	}
	
	/**
	 * This class implements provider to determine what is about
	 * to shown on structure list from input object.
	 */
	class StructureContentProvider extends DefaultContentProvider implements IStructuredContentProvider {

		ExpressionPool pool = new ExpressionPool();

		public Object[] getElements(Object inputElement) {

			ArrayList<StructureItem> elements = new ArrayList<StructureItem>();

			// input object should be HQServerNode, HQServiceNode,
			// HQPlatformNode, PluginElementNode

			// first we want to add global elements, which can be 
			// always added to structure. These are instances of
			// server, service and platform.
//			Grammar grammar = null;
//			try {
//				HQGrammarLoader loader = new HQGrammarLoader();
//				loader.setController(new DebugController(false,false));
//				URL url = HQDEPlugin.getDefault().find(new Path("schema/hq-plugin.rng"));
//				URLConnection uc = url.openConnection();
//				InputSource is = new InputSource(uc.getInputStream());
//				grammar = loader.loadSchema(is);
//			} catch (IOException e1) {
//			} catch (SAXException e1) {
//			} catch (ParserConfigurationException e1) {
//			}
			Grammar grammar = HQDEPlugin.getDefault().getGrammar();

			ArrayList<ElementExp> gElements = new ArrayList<ElementExp>();
			ElementPathCollector epColl = new ElementPathCollector();
			if(grammar != null)
				epColl.collect(grammar.getTopLevel(), gElements, "/plugin");
			
			ListIterator<ElementExp> iter = gElements.listIterator();
//			ArrayList<String> filter = new ArrayList<String>();
//			filter.add("platform");
//			filter.add("server");
//			filter.add("service");
			while(iter.hasNext()) {
				ElementExp exp = iter.next();
				String name = exp.getNameClass().toString();
//				if(filter.contains(name)) {
					StructureItem item = new StructureItem();
					item.exp = exp;
					item.type = StructureItem.TYPE_GLOBAL;
					item.parent = null;
					elements.add(item);
//				}
			}
			
			ArrayList<ElementExp> sElements = new ArrayList<ElementExp>();			
			if(inputElement instanceof ISchemaElement) {
				ElementCollector coll = new ElementCollector();
				ElementExp e = ((ISchemaElement)inputElement).getElementExp();
				Expression eExp = e.contentModel.getExpandedExp(pool);
				coll.collect(eExp, sElements);

				
				
			} else if (inputElement instanceof PluginElementNode) {
				
			}

			iter = sElements.listIterator();
			while(iter.hasNext()) {
				StructureItem item = new StructureItem();
				item.exp = iter.next();
				item.type = StructureItem.TYPE_OTHER;
				item.parent = (IPluginObject)inputElement;
				elements.add(item);				
			}
			
			
			return elements.toArray(new StructureItem[0]);
		}
		
	}
	

	/**
	 * This class implements a provider to format text and
	 * image in structure list.
	 */
	class StructureLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if(element instanceof StructureItem) {
				ElementExp exp = ((StructureItem)element).exp;
				String name = exp.getNameClass().toString();
				if(name.equals("metric"))
					return fMetricImage;
				else if(name.equals("service"))
					return fServiceImage;
				else if(name.equals("help"))
					return fHelpImage;
				else if(name.equals("plugin"))
					return fPluginImage;
				else if(name.equals("config"))
					return fConfigImage;
				else if(name.equals("option"))
					return fOptionImage;
				else if(name.equals("scan"))
					return fScanImage;
				else if(name.equals("include"))
					return fIncludeImage;
				else if(name.equals("property"))
					return fPropertyImage;
				else if(name.equals("filter"))
					return fFilterImage;
				else if(name.equals("properties"))
					return fPropertiesImage;
				else if(name.equals("actions"))
					return fActionsImage;
				else if(name.equals("metrics"))
					return fMetricsImage;
				else if(name.equals("script"))
					return fScriptImage;
				else if(name.equals("platform"))
					return fPlatformImage;
				else if(name.equals("classpath"))
					return fClasspathImage;
				else if(name.equals("server"))
					return fServerImage;
				else
					return fGenericElementImage;

			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			StructureItem item = (StructureItem)element;
			String name = item.exp.getNameClass().toString();
			if(item.type == StructureItem.TYPE_GLOBAL)
				return name + " (global)";
			else return name;
		}
		
	}

	/**
	 * This class implements provider to determine what is about
	 * to shown on template list from input object.
	 */
	class TemplateContentProvider extends DefaultContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			StructureItem item = (StructureItem)inputElement;
			String name = item.exp.getNameClass().toString();
			ArrayList result = new ArrayList();
			if (fTemplateCollection.getWizards() != null) {
				Object[] wizards = fTemplateCollection.getWizards().getChildren();
				for (int i = 0; i < wizards.length; i++) {
					String wizardContributorId = ((WizardElement) wizards[i]).getContributingId();
					if (wizardContributorId == null)
						continue;
					if (wizards[i] instanceof WizardElement && wizardContributorId.equals(name))
						result.add(wizards[i]);
				}
				return result.toArray();
			}

			return new Object[0];
		}
		
	}

	
}
