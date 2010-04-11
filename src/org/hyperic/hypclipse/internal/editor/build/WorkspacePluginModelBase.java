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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.hqmodel.HQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.HQMetric;
import org.hyperic.hypclipse.internal.hqmodel.HQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.HQServer;
import org.hyperic.hypclipse.internal.hqmodel.IHQClasspath;
import org.hyperic.hypclipse.internal.hqmodel.IHQHelp;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetric;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQPlatform;
import org.hyperic.hypclipse.internal.hqmodel.IHQScript;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.plugin.PluginBase;
import org.hyperic.hypclipse.internal.util.CoreUtility;
import org.hyperic.hypclipse.plugin.IBuildModel;
import org.hyperic.hypclipse.plugin.IEditableModel;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginBase;

/**
 * This class only represents 3.0 style plug-ins
 */
public class WorkspacePluginModelBase extends AbstractPluginModelBase implements IEditableModel {

	private static final long serialVersionUID = 1L;

	private IFile fUnderlyingResource;

	private boolean fDirty;

	private boolean fEditable = true;

	private IBuildModel fBuildModel;



	public WorkspacePluginModelBase(IFile file, boolean abbreviated) {
		fUnderlyingResource = file;
		fAbbreviated = abbreviated;
		setEnabled(true);
	}

	public void fireModelChanged(IModelChangedEvent event) {
		fDirty = true;
		super.fireModelChanged(event);
	}

	public IBuildModel getBuildModel() {
		return fBuildModel;
	}

	public String getContents() {
		StringWriter swriter = new StringWriter();
		PrintWriter writer = new PrintWriter(swriter);
		save(writer);
		writer.flush();
		try {
			swriter.close();
		} catch (IOException e) {
		}
		return swriter.toString();
	}

	public IFile getFile() {
		return fUnderlyingResource;
	}

	public String getInstallLocation() {
		IPath path = fUnderlyingResource.getLocation();
		return path == null ? null : path.removeLastSegments(1).addTrailingSeparator().toOSString();
	}

	public IResource getUnderlyingResource() {
		return fUnderlyingResource;
	}

	public boolean isInSync() {
		if (fUnderlyingResource == null)
			return true;
		IPath path = fUnderlyingResource.getLocation();
		if (path == null)
			return false;
		return super.isInSync(path.toFile());
	}

	public boolean isDirty() {
		return fDirty;
	}

	public boolean isEditable() {
		return fEditable;
	}

	public void load() {
		if (fUnderlyingResource == null)
			return;
		if (fUnderlyingResource.exists()) {
			try {
				InputStream stream = new BufferedInputStream(fUnderlyingResource.getContents(true));
				load(stream, false);
				stream.close();
			} catch (CoreException e) {
			} catch (IOException e) {
				HQDEPlugin.logException(e);
			}
		} else {
			fPluginBase = createPluginBase();
			setLoaded(true);
		}
	}

	protected void updateTimeStamp() {
		updateTimeStamp(fUnderlyingResource.getLocation().toFile());
	}

	public void save() {
		if (fUnderlyingResource == null)
			return;
		try {
			String contents = getContents();
			ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes("UTF8")); //$NON-NLS-1$
			if (fUnderlyingResource.exists()) {
				fUnderlyingResource.setContents(stream, false, false, null);
			} else {
				IContainer p = fUnderlyingResource.getParent();
				if(p instanceof IFolder) {
					((IFolder)p).create(false, true, null);
				}
				fUnderlyingResource.create(stream, false, null);
			}
			stream.close();
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		} catch (IOException e) {
		}
	}

	public void save(PrintWriter writer) {
		if (isLoaded()) {
			fPluginBase.write("", writer);
		}
		fDirty = false;
	}

	public void setBuildModel(IBuildModel buildModel) {
		fBuildModel = buildModel;
	}

	public void setDirty(boolean dirty) {
		fDirty = dirty;
	}

	public void setEditable(boolean editable) {
		fEditable = editable;
	}

	
	public IPluginBase createPluginBase() {
		PluginBase plugin = new PluginBase();
		plugin.setModel(this);
		return plugin;
	}


	// TODO wrong place...
	public IHQClasspath createClasspath() {
		HQClasspath node = new HQClasspath();
		node.setParent(getPluginBase());
		node.setModel(this);
//		node.setXMLTagName("foo");
		return node;
	}
	
	public IHQServer createServer() {
		HQServer node = new HQServer();
		node.setParent(getPluginBase());
		node.setModel(this);
		return node;
	}

	public IHQMetric createMetric() {
		HQMetric node = new HQMetric();
		node.setParent(getPluginBase());
		node.setModel(this);
		return node;
	}

	public IHQMetrics createMetrics() {
		HQMetrics node = new HQMetrics();
		node.setParent(getPluginBase());
		node.setModel(this);
		return node;
	}

	public IHQHelp createHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public IHQScript createScript() {
		// TODO Auto-generated method stub
		return null;
	}

	public IHQPlatform createPlatform() {
		// TODO Auto-generated method stub
		return null;
	}

}
