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
package org.hyperic.hypclipse.internal.elements;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ElementLabelProvider extends LabelProvider {
	public static final ElementLabelProvider INSTANCE = new ElementLabelProvider();

	public ElementLabelProvider() {
		super();
	}

	public Image getImage(Object element) {
		if (element instanceof IPDEElement) {
			return ((IPDEElement) element).getImage();
		}
		return super.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof IPDEElement) {
			return ((IPDEElement) element).getLabel();
		}
		return super.getText(element);
	}
}
