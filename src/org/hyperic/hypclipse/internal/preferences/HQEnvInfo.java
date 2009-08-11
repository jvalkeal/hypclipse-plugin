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

public class HQEnvInfo {

	private String name;
	private String location;
	private boolean isDefault;

	public HQEnvInfo(String name, String location, boolean isDefault) {
		super();
		this.name = name;
		this.location = location;
		this.isDefault = isDefault;
	}
	
	public HQEnvInfo(String name, String location) {
		this(name, location, false);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean isDefault() {
		return isDefault;
	}
	
	
	
}
