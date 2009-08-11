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

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.hyperic.hypclipse.internal.context.XMLDocumentSetupParticipant;
import org.hyperic.hypclipse.internal.editor.HQDESourcePage;

public class XMLConfiguration extends ChangeAwareSourceViewerConfiguration {
	private AnnotationHover fAnnotationHover;
	private XMLDoubleClickStrategy fDoubleClickStrategy;
	private XMLTagScanner fTagScanner;
	private XMLScanner fHqdeScanner;

	private TextAttribute fXMLCommentAttr;
	private MultilineDamagerRepairer fDamagerRepairer;
	// TODO do we have quick assistance
//	private PDEQuickAssistAssistant fQuickAssistant;

	public XMLConfiguration(IColorManager colorManager) {
		this(colorManager, null);
	}

	public XMLConfiguration(IColorManager colorManager, HQDESourcePage page) {
		super(page, colorManager);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {IDocument.DEFAULT_CONTENT_TYPE, XMLPartitionScanner.XML_COMMENT, XMLPartitionScanner.XML_TAG};
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (fDoubleClickStrategy == null)
			fDoubleClickStrategy = new XMLDoubleClickStrategy();
		return fDoubleClickStrategy;
	}

	protected XMLScanner getHQDEScanner() {
		if (fHqdeScanner == null)
			fHqdeScanner = new XMLScanner(fColorManager);
		return fHqdeScanner;
	}

	protected XMLTagScanner getHQDETagScanner() {
		if (fTagScanner == null)
			fTagScanner = new XMLTagScanner(fColorManager);
		return fTagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(XMLDocumentSetupParticipant.XML_PARTITIONING);

		MultilineDamagerRepairer dr = new MultilineDamagerRepairer(getHQDEScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new MultilineDamagerRepairer(getHQDETagScanner());
		reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);

		fXMLCommentAttr = BaseHQDEScanner.createTextAttribute(fColorManager, IPDEColorConstants.P_XML_COMMENT);
		fDamagerRepairer = new MultilineDamagerRepairer(null, fXMLCommentAttr);
		reconciler.setDamager(fDamagerRepairer, XMLPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(fDamagerRepairer, XMLPartitionScanner.XML_COMMENT);

		return reconciler;
	}

	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (fAnnotationHover == null)
			fAnnotationHover = new AnnotationHover();
		return fAnnotationHover;
	}

	/**
	 * Preference colors or fonts have changed.  
	 * Update the default tokens of the scanners.
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fTagScanner == null)
			return; //property change before the editor is fully created
		if (affectsColorPresentation(event))
			fColorManager.handlePropertyChangeEvent(event);
		fTagScanner.adaptToPreferenceChange(event);
		fHqdeScanner.adaptToPreferenceChange(event);
		String property = event.getProperty();
		if (property.startsWith(IPDEColorConstants.P_XML_COMMENT)) {
			adaptTextAttribute(event);
		}
	}

	private void adaptTextAttribute(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.endsWith(IPDEColorConstants.P_BOLD_SUFFIX)) {
			fXMLCommentAttr = adaptToStyleChange(event, SWT.BOLD, fXMLCommentAttr);
		} else if (property.endsWith(IPDEColorConstants.P_ITALIC_SUFFIX)) {
			fXMLCommentAttr = adaptToStyleChange(event, SWT.ITALIC, fXMLCommentAttr);
		} else {
			fXMLCommentAttr = new TextAttribute(fColorManager.getColor(event.getProperty()), fXMLCommentAttr.getBackground(), fXMLCommentAttr.getStyle());
		}
		fDamagerRepairer.setDefaultTextAttribute(fXMLCommentAttr);
	}

	private TextAttribute adaptToStyleChange(PropertyChangeEvent event, int styleAttribute, TextAttribute textAttribute) {
		boolean eventValue = false;
		Object value = event.getNewValue();
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();

		boolean activeValue = (textAttribute.getStyle() & styleAttribute) == styleAttribute;
		if (activeValue != eventValue) {
			Color foreground = textAttribute.getForeground();
			Color background = textAttribute.getBackground();
			int style = eventValue ? textAttribute.getStyle() | styleAttribute : textAttribute.getStyle() & ~styleAttribute;
			textAttribute = new TextAttribute(foreground, background, style);
		}
		return textAttribute;
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		String property = event.getProperty();
		return property.startsWith(IPDEColorConstants.P_DEFAULT) || property.startsWith(IPDEColorConstants.P_PROC_INSTR) || property.startsWith(IPDEColorConstants.P_STRING) /*|| property.startsWith(IPDEColorConstants.P_EXTERNALIZED_STRING)*/ || property.startsWith(IPDEColorConstants.P_TAG) || property.startsWith(IPDEColorConstants.P_XML_COMMENT);
	}

	public boolean affectsColorPresentation(PropertyChangeEvent event) {
		String property = event.getProperty();
		return property.equals(IPDEColorConstants.P_DEFAULT) || property.equals(IPDEColorConstants.P_PROC_INSTR) || property.equals(IPDEColorConstants.P_STRING) /*|| property.equals(IPDEColorConstants.P_EXTERNALIZED_STRING)*/ || property.equals(IPDEColorConstants.P_TAG) || property.equals(IPDEColorConstants.P_XML_COMMENT);
	}

	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
//		if (sourceViewer.isEditable()) {
//			if (fQuickAssistant == null)
//				fQuickAssistant = new PDEQuickAssistAssistant();
//			return fQuickAssistant;
//		}
		return null;
	}

	public void dispose() {
//		if (fQuickAssistant != null)
//			fQuickAssistant.dispose();
	}

	protected int getInfoImplementationType() {
		return SourceInformationProvider.F_XML_IMP;
	}

	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return XMLDocumentSetupParticipant.XML_PARTITIONING;
	}

}
