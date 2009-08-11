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
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.text.IDocumentKey;
import org.hyperic.hypclipse.internal.util.PropertiesUtil;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IEditingModel;

public class BuildEntry implements IBuildEntry, IDocumentKey {

	private int fLength = -1;
	private int fOffset = -1;
	private IBuildModel fModel;
	private String fName;
	private ArrayList<Object> fTokens = new ArrayList<Object>();
	private String fLineDelimiter;

	public BuildEntry(String name, IBuildModel model) {
		fName = name;
		fModel = model;
		setLineDelimiter();
	}

	private void setLineDelimiter() {
		if (fModel instanceof IEditingModel) {
			IDocument document = ((IEditingModel) fModel).getDocument();
			fLineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
		} else {
			fLineDelimiter = System.getProperty("line.separator"); //$NON-NLS-1$
		}
	}

	public void addToken(String token) throws CoreException {
		if (fTokens.contains(token))
			return;
		if (fTokens.add(token))
			getModel().fireModelObjectChanged(this, getName(), null, token);
	}

	public String getName() {
		return fName;
	}

	public String[] getTokens() {
		return (String[]) fTokens.toArray(new String[fTokens.size()]);
	}

	public boolean contains(String token) {
		return fTokens.contains(token);
	}

	public void removeToken(String token) throws CoreException {
		if (fTokens.remove(token))
			getModel().fireModelObjectChanged(this, getName(), token, null);
	}

	public void renameToken(String oldToken, String newToken) throws CoreException {
		int index = fTokens.indexOf(oldToken);
		if (index != -1) {
			fTokens.set(index, newToken);
			getModel().fireModelObjectChanged(this, getName(), oldToken, newToken);
		}
	}

	public void setName(String name) {
		String oldName = fName;
		if (getModel() != null) {
			try {
				IBuild build = getModel().getBuild();
				build.remove(this);
				fName = name;
				build.add(this);
			} catch (CoreException e) {
				HQDEPlugin.logException(e);
			}
			getModel().fireModelObjectChanged(this, getName(), oldName, name);
		} else
			fName = name;
	}

	public int getOffset() {
		return fOffset;
	}

	public void setOffset(int offset) {
		fOffset = offset;
	}

	public int getLength() {
		return fLength;
	}

	public void setLength(int length) {
		fLength = length;
	}

	public void write(String indent, PrintWriter writer) {
	}

	public IBuildModel getModel() {
		return fModel;
	}

	public void processEntry(String value) {
		StringTokenizer stok = new StringTokenizer(value, ","); //$NON-NLS-1$
		while (stok.hasMoreTokens()) {
			fTokens.add(stok.nextToken().trim());
		}
	}

	public String write() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(PropertiesUtil.createWritableName(fName));
		buffer.append(" = "); //$NON-NLS-1$
		int indentLength = fName.length() + 3;
		for (int i = 0; i < fTokens.size(); i++) {
			buffer.append(PropertiesUtil.createEscapedValue(fTokens.get(i).toString()));
			if (i < fTokens.size() - 1) {
				buffer.append(",\\"); //$NON-NLS-1$
				buffer.append(fLineDelimiter);
				for (int j = 0; j < indentLength; j++) {
					buffer.append(" "); //$NON-NLS-1$
				}
			}
		}
		buffer.append(fLineDelimiter); //$NON-NLS-1$
		return buffer.toString();
	}

	public void swap(int index1, int index2) {
		Object obj1 = fTokens.get(index1);
		Object obj2 = fTokens.set(index2, obj1);
		fTokens.set(index1, obj2);
		getModel().fireModelObjectChanged(this, getName(), new Object[] {obj1, obj2}, new Object[] {obj2, obj1});
	}

	/**
	 * @param targetToken
	 * @return
	 */
	public String getPreviousToken(String targetToken) {
		// Ensure we have tokens
		if (fTokens.size() <= 1) {
			return null;
		}
		// Get the index of the target token
		int targetIndex = fTokens.indexOf(targetToken);
		// Validate index
		if (targetIndex < 0) {
			// Target token does not exist
			return null;
		} else if (targetIndex == 0) {
			// Target token has no previous token
			return null;
		}
		// 1 <= index < size()
		// Get the previous token
		String previousToken = (String) fTokens.get(targetIndex - 1);

		return previousToken;
	}

	/**
	 * @param targetToken
	 * @return
	 */
	public String getNextToken(String targetToken) {
		// Ensure we have tokens
		if (fTokens.size() <= 1) {
			return null;
		}
		// Get the index of the target token
		int targetIndex = fTokens.indexOf(targetToken);
		// Get the index of the last token
		int lastIndex = fTokens.size() - 1;
		// Validate index
		if (targetIndex < 0) {
			// Target token does not exist
			return null;
		} else if (targetIndex >= lastIndex) {
			// Target token has no next token
			return null;
		}
		// 0 <= index < last token < size()
		// Get the next token
		String nextToken = (String) fTokens.get(targetIndex + 1);

		return nextToken;
	}

	/**
	 * @param targetToken
	 * @return
	 */
	public int getIndexOf(String targetToken) {
		return fTokens.indexOf(targetToken);
	}

	/**
	 * @param token
	 * @param position
	 */
	public void addToken(String token, int position) {
		// Validate position
		if (position < 0) {
			return;
		} else if (position > fTokens.size()) {
			return;
		}
		// Ensure no duplicates
		if (fTokens.contains(token)) {
			return;
		}
		// Add the token at the specified position
		fTokens.add(position, token);
		// Fire event
		getModel().fireModelObjectChanged(this, getName(), null, token);
	}

}