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
package org.hyperic.hypclipse.internal.context;

import org.eclipse.core.resources.IFile;

public interface IInputContextListener {
	/**
	 * Informs the listener that a new context has been added.
	 * This should result in a new source tab.
	 * @param context
	 */
	void contextAdded(InputContext context);

	/**
	 * Informs the listener that the context has been removed.
	 * This should result in removing the source tab.
	 * @param context
	 */
	void contextRemoved(InputContext context);

	/**
	 * Informs the listener that a monitored file has
	 * been added.
	 * @param monitoredFile the file we were monitoring
	 */
	void monitoredFileAdded(IFile monitoredFile);

	/**
	 * Informs the listener that a monitored file has
	 * been removed.
	 * @param monitoredFile
	 * @return <code>true</code> if it is OK to remove
	 * the associated context, <code>false</code> otherwise.
	 */
	boolean monitoredFileRemoved(IFile monitoredFile);
}
