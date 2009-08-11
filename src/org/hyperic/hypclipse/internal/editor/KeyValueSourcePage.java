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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.hyperic.hypclipse.internal.text.IDocumentKey;
import org.hyperic.hypclipse.internal.util.PropertiesUtil;

public abstract class KeyValueSourcePage extends HQDEProjectionSourcePage {

	public KeyValueSourcePage(HQDEFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.neweditor.PDESourcePage#createViewerSorter()
	 */
	public ViewerComparator createDefaultOutlineComparator() {
		return new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IDocumentKey) && (e2 instanceof IDocumentKey)) {
					IDocumentKey key1 = (IDocumentKey) e1;
					IDocumentKey key2 = (IDocumentKey) e2;
					return key1.getOffset() < key2.getOffset() ? -1 : 1;
				}
				// Bundle manifest header elements
				// Do not sort by default
				return 0;
			}
		};
	}

	public void setHighlightRange(IDocumentKey key) {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return;

		IDocument document = sourceViewer.getDocument();
		if (document == null)
			return;

		int offset = key.getOffset();
		int length = key.getLength();

		if (offset == -1 || length == -1)
			return;
		setHighlightRange(offset, length, true);
		int nameLength = PropertiesUtil.createWritableName(key.getName()).length();
		sourceViewer.setSelectedRange(offset, Math.min(nameLength, length));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESourcePage#createOutlineSorter()
	 */
	public ViewerComparator createOutlineComparator() {
		return new ViewerComparator();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDEProjectionSourcePage#isQuickOutlineEnabled()
	 */
	public boolean isQuickOutlineEnabled() {
		return true;
	}

}
