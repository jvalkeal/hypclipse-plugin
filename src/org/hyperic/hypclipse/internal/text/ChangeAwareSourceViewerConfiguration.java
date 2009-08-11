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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.editor.HQDESourcePage;
import org.hyperic.hypclipse.internal.editor.ISortableContentOutlinePage;
import org.hyperic.hypclipse.plugin.IBaseModel;
import org.hyperic.hypclipse.plugin.IReconcilingParticipant;

public abstract class ChangeAwareSourceViewerConfiguration extends TextSourceViewerConfiguration {

	protected HQDESourcePage fSourcePage;
	protected IColorManager fColorManager;
	private MonoReconciler fReconciler;
	private InformationPresenter fInfoPresenter;
	private InformationPresenter fOutlinePresenter;

	/**
	 * @param page
	 * @param manager - an IColorManager, clients must dispose this themselves.
	 * @param store
	 */
	public ChangeAwareSourceViewerConfiguration(HQDESourcePage page, IColorManager manager, IPreferenceStore store) {
		super(store);
		fColorManager = manager;
		fSourcePage = page;
	}

	public ChangeAwareSourceViewerConfiguration(HQDESourcePage page, IColorManager manager) {
		this(page, manager, new ChainedPreferenceStore(new IPreferenceStore[] {HQDEPlugin.getDefault().getPreferenceStore(), EditorsUI.getPreferenceStore() // general text editor store
				}));
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (fSourcePage != null && fReconciler == null) {
			IBaseModel model = fSourcePage.getInputContext().getModel();
			if (model instanceof IReconcilingParticipant) {
				ReconcilingStrategy strategy = new ReconcilingStrategy();
				strategy.addParticipant((IReconcilingParticipant) model);
				ISortableContentOutlinePage outline = fSourcePage.getContentOutline();
				if (outline instanceof IReconcilingParticipant)
					strategy.addParticipant((IReconcilingParticipant) outline);
				fReconciler = new MonoReconciler(strategy, false);
				fReconciler.setDelay(500);
			}
		}
		return fReconciler;
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		if (fSourcePage == null)
			return null;
		if (fInfoPresenter == null && getInfoImplementationType() != SourceInformationProvider.F_NO_IMP) {
			IInformationControlCreator icc = getInformationControlCreator(false);
			fInfoPresenter = new InformationPresenter(icc);
			fInfoPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			// Register information provider
			IInformationProvider provider = new SourceInformationProvider(fSourcePage, icc, getInfoImplementationType());
			String[] contentTypes = getConfiguredContentTypes(sourceViewer);
			for (int i = 0; i < contentTypes.length; i++)
				fInfoPresenter.setInformationProvider(provider, contentTypes[i]);

			fInfoPresenter.setSizeConstraints(60, 10, true, true);
		}
		return fInfoPresenter;
	}

	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer) {
//		// Ensure the source page is defined
//		if (fSourcePage == null) {
//			return null;
//		}
//		// Reuse the old outline presenter
//		if (fOutlinePresenter != null) {
//			return fOutlinePresenter;
//		}
//		// Define a new outline presenter
//		fOutlinePresenter = new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, PDEActionConstants.COMMAND_ID_QUICK_OUTLINE));
//		fOutlinePresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//		fOutlinePresenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
//		// Define a new outline provider
//		IInformationProvider provider = new HQDESourceInfoProvider(fSourcePage);
//		// Set the provider on all defined content types
//		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
//		for (int i = 0; i < contentTypes.length; i++) {
//			fOutlinePresenter.setInformationProvider(provider, contentTypes[i]);
//		}
//		// Set the presenter size constraints
//		fOutlinePresenter.setSizeConstraints(50, 20, true, false);
//
//		return fOutlinePresenter;
		return null;
	}

	/**
	 * Returns the outline presenter control creator. The creator is a 
	 * factory creating outline presenter controls for the given source viewer. 
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param commandId the ID of the command that opens this control
	 * @return an information control creator
	 */
//	private IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
//		return new IInformationControlCreator() {
//			public IInformationControl createInformationControl(Shell parent) {
//				int shellStyle = SWT.RESIZE;
//				QuickOutlinePopupDialog dialog = new QuickOutlinePopupDialog(parent, shellStyle, fSourcePage, fSourcePage);
//				return dialog;
//			}
//		};
//	}

	protected int getInfoImplementationType() {
		return SourceInformationProvider.F_NO_IMP;
	}

	protected IInformationControlCreator getInformationControlCreator(final boolean cutDown) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, !cutDown);
			}
		};
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] registeredDetectors = super.getHyperlinkDetectors(sourceViewer);
		if (registeredDetectors == null)
			return null;

		if (fSourcePage == null)
			return registeredDetectors;

		IHyperlinkDetector additionalDetector = (IHyperlinkDetector) fSourcePage.getAdapter(IHyperlinkDetector.class);
		if (additionalDetector == null)
			return registeredDetectors;

		IHyperlinkDetector[] allDetectors = new IHyperlinkDetector[registeredDetectors.length + 1];
		System.arraycopy(registeredDetectors, 0, allDetectors, 0, registeredDetectors.length);
		allDetectors[registeredDetectors.length] = additionalDetector;
		return allDetectors;
	}

	public abstract boolean affectsTextPresentation(PropertyChangeEvent event);

	public abstract boolean affectsColorPresentation(PropertyChangeEvent event);

	public abstract void adaptToPreferenceChange(PropertyChangeEvent event);

	public abstract void dispose();

}
