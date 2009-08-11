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
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.ModelChangedEvent;
import org.hyperic.hypclipse.internal.util.PropertiesUtil;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;

public class WorkspaceBuildEntry extends BuildObject implements IBuildEntry {

	private Vector<String> tokens = new Vector<String>();
	private String name;

	public WorkspaceBuildEntry(String name) {
		this.name = name;
	}

	public void addToken(String token) throws CoreException {
		ensureModelEditable();
		tokens.add(token);
		getModel().fireModelChanged(new ModelChangedEvent(getModel(), IModelChangedEvent.INSERT, new Object[] {token}, null));
	}

	public String getName() {
		return name;
	}

	public String[] getTokens() {
		String[] result = new String[tokens.size()];
		tokens.copyInto(result);
		return result;
	}

	public boolean contains(String token) {
		return tokens.contains(token);
	}

	void processEntry(String value) {
		StringTokenizer stok = new StringTokenizer(value, ","); //$NON-NLS-1$
		while (stok.hasMoreTokens()) {
			String token = stok.nextToken();
			token = token.trim();
			tokens.add(token);
		}
	}

	public void removeToken(String token) throws CoreException {
		ensureModelEditable();
		tokens.remove(token);
		getModel().fireModelChanged(new ModelChangedEvent(getModel(), IModelChangedEvent.REMOVE, new Object[] {token}, null));
	}

	public void renameToken(String oldName, String newName) throws CoreException {
		ensureModelEditable();
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.elementAt(i).toString().equals(oldName)) {
				tokens.setElementAt(newName, i);
				break;
			}
		}
		getModel().fireModelChanged(new ModelChangedEvent(getModel(), IModelChangedEvent.CHANGE, new Object[] {oldName}, null));
	}

	public void setName(String name) throws CoreException {
		ensureModelEditable();
		String oldValue = this.name;
		this.name = name;
		getModel().fireModelObjectChanged(this, P_NAME, oldValue, name);
	}

	public String toString() {
		return name;
	}

	public void write(String indent, PrintWriter writer) {
		PropertiesUtil.writeKeyValuePair(indent, name, tokens.elements(), writer);
	}

	public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
		if (name.equals(P_NAME)) {
			setName(newValue != null ? newValue.toString() : null);
		}
	}

}
