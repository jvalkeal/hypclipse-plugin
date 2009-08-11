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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildModelFactory;


public class BuildModel extends AbstractEditingModel implements IBuildModel {

	//private Properties fProperties;
	private BuildModelFactory fFactory;
	private Build fBuild;

	/**
	 * @param document
	 * @param isReconciling
	 */
	public BuildModel(IDocument document, boolean isReconciling) {
		super(document, isReconciling);
	}

//	protected NLResourceHelper createNLResourceHelper() {
//		return null;
//	}

	/*
	 * 
	 */
	public void load(InputStream source, boolean outOfSync) throws CoreException {
		try {
			fLoaded = true;
			((Build) getBuild()).load(source);
		} catch (IOException e) {
			fLoaded = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.editor.build.AbstractEditingModel#adjustOffsets(org.eclipse.jface.text.IDocument)
	 */
	public void adjustOffsets(IDocument document) {
		((Build) getBuild()).adjustOffsets(document);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IBuildModel#getBuild()
	 */
	public IBuild getBuild() {
		if (fBuild == null)
			fBuild = new Build(this);
		return fBuild;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IBuildModel#getFactory()
	 */
	public IBuildModelFactory getFactory() {
		if (fFactory == null)
			fFactory = new BuildModelFactory(this);
		return fFactory;
	}
}