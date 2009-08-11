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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.internal.editor.KeyValueSourcePage;
import org.hyperic.hypclipse.internal.elements.DefaultContentProvider;
import org.hyperic.hypclipse.internal.text.IDocumentKey;
import org.hyperic.hypclipse.internal.text.IDocumentRange;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;

public class BuildSourcePage extends KeyValueSourcePage {
	class BuildOutlineContentProvider extends DefaultContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parent) {
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			return false;
		}

		public Object getParent(Object child) {
			if (child instanceof IBuildEntry)
				return ((IBuildEntry) child).getModel();
			return null;
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof IBuildModel) {
				IBuildModel model = (IBuildModel) parent;
				IBuild build = model.getBuild();
				return build.getBuildEntries();
			}
			return new Object[0];
		}
	}

	class BuildLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof IBuildEntry) {
				return ((IBuildEntry) obj).getName();
			}
			return super.getText(obj);
		}

		public Image getImage(Object obj) {
			if (obj instanceof IBuildEntry)
				return HQDEPlugin.getDefault().getLabelProvider().get(HQDEPluginImages.DESC_BUILD_VAR_OBJ);
			return null;
		}
	}

	public BuildSourcePage(HQDEFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
	}

	public ILabelProvider createOutlineLabelProvider() {
		return new BuildLabelProvider();
	}

	public ITreeContentProvider createOutlineContentProvider() {
		return new BuildOutlineContentProvider();
	}

	protected IDocumentRange getRangeElement(ITextSelection selection) {
		if (selection.isEmpty())
			return null;
		IBuildModel model = (IBuildModel) getInputContext().getModel();
		return findBuildNode(model.getBuild().getBuildEntries(), selection.getOffset());
	}

	private BuildEntry findBuildNode(IBuildEntry[] nodes, int offset) {
		for (int i = 0; i < nodes.length; i++) {
			BuildEntry node = (BuildEntry) nodes[i];
			if (offset >= node.getOffset() && offset < node.getOffset() + node.getLength()) {
				return node;
			}
		}
		return null;
	}

	protected String[] collectContextMenuPreferencePages() {
		String[] ids = super.collectContextMenuPreferencePages();
		String[] more = new String[ids.length + 1];
		more[0] = "org.eclipse.jdt.ui.preferences.PropertiesFileEditorPreferencePage";
		System.arraycopy(ids, 0, more, 1, ids.length);
		return more;
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return ((BuildSourceViewerConfiguration) getSourceViewerConfiguration()).affectsTextPresentation(event) || super.affectsTextPresentation(event);
	}

	public IDocumentRange getRangeElement(int offset, boolean searchChildren) {
		IBuildModel model = (IBuildModel) getInputContext().getModel();
		IBuildEntry[] buildEntries = model.getBuild().getBuildEntries();

		for (int i = 0; i < buildEntries.length; i++) {
			IDocumentKey key = (IDocumentKey) buildEntries[i];
			if (offset >= key.getOffset() && offset < key.getOffset() + key.getLength())
				return key;
		}
		return null;
	}

	public Object getAdapter(Class adapter) {
//		if (IHyperlinkDetector.class.equals(adapter))
//			return new BuildHyperlinkDetector(this);
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.HQDESourcePage#updateSelection(java.lang.Object)
	 */
	public void updateSelection(Object object) {
		if (object instanceof IDocumentKey) {
			setHighlightRange((IDocumentKey) object);
		} else {
			resetHighlightRange();
		}
	}

}
