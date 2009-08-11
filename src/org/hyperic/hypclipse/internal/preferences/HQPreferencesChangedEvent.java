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
package org.hyperic.hypclipse.internal.preferences;

public class HQPreferencesChangedEvent {
	
	public static final int INSERT = 1;
	
	public static final int REMOVE = 2;
	
	public static final int WORLD_CHANGED = 99;
	
	private int type;
	
	public HQPreferencesChangedEvent(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	
}
