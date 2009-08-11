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
package org.hyperic.hypclipse.internal.editor;

import java.io.File;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class SystemFileEditorInput implements IStorageEditorInput, IPersistableElement {
	private SystemFileStorage storage;
	private static final String FACTORY_ID = HQDEPlugin.getPluginId() + ".systemFileEditorInputFactory"; //$NON-NLS-1$

	public SystemFileEditorInput(File file) {
		storage = new SystemFileStorage(file);
	}

	public boolean exists() {
		return storage.getFile().exists();
	}

	public Object getAdapter(Class adapter) {
		if (adapter.equals(File.class))
			return storage.getFile();
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return storage.getFile().getName();
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public void saveState(IMemento memento) {
		memento.putString("path", storage.getFile().getAbsolutePath()); //$NON-NLS-1$
	}

	public String getFactoryId() {
		return FACTORY_ID;
	}

	public IStorage getStorage() {
		return storage;
	}

	public String getToolTipText() {
		return storage.getFile().getAbsolutePath();
	}

	public boolean equals(Object object) {
		return object instanceof SystemFileEditorInput && getStorage().equals(((SystemFileEditorInput) object).getStorage());
	}

	public int hashCode() {
		return getStorage().hashCode();
	}
}
