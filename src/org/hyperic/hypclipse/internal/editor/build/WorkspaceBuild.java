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

import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.ModelChangedEvent;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;

public class WorkspaceBuild extends BuildObject implements IBuild {

	protected ArrayList<IBuildEntry> fEntries = new ArrayList<IBuildEntry>();

	public void add(IBuildEntry entry) throws CoreException {
		ensureModelEditable();
		fEntries.add(entry);
		((WorkspaceBuildEntry) entry).setInTheModel(true);
		getModel().fireModelChanged(new ModelChangedEvent(getModel(), IModelChangedEvent.INSERT, new Object[] {entry}, null));
	}

	public IBuildEntry[] getBuildEntries() {
		return (IBuildEntry[]) fEntries.toArray(new IBuildEntry[fEntries.size()]);
	}

	public IBuildEntry getEntry(String name) {
		for (int i = 0; i < fEntries.size(); i++) {
			IBuildEntry entry = (IBuildEntry) fEntries.get(i);
			if (entry.getName().equals(name))
				return entry;
		}
		return null;
	}

	public void processEntry(String name, String value) {
		WorkspaceBuildEntry entry = (WorkspaceBuildEntry) getModel().getFactory().createEntry(name);
		fEntries.add(entry);
		entry.processEntry(value);
	}

	public void remove(IBuildEntry entry) throws CoreException {
		ensureModelEditable();
		fEntries.remove(entry);
		getModel().fireModelChanged(new ModelChangedEvent(getModel(), IModelChangedEvent.REMOVE, new Object[] {entry}, null));
	}

	public void reset() {
		fEntries.clear();
	}

	public void write(String indent, PrintWriter writer) {
		for (int i = 0; i < fEntries.size(); i++) {
			IBuildEntry entry = (IBuildEntry) fEntries.get(i);
			entry.write("", writer); //$NON-NLS-1$
		}
	}

}
