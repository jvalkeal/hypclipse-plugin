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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Wrapper for sax parser methods.
 *
 */
public class SAXParserWrapper {

	/** Handle to underlying parser */
	protected SAXParser fParser;
	
	/** Flag if class is disposed */
	protected boolean isdisposed;

	/**
	 * Constructs new parser wrapper.
	 */
	public SAXParserWrapper() throws ParserConfigurationException, SAXException, FactoryConfigurationError {
		fParser = HQDEXMLHelper.Instance().getDefaultSAXParser();
		isdisposed = false;
	}

	/**
	 * Parse the input.
	 * 
	 * @param f
	 * @param dh
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(File f, DefaultHandler dh) throws SAXException, IOException {
		fParser.parse(f, dh);
	}

	/**
	 * Parse the input.
	 * 
	 * @param is
	 * @param dh
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException {
		fParser.parse(is, dh);
	}

	/**
	 * Parse the input.
	 * 
	 * @param is
	 * @param dh
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
		fParser.parse(is, dh);
	}

	/**
	 * Dispose the class and recycle parser.
	 */
	public void dispose() {
		if (isdisposed == false) {
			HQDEXMLHelper.Instance().recycleSAXParser(fParser);
			isdisposed = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		dispose();
	}

}
