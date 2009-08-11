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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.schema.HQGrammarLoader;
import org.hyperic.hypclipse.internal.util.XMLReaderWrapper;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.psvi.TypeDetector;
import com.sun.msv.verifier.psvi.TypedContentHandler;

public class RelaxParser {

	public static void parse(IFile file, TypedContentHandler reporter, ErrorHandler errorHandler) {
//		DefaultHandler tch;
		InputStream source = null;

		try {
			source = new BufferedInputStream(file.getContents());
			XMLReaderWrapper reader = new XMLReaderWrapper();
			HQGrammarLoader loader = new HQGrammarLoader();
			loader.setController(new DebugController(false,false));
			URL url = HQDEPlugin.getDefault().find(new Path("schema/hq-plugin.rng"));
			URLConnection uc = url.openConnection();
			InputSource is = new InputSource(uc.getInputStream());
			DocumentDeclaration grammar = HQGrammarLoader.loadVGM(is);
			TypeDetector verifier = new TypeDetector(grammar, errorHandler);
			reader.parse(source, verifier, reporter);
		} catch (CoreException e) {
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (FactoryConfigurationError e) {
		} catch (IOException e) {
		}

	}

}
