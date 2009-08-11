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
package org.hyperic.hypclipse.internal.builders;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.HQDEPlugin;

public abstract class ErrorReporter {

	protected static final String[] BOOLEAN_VALUES = new String[] {"true", "false"}; //$NON-NLS-1$ //$NON-NLS-2$

	private int fErrorCount;
	protected IFile fFile;
	protected IProject fProject;
	private HQDEMarkerFactory fMarkerFactory;

	public ErrorReporter(IFile file) {
		fErrorCount = 0;
		fFile = file;
		if (fFile != null) {
			fProject = fFile.getProject();
		}
	}

	protected IMarker addMarker(String message, int lineNumber, int severity, int problemID, String category) {
		try {
			IMarker marker = getMarkerFactory().createMarker(fFile, problemID, category);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1)
				lineNumber = 1;
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			if (severity == IMarker.SEVERITY_ERROR) {
				fErrorCount += 1;
			}
			return marker;
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
		return null;
	}

	protected IDocument createDocument(IFile file) {
		if (!file.exists()) {
			return null;
		}
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		if (manager == null) {
			return null;
		}
		try {
			manager.connect(file.getFullPath(), LocationKind.NORMALIZE, null);
			ITextFileBuffer textBuf = manager.getTextFileBuffer(file.getFullPath(), LocationKind.NORMALIZE);
			IDocument document = textBuf.getDocument();
			manager.disconnect(file.getFullPath(), LocationKind.NORMALIZE, null);
			return document;
		} catch (CoreException e) {
			HQDEPlugin.log(e);
		}
		return null;
	}

	public int getErrorCount() {
		return fErrorCount;
	}

	private HQDEMarkerFactory getMarkerFactory() {
		if (fMarkerFactory == null)
			fMarkerFactory = new HQDEMarkerFactory();
		return fMarkerFactory;
	}

	private void removeFileMarkers() {
		try {
			fFile.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			fFile.deleteMarkers(HQDEMarkerFactory.MARKER_ID, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}

	public IMarker report(String message, int line, int severity, int problemID, String category) {
		if (severity == CompilerFlags.ERROR)
			return addMarker(message, line, IMarker.SEVERITY_ERROR, problemID, category);
		else if (severity == CompilerFlags.WARNING)
			return addMarker(message, line, IMarker.SEVERITY_WARNING, problemID, category);
		return null;
	}

	public IMarker report(String message, int line, int severity, String category) {
		return report(message, line, severity, HQDEMarkerFactory.NO_RESOLUTION, category);
	}

	protected IMarker report(String message, int line, String compilerFlag, int problemID, String category) {
		int severity = CompilerFlags.getFlag(fProject, compilerFlag);
		if (severity != CompilerFlags.IGNORE) {
			return report(message, line, severity, problemID, category);
		}
		return null;
	}

	protected void report(String message, int line, String compilerFlag, String category) {
		report(message, line, compilerFlag, HQDEMarkerFactory.NO_RESOLUTION, category);
	}

	public void validateContent(IProgressMonitor monitor) {
		removeFileMarkers();
		validate(monitor);
	}

	protected abstract void validate(IProgressMonitor monitor);
}
