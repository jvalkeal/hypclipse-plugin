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

import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.hyperic.hypclipse.internal.ModelChangedEvent;
import org.hyperic.hypclipse.internal.plugin.PluginAttribute;
import org.hyperic.hypclipse.internal.plugin.PluginBase;
import org.hyperic.hypclipse.internal.text.plugin.PluginElement;
import org.hyperic.hypclipse.plugin.IHQModelFactory;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginAttribute;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.IPluginModelFactory;
import org.hyperic.hypclipse.plugin.IPluginObject;

public abstract class AbstractPluginModelBase extends AbstractModel implements IPluginModelBase, IPluginModelFactory {

	private static final long serialVersionUID = 1L;
	protected IPluginBase fPluginBase;
	private boolean enabled;
	private BundleDescription fBundleDescription;
	protected boolean fAbbreviated;

	public AbstractPluginModelBase() {
		super();
	}

	public abstract String getInstallLocation();

	public abstract IPluginBase createPluginBase();

	public IPluginModelFactory getPluginFactory() {
		return this;
	}

	public IPluginBase getPluginBase() {
		return getPluginBase(true);
	}

	public IPluginBase getPluginBase(boolean createIfMissing) {
		if (fPluginBase == null && createIfMissing) {
			fPluginBase = createPluginBase();
			setLoaded(true);
		}
		return fPluginBase;
	}

	public IHQModelFactory getFactory() {
		return this;
	}
	
	public void load(InputStream stream, boolean outOfSync) throws CoreException {
		load(stream, outOfSync, new PluginHandler(fAbbreviated));
	}

	public void load(InputStream stream, boolean outOfSync, PluginHandler handler) throws CoreException {
		if (fPluginBase == null)
			fPluginBase = createPluginBase();

		((PluginBase) fPluginBase).reset();
		setLoaded(false);
		try {
			SAXParser parser = getSaxParser();
			parser.parse(stream, handler);
			((PluginBase) fPluginBase).load(handler.getDocumentElement());
			setLoaded(true);
			if (!outOfSync)
				updateTimeStamp();
		} catch (Exception e) {
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFragmentModel() {
		return false;
	}

	public void reload(InputStream stream, boolean outOfSync) throws CoreException {
		load(stream, outOfSync);
		fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, new Object[] {fPluginBase}, null));
	}

	public void setEnabled(boolean newEnabled) {
		enabled = newEnabled;
	}

	public String toString() {
		IPluginBase pluginBase = getPluginBase();
		if (pluginBase != null)
			return pluginBase.getId();
		return super.toString();
	}

	protected abstract void updateTimeStamp();

	public IPluginAttribute createAttribute(IPluginElement element) {
		PluginAttribute attribute = new PluginAttribute();
		attribute.setModel(this);
		// TODO implement setParent
		//attribute.setParent(element);
		return attribute;
	}

	public IPluginElement createElement(IPluginObject parent) {
		PluginElement element = new PluginElement();
		element.setModel(this);
		element.setParent(parent);
		return element;
	}






	public boolean isValid() {
		if (!isLoaded())
			return false;
		if (fPluginBase == null)
			return false;
		return fPluginBase.isValid();
	}

	public boolean isBundleModel() {
		return false;
	}

	public void dispose() {
		fBundleDescription = null;
		super.dispose();
	}

	public BundleDescription getBundleDescription() {
		return fBundleDescription;
	}

	public void setBundleDescription(BundleDescription description) {
		fBundleDescription = description;
	}

}
