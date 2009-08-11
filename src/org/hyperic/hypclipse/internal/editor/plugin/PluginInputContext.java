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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.hyperic.hypclipse.internal.context.XMLInputContext;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.editor.SystemFileEditorInput;
import org.hyperic.hypclipse.internal.text.AbstractEditingModel;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.PluginModel;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginModelBase;
import org.hyperic.hypclipse.plugin.IBaseModel;

/**
 * 
 *
 */
public class PluginInputContext extends XMLInputContext {
	public static final String CONTEXT_ID = "plugin-context";

	public PluginInputContext(HQDEFormEditor editor, IEditorInput input, boolean primary) {
		super(editor, input, primary);
		create();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.InputContext#createModel(org.eclipse.ui.IEditorInput)
	 */
	protected IBaseModel createModel(IEditorInput input) throws CoreException {
		PluginModelBase model = null;
		if (input instanceof IStorageEditorInput) {
			boolean isReconciling = input instanceof IFileEditorInput;
			IDocument document = getDocumentProvider().getDocument(input);
			model = new PluginModel(document, isReconciling);
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				model.setUnderlyingResource(file);
				model.setCharset(file.getCharset());
			} else if (input instanceof SystemFileEditorInput) {
				File file = (File) ((SystemFileEditorInput) input).getAdapter(File.class);
				model.setInstallLocation(file.getParent());
				model.setCharset(getDefaultCharset());
			} else {
				model.setCharset(getDefaultCharset());
			}
			model.load();
		}
		return model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.InputContext#getId()
	 */
	public String getId() {
		return CONTEXT_ID;
	}


	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.XMLInputContext#reorderInsertEdits(java.util.ArrayList)
	 */
	protected void reorderInsertEdits(ArrayList ops) {
		HashMap map = getOperationTable();
		Iterator iter = map.keySet().iterator();
		TextEdit runtimeInsert = null;
		TextEdit requiresInsert = null;
		ArrayList extensionPointInserts = new ArrayList();
		ArrayList extensionInserts = new ArrayList();

		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof IDocumentElementNode) {
				IDocumentElementNode node = (IDocumentElementNode) object;
				if (node.getParentNode() instanceof PluginBaseNode) {
					TextEdit edit = (TextEdit) map.get(node);
					if (edit instanceof InsertEdit) {
						if (node.getXMLTagName().equals("plugin")) 
							runtimeInsert = edit;
						/*if (node.getXMLTagName().equals("runtime")) { //$NON-NLS-1$
							runtimeInsert = edit;
						} else if (node.getXMLTagName().equals("requires")) { //$NON-NLS-1$
							requiresInsert = edit;
						} else if (node.getXMLTagName().equals("extension")) { //$NON-NLS-1$
							extensionInserts.add(edit);
						} else if (node.getXMLTagName().equals("extension-point")) { //$NON-NLS-1$
							extensionPointInserts.add(edit);
						} */
					}
				}
			}
		}

		for (int i = 0; i < ops.size(); i++) {
			TextEdit edit = (TextEdit) ops.get(i);
			if (edit instanceof InsertEdit) {
				if (extensionPointInserts.contains(edit)) {
					ops.remove(edit);
					ops.add(0, edit);
				}
			}
		}

		if (requiresInsert != null) {
			ops.remove(requiresInsert);
			ops.add(0, requiresInsert);
		}

		if (runtimeInsert != null) {
			ops.remove(runtimeInsert);
			ops.add(0, runtimeInsert);
		}
	}

	public void doRevert() {
		fEditOperations.clear();
		fOperationTable.clear();
		fMoveOperations.clear();
		AbstractEditingModel model = (AbstractEditingModel) getModel();
		model.reconciled(model.getDocument());
	}

	protected String getPartitionName() {
		return "___plugin_partition";
	}
}