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
package org.hyperic.hypclipse.internal.context;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.editor.IModelUndoManager;
import org.hyperic.hypclipse.plugin.IBaseModel;
import org.hyperic.hypclipse.plugin.IModelChangeProvider;

/**
 * 
 *
 */
public abstract class InputContextManager implements IResourceChangeListener {
	
	private HQDEFormEditor editor;
	
	private Hashtable<IEditorInput, InputContext> inputContexts;
	
	private ArrayList<IFile> monitoredFiles;
	
	private ArrayList<IInputContextListener> listeners;
	
	private IModelUndoManager undoManager;

	/**
	 * Constructs input context manager. Manager adds itself to listen
	 * resource change events from plug-in activator.
	 * 
	 * @param editor Editor where this manager is attached.
	 */
	public InputContextManager(HQDEFormEditor editor) {
		this.editor = editor;
		inputContexts = new Hashtable<IEditorInput, InputContext>();
		listeners = new ArrayList<IInputContextListener>();
		HQDEPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addInputContextListener(IInputContextListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeInputContextListener(IInputContextListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 
	 *
	 */
	public void dispose() {
		HQDEPlugin.getWorkspace().removeResourceChangeListener(this);
		// dispose input contexts
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			unhookUndo(context);
			context.dispose();
		}
		inputContexts.clear();
		undoManager = null;
	}

	/**
	 * Saves dirty contexts.
	 * @param monitor
	 */
	public void save(IProgressMonitor monitor) {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.mustSave())
				context.doSave(monitor);
		}
	}

	/**
	 * 
	 * @return
	 */
	public IProject getCommonProject() {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			IEditorInput input = context.getInput();
			if (input instanceof IFileEditorInput)
				return ((IFileEditorInput) input).getFile().getProject();
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasContext(String id) {
		return findContext(id) != null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public InputContext findContext(String id) {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.getId().equals(id))
				return context;
		}
		return null;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public InputContext findContext(IResource resource) {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.matches(resource))
				return context;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public abstract IBaseModel getAggregateModel();

	/**
	 * 
	 * @param input
	 * @return
	 */
	public InputContext getContext(IEditorInput input) {
		return inputContexts.get(input);
	}

	/**
	 * 
	 * @param input
	 * @param context
	 */
	public void putContext(IEditorInput input, InputContext context) {
		inputContexts.put(input, context);
		fireContextChange(context, true);
	}

	/**
	 * Update the key (the editor input in this case) associated with the
	 * input context without firing a context change event.
	 * Used for save as operations.
	 * 
	 * @param newInput
	 * @param oldInput
	 * @throws Exception
	 */
	private void updateInputContext(IEditorInput newInput, IEditorInput oldInput) throws Exception {
		InputContext value = null;
		// Retrieve the input context referenced by the old editor input and
		// remove it from the context manager
		if (inputContexts.containsKey(oldInput)) {
			value = inputContexts.remove(oldInput);
		} else {
			throw new Exception("InputContextManager_errorMessageInputContextNotFound");
		}
		// Re-insert the input context back into the context manager using the
		// new editor input as its key
		inputContexts.put(newInput, value);
	}

	/**
	 * @param monitor
	 * @param contextID
	 * @throws Exception
	 */
	public void saveAs(IProgressMonitor monitor, String contextID) throws Exception {
		// Find the existing context
		InputContext inputContext = findContext(contextID);
		if (inputContext != null) {
			// Keep the old editor input
			IEditorInput oldInput = editor.getEditorInput();
			// Perform the save as operation
			inputContext.doSaveAs(monitor);
			// Get the new editor input
			IEditorInput newInput = inputContext.getInput();
			// Update the context manager accordingly
			updateInputContext(newInput, oldInput);
		} else {
			throw new Exception("InputContextManager_errorMessageInputContextNotFound");
		}
	}

	/**
	 * 
	 * @return
	 */
	public InputContext getPrimaryContext() {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.isPrimary())
				return context;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public InputContext[] getInvalidContexts() {
		ArrayList<InputContext> result = new ArrayList<InputContext>();
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.isModelCorrect() == false)
				result.add(context);
		}
		return (InputContext[]) result.toArray(new InputContext[result.size()]);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDirty() {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			if (context.mustSave())
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param file
	 */
	public void monitorFile(IFile file) {
		if (monitoredFiles == null)
			monitoredFiles = new ArrayList<IFile>();
		monitoredFiles.add(file);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) {
					int kind = delta.getKind();
					IResource resource = delta.getResource();
					if (resource instanceof IFile) {
						if (kind == IResourceDelta.ADDED)
							asyncStructureChanged((IFile) resource, true);
						else if (kind == IResourceDelta.REMOVED)
							asyncStructureChanged((IFile) resource, false);
						return false;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	/**
	 * 
	 * @param file
	 * @param added
	 */
	private void asyncStructureChanged(final IFile file, final boolean added) {
		if (editor == null || editor.getEditorSite() == null)
			return;
		Shell shell = editor.getEditorSite().getShell();
		Display display = shell != null ? shell.getDisplay() : Display.getDefault();

		display.asyncExec(new Runnable() {
			public void run() {
				structureChanged(file, added);
			}
		});
	}

	/**
	 * 
	 * @param file
	 * @param added
	 */
	private void structureChanged(IFile file, boolean added) {
		if (monitoredFiles == null)
			return;
		for (int i = 0; i < monitoredFiles.size(); i++) {
			IFile ifile = (IFile) monitoredFiles.get(i);
			if (ifile.equals(file)) {
				if (added) {
					fireStructureChange(file, true);
				} else {
					fireStructureChange(file, false);
					removeContext(file);
				}
			}
		}
	}

	/**
	 * 
	 * @param file
	 */
	private void removeContext(IFile file) {
		for (Enumeration<InputContext> contexts = inputContexts.elements(); contexts.hasMoreElements();) {
			InputContext context = contexts.nextElement();
			IEditorInput input = context.getInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				if (file.equals(fileInput.getFile())) {
					inputContexts.remove(input);
					fireContextChange(context, false);
					return;
				}
			}
		}
	}

	/**
	 * 
	 * @param file
	 * @param added
	 */
	protected void fireStructureChange(IFile file, boolean added) {
		for (int i = 0; i < listeners.size(); i++) {
			IInputContextListener listener = listeners.get(i);
			if (added)
				listener.monitoredFileAdded(file);
			else
				listener.monitoredFileRemoved(file);
		}
	}

	/**
	 * 
	 * @param context
	 * @param added
	 */
	protected void fireContextChange(InputContext context, boolean added) {
		for (int i = 0; i < listeners.size(); i++) {
			IInputContextListener listener = listeners.get(i);
			if (added)
				listener.contextAdded(context);
			else
				listener.contextRemoved(context);
		}
		if (added)
			hookUndo(context);
		else
			unhookUndo(context);
	}

	/**
	 * Undo previous change if possible.
	 */
	public void undo() {
		if (undoManager != null && undoManager.isUndoable())
			undoManager.undo();
	}

	/**
	 * Redo previous change if possible.
	 */
	public void redo() {
		if (undoManager != null && undoManager.isRedoable())
			undoManager.redo();
	}

	/**
	 * 
	 * @param context
	 */
	private void hookUndo(InputContext context) {
		if (undoManager == null)
			return;
		IBaseModel model = context.getModel();
		if (model instanceof IModelChangeProvider)
			undoManager.connect((IModelChangeProvider) model);
	}

	/**
	 * 
	 * @param context
	 */
	private void unhookUndo(InputContext context) {
		if (undoManager == null)
			return;
		IBaseModel model = context.getModel();
		if (model instanceof IModelChangeProvider)
			undoManager.disconnect((IModelChangeProvider) model);
	}

	/**
	 * @return Returns the undoManager.
	 */
	public IModelUndoManager getUndoManager() {
		return undoManager;
	}

	/**
	 * @param undoManager The undoManager to set.
	 */
	public void setUndoManager(IModelUndoManager undoManager) {
		this.undoManager = undoManager;
	}
}
