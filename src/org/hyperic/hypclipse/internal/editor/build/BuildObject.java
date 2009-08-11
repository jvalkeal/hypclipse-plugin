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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.plugin.IBuildModel;

public class BuildObject implements IBuildObject {
	private IBuildModel fModel;

	private boolean fInTheModel;

	public boolean isInTheModel() {
		return fInTheModel;
	}

	public void setInTheModel(boolean inTheModel) {
		fInTheModel = inTheModel;
	}

	protected void ensureModelEditable() throws CoreException {
		if (!fModel.isEditable()) {
			throwCoreException(HQDEMessages.BuildObject_readOnlyException);
		}
	}

	public IBuildModel getModel() {
		return fModel;
	}

	void setModel(IBuildModel model) {
		fModel = model;
	}

	protected void throwCoreException(String message) throws CoreException {
		Status status = new Status(IStatus.ERROR, HQDEPlugin.PLUGIN_ID, IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
	}
}
