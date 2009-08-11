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
package org.hyperic.hypclipse.internal.preferences;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.context.XMLDocumentSetupParticipant;
import org.hyperic.hypclipse.internal.editor.build.BuildSourcePage;
import org.hyperic.hypclipse.internal.editor.build.BuildSourceViewerConfiguration;
import org.hyperic.hypclipse.internal.text.ChangeAwareSourceViewerConfiguration;
import org.hyperic.hypclipse.internal.text.IColorManager;
import org.hyperic.hypclipse.internal.text.IPDEColorConstants;
import org.hyperic.hypclipse.internal.text.XMLConfiguration;

public class BuildSyntaxColorTab extends SyntaxColorTab {

	private static final String[][] COLOR_STRINGS = new String[][] { 
		{HQDEMessages.ManifestSyntaxColorTab_reservedOSGi, IPDEColorConstants.P_HEADER_OSGI},
		{HQDEMessages.ManifestSyntaxColorTab_keys, IPDEColorConstants.P_HEADER_KEY},
		{HQDEMessages.ManifestSyntaxColorTab_assignment, IPDEColorConstants.P_HEADER_ASSIGNMENT},
		{HQDEMessages.ManifestSyntaxColorTab_values, IPDEColorConstants.P_HEADER_VALUE},
		{HQDEMessages.ManifestSyntaxColorTab_attributes, IPDEColorConstants.P_HEADER_ATTRIBUTES}};

	public BuildSyntaxColorTab(IColorManager manager) {
		super(manager);
	}

	protected IDocument getDocument() {
		StringBuffer buffer = new StringBuffer();
		String delimiter = System.getProperty("line.separator");
		buffer.append("source.. = src/");
		buffer.append(delimiter);
		buffer.append("output.. = bin/");
		buffer.append(delimiter);
		buffer.append("jars.extra.classpath = C:\\agent\\pdk\\lib\\hq-product.jar,\\");
		buffer.append(delimiter);
		buffer.append("                       C:\\agent\\pdk\\lib\\hyperic-util.jar");
		buffer.append(delimiter);
		buffer.append("bin.includes =");
		IDocument document = new Document(buffer.toString());
//		new ManifestDocumentSetupParticipant().setup(document);
		new XMLDocumentSetupParticipant().setup(document);
		return document;
	}
    

	protected ChangeAwareSourceViewerConfiguration getSourceViewerConfiguration() {
//		return new ManifestConfiguration(fColorManager);
		return new XMLConfiguration(fColorManager);
//		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
//		((BuildSourcePage) page).setPreferenceStore(store);
//		return new BuildSourceViewerConfiguration(manager, store, page);
	}

//	if (page instanceof BuildSourcePage) {
//		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
//		((BuildSourcePage) page).setPreferenceStore(store);
//		return new BuildSourceViewerConfiguration(manager, store, page);
//	}
//	BuildSourceViewerConfiguration

	protected String[][] getColorStrings() {
		return COLOR_STRINGS;
	}

}
