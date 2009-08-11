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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Display;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.editor.HQDEProjectionSourcePage;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.IDocumentRange;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginObject;

public class PluginSourcePage extends HQDEProjectionSourcePage {

	public PluginSourcePage(HQDEFormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	public boolean canLeaveThePage() {
		boolean cleanModel = getInputContext().isModelCorrect();
		if (!cleanModel) {
			Display.getCurrent().beep();
			String title = getEditor().getSite().getRegisteredName();
			MessageDialog.openError(HQDEPlugin.getActiveWorkbenchShell(), title, HQDEMessages.SourcePage_errorMessage);
		}
		return cleanModel;
	}

	public IDocumentRange findRange() {

		Object selectedObject = getSelection();

		if (selectedObject instanceof IDocumentElementNode)
			return (IDocumentElementNode) selectedObject;

		return null;
	}

	public IDocumentRange getRangeElement(int offset, boolean searchChildren) {
		IPluginBase base = ((IPluginModelBase) getInputContext().getModel()).getPluginBase();
		IDocumentRange node = findNode(new IPluginObject[] {base}, offset, searchChildren);
		return node;
	}


	public boolean isQuickOutlineEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public ViewerComparator createOutlineComparator() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITreeContentProvider createOutlineContentProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public ILabelProvider createOutlineLabelProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDESourcePage#isSelectionListener()
	 */
	protected boolean isSelectionListener() {
		return true;
	}

	public void updateSelection(Object object) {
		if ((object instanceof IDocumentElementNode) && !((IDocumentElementNode) object).isErrorNode()) {
			setSelectedObject(object);
			setHighlightRange((IDocumentElementNode) object, true);
			setSelectedRange((IDocumentElementNode) object, false);
		}		
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		// Update the text selection if this page is being activated
		if (active) {
			updateTextSelection();
		}
	}


}
