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
package org.hyperic.hypclipse.internal.editor.plugin;

import org.hyperic.hypclipse.internal.context.InputContext;
import org.hyperic.hypclipse.internal.context.InputContextManager;
import org.hyperic.hypclipse.internal.editor.HQDEFormEditor;
import org.hyperic.hypclipse.plugin.IBaseModel;
import org.hyperic.hypclipse.plugin.IModel;

public class PluginInputContextManager extends InputContextManager {
//	private BundlePluginModelBase bmodel;

	/**
	 * 
	 */
	public PluginInputContextManager(HQDEFormEditor editor) {
		super(editor);
	}

	public IBaseModel getAggregateModel() {
//		if (bmodel != null)
//			return bmodel;
		return findPluginModel();
	}
//
//	public IModel getPluginModel() {
//		if (bmodel != null)
//			return bmodel.getExtensionsModel();
//		return findPluginModel();
//	}

	protected void fireContextChange(InputContext context, boolean added) {
		super.fireContextChange(context, added);
//		if (context.getId().equals(BundleInputContext.CONTEXT_ID)) {
//			if (added)// bundle arriving
//				bundleAdded(context);
//			else
//				// bundle going away
//				bundleRemoved(context);
//		} else if (context.getId().equals(BuildInputContext.CONTEXT_ID)) {
//			if (added)
//				buildAdded(context);
//			else
//				buildRemoved(context);
//		} else if (context.getId().equals(PluginInputContext.CONTEXT_ID)) {
//			if (added)
//				pluginAdded(context);
//			else
//				pluginRemoved(context);
//		}
	}

//	private void bundleAdded(InputContext bundleContext) {
//		IBundleModel model = (IBundleModel) bundleContext.getModel();
//		if (model.isFragmentModel())
//			bmodel = new BundleFragmentModel();
//		else
//			bmodel = new BundlePluginModel();
//		bmodel.setBundleModel(model);
//		syncExtensions();
//	}

//	private void syncExtensions() {
//		IModel emodel = findPluginModel();
//		if (emodel != null && emodel instanceof ISharedExtensionsModel) {
//			bmodel.setExtensionsModel((ISharedExtensionsModel) emodel);
//			transferListeners(emodel, bmodel);
//		} else
//			bmodel.setExtensionsModel(null);
//	}

	private IModel findPluginModel() {
		InputContext pcontext = findContext(PluginInputContext.CONTEXT_ID);
		return (pcontext != null) ? (IModel) pcontext.getModel() : null;
	}

//	private void bundleRemoved(InputContext bundleContext) {
//		if (bmodel != null) {
//			BundlePluginModelBase preserved = bmodel;
//			bmodel = null;
//			IModel emodel = findPluginModel();
//			if (emodel != null)
//				transferListeners(preserved, emodel);
//		}
//	}

//	private void transferListeners(IModel source, IModel target) {
//		if (source instanceof IModelChangeProviderExtension && target instanceof IModelChangeProviderExtension) {
//			IModelChangeProviderExtension smodel = (IModelChangeProviderExtension) source;
//			IModelChangeProviderExtension tmodel = (IModelChangeProviderExtension) target;
//			// first fire one last event to all the listeners to 
//			// refresh
//			smodel.fireModelChanged(new ModelChangedEvent(smodel, IModelChangedEvent.WORLD_CHANGED, null, null));
//			// now pass the listener to the target model
//			smodel.transferListenersTo(tmodel, new IModelChangedListenerFilter() {
//				public boolean accept(IModelChangedListener listener) {
//					if (listener instanceof IFormPart || listener instanceof FormOutlinePage)
//						return true;
//					return false;
//				}
//			});
//		}
//	}

//	private void pluginAdded(InputContext pluginContext) {
//		if (bmodel != null)
//			syncExtensions();
//	}
//
//	private void pluginRemoved(InputContext pluginContext) {
//		if (bmodel != null)
//			syncExtensions();
//	}
//
//	private void buildAdded(InputContext buildContext) {
//	}
//
//	private void buildRemoved(InputContext buildContext) {
//	}
}
