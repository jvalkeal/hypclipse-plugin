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
package org.hyperic.hypclipse.internal;

import org.hyperic.hypclipse.plugin.IModelChangedListener;


/**
 * This filted is to be used when listeners are copied from
 * model to model. It allows some listeners to be skipped in
 * the process.
 */
public interface IModelChangedListenerFilter {
	/**
	 * Tests if the listener should be accepted.
	 * @param listener the listener to test
	 * @return <code>true</code> if the listener should pass
	 * the filter, <code>false</code> otherwise.
	 */
	public boolean accept(IModelChangedListener listener);

}
