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
package org.hyperic.hypclipse.internal.editor;

import org.hyperic.hypclipse.internal.editor.plugin.PluginFoldingStructureProvider;
import org.hyperic.hypclipse.internal.text.PluginModel;
import org.hyperic.hypclipse.plugin.IEditingModel;


public class FoldingStructureProviderFactory {

	public static IFoldingStructureProvider createProvider(HQDESourcePage editor, IEditingModel model) {
		if (model instanceof PluginModel) {
			return new PluginFoldingStructureProvider(editor, model);
		}
		// TODO folding for build model
//		if (model instanceof BundleModel) {
//			return new BundleFoldingStructureProvider(editor, model);
//		}
		return null;
	}

}
