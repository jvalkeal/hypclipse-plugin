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

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;

public interface IColorManager {

	public static final String[] PROPERTIES_COLORS = new String[] {PreferenceConstants.PROPERTIES_FILE_COLORING_KEY, PreferenceConstants.PROPERTIES_FILE_COLORING_COMMENT, PreferenceConstants.PROPERTIES_FILE_COLORING_VALUE, PreferenceConstants.PROPERTIES_FILE_COLORING_ASSIGNMENT, PreferenceConstants.PROPERTIES_FILE_COLORING_ARGUMENT,};

	void dispose();

	Color getColor(String key);

	void handlePropertyChangeEvent(PropertyChangeEvent event);

}
