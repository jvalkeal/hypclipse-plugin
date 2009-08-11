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

import org.eclipse.swt.graphics.Image;

public class NamedElement extends DefaultElement {
	protected Image image;

	private String name;

	private IPDEElement parent;

	public NamedElement(String name) {
		this(name, null, null);
	}

	public NamedElement(String name, Image icon) {
		this(name, icon, null);
	}

	public NamedElement(String name, Image image, IPDEElement parent) {
		this.name = name;
		this.image = image;
		this.parent = parent;
	}

	public Image getImage() {
		return image;
	}

	public String getLabel() {
		return name;
	}

	public Object getParent() {
		return parent;
	}

	public String toString() {
		return getLabel();
	}
}
