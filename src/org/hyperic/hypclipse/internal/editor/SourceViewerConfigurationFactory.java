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

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.hyperic.hypclipse.internal.editor.build.BuildSourcePage;
import org.hyperic.hypclipse.internal.editor.build.BuildSourceViewerConfiguration;
import org.hyperic.hypclipse.internal.editor.plugin.PluginSourcePage;
import org.hyperic.hypclipse.internal.text.ChangeAwareSourceViewerConfiguration;
import org.hyperic.hypclipse.internal.text.IColorManager;
import org.hyperic.hypclipse.internal.text.PluginXMLConfiguration;

public class SourceViewerConfigurationFactory {
	
	public static ChangeAwareSourceViewerConfiguration createSourceViewerConfiguration(HQDESourcePage page, IColorManager manager) {
		if (page instanceof PluginSourcePage) {
				return new PluginXMLConfiguration(manager, page);
		}
		
		if (page instanceof BuildSourcePage) {
			IPreferenceStore store = PreferenceConstants.getPreferenceStore();
			((BuildSourcePage) page).setPreferenceStore(store);
			return new BuildSourceViewerConfiguration(manager, store, page);
		}

		return null;
	}

}
