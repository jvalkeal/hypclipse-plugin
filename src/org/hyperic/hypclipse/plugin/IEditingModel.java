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
package org.hyperic.hypclipse.plugin;

import org.eclipse.jface.text.IDocument;

public interface IEditingModel extends IModel, IModelChangeProvider, IReconcilingParticipant, IEditable {

	public IDocument getDocument();

	public void setStale(boolean stale);

	public boolean isStale();

	public String getCharset();

	public void setCharset(String charset);

}
