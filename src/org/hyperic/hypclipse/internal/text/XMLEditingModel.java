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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.schema.HQGrammarLoader;
import org.hyperic.hypclipse.internal.util.XMLReaderWrapper;
import org.hyperic.hypclipse.plugin.IModel;
import org.hyperic.hypclipse.plugin.IWritable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.psvi.TypeDetector;
import com.sun.msv.verifier.psvi.TypedContentHandler;
import com.sun.msv.verifier.util.ErrorHandlerImpl;

/**
 * 
 *
 */
public abstract class XMLEditingModel extends AbstractEditingModel {

	private IStatus status;
	private DefaultHandler tch;
	private PluginTypeDetector verifier;
	
	public XMLEditingModel(IDocument document, boolean isReconciling) {
		super(document, isReconciling);
	}

	public void load(InputStream source, boolean outOfSync) {
		try {
			fLoaded = true;
			status = new Status(IStatus.OK, HQDEPlugin.PLUGIN_ID, null);
			
			XMLReaderWrapper reader = new XMLReaderWrapper();
	        tch = createDocumentHandler(this, true);
	        tch.setDocumentLocator(getVerifier().getLocator());	        
			reader.parse(source, getVerifier(), (TypedContentHandler)tch);
			
		} catch (ParserConfigurationException e) {
			fLoaded = false;
		} catch (SAXException e) {
			fLoaded = false;
			status = new Status(IStatus.ERROR, HQDEPlugin.PLUGIN_ID, e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			fLoaded = false;
		} catch (IOException e) {
			fLoaded = false;
		}
	}

	// need to get access to locator
	class PluginTypeDetector extends TypeDetector {
	    public PluginTypeDetector( DocumentDeclaration documentDecl, ErrorHandler errorHandler ) {
	    	super(documentDecl,errorHandler);
	    }
	    
	    public void setDocumentLocator( Locator loc ) {
	    	super.setDocumentLocator(loc);
	    	tch.setDocumentLocator(loc);
	    }

	}
	
	private PluginTypeDetector getVerifier() throws IOException, SAXException, ParserConfigurationException {
		if(verifier == null) {
			HQGrammarLoader loader = new HQGrammarLoader();
			loader.setController(new DebugController(false,false));
			URL url = FileLocator.find(HQDEPlugin.getDefault().getBundle(), new Path("schema/hq-plugin.rng"), null);
			URLConnection uc = url.openConnection();
			InputSource is = new InputSource(uc.getInputStream());
			DocumentDeclaration grammar = HQGrammarLoader.loadVGM(is);
			verifier = new PluginTypeDetector(grammar, new ErrorHandlerImpl() );
		}
		return verifier;
	}

	protected abstract DefaultHandler createDocumentHandler(IModel model, boolean reconciling);

	public void adjustOffsets(IDocument document) {
		try {

			
			XMLReaderWrapper reader = new XMLReaderWrapper();
//	        tch = createDocumentHandler(this, true);
	        tch = createDocumentHandler(this, false);
	        tch.setDocumentLocator(getVerifier().getLocator());	        
			reader.parse(getInputStream(document), getVerifier(), (TypedContentHandler)tch);

			
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (ParserConfigurationException e) {
		} catch (FactoryConfigurationError e) {
		}
	}

	
	protected InputStream getInputStream(IDocument document) throws UnsupportedEncodingException {
		return new BufferedInputStream(new ByteArrayInputStream(document.get().getBytes(getCharset())));
	}

	private boolean isResourceFile() {
		if (getUnderlyingResource() == null) {
			return false;
		} else if ((getUnderlyingResource() instanceof IFile) == false) {
			return false;
		}
		return true;
	}

	public void save() {
		if (isResourceFile() == false) {
			return;
		}
		try {
			IFile file = (IFile) getUnderlyingResource();
			String contents = getContents();
			ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes("UTF8")); //$NON-NLS-1$
			if (file.exists()) {
				file.setContents(stream, false, false, null);
			} else {
				file.create(stream, false, null);
			}
			stream.close();
		} catch (CoreException e) {
			HQDEPlugin.logException(e);
		} catch (IOException e) {
		}
	}

	public void reload() {
		if (isResourceFile() == false) {
			return;
		}
		IFile file = (IFile) getUnderlyingResource();
		// Underlying file has to exist in order to reload the model
		if (file.exists()) {
			InputStream stream = null;
			try {
				// Get the file contents
				stream = new BufferedInputStream(file.getContents(true));
				// Load the model using the last saved file contents
				reload(stream, false);
				// Remove the dirty (*) indicator from the editor window
				setDirty(false);
			} catch (CoreException e) {
				// Ignore
			}
		}
	}

	public void reload(IDocument document) {
		// Get the document's text
		String text = document.get();
		InputStream stream = null;

		try {
			// Turn the document's text into a stream
			stream = new ByteArrayInputStream(text.getBytes("UTF8"));
			// Reload the model using the stream
			reload(stream, false);
			// Remove the dirty (*) indicator from the editor window
			setDirty(false);
		} catch (UnsupportedEncodingException e) {
			HQDEPlugin.logException(e);
		} catch (CoreException e) {
			// Ignore
		}
	}

	public String getContents() {
		StringWriter swriter = new StringWriter();
		PrintWriter writer = new PrintWriter(swriter);
		setLoaded(true);
		save(writer);
		writer.flush();
		try {
			swriter.close();
		} catch (IOException e) {
		}
		return swriter.toString();
	}

	public void save(PrintWriter writer) {
		if (isLoaded()) {
			getRoot().write("", writer);
		}
		setDirty(false);
	}

	protected abstract IWritable getRoot();


}
