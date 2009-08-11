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

import java.io.*;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.*;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public class SystemFileStorage extends PlatformObject implements IStorage {
	private File file;

	/**
	 * Constructor for SystemFileStorage.
	 */
	public SystemFileStorage(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			IStatus status = new Status(IStatus.ERROR, HQDEPlugin.getPluginId(), IStatus.OK, null, e);
			throw new CoreException(status);
		}
	}

	public IPath getFullPath() {
		return new Path(file.getAbsolutePath());
	}

	public String getName() {
		return file.getName();
	}

	public boolean isReadOnly() {
		return true;
	}

	public boolean equals(Object object) {
		return object instanceof SystemFileStorage && getFile().equals(((SystemFileStorage) object).getFile());
	}

	public int hashCode() {
		return getFile().hashCode();
	}
}
