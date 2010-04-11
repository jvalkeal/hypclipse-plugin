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

import java.io.File;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.IConstants;
import org.hyperic.hypclipse.internal.IHQDEConstants;
import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.context.InputContextManager;
import org.hyperic.hypclipse.internal.editor.build.BuildInputContext;
import org.hyperic.hypclipse.internal.editor.build.BuildSourcePage;
import org.hyperic.hypclipse.internal.editor.build.ClasspathPage;
import org.hyperic.hypclipse.internal.editor.plugin.PluginExportAction;
import org.hyperic.hypclipse.internal.editor.plugin.PluginInputContext;
import org.hyperic.hypclipse.internal.editor.plugin.PluginInputContextManager;
import org.hyperic.hypclipse.internal.editor.plugin.PluginLauncherFormPageHelper;
import org.hyperic.hypclipse.internal.editor.plugin.PluginSourcePage;
import org.hyperic.hypclipse.internal.editor.plugin.StructurePage;

/**
 * 
 *
 */
public class HQDEPluginEditor extends HQDELauncherFormEditor {

	private ILauncherFormPageHelper fLauncherHelper;

	private PluginExportAction fExportAction;

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#addEditorPages()
	 */
	protected void addEditorPages() {
		try {
			addPage(new OverviewPage(this));
			addPage(new StructurePage(this));
			addPage(new ClasspathPage(this));
			
			addSourcePage(BuildInputContext.CONTEXT_ID);
			addSourcePage(PluginInputContext.CONTEXT_ID);
		} catch (Exception e) {
			HQDEPlugin.logException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#getEditorID()
	 */
	protected String getEditorID() {
		return IHQDEConstants.HQPLUGIN_EDITOR_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#createResourceContexts(org.hyperic.hypclipse.internal.context.InputContextManager, org.eclipse.ui.IFileEditorInput)
	 */
	protected void createResourceContexts(InputContextManager manager, IFileEditorInput input) {
		IFile file = input.getFile();
		IContainer container = file.getParent();

		IFile pluginFile = null;
		IFile buildFile = null;
		
		String name = file.getName().toLowerCase(Locale.ENGLISH);
		if (name.equals("hq-plugin.xml")) {
			pluginFile = file;
			
			buildFile = pluginFile.getProject().getFile(IConstants.BUILD_FILENAME_DESCRIPTOR); 
//			buildFile = container.getFile(new Path("hqbuild.properties"));
		}

		if (pluginFile.exists()) {
			FileEditorInput in = new FileEditorInput(pluginFile);
			manager.putContext(in, new PluginInputContext(this, in, file == pluginFile));
		}

		if (buildFile.exists()) {
			FileEditorInput in = new FileEditorInput(buildFile);
			manager.putContext(in, new BuildInputContext(this, in, false));
		}

		
		manager.monitorFile(pluginFile);
		manager.monitorFile(buildFile);

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#getInputContext(java.lang.Object)
	 */
	protected InputContext getInputContext(Object object) {
		InputContext context = null;
		if (object instanceof IFile) {
			String name = ((IFile) object).getName();
			if (name.equals("hq-plugin.xml"))
				context = fInputContextManager.findContext(PluginInputContext.CONTEXT_ID);

		}
		return context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#createSystemFileContexts(org.hyperic.hypclipse.internal.context.InputContextManager, org.hyperic.hypclipse.internal.editor.SystemFileEditorInput)
	 */
	protected void createSystemFileContexts(InputContextManager manager, SystemFileEditorInput input) {
		File file = (File) input.getAdapter(File.class);
		File buildFile = null;
		File pluginFile = null;
		String name = file.getName().toLowerCase(Locale.ENGLISH);

		if (name.equals("hqbuild.properties")) {
			buildFile = file;
			File dir = file.getParentFile();
			// TODO removed for now
			//pluginFile = createPluginFile(dir);
		} else if (name.equals("hq-plugin.xml")) {
			pluginFile = file;
			File dir = file.getParentFile();
			pluginFile = new File(dir, "hq-plugin.xml");
		}
		
		if (pluginFile.exists()) {
			SystemFileEditorInput in = new SystemFileEditorInput(pluginFile);
			manager.putContext(in, new PluginInputContext(this, in, file == pluginFile)); //$NON-NLS-1$
		}
		if (buildFile.exists()) {
			SystemFileEditorInput in = new SystemFileEditorInput(buildFile);
			manager.putContext(in, new BuildInputContext(this, in, file == buildFile));
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#createStorageContexts(org.hyperic.hypclipse.internal.context.InputContextManager, org.eclipse.ui.IStorageEditorInput)
	 */
	protected void createStorageContexts(InputContextManager manager, IStorageEditorInput input) {
		// TODO fill this method
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#editorContextAdded(org.hyperic.hypclipse.internal.context.InputContext)
	 */
	public void editorContextAdded(InputContext context) {
		addSourcePage(context.getId());
		try {
			if (context.getId().equals(BuildInputContext.CONTEXT_ID))
				addPage(new ClasspathPage(this));
			else {
				//updateFirstThreePages();
			}
		} catch (PartInitException e) {
			HQDEPlugin.logException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.IInputContextListener#monitoredFileAdded(org.eclipse.core.resources.IFile)
	 */
	public void monitoredFileAdded(IFile file) {
		if (fInputContextManager == null)
			return;
		String name = file.getName();
		
		if (name.equalsIgnoreCase("hq-plugin.xml")) {
			if (!fInputContextManager.hasContext(PluginInputContext.CONTEXT_ID)) {
				IEditorInput in = new FileEditorInput(file);
				fInputContextManager.putContext(in, new PluginInputContext(this, in, false));
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.IInputContextListener#monitoredFileRemoved(org.eclipse.core.resources.IFile)
	 */
	public boolean monitoredFileRemoved(IFile file) {
		//TODO may need to check with the user if there
		//are unsaved changes in the model for the
		//file that just got removed under us.
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.IInputContextListener#contextRemoved(org.hyperic.hypclipse.internal.context.InputContext)
	 */
	public void contextRemoved(InputContext context) {
		close(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDEFormEditor#createInputContextManager()
	 */
	protected InputContextManager createInputContextManager() {
		PluginInputContextManager manager = new PluginInputContextManager(this);
//		manager.setUndoManager(new PluginUndoManager(this));
		return manager;
	}

	protected HQDESourcePage createSourcePage(HQDEFormEditor editor, String title, String name, String contextId) {
		if (contextId.equals(PluginInputContext.CONTEXT_ID))
			return new PluginSourcePage(editor, title, name);
		else if (contextId.equals(BuildInputContext.CONTEXT_ID))
			return new BuildSourcePage(editor, title, name);
		return super.createSourcePage(editor, title, name, contextId);

	}

	protected ILauncherFormPageHelper getLauncherHelper() {
		if (fLauncherHelper == null)
			fLauncherHelper = new PluginLauncherFormPageHelper(this);
		return fLauncherHelper;
	}

	public void contributeToToolbar(IToolBarManager manager) {
//		contributeLaunchersToToolbar(manager);
		manager.add(getExportAction());
	}
	
	private PluginExportAction getExportAction() {
		if (fExportAction == null) {
			fExportAction = new PluginExportAction(this);
			fExportAction.setToolTipText(HQDEMessages.PluginEditor_exportTooltip);
			fExportAction.setImageDescriptor(HQDEPluginImages.DESC_EXPORT_PLUGIN_TOOL);
		}
		return fExportAction;
	}


}
