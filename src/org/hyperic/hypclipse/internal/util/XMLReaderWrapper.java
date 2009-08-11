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
package org.hyperic.hypclipse.internal.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.msv.verifier.psvi.TypeDetector;
import com.sun.msv.verifier.psvi.TypedContentHandler;

public class XMLReaderWrapper extends SAXParserWrapper {

	/** Handle to xml reader */
	XMLReader fReader;

	/**
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public XMLReaderWrapper() throws ParserConfigurationException, SAXException, FactoryConfigurationError {
		super();
		fReader = fParser.getXMLReader();
	}

	/**
	 * 
	 * @param is
	 * @param verifier
	 * @param handler
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputSource is, TypeDetector verifier, TypedContentHandler handler) throws SAXException, IOException {
		fReader.setContentHandler(verifier);
		verifier.setContentHandler(handler);
		fReader.setErrorHandler(verifier.getErrorHandler());
		fReader.parse(is);
	}

	/**
	 * 
	 * @param is
	 * @param verifier
	 * @param handler
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputStream is, TypeDetector verifier, TypedContentHandler handler) throws SAXException, IOException {
		parse(new InputSource(is),verifier,handler);
	}

	/**
	 * 
	 * @param f
	 * @param verifier
	 * @param handler
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(File f, TypeDetector verifier, TypedContentHandler handler) throws SAXException, IOException {
		InputStream is = new BufferedInputStream(
	            new FileInputStream(f));
		parse(is,verifier,handler);		
	}
	
	/**
	 * Dispose the class
	 */
	public void dispose() {
		if (isdisposed == false) {
			super.dispose();
		}

	}


}
