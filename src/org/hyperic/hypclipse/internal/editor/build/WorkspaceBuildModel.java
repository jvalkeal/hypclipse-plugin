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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.IModelChangeProviderExtension;
import org.hyperic.hypclipse.internal.IModelChangedListenerFilter;
import org.hyperic.hypclipse.internal.ModelChangedEvent;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IBuildModelFactory;
import org.hyperic.hypclipse.plugin.IEditableModel;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;
import org.xml.sax.SAXException;

public class WorkspaceBuildModel 
	extends PlatformObject 
	implements IModel, IModelChangeProviderExtension, IBuildModel, IEditableModel {

	private IFile fUnderlyingResource;
	
	private static final long serialVersionUID = 1L;

	private transient List<IModelChangedListener> fListeners;

	private boolean fLoaded;

	protected boolean fDisposed;

	private long fTimestamp;

	private Exception fException;

	protected WorkspaceBuild fBuild;
	
	private WorkspaceBuildModelFactory fFactory;
	
	private boolean fDirty;
	
	private boolean fEditable = true;

	/**
	 * 
	 * @param file
	 */
	public WorkspaceBuildModel(IFile file) {
		fListeners = Collections.synchronizedList(new ArrayList<IModelChangedListener>());
		fUnderlyingResource = file;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IModelChangeProvider#addModelChangedListener(org.hyperic.hypclipse.plugin.IModelChangedListener)
	 */
	public void addModelChangedListener(IModelChangedListener listener) {
		fListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.IModelChangeProviderExtension#transferListenersTo(org.hyperic.hypclipse.internal.IModelChangeProviderExtension, org.hyperic.hypclipse.internal.IModelChangedListenerFilter)
	 */
	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		ArrayList<IModelChangedListener> removed = new ArrayList<IModelChangedListener>();
		for (int i = 0; i < fListeners.size(); i++) {
			IModelChangedListener listener = fListeners.get(i);
			if (filter == null || filter.accept(listener)) {
				target.addModelChangedListener(listener);
				removed.add(listener);
			}
		}
		fListeners.removeAll(removed);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IBaseModel#dispose()
	 */
	public void dispose() {
		fDisposed = true;
	}

	public void fireModelChanged(IModelChangedEvent event) {
		setDirty(event.getChangeType() != IModelChangedEvent.WORLD_CHANGED);
		IModelChangedListener[] list = fListeners.toArray(new IModelChangedListener[fListeners.size()]);
		for (int i = 0; i < list.length; i++) {
			IModelChangedListener listener = list[i];
			listener.modelChanged(event);
		}
	}

	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}

	public String getResourceString(String key) {
		return key;
	}

	public IResource getUnderlyingResource() {
		return null;
	}

	protected boolean isInSync(File localFile) {
		return localFile.exists() && localFile.lastModified() == getTimeStamp();
	}

	public boolean isValid() {
		return !isDisposed() && isLoaded();
	}

	public final long getTimeStamp() {
		return fTimestamp;
	}

	protected void updateTimeStamp(File localFile) {
		if (localFile.exists())
			fTimestamp = localFile.lastModified();
	}

	public boolean isDisposed() {
		return fDisposed;
	}

	public boolean isLoaded() {
		return fLoaded;
	}

	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}

	public void setException(Exception e) {
		fException = e;
	}

	public Exception getException() {
		return fException;
	}

	public void removeModelChangedListener(IModelChangedListener listener) {
		fListeners.remove(listener);
	}

	public void throwParseErrorsException(Throwable e) throws CoreException {
		Status status = new Status(IStatus.ERROR, HQDEPlugin.PLUGIN_ID, IStatus.OK, "Error in the manifest file", //$NON-NLS-1$
				e);
		throw new CoreException(status);
	}

	protected SAXParser getSaxParser() throws ParserConfigurationException, SAXException, FactoryConfigurationError {
		return SAXParserFactory.newInstance().newSAXParser();
	}

	public boolean isReconcilingModel() {
		return false;
	}
	
	public IBuild getBuild() {
		if (isLoaded() == false)
			load();
		return fBuild;
	}

	public IBuild getBuild(boolean createIfMissing) {
		if (fBuild == null && createIfMissing) {
			fBuild = new WorkspaceBuild();
			fBuild.setModel(this);
			setLoaded(true);
		}
		return getBuild();
	}

	public IBuildModelFactory getFactory() {
		if (fFactory == null)
			fFactory = new WorkspaceBuildModelFactory(this);
		return fFactory;
	}


	public void load(InputStream source, boolean outOfSync) {
		Properties properties = new Properties();
		try {
			properties.load(source);
			if (!outOfSync)
				updateTimeStamp();
		} catch (IOException e) {
			HQDEPlugin.logException(e);
			return;
		}
		fBuild = new WorkspaceBuild();
		fBuild.setModel(this);
		for (Enumeration names = properties.propertyNames(); names.hasMoreElements();) {
			String name = names.nextElement().toString();
			fBuild.processEntry(name, (String) properties.get(name));
		}
		setLoaded(true);
	}

	public void reload(InputStream source, boolean outOfSync) {
		if (fBuild != null)
			fBuild.reset();
		else {
			fBuild = new WorkspaceBuild();
			fBuild.setModel(this);
		}
		load(source, outOfSync);
		fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, new Object[0], null));
	}

	
	public String getContents() {
		StringWriter swriter = new StringWriter();
		PrintWriter writer = new PrintWriter(swriter);
		save(writer);
		writer.flush();
		try {
			swriter.close();
			writer.close();
		} catch (IOException e) {
		}
		return swriter.toString();
	}

	public boolean isDirty() {
		return fDirty;
	}

	public boolean isEditable() {
		return fEditable;
	}

	public void load() {
		if (fUnderlyingResource.exists()) {
			try {
				InputStream stream = fUnderlyingResource.getContents(true);
				load(stream, false);
				stream.close();
			} catch (Exception e) {
				HQDEPlugin.logException(e);
			}
		} else {
			fBuild = new WorkspaceBuild();
			fBuild.setModel(this);
			setLoaded(true);
		}
	}

	public boolean isInSync() {
		return true;
	}

	protected void updateTimeStamp() {
		updateTimeStamp(fUnderlyingResource.getLocation().toFile());
	}

	public void save() {
		if (fUnderlyingResource == null)
			return;
		try {
			String contents = getContents();
			ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes("8859_1")); //$NON-NLS-1$
			if (fUnderlyingResource.exists()) {
				fUnderlyingResource.setContents(stream, false, false, null);
			} else {
				fUnderlyingResource.create(stream, false, null);
			}
			stream.close();
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		} catch (IOException e) {
		}
	}

	public void save(PrintWriter writer) {
		getBuild().write("", writer); //$NON-NLS-1$
		fDirty = false;
	}

	public void setDirty(boolean dirty) {
		fDirty = dirty;
	}

	public void setEditable(boolean editable) {
		fEditable = editable;
	}

	public String getInstallLocation() {
		return fUnderlyingResource.getLocation().toOSString();
	}


}
