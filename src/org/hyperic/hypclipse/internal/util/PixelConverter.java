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
package org.hyperic.hypclipse.internal.util;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

public class PixelConverter {
	
	private FontMetrics fFontMetrics;
	
	public PixelConverter(Control control) {
		GC gc = new GC(control);
		gc.setFont(control.getFont());
		fFontMetrics= gc.getFontMetrics();
		gc.dispose();
	}
		
	/**
	 * @see DialogPage#convertHorizontalDLUsToPixels
	 */
	public int convertHorizontalDLUsToPixels(int dlus) {
		return Dialog.convertHorizontalDLUsToPixels(fFontMetrics, dlus);
	}
	
	/**
	 * @see DialogPage#convertWidthInCharsToPixels
	 */
	public int convertWidthInCharsToPixels(int chars) {
		return Dialog.convertWidthInCharsToPixels(fFontMetrics, chars);
	}	
}
