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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.internal.text.plugin.PluginModelBase;
import org.hyperic.hypclipse.internal.text.plugin.PluginNode;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginModelBase;

public class PluginModel extends PluginModelBase implements IPluginModelBase {

	private IDocument fDocument;

	//private IPluginModelFactory fFactory;
	
	public PluginModel(IDocument document, boolean isReconciling) {
		super(document,isReconciling);
		fDocument = document;
//		fFactory = new PluginDocumentNodeFactory(this);
	}
	
	
	public IDocument getDocument() {
		return fDocument;
	}

//	public IPluginModelFactory getPluginFactory() {
//		return fFactory;
//	}
	

	protected InputStream getInputStream(IDocument document) throws UnsupportedEncodingException {
		return new BufferedInputStream(new ByteArrayInputStream(document.get().getBytes("UTF-8")));
	}


	public IPluginBase getPluginBase() {
//		return new PluginBase();
		return getPluginBase(true);
	}

	private PluginBaseNode fPluginBase;

	// TODO load happens already from PluginInputContext
	public IPluginBase getPluginBase(boolean createIfMissing) {
		if (!fLoaded && createIfMissing) {			
			createPluginBase();
			try {
				load();
			} catch (CoreException e) {
			}
		}
		return fPluginBase;
	}


	public IPluginBase createPluginBase() {
		
		fPluginBase = new PluginNode();
		fPluginBase.setXMLTagName("plugin");
		fPluginBase.setInTheModel(true);
		fPluginBase.setModel(this);
		return fPluginBase;
	}

//	@Override
//	protected DefaultHandler createDocumentHandler(IModel model, boolean reconciling) {
//		// TODO Auto-generated method stub
//		return null;
//	}


//	@Override
//	public void adjustOffsets(IDocument document) throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}


//	@Override
//	public IPluginModelFactory getFactory() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	public IBuildModel getBuildModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
