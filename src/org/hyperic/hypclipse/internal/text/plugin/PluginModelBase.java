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
package org.hyperic.hypclipse.internal.text.plugin;

import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.text.DocumentHandler;
import org.hyperic.hypclipse.internal.text.XMLEditingModel;
import org.hyperic.hypclipse.plugin.IHQModelFactory;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IWritable;

public abstract class PluginModelBase extends XMLEditingModel implements IPluginModelBase {

	private PluginDocumentHandler fHandler;
	private IPluginModelFactory fFactory;

	public PluginModelBase(IDocument document, boolean isReconciling) {
		super(document, isReconciling);
		fFactory = new PluginDocumentNodeFactory(this);
	}

	protected DocumentHandler createDocumentHandler(IModel model, boolean reconciling) {
//		if (fHandler == null)
			fHandler = new PluginDocumentHandler(this,reconciling);
		return fHandler;
	}

	public IPluginModelFactory getPluginFactory() {
		return fFactory;
	}
	
	public IHQModelFactory getFactory() {
		return fFactory;
	}

	protected IWritable getRoot() {
		return getPluginBase();
	}


}
