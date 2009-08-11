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

import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildModelFactory;


public class BuildModelFactory implements IBuildModelFactory {

	private IBuildModel fModel;

	public BuildModelFactory(IBuildModel model) {
		fModel = model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IBuildModelFactory#createEntry(java.lang.String)
	 */
	public IBuildEntry createEntry(String name) {
		return new BuildEntry(name, fModel);
	}
}
