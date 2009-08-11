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

import java.util.Vector;
import org.eclipse.swt.graphics.Image;

public class ElementList extends NamedElement {
	private Vector<IPDEElement> children = new Vector<IPDEElement>();

	public ElementList(String name) {
		super(name);
	}

	public ElementList(String name, Image icon) {
		super(name, icon);
	}

	public ElementList(String name, Image icon, IPDEElement parent) {
		super(name, icon, parent);
	}

	public void add(IPDEElement child) {
		children.addElement(child);
	}

	public Object[] getChildren() {
		if (children.size() == 0)
			return new Object[0];
		Object[] result = new Object[children.size()];
		children.copyInto(result);
		return result;
	}

	public void remove(IPDEElement child) {
		children.remove(child);
	}

	public int size() {
		return children.size();
	}

	public String toString() {
		return children.toString();
	}
}
