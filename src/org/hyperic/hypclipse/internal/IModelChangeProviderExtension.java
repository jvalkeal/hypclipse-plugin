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

import org.hyperic.hypclipse.plugin.IModelChangeProvider;


/**
 *
 */
public interface IModelChangeProviderExtension extends IModelChangeProvider {
	/**
	 * Passes all the listeners to the target change provider.
	 * @param target the target provider
	 * @param filter if not <code>null</code>, the filter will be used to
	 * filter listeners that need to be transfered. Listeners that
	 * do not pass the filter will be exempt from the transfer.
	 */
	void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter);
}
