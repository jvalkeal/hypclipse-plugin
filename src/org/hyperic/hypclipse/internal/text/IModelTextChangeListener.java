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

import org.eclipse.text.edits.TextEdit;
import org.hyperic.hypclipse.plugin.IModelChangedListener;

public interface IModelTextChangeListener extends IModelChangedListener {

	TextEdit[] getTextOperations();

	/**
	 * Get a human readable name for the given TextEdit for use in a refactoring
	 * preview, for instance.
	 * 
	 * @param edit
	 * 			the edit to get a name for
	 * @return
	 * 			the name associated to the given edit, or null if there is none
	 */
	String getReadableName(TextEdit edit);
}
