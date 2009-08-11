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
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.verifier.psvi.TypedContentHandler;

public class XMLErrorReporter implements TypedContentHandler,ErrorHandler {
	
	/** File to parse */
	protected IFile fFile;
	
	/** Project where file belongs */
	protected IProject fProject;

	/** Used marker factory instance */
	private HQDEMarkerFactory fMarkerFactory;

	/** Number of counted markers on error */
	private int fErrorCount;
	
	/**
	 * 
	 * @param file
	 */
	public XMLErrorReporter(IFile file) {
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		try {
			fFile = file;
			fProject = file.getProject();
			manager.connect(file.getFullPath(), LocationKind.NORMALIZE, null);
			manager.disconnect(file.getFullPath(), LocationKind.NORMALIZE, null);
			removeFileMarkers();
		} catch (CoreException e) {
			HQDEPlugin.log(e);
		}
	}

	/**
	 * 
	 * @param message
	 * @param lineNumber
	 * @param severity
	 * @param fixId
	 * @param category
	 * @return
	 */
	private IMarker addMarker(String message, int lineNumber, int severity, int fixId, String category) {
		try {
			IMarker marker = getMarkerFactory().createMarker(fFile, fixId, category);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1)
				lineNumber = 1;
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			if (severity == IMarker.SEVERITY_ERROR)
				fErrorCount += 1;
			return marker;
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
		return null;
	}
	
	/**
	 * Returns instance of marker factory.
	 * @return
	 */
	private HQDEMarkerFactory getMarkerFactory() {
		if (fMarkerFactory == null)
			fMarkerFactory = new HQDEMarkerFactory();
		return fMarkerFactory;
	}

	/**
	 * Adds marker
	 * @param e
	 * @param severity
	 */
	private void addMarker(SAXParseException e, int severity) {
		addMarker(e.getMessage(), e.getLineNumber(), severity, HQDEMarkerFactory.NO_RESOLUTION, HQDEMarkerFactory.CAT_OTHER);
	}



	/**
	 * Removes file markers which are
	 * attached to parsed file.
	 */
	private void removeFileMarkers() {
		try {
			fFile.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			fFile.deleteMarkers(HQDEMarkerFactory.MARKER_ID, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		}
	}
	
	public void validateContent(IProgressMonitor monitor) {
	}


	public void characterChunk(String literal, Datatype type)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endAttribute(String namespaceURI, String localName,
			String name, AttributeExp type) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endAttributePart() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endElement(String namespaceURI, String localName, String name,
			ElementExp type) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startAttribute(String namespaceURI, String localName,
			String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startDocument(ValidationContext context) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startElement(String namespaceURI, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void error(SAXParseException e) throws SAXException {
		addMarker(e, IMarker.SEVERITY_ERROR);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		addMarker(e, IMarker.SEVERITY_ERROR);
	}

	public void warning(SAXParseException arg0) throws SAXException {
		
	}

}
