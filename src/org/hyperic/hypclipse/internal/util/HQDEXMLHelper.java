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

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Utility and helper class for xml parser operations.
 *
 */
public class HQDEXMLHelper {

	/** Singleton instance */
	protected static HQDEXMLHelper fPinstance;
	
	/** Holder for sax parser factory */
	protected static SAXParserFactory fSAXFactory;
	
	/** Holder for document factory */
	protected static DocumentBuilderFactory fDOMFactory;
	
	/** Sax pool */
	protected static List<SoftReference<SAXParser>> fSAXParserQueue;

	/** Dom parser pool */
	protected static List<SoftReference<DocumentBuilder>> fDOMParserQueue;
	
	protected static int fSAXPoolLimit;
	protected static int fDOMPoolLimit;
	protected static final int FMAXPOOLLIMIT = 1;

	/**
	 * Constructor to prevent public instatiation
	 * @throws FactoryConfigurationError
	 */
	protected HQDEXMLHelper() throws FactoryConfigurationError {
		fSAXFactory = SAXParserFactory.newInstance();
		fDOMFactory = DocumentBuilderFactory.newInstance();
		fSAXParserQueue = Collections.synchronizedList(new LinkedList<SoftReference<SAXParser>>());
		fDOMParserQueue = Collections.synchronizedList(new LinkedList<SoftReference<DocumentBuilder>>());
		fSAXPoolLimit = FMAXPOOLLIMIT;
		fDOMPoolLimit = FMAXPOOLLIMIT;
	}

	/**
	 * Returns singleton instance of this class.
	 * @return Singleton instance
	 * @throws FactoryConfigurationError
	 */
	public static HQDEXMLHelper Instance() throws FactoryConfigurationError {
		if (fPinstance == null) {
			fPinstance = new HQDEXMLHelper();
		}
		return fPinstance;
	}


	public synchronized SAXParser getDefaultSAXParser() throws ParserConfigurationException, SAXException {

		SAXParser parser = null;
		if (fSAXParserQueue.isEmpty()) {
			parser = fSAXFactory.newSAXParser();
		} else {
			SoftReference<SAXParser> reference = fSAXParserQueue.remove(0);
			if (reference.get() != null) {
				parser = (SAXParser) reference.get();
			} else {
				parser = fSAXFactory.newSAXParser();
			}
		}
		return parser;
	}

	
	public synchronized DocumentBuilder getDefaultDOMParser() throws ParserConfigurationException {

		DocumentBuilder parser = null;
		if (fDOMParserQueue.isEmpty()) {
			parser = fDOMFactory.newDocumentBuilder();
		} else {
			SoftReference<DocumentBuilder> reference = fDOMParserQueue.remove(0);
			if (reference.get() != null) {
				parser = (DocumentBuilder) reference.get();
			} else {
				parser = fDOMFactory.newDocumentBuilder();
			}
		}
		return parser;
	}


	public synchronized void recycleSAXParser(SAXParser parser) {
		if (fSAXParserQueue.size() < fSAXPoolLimit) {
			SoftReference<SAXParser> reference = new SoftReference<SAXParser>(parser);
			fSAXParserQueue.add(reference);
		}
	}

	public synchronized void recycleDOMParser(DocumentBuilder parser) {
		if (fDOMParserQueue.size() < fDOMPoolLimit) {
			SoftReference<DocumentBuilder> reference = new SoftReference<DocumentBuilder>(parser);
			fDOMParserQueue.add(reference);
		}
	}

	public static String getWritableString(String source) {
		if (source == null)
			return "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
				case '&' :
					buf.append("&amp;");
					break;
				case '<' :
					buf.append("&lt;");
					break;
				case '>' :
					buf.append("&gt;");
					break;
				case '\'' :
					buf.append("&apos;");
					break;
				case '\"' :
					buf.append("&quot;");
					break;
				default :
					buf.append(c);
					break;
			}
		}
		return buf.toString();
	}

	/**
	 * @param source
	 * @return
	 */
	public static String getWritableAttributeString(String source) {
		// Ensure source is defined
		if (source == null) {
			return "";
		}
		// Trim the leading and trailing whitespace if any
		source = source.trim();
		// Translate source using a buffer
		StringBuffer buffer = new StringBuffer();
		// Translate source character by character
		for (int i = 0; i < source.length(); i++) {
			char character = source.charAt(i);
			switch (character) {
				case '&' :
					buffer.append("&amp;");
					break;
				case '<' :
					buffer.append("&lt;");
					break;
				case '>' :
					buffer.append("&gt;");
					break;
				case '\'' :
					buffer.append("&apos;");
					break;
				case '\"' :
					buffer.append("&quot;");
					break;
				case '\r' :
					buffer.append("&#x0D;");
					break;
				case '\n' :
					buffer.append("&#x0A;");
					break;
				default :
					buffer.append(character);
					break;
			}
		}
		return buffer.toString();
	}

	public static int getSAXPoolLimit() {
		return fSAXPoolLimit;
	}

	public static void setSAXPoolLimit(int poolLimit) {
		fSAXPoolLimit = poolLimit;
	}

	public static int getDOMPoolLimit() {
		return fDOMPoolLimit;
	}

	public static void setDOMPoolLimit(int poolLimit) {
		fDOMPoolLimit = poolLimit;
	}

}
