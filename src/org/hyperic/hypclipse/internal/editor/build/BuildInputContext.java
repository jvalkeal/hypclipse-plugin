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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.editor.SystemFileEditorInput;
import org.hyperic.hypclipse.internal.text.AbstractEditingModel;
import org.hyperic.hypclipse.internal.text.IDocumentKey;
import org.hyperic.hypclipse.internal.util.PropertiesUtil;
import org.hyperic.hypclipse.plugin.IBaseModel;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;

/**
 * 
 *
 */
public class BuildInputContext extends InputContext {
	
	public static final String CONTEXT_ID = "build-context";

	private HashMap<IDocumentKey, TextEdit> fOperationTable = new HashMap<IDocumentKey, TextEdit>();

	public BuildInputContext(HQDEFormEditor editor, IEditorInput input, boolean primary) {
		super(editor, input, primary);
		create();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.context.InputContext#getDefaultCharset()
	 */
	protected String getDefaultCharset() {
		return "ISO-8859-1"; //$NON-NLS-1$
	}


	protected IBaseModel createModel(IEditorInput input) throws CoreException {
		BuildModel model = null;
		if (input instanceof IStorageEditorInput) {
			boolean isReconciling = input instanceof IFileEditorInput;
			IDocument document = getDocumentProvider().getDocument(input);
			model = new BuildModel(document, isReconciling);
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
	 * @see org.hyperic.hypclipse.internal.context.InputContext#addTextEditOperation(java.util.ArrayList, org.hyperic.hypclipse.plugin.IModelChangedEvent)
	 */
	protected void addTextEditOperation(ArrayList<TextEdit> ops, IModelChangedEvent event) {
		Object[] objects = event.getChangedObjects();
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			IDocumentKey key = (IDocumentKey) object;
			TextEdit op = (TextEdit) fOperationTable.get(key);
			if (op != null) {
				fOperationTable.remove(key);
				ops.remove(op);
			}
			switch (event.getChangeType()) {
				case IModelChangedEvent.REMOVE :
					deleteKey(key, ops);
					break;
				case IModelChangedEvent.INSERT :
					insertKey(key, ops);
					break;
				case IModelChangedEvent.CHANGE :
					modifyKey(key, ops);
				default :
					break;
			}
		}
	}

	private void insertKey(IDocumentKey key, ArrayList<TextEdit> ops) {
		IDocument doc = getDocumentProvider().getDocument(getInput());
		InsertEdit op = new InsertEdit(PropertiesUtil.getInsertOffset(doc), key.write());
		fOperationTable.put(key, op);
		ops.add(op);
	}

	private void deleteKey(IDocumentKey key, ArrayList<TextEdit> ops) {
		if (key.getOffset() >= 0) {
			TextEdit op = new DeleteEdit(key.getOffset(), key.getLength());
			fOperationTable.put(key, op);
			ops.add(op);
		}
	}

	private void modifyKey(IDocumentKey key, ArrayList<TextEdit> ops) {
		if (key.getOffset() == -1) {
			insertKey(key, ops);
		} else {
			TextEdit op = new ReplaceEdit(key.getOffset(), key.getLength(), key.write());
			fOperationTable.put(key, op);
			ops.add(op);
		}
	}

	public void doRevert() {
		fEditOperations.clear();
		fOperationTable.clear();
		AbstractEditingModel model = (AbstractEditingModel) getModel();
		model.reconciled(model.getDocument());
	}

	protected String getPartitionName() {
		return "___build_partition"; //$NON-NLS-1$
	}

}
