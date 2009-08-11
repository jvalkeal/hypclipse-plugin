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
package org.hyperic.hypclipse.internal.text;

import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.hyperic.hypclipse.plugin.IReconcilingParticipant;

public class ReconcilingStrategy implements IReconcilingStrategy {

	private IDocument fDocument;
	private ArrayList fParticipants = new ArrayList();

	public ReconcilingStrategy() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		fDocument = document;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (fDocument != null)
			notifyParticipants();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition) {
		if (fDocument != null)
			notifyParticipants();
	}

	private synchronized void notifyParticipants() {
		for (int i = 0; i < fParticipants.size(); i++) {
			((IReconcilingParticipant) fParticipants.get(i)).reconciled(fDocument);
		}
	}

	public void addParticipant(IReconcilingParticipant participant) {
		fParticipants.add(participant);
	}

	public void removeParticipant(IReconcilingParticipant participant) {
		fParticipants.remove(participant);
	}
}
