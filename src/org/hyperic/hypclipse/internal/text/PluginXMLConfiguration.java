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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.hyperic.hypclipse.internal.editor.HQDESourcePage;

public class PluginXMLConfiguration extends XMLConfiguration {

	private ContentAssistant fContentAssistant;
//	private XMLContentAssistProcessor fContentAssistProcessor;
	private PluginXMLTextHover fTextHover;

	public PluginXMLConfiguration(IColorManager colorManager, HQDESourcePage page) {
		super(colorManager, page);
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (sourceViewer.isEditable() && fContentAssistant == null) {
//			fContentAssistProcessor = new XMLContentAssistProcessor(fSourcePage);
			fContentAssistant = new ContentAssistant();
			fContentAssistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//			fContentAssistant.setContentAssistProcessor(fContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
//			fContentAssistant.setContentAssistProcessor(fContentAssistProcessor, XMLPartitionScanner.XML_TAG);
			fContentAssistant.setInformationControlCreator(getInformationControlCreator(true));
			fContentAssistant.setShowEmptyList(false);
//			fContentAssistant.addCompletionListener(fContentAssistProcessor);
		}
		return fContentAssistant;
	}

	public void dispose() {
//		if (fContentAssistProcessor != null)
//			fContentAssistProcessor.dispose();
		super.dispose();
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (fTextHover == null && fSourcePage != null)
			fTextHover = new PluginXMLTextHover(fSourcePage);
		return fTextHover;
	}

}
