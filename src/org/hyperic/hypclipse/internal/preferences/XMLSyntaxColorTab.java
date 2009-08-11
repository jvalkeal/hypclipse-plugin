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

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.context.XMLDocumentSetupParticipant;
import org.hyperic.hypclipse.internal.text.ChangeAwareSourceViewerConfiguration;
import org.hyperic.hypclipse.internal.text.IColorManager;
import org.hyperic.hypclipse.internal.text.IPDEColorConstants;
import org.hyperic.hypclipse.internal.text.XMLConfiguration;

public class XMLSyntaxColorTab extends SyntaxColorTab {

	private static final String[][] COLOR_STRINGS = new String[][] {
	/*		{Display name, IPreferenceStore key}		*/
	{HQDEMessages.EditorPreferencePage_text, IPDEColorConstants.P_DEFAULT}, 
	{HQDEMessages.EditorPreferencePage_proc, IPDEColorConstants.P_PROC_INSTR}, 
	{HQDEMessages.EditorPreferencePage_tag, IPDEColorConstants.P_TAG}, 
	{HQDEMessages.EditorPreferencePage_string, IPDEColorConstants.P_STRING}, 
//	{HQDEMessages.XMLSyntaxColorTab_externalizedStrings, IPDEColorConstants.P_EXTERNALIZED_STRING}, 
	{HQDEMessages.EditorPreferencePage_comment, IPDEColorConstants.P_XML_COMMENT}};

	public XMLSyntaxColorTab(IColorManager manager) {
		super(manager);
	}

	protected IDocument getDocument() {
		StringBuffer buffer = new StringBuffer();
		String delimiter = System.getProperty("line.separator");
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append(delimiter);
		buffer.append("<plugin name=\"plugin_name\">");
		buffer.append(delimiter);
		buffer.append("<!-- Comment -->");
		buffer.append(delimiter);
		buffer.append("   <server name=\"some.id\">");
		buffer.append(delimiter);
		buffer.append("   </server>");
		buffer.append(delimiter);
		buffer.append("<help>");
		buffer.append(delimiter);
		buffer.append("body text");
		buffer.append(delimiter);
		buffer.append("</help>");
		buffer.append(delimiter);
		buffer.append("</plugin>");

		IDocument document = new Document(buffer.toString());
		new XMLDocumentSetupParticipant().setup(document);
		return document;
	}

	protected ChangeAwareSourceViewerConfiguration getSourceViewerConfiguration() {
		return new XMLConfiguration(fColorManager);
	}

	protected String[][] getColorStrings() {
		return COLOR_STRINGS;
	}
}
