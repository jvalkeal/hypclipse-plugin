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
package org.hyperic.hypclipse.internal.schema;

import java.util.ArrayList;

public class AnnoStackItem {

	/**
	 * This list contains annonations. 
	 */
	private ArrayList<StringBuffer> annonations = new ArrayList<StringBuffer>();
	
	/** true if we are writing to item in end of list */
	private boolean finished = true;

	/**
	 * Writes string to active buffer on annotation list.
	 * @param str
	 */
	public void write(String str) {
		
		// if list is empty is we are finished
		// to writing to last element, create
		// new list item.
		if(annonations.isEmpty() || finished) {
			StringBuffer buf = new StringBuffer();
			buf.append(str);
			annonations.add(buf);
			finished = false;
		} else {
			annonations.get(annonations.size()-1).append(str);
		}
			
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public String getFirst() {
		if(annonations.isEmpty())
			return null;
		return annonations.get(annonations.size()-1).toString();
	}

}
