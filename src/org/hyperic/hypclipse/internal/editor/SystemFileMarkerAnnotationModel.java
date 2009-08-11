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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

/**
 * A marker annotation model whose underlying source of markers is 
 * a resource in the workspace.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class SystemFileMarkerAnnotationModel extends AbstractMarkerAnnotationModel {

	protected IMarker[] retrieveMarkers() throws CoreException {
		return null;
	}

	protected void deleteMarkers(IMarker[] markers) throws CoreException {
	}

	protected void listenToMarkerChanges(boolean listen) {
	}

	protected boolean isAcceptable(IMarker marker) {
		return true;
	}

}
