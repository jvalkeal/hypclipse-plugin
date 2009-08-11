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

import org.hyperic.hypclipse.internal.editor.HQDEDetails;
import org.hyperic.hypclipse.internal.editor.HQDESection;

/**
 * AbstractPluginElementDetails
 *
 */
public abstract class AbstractPluginElementDetails extends HQDEDetails {

	private HQDESection fMasterSection;

	/**
	 * @param masterSection
	 */
	public AbstractPluginElementDetails(HQDESection masterSection) {
		fMasterSection = masterSection;
	}

	/**
	 * @return
	 */
	public HQDESection getMasterSection() {
		return fMasterSection;
	}

}
