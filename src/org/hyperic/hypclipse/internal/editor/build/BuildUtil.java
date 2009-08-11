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

package org.hyperic.hypclipse.internal.editor.build;

import java.util.Vector;

import org.hyperic.hypclipse.plugin.IBuildEntry;

public class BuildUtil {

	public static IBuildEntry[] getBuildLibraries(IBuildEntry[] entries) {
		Vector<IBuildEntry> temp = new Vector<IBuildEntry>();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getName().startsWith(IBuildEntry.JAR_PREFIX))
				temp.add(entries[i]);
		}
		return temp.toArray(new IBuildEntry[0]);
	}

}
